package com.merchant.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.merchant.service.common.APIResponse;
import com.merchant.service.entity.Authentication;
import com.merchant.service.enumclass.ErrorCode;
import com.merchant.service.enumclass.StatusCode;
import com.merchant.service.model.FieldRequestModel;
import com.merchant.service.model.Merchant;
import com.merchant.service.model.MerchantDTO;
import com.merchant.service.repository.AuthenticationRepository;
import com.merchant.service.repository.MerchantRepository;
import com.merchant.service.services.MerchantService;
import com.merchant.service.services.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.merchant.service.utils.Utils.fileWrite;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    MerchantService merchantService;


    @Autowired
    private MerchantRepository merchantRepository;

    private static Map<String, Object> accumulatedData = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //MERCHANT JSON FILE UPLOAD FLOW
    @PostMapping("/selected_fields")
    public APIResponse updateSelectedFields(@RequestBody FieldRequestModel fieldRequestModel) {

        APIResponse apiResponse = new APIResponse();

        try{
            for (Map.Entry<String, Object> entry : fieldRequestModel.json.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // If the value is null, it means the user wants to remove the key
                if (value == null) {
                    accumulatedData.remove(key);
                } else {
                    // Otherwise, add or update the key in the accumulated data
                    accumulatedData.put(key, value);
                }
            }

            accumulatedData.forEach((key, value) -> {
                if(!fieldRequestModel.json.containsKey(key)) {
                    accumulatedData.remove(key);
                }
            });
        }catch (Exception e){
            apiResponse = showError(e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR.code);
        }

       // accumulatedData.putAll(test.json);
        try {
            String jsonString = objectMapper.writeValueAsString(accumulatedData);
            apiResponse = getAuth(fieldRequestModel,jsonString);
        } catch (JsonProcessingException e) {
            apiResponse = showError(e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR.code);
        }

        return apiResponse;
    }

    @PostMapping("/edit_selected_fields")
    public APIResponse updateMerchant(@RequestBody Map<String, Object> updatedModel) throws JsonProcessingException {
        Object jsonRequirements = updatedModel.get("user_service_data");
        String jsonRequirementsString = new ObjectMapper().writeValueAsString(jsonRequirements);
        Merchant merchant = new Merchant();
        merchant = merchantRepository.findByMid(updatedModel.get("mid").toString());
        if (merchant == null) {
            APIResponse response = new APIResponse();
            response.setStatus(false);
            response.setCode(400);
            response.setMsg(ErrorCode.INVALID_MERCHANT.code);
            response.setError(null);
            return response;
        }else{
            merchant.setUser_register_data(jsonRequirementsString);
            return merchantService.updateMerchant(merchant);
        }
    }


    @PostMapping("/delete_selected_fields")
    public APIResponse deleteMerchantKey(@RequestParam String key, @RequestParam String mid) {
        return merchantService.deleteMerchantKey(mid, key);
    }

    public APIResponse getAuth(FieldRequestModel fieldRequestModel, String jsonString) {
        APIResponse apiResponse = new APIResponse();
        Merchant merchant = new Merchant();
       // String authToken = extractTokenFromHeader(token);
        // String remoteIP = request.getRemoteAddr();

        if (fieldRequestModel.token.isEmpty() || fieldRequestModel.token == null) {
            apiResponse = showError("Invalid authentication", StatusCode.INTERNAL_SERVER_ERROR.code);
        }
        try {
            fileWrite("authenticationRepository",new Gson().toJson(authenticationRepository.findByMerchantId(fieldRequestModel.merchant_id)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            Authentication authentications = authenticationRepository.findByMerchantId(fieldRequestModel.merchant_id);
            try {
                String aa = new Gson().toJson(authentications);
                fileWrite("authentications",aa);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (authentications == null) {
                apiResponse = showError("Authentication Error", StatusCode.FAILURE.code);
            } else {
                if (authentications.token.equals(fieldRequestModel.token)) {
                    if (/*authentications.is_service*/authentications.payin_status != null && authentications.payin_status.equals("1")) {
                        LocalDateTime nowIst = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
                        try {
                            Merchant merchants = merchantRepository.findByMid(authentications.merchant_id);
                            if(merchants == null){
                                merchants = new Merchant();
                                long id = sequenceGeneratorService.generateSequence(Merchant.SEQUENCE_NAME);
                                merchants.setId(id);
                                merchants.setMid(authentications.merchant_id);
                                merchants.setUser_register_data(jsonString);
                                merchants.setApp_id(Collections.singletonList(fieldRequestModel.getApp_id()));
                                merchants.setApp_name(fieldRequestModel.getApp_name());
                                merchants.setCreated_date(nowIst);
                            }else{
                                merchants.setMid(merchants.getMid());
                                merchants.setCreated_date(merchants.getCreated_date());
                                merchants.setId(merchants.getId());
                                merchants.setApp_id(merchants.getApp_id());
                                merchants.setApp_name(merchants.getApp_name());
                                merchants.setUser_register_data(jsonString);
                                merchants.setUpdated_date(nowIst);
                            }
                            merchantRepository.save(merchants);
                            apiResponse.setStatus(true);
                            apiResponse.setData(merchants);
                            apiResponse.setMsg("Merchant requirements updated successfully.");
                        }catch (Exception e) {
                            apiResponse.setStatus(false);
                            apiResponse.setMsg("An error occurred: " + e.getMessage());
                        }
                    } else {
                        apiResponse = showError("Service not available, contact admin", StatusCode.FAILURE.code);
                    }
                } else {
                    apiResponse = showError("Authentication Failed", StatusCode.FAILURE.code);
                }
            }
        }catch (Exception e){
            apiResponse = showError(e.getMessage(), StatusCode.FAILURE.code);
        }

        /*if(authentications.whitelist_status > 0 && !authentications.white_list_ip.equals(remoteIP)) {
            return new ResponseEntity<>("Invalid IP access", HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(authentications,HttpStatus.OK);
        }*/

        return apiResponse;
    }



    public APIResponse showError(String errorMsg, Integer code) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setStatus(false);
        apiResponse.setData(null);
        apiResponse.setMsg(errorMsg);
        apiResponse.setCode(code);
        return apiResponse;
    }

}
