package com.merchant.service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.merchant.service.common.APIResponse;
import com.merchant.service.common.ErrorResponses;
import com.merchant.service.entity.Authentication;
import com.merchant.service.enumclass.ErrorCode;
import com.merchant.service.enumclass.StatusCode;
import com.merchant.service.model.*;
import com.merchant.service.repository.AuthenticationRepository;
import com.merchant.service.repository.MerchantRepository;
import com.merchant.service.repository.UserRepository;
import com.merchant.service.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/api")
public class UserController {

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    AuthTokenModel authTokenModel;

    @PostMapping("/api/getUserList")
    public APIResponse getUserData(HttpServletRequest httpServletRequest, @RequestBody UserFetchRequest request,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        APIResponse response = new APIResponse();
        String token = "";
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract token from header
            token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        } else {
            response = showError(null, "Invalid Token", HttpStatus.FORBIDDEN.value());
            return response;
        }

        try {
            if (request.getMid() == null || request.getMid().isEmpty()) {
                response = showError(null, ErrorCode.RESOURCE_NOT_FOUND.message, StatusCode.INTERNAL_SERVER_ERROR.code);
                return response;
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = null;

            try {
                Authentication authentications = authenticationRepository.findByMerchantId(request.getMid());
                if (authentications == null) {
                    response = showError(null, "Authentication Error", StatusCode.FAILURE.code);
                    return response;
                } else {
                    if (authentications.token.equals(token)) {
                        if (request.getApp_id() == null || request.getApp_id().isEmpty()) {
                            userPage = userRepository.findByMid(request.getMid(), pageable);
                        } else {
                            userPage = userRepository.findByMidAndAppId(request.getMid(), request.getApp_id(), pageable);
                        }
                    } else {
                        response = showError(null, "Authentication Error", StatusCode.FAILURE.code);
                        return response;
                    }
                }
            } catch (Exception e) {
                return response = showError(null, e.getMessage(), StatusCode.FAILURE.code);
            }

            if (userPage == null || userPage.isEmpty()) {
                response.setData(null);
                response.setStatus(false);
                response.setMsg("No data for the selected criteria.");
                response.setCode(StatusCode.SUCCESS.code);
                ErrorResponses errorResponse = new ErrorResponses(ErrorCode.NO_DATA_FOUND);
                errorResponse.additionalInfo.excepText = String.valueOf(ErrorCode.NO_DATA_FOUND);
                response.setError(errorResponse);
                return response;
            } else {
                response.setStatus(true);
                response.setCode(StatusCode.SUCCESS.code);
                response.setData(userPage.getContent());
                response.setError(null);
                response.setMsg("Data fetched successfully.");
                response.setTotalPages(userPage.getTotalPages());
                response.setTotalElements(userPage.getTotalElements());
                response.setPageNumber(userPage.getNumber());
                response.setPageData(userPage.getNumberOfElements());
            }
        } catch (Exception e) {
            response = showError(null, e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR.code);
            return response;
        }

        return response;
    }


    @PostMapping("/api/login")
    public APIResponse getLogin(HttpServletRequest request, @RequestBody LoginModel loginModel) throws IOException {
        APIResponse apiResponse = new APIResponse();
        String token = "";
        String app_id = "";
        List<String> errors = new ArrayList<>();
        List<String> list = new ArrayList<>();
        User user = new User();
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract token from header
            token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        } else {
            apiResponse = showError(null, "Invalid Token", HttpStatus.FORBIDDEN.value());
            return apiResponse;
        }
        Authentication authentications = authenticationRepository.findByToken(token);
        if (authentications != null) {
            Merchant merchant = merchantRepository.findByMid(authentications.merchant_id);
            if (merchant != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    Map<String, Object> requirements = objectMapper.readValue(merchant.user_register_data, new TypeReference<Map<String, Object>>() {
                    });
                    if (requirements == null || requirements.isEmpty()) {
                        apiResponse = showError(new ErrorResponses(ErrorCode.CONFIGURATION_FAILED), "Failure", StatusCode.FAILURE.code);
                    } else {
                        user = userRepository.findByMobileNumberAndMid(loginModel.mobile_number, merchant.mid);
                        LocalDateTime nowIst = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
                        if (user != null) {
                            User finalUser = user;
                            requirements.forEach((key, value) -> {
                                Object nameObject = requirements.get(key);
                                // Check if it's a Map and cast
                                if (nameObject instanceof Map) {
                                    Map<String, Object> nameMap = (Map<String, Object>) nameObject;
                                    if (key.equals("mobile_number")) {
                                        if (nameMap.get("is_otp") != null) {
                                            boolean isOtp = (boolean) nameMap.get("is_otp");
                                            if (isOtp) {
                                                    if(!finalUser.otpDetails.getOtp_verify().isEmpty() && finalUser.otpDetails.getOtp_verify().equals("1")) {
                                                        finalUser.otpDetails.setOtp_verify("0");
                                                        finalUser.setUpdated_date(nowIst);
                                                        userRepository.save(finalUser);
                                                    }
                                                    VerifyOtpRequest verifyOtpRequest = new VerifyOtpRequest();
                                                    verifyOtpRequest.setOtp("");
                                                    verifyOtpRequest.setMobileNumber(loginModel.mobile_number);
                                                    verifyOtpRequest.setLastVerificationId("");
                                                    verifyOtp(verifyOtpRequest);
                                                    errors.add("SUCCESS");
                                            } else {
                                                list.add("SUCCESS");
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            apiResponse = showError(null, "Invalid Request", StatusCode.FAILURE.code);
                        }
                    }
                    if (!errors.isEmpty() || !list.isEmpty()) {
                        apiResponse = showSuccess(user, "Login Success", StatusCode.SUCCESS.code);
                    } else {
                        apiResponse = showError(null, "Login Failed!!", StatusCode.FAILURE.code);
                    }
                } catch (Exception e) {
                    apiResponse.setStatus(false);
                    apiResponse.setCode(StatusCode.INTERNAL_SERVER_ERROR.code);
                    apiResponse.setData(null);
                    apiResponse.setError("Internal server error");
                    apiResponse.setMsg("An error occurred during OTP verification" + " " + e.getMessage());
                }
            } else {
                apiResponse = showError(new ErrorResponses(ErrorCode.INVALID_AUTHENTICATION), "Merchant Not found", StatusCode.FORBIDDEN.code);
            }
        } else {
            apiResponse = showError(new ErrorResponses(ErrorCode.INVALID_AUTHENTICATION), "Invalid Token", StatusCode.FORBIDDEN.code);
        }
        return apiResponse;
    }


    @PostMapping("/verify_otp")
    public APIResponse verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        APIResponse response = new APIResponse();
        try {
            User user = userRepository.findByMobileNumberAndAppId(verifyOtpRequest.getMobileNumber(),authTokenModel.app_id);
            if (user == null) {
                response.setStatus(false);
                response.setCode(StatusCode.INTERNAL_SERVER_ERROR.code);
                response.setData(null);
                response.setError(ErrorCode.INVALID_VERIFICATION_ID.code);
                response.setMsg("Invalid Authentication");
                return response;
            }
            /*Merchant merchant = merchantRepository.findByMobileNumber(verifyOtpRequest.getMobileNumber());
            if (merchant == null) {

            }*/

            if ("1111".equals(verifyOtpRequest.getOtp())) {
                //merchant.setSend_otp("1");
                // merchant.setLast_verification_id(verifyOtpRequest.getLastVerificationId());
                LocalDateTime nowIst = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
                user.setUpdated_date(nowIst);
                userRepository.save(user);
                response.setStatus(true);
                response.setCode(StatusCode.SUCCESS.code);
                response.setData(user);
                response.setError(null);
                response.setMsg("OTP Verified Successfully");
            } else {
                response.setStatus(false);
                response.setCode(StatusCode.FAILURE.code);
                response.setData(null);
                response.setError(ErrorCode.INVALID_OTP.code);
                response.setMsg("Invalid OTP");
            }
        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(500);
            response.setData(null);
            response.setError("Internal server error");
            response.setMsg("An error occurred during OTP verification" + " " + e.getMessage());
        }
        return response;
    }

    public APIResponse showError(Object data, String errorMsg, Integer code) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setMsg(errorMsg);
        apiResponse.setStatus(false);
        apiResponse.setCode(code);
        apiResponse.setData(data);
        return apiResponse;
    }

    public APIResponse showSuccess(Object data, String msg, Integer code) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setStatus(true);
        apiResponse.setData(data);
        apiResponse.setMsg(msg);
        apiResponse.setCode(code);
        return apiResponse;
    }
}