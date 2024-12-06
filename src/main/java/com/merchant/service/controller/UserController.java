package com.merchant.service.controller;

import com.merchant.service.common.APIResponse;
import com.merchant.service.common.ErrorResponses;
import com.merchant.service.entity.Authentication;
import com.merchant.service.enumclass.ErrorCode;
import com.merchant.service.enumclass.StatusCode;
import com.merchant.service.model.User;
import com.merchant.service.model.UserFetchRequest;
import com.merchant.service.model.VerifyOtpRequest;
import com.merchant.service.repository.AuthenticationRepository;
import com.merchant.service.repository.MerchantRepository;
import com.merchant.service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
     MerchantRepository merchantRepository;

    @Autowired
     UserRepository userRepository;
    @Autowired
     AuthenticationRepository authenticationRepository;

    @PostMapping("/getUserList")
    public APIResponse getUserData(HttpServletRequest httpServletRequest,@RequestBody UserFetchRequest request,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        APIResponse response = new APIResponse();
        String token = "";
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract token from header
            token= authorizationHeader.substring(7); // Remove "Bearer " prefix
        } else{
            response = showError("Invalid Token", HttpStatus.FORBIDDEN.value());
            return response;
        }

        try {
            if (request.getMid() == null || request.getMid().isEmpty()) {
                response = showError(ErrorCode.RESOURCE_NOT_FOUND.message, StatusCode.INTERNAL_SERVER_ERROR.code);
                return response;
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = null;

            try {
                Authentication authentications = authenticationRepository.findByMerchantId(request.getMid());
                if (authentications == null) {
                    response = showError("Authentication Error", StatusCode.FAILURE.code);
                    return response;
                } else {
                    if (authentications.token.equals(token)) {
                        if (request.getApp_id() == null || request.getApp_id().isEmpty()) {
                            userPage = userRepository.findByMid(request.getMid(), pageable);
                        } else {
                            userPage = userRepository.findByMidAndAppId(request.getMid(), request.getApp_id(), pageable);
                        }
                    }else{
                        response = showError("Authentication Error", StatusCode.FAILURE.code);
                        return response;
                    }
                }
            }catch (Exception e){
                return  response = showError(e.getMessage(),StatusCode.FAILURE.code);
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
                response.setCode(200);
                response.setData(userPage.getContent());
                response.setError(null);
                response.setMsg("Data fetched successfully.");
                response.setTotalPages(userPage.getTotalPages());
                response.setTotalElements(userPage.getTotalElements());
                response.setPageNumber(userPage.getNumber());
                response.setPageData(userPage.getNumberOfElements());
            }
        } catch (Exception e) {
            response = showError(e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR.code);
            return response;
        }

        return response;
    }



    @PostMapping("/verify_otp")
    public APIResponse verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        APIResponse response = new APIResponse();

        try {

            User user = userRepository.findByMobileNumber(verifyOtpRequest.getMobileNumber());
            if(user == null){
                response.setStatus(false);
                response.setCode(400);
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
                response.setCode(200);
                response.setData(user);
                response.setError(null);
                response.setMsg("OTP Verified Successfully");
            } else {
                response.setStatus(false);
                response.setCode(400);
                response.setData(null);
                response.setError(ErrorCode.INVALID_OTP.code);
                response.setMsg("Invalid OTP");
            }
        } catch (Exception e) {
            response.setStatus(false);
            response.setCode(500);
            response.setData(null);
            response.setError("Internal server error");
            response.setMsg("An error occurred during OTP verification"+ " "+e.getMessage());
        }
        return response;
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