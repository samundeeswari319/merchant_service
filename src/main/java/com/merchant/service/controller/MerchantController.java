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
import com.merchant.service.repository.AdminUserRepository;
import com.merchant.service.repository.AuthenticationRepository;
import com.merchant.service.repository.MerchantRepository;
import com.merchant.service.repository.UserRepository;
import com.merchant.service.services.MerchantService;
import com.merchant.service.services.SequenceGeneratorService;
import com.merchant.service.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/api")
public class MerchantController {

    @Autowired
    SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    MerchantRepository merchantRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    MerchantService merchantService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private AdminUserRepository adminUserRepository;

    // USER REGISTER FLOW
    @PostMapping("/user")
    private APIResponse userRegister(HttpServletRequest request, @RequestBody HashMap<String, Object> register) throws IOException {
        String token = "";
        String authorizationHeader = request.getHeader("Authorization");
        String appId = request.getHeader("X-AppId");
        APIResponse apiResponse = new APIResponse();
        //String midL = register.get("mid").toString();
        List<String> errors = new ArrayList<>();
        HashMap<String, Object> db_requirement = new HashMap<>();
        User user = new User();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract token from header
            token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        } else {
            apiResponse = showError(null, StatusCode.FORBIDDEN.code, "Invalid Token");
            return apiResponse;
        }
        Authentication authentications = authenticationRepository.findByToken(token);
        if (authentications != null) {
            Merchant merchant = merchantRepository.findByMidAndAppId(authentications.merchant_id,appId);
            if (merchant != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    // Convert JSON string to Map
                    Map<String, Object> requirements = objectMapper.readValue(merchant.user_register_data, new TypeReference<Map<String, Object>>() {
                    });
                    if (requirements == null || requirements.isEmpty()) {
                        apiResponse = showError(new ErrorResponses(ErrorCode.CONFIGURATION_FAILED), StatusCode.SUCCESS.code, null);
                    } else {
                        requirements.forEach((key, value) -> {
                            Object nameObject = requirements.get(key);
                            // Check if it's a Map and cast
                            if (nameObject instanceof Map) {
                                Map<String, Object> nameMap = (Map<String, Object>) nameObject;
                                boolean isMandatory = (boolean) nameMap.get("is_mandatory");
                                if (isMandatory) {
                                    if (!register.containsKey(key)) {
                                        errors.add(ErrorCode.RESOURCE_NOT_FOUND.message);
                                    } else if (!register.get(key).equals("")) {
                                        if (key.equals("email")) {
                                            if (Utils.validateEmail(register.get(key).toString())) {
                                                db_requirement.put(key, register.get(key));
                                            } else {
                                                errors.add(ErrorCode.INVALID_USERID.message);
                                            }
                                        }

                                        if (key.equals("mobile_number")) {
                                            if (Utils.validateMobileInput(register.get(key).toString())) {
                                                db_requirement.put(key, register.get(key));
                                            } else {
                                                errors.add(ErrorCode.INVALID_USERID.message);
                                            }
                                        }
                                        if (key.equals("name") || key.equals("gender")) {
                                            if (Utils.validateText(register.get(key).toString())) {
                                                db_requirement.put(key, register.get(key));
                                            } else {
                                                errors.add(ErrorCode.INVALID_USERID.message);
                                            }
                                        }
                                        if (nameMap.get("is_otp") != null) {
                                            boolean isOtp = (boolean) nameMap.get("is_otp");
                                            if (isOtp) {
                                                user.otpDetails.setSent_otp("0");
                                                user.otpDetails.setLast_verification_id("0");
                                                user.otpDetails.setOtp_type(nameMap.get("param_name").toString());
                                                user.otpDetails.setOtp_verify("0");
                                            }
                                        }
                                        if (key.equals("fcm_token")) {
                                            db_requirement.put(key, register.get(key));
                                        }
                                    } else {
                                        errors.add(ErrorCode.RESOURCE_NOT_FOUND.message);
                                    }

                                } else {
                                    if (register.containsKey(key)) {
                                        db_requirement.put(key, register.get(key));
                                    }
                                }
                            }
                        });
                    }
                    if (appId != null) {
                        if (merchant.getApp_id().equals(appId)) {
                            user.setApp_id(appId);
                        } else {
                            apiResponse = showError(null, StatusCode.FAILURE.code, ErrorCode.CONFIGURATION_MISMATCH.message);
                            return apiResponse;
                        }
                    } else {
                        apiResponse = showError(null, StatusCode.FAILURE.code, ErrorCode.CONFIGURATION_MISMATCH.message);
                        return apiResponse;
                    }
                    if (errors.isEmpty()) {
                        User userDB = userRepository.findByMobileNumberAndAppId(String.valueOf(register.get("mobile_number")), appId);
                        if (userDB != null) {
                            apiResponse = showError(null, StatusCode.SUCCESS.code, "Already Registered!!");
                        } else {
                            long id = sequenceGeneratorService.generateSequence(Merchant.SEQUENCE_NAME);
                            user.setId(id);
                            user.setMid(merchant.mid);
                            user.setUser_id(id);
                            user.setApp_name(merchant.getApp_name());
                            user.user_details = db_requirement;
                            user.setAuth_token(jwtUtils.createToken(user,token));
                            LocalDateTime nowIst = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
                            user.setCreated_date(nowIst);
                            user.setUpdated_date(nowIst);
                            userRepository.save(user);
                            apiResponse.setStatus(true);
                            apiResponse.setData(user);
                            apiResponse.setCode(StatusCode.SUCCESS.code);
                        }
                    } else {
                        apiResponse = showError(errors, StatusCode.FAILURE.code, "Invalid Request");
                    }
                } catch (Exception e) {
                    apiResponse = showError(null, StatusCode.INTERNAL_SERVER_ERROR.code, e.getMessage());
                }
            } else {
                apiResponse = showError(new ErrorResponses(ErrorCode.INVALID_AUTHENTICATION), StatusCode.FORBIDDEN.code, "Merchant Not found");
            }
        } else {
            apiResponse = showError(null, StatusCode.FORBIDDEN.code, "Invalid Token");
        }
        return apiResponse;
    }


    @PostMapping("/admin_data")
    public APIResponse updateSelectedFields(@RequestParam("jsonFilepath") MultipartFile file) {
        return merchantService.updateAdminData(file);
    }


    @PostMapping("/add_admin_data")
    public APIResponse updateSelectedFields(@RequestBody HashMap<String, Object> register) {
        APIResponse apiResponse = new APIResponse();
        return merchantService.updateFields(register);
    }

    private APIResponse showError(Object data, Integer code, String message) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setMsg(message);
        apiResponse.setStatus(false);
        apiResponse.setCode(code);
        apiResponse.setData(data);
        return apiResponse;
    }
}
