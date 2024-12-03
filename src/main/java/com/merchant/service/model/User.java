package com.merchant.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class User {
    @Transient
    public static final String SEQUENCE_NAME = "merchant_sequence";

    @Id
    public long id;
    public HashMap<String,Object> user_details;
    public String mid;
    public long user_id;
    public String app_id;
    public String app_name;
    private String send_otp;
    private String last_verification_id;
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

    public String getSend_otp() {
        return send_otp;
    }

    public void setSend_otp(String send_otp) {
        this.send_otp = send_otp;
    }

    public String getLast_verification_id() {
        return last_verification_id;
    }

    public void setLast_verification_id(String last_verification_id) {
        this.last_verification_id = last_verification_id;
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
}
