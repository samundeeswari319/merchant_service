package com.merchant.service.controller;

import com.merchant.service.common.APIResponse;
import com.merchant.service.enumclass.ErrorCode;
import com.merchant.service.enumclass.StatusCode;
import com.merchant.service.model.User;
import com.merchant.service.model.UserFetchRequest;
import com.merchant.service.model.VerifyOtpRequest;
import com.merchant.service.repository.MerchantRepository;
import com.merchant.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/get/user/data")
    public APIResponse getUserData(@RequestBody UserFetchRequest request,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        APIResponse response = new APIResponse();

        try {
            if (request.getMid() == null || request.getMid().isEmpty()) {
                response.setStatus(false);
                response.setCode(400);
                response.setData(null);
                response.setError(ErrorCode.SOMETHING_WENT_WRONG.message);
                response.setMsg("MID is required to fetch data.");
                return response;
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage;

            if (request.getApp_id() == null || request.getApp_id().isEmpty()) {
                userPage = userRepository.findByMid(request.getMid(), pageable);
            } else {
                userPage = userRepository.findByMidAndAppId(request.getMid(), request.getApp_id(), pageable);
            }

            if (userPage.isEmpty()) {
                response.setStatus(false);
                response.setCode(404);
                response.setData(null);
                response.setError(ErrorCode.SOMETHING_WENT_WRONG.message);
                response.setMsg("No data found for the provided MID and App ID.");
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
            response.setStatus(false);
            response.setCode(StatusCode.INTERNAL_SERVER_ERROR.code);
            response.setData(e.getMessage());
            response.setMsg("An error occurred while fetching user data.");
        }

        return response;
    }



    @PostMapping("/verify-otp")
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
}