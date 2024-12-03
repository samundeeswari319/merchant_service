package com.merchant.service.model;

public class VerifyOtpRequest {
    private String mobileNumber;
    private String otp;
    private String lastVerificationId;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getLastVerificationId() {
        return lastVerificationId;
    }

    public void setLastVerificationId(String lastVerificationId) {
        this.lastVerificationId = lastVerificationId;
    }
}
