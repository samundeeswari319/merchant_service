package com.merchant.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;

@Document("user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Transient
    public static final String SEQUENCE_NAME = "merchant_sequence";

    @Id
    public long id;
    public HashMap<String,Object> user_details = new HashMap<>();
    public String mid;
    public long user_id;
    public String app_id;
    public String app_name;
    public String auth_token;
    public OTPDetails otpDetails = new OTPDetails();
    @CreatedDate
    private LocalDateTime created_date;
    @LastModifiedDate
    private LocalDateTime updated_date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HashMap<String, Object> getUser_details() {
        return user_details;
    }

    public void setUser_details(HashMap<String, Object> user_details) {
        this.user_details = user_details;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public LocalDateTime getCreated_date() {
        return created_date;
    }

    public void setCreated_date(LocalDateTime created_date) {
        this.created_date = created_date;
    }

    public LocalDateTime getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(LocalDateTime updated_date) {
        this.updated_date = updated_date;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public OTPDetails getOtpDetails() {
        return otpDetails;
    }

    public void setOtpDetails(OTPDetails otpDetails) {
        this.otpDetails = otpDetails;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OTPDetails {
        private String sent_otp;
        private String otp_verify;
        private String otp_type;
        private String last_verification_id;

        public String getSent_otp() {
            return sent_otp;
        }

        public void setSent_otp(String sent_otp) {
            this.sent_otp = sent_otp;
        }

        public String getOtp_verify() {
            return otp_verify;
        }

        public void setOtp_verify(String otp_verify) {
            this.otp_verify = otp_verify;
        }

        public String getOtp_type() {
            return otp_type;
        }

        public void setOtp_type(String otp_type) {
            this.otp_type = otp_type;
        }

        public String getLast_verification_id() {
            return last_verification_id;
        }

        public void setLast_verification_id(String last_verification_id) {
            this.last_verification_id = last_verification_id;
        }
    }
}
