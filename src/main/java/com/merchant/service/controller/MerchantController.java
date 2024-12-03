package com.merchant.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.service.common.APIResponse;
import com.merchant.service.common.ErrorResponses;
import com.merchant.service.config.JwtUtils;
import com.merchant.service.entity.Authentication;
import com.merchant.service.enumclass.ErrorCode;
import com.merchant.service.enumclass.StatusCode;
import com.merchant.service.model.*;
import com.merchant.service.repository.AuthenticationRepository;
import com.merchant.service.repository.MerchantRepository;
import com.merchant.service.repository.UserRegisterRepo;
import com.merchant.service.repository.UserRepository;
import com.merchant.service.services.MerchantService;
import com.merchant.service.services.SequenceGeneratorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
/*@RequestMapping("/v1/api")*/
public class MerchantController {

    @Autowired
    UserRegisterRepo userRegisterRepo;
    @Autowired
    SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    MerchantRepository merchantRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthTokenModel authTokenModel;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @PostMapping("/login/auth/create_merchant")
    public APIResponse createMerchant(@RequestBody MerchantDTO model) {
        APIResponse response = new APIResponse();

        try {
            if (model.getApp_id() == null || model.getApp_id().isEmpty() || model.getApp_name() == null) {
                response.setStatus(false);
                response.setData(new ErrorResponses(ErrorCode.RESOURCE_NOT_FOUND));
                response.setCode(StatusCode.INTERNAL_SERVER_ERROR.code);
                return response;
            }
            /*if (model.getMerchant_id() == null || model.getMerchant_id().isEmpty()) {
                response.setStatus(false);
                response.setCode(400);
                response.setData(null);
                response.setError(ErrorCode.INVALID_MERCHANT.code);
                response.setMsg("Merchant ID cannot be null or empty.");
                return response;
            }*/
           /* if (merchantRepository.findByMid(model.getMerchant_id()) != null) {
                response.setStatus(false);
                response.setCode(400);
                response.setData(null);
                response.setError(ErrorCode.MERCHANT_DUPLICATE.code);
                response.setMsg("Duplicate merchant ID.");
                return response;
            }*/
            Merchant merchant = updateMerchant(model);
            Merchant savedModel = merchantRepository.save(merchant);
            response.setStatus(true);
            response.setCode(200);
            response.setData(savedModel);
            response.setError(null);
            response.setMsg("Merchant created successfully.");

        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(500);
            response.setData(null);
            response.setError("Internal server error");
            response.setMsg(e.getMessage());
        }

        return response;
    }

    private Merchant updateMerchant(MerchantDTO merchantDTO) {
        String authToken;
        Merchant model = new Merchant();
        long id = sequenceGeneratorService.generateSequence(Merchant.SEQUENCE_NAME);
        model.setId(id);
        model.setUser_register_data(null);
        model.setMobile_number(merchantDTO.getMobile_number());
        model.setName(merchantDTO.getName());
        model.setApp_name(merchantDTO.getApp_name());
        authToken = jwtUtils.createToken(model);
        model.setToken(authToken);
        model.setMid(String.valueOf(id));
        model.setApp_id(Collections.singletonList(merchantDTO.app_id));
        return model;
    }

    @PostMapping("/add_register_data")
    private APIResponse getUserRegisterData(@RequestBody UserRegisterData userRegisterData) throws IOException {
        APIResponse apiResponse = new APIResponse();
        userRegisterData.id = sequenceGeneratorService.generateSequence(UserRegisterData.SEQUENCE_NAME);
        userRegisterRepo.save(userRegisterData);
        apiResponse.setStatus(true);
        apiResponse.setMsg("Success!!");
        apiResponse.setData(userRegisterData);
        return apiResponse;
    }


    @PostMapping("/edit_merchant")
    public APIResponse updateMerchant(@RequestBody Map<String, Object> updatedModel) throws JsonProcessingException {
        Object jsonRequirements = updatedModel.get("user_register_data");
        String jsonRequirementsString = new ObjectMapper().writeValueAsString(jsonRequirements);
        Merchant merchant = new Merchant();
        merchant.setUser_register_data(jsonRequirementsString);
        String userId = authTokenModel.getUser_id();
        if (userId == null || userId.trim().isEmpty()) {
            APIResponse response = new APIResponse();
            response.setStatus(false);
            response.setCode(400);
            response.setMsg(ErrorCode.INVALID_USERID.code);
            response.setError(null);
            return response;
        }

        // return merchantService.updateMerchant(userId, merchant);
        return merchantService.updateMerchant(merchant);
    }


    @PostMapping("/delete_merchant")
    public ResponseEntity<APIResponse> deleteMerchantKey(@RequestParam String key) {
        String userId = authTokenModel.getUser_id();
        if (userId == null || userId.trim().isEmpty()) {
            APIResponse response = new APIResponse();
            response.setStatus(false);
            response.setCode(400);
            response.setMsg(ErrorCode.INVALID_USERID.code);
            response.setError(null);
            return ResponseEntity.badRequest().body(response);
        }
        APIResponse response = merchantService.deleteMerchantKey(userId, key);
        return ResponseEntity.ok(response);
    }


    //for merchant to select fields in page
    @PostMapping("/user_register_data")
    private APIResponse showUserRequirements(@RequestHeader("Authorization") String token) {
        APIResponse apiResponse = new APIResponse();
        if (token != null) {
            List<UserRegisterData> userRegisterData = userRegisterRepo.findAll();
            UserRegisterData registerData = userRegisterData.get(0);
            //registerData
            apiResponse.setStatus(true);
            apiResponse.setMsg("Success!!");
            apiResponse.setCode(StatusCode.SUCCESS.code);
            apiResponse.setData(registerData);
        }
        return apiResponse;
    }

    @PostMapping("/selected_fields")
    public APIResponse updateSelectedFields(@RequestBody FieldRequestModel test,
                                            @RequestParam("jsonFilepath") MultipartFile file) {
        String userId = authTokenModel.getUser_id();
        if (userId == null || userId.trim().isEmpty()) {
            APIResponse response = new APIResponse();
            response.setStatus(false);
            response.setCode(400);
            response.setMsg(ErrorCode.INVALID_USERID.code);
            response.setError("INVALID_USER_ID");
            return response;
        }
        return merchantService.updateSelectedFields(test.merchant_id, file);
    }


    // USER REGISTER FLOW
    @PostMapping("/api/register_user")
    private APIResponse userRegister(HttpServletRequest request, @RequestBody HashMap<String, Object> register) throws IOException {
        String token = "";
        String authorizationHeader = request.getHeader("Authorization");
        APIResponse apiResponse = new APIResponse();
        //String midL = register.get("mid").toString();
        List<String> errors = new ArrayList<>();
        HashMap<String, Object> db_requirement = new HashMap<>();
        User user = new User();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract token from header
            token= authorizationHeader.substring(7); // Remove "Bearer " prefix
        } else{
            apiResponse.setStatus(false);
            apiResponse.setCode(HttpStatus.FORBIDDEN.value());
            apiResponse.setData("Invalid Token");
            return apiResponse;
        }
        Authentication authentications = authenticationRepository.findByToken(token);
        if (authentications != null) {
            Merchant merchant = merchantRepository.findByMid(authentications.merchant_id);
            if (merchant != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    // Convert JSON string to Map
                    Map<String, Object> requirements = objectMapper.readValue(merchant.user_register_data, new TypeReference<Map<String, Object>>() {
                    });
                    if (requirements == null || requirements.isEmpty()) {
                        apiResponse.setStatus(false);
                        apiResponse.setData(new ErrorResponses(ErrorCode.CONFIGURATION_FAILED));
                        apiResponse.setCode(StatusCode.FAILURE.code);
                    } else {
                        requirements.forEach((key, value) -> {
                            Object nameObject = requirements.get(key);
                            // Check if it's a Map and cast
                            if (nameObject instanceof Map) {
                                Map<String, Object> nameMap = (Map<String, Object>) nameObject;
                                boolean isMandatory = (boolean) nameMap.get("is_mandatory");
                                if(isMandatory){
                                    if (register.containsKey(key) || !register.get(key).equals("")) {
                                        db_requirement.put(key, register.get(key));
                                        if(nameMap.get("is_otp") != null){
                                            boolean isOtp = (boolean) nameMap.get("is_otp");
                                            if(isOtp){
                                                user.setSend_otp("0");
                                                user.setLast_verification_id("0");
                                            }
                                        }
                                    }else{
                                        errors.add(ErrorCode.RESOURCE_NOT_FOUND.message);
                                    }
                                }else{
                                    if (register.containsKey(key)){
                                        db_requirement.put(key, register.get(key));
                                    }
                                }
                            }

                            /*if (register.containsKey(key)) {

                                db_requirement.put(key, register.get(key));
                                // Check if it's a Map and cast
                                if (nameObject instanceof Map) {
                                    Map<String, Object> nameMap = (Map<String, Object>) nameObject;
                                    boolean isMandatory = (boolean) nameMap.get("is_mandatory");
                                    if (isMandatory && register.get(key).equals("")) {
                                        errors.add(ErrorCode.RESOURCE_NOT_FOUND.message);
                                    }
                                    if(nameMap.get("is_otp") != null){
                                        boolean isOtp = (boolean) nameMap.get("is_otp");
                                        if(isOtp){
                                            user.setSend_otp("0");
                                            user.setLast_verification_id("0");
                                        }
                                    }
                                }
                            } else {
                                errors.add(ErrorCode.RESOURCE_NOT_FOUND.message);
                            }*/
                        });
                    }
                    if (register.containsKey("app_id")) {
                        user.setApp_id(String.valueOf(register.get("app_id")));
                    } else {
                        errors.add(ErrorCode.RESOURCE_NOT_FOUND.message);
                    }
                    if (errors.isEmpty()) {
                        long id = sequenceGeneratorService.generateSequence(Merchant.SEQUENCE_NAME);
                        user.setId(id);
                        user.setMid(merchant.mid);
                        user.setUser_id(id);
                        user.setApp_name(merchant.getApp_name());
                        //authToken = jwtUtils.createToken(model);
                        LocalDateTime nowIst = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
                        user.setCreated_date(nowIst);
                        user.user_details = db_requirement;
                        userRepository.save(user);
                        apiResponse.setStatus(true);
                        apiResponse.setData(user);
                        apiResponse.setCode(StatusCode.SUCCESS.code);
                    } else {
                        apiResponse.setStatus(false);
                        apiResponse.setData(new ErrorResponses(ErrorCode.RESOURCE_NOT_FOUND));
                        apiResponse.setCode(StatusCode.INTERNAL_SERVER_ERROR.code);
                    }
                } catch (Exception e) {
                    apiResponse.setMsg(e.getMessage());
                    apiResponse.setStatus(false);
                    apiResponse.setData(errors);
                    apiResponse.setCode(StatusCode.INTERNAL_SERVER_ERROR.code);
                }
            } else {
                apiResponse.setMsg("Merchant Not found");
                apiResponse.setStatus(false);
                apiResponse.setData(new ErrorResponses(ErrorCode.INVALID_AUTHENTICATION));
                apiResponse.setCode(StatusCode.FORBIDDEN.code);
            }
        }else{
            apiResponse.setStatus(false);
            apiResponse.setCode(HttpStatus.FORBIDDEN.value());
            apiResponse.setData("Invalid Token");
        }
        return apiResponse;
    }
}
