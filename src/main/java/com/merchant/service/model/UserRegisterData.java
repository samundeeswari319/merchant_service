package com.merchant.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("user_registration_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterData {
    @Transient
    public static final String SEQUENCE_NAME = "register_sequence";
    @Id
    public long id;
    public String app_id;
    public String app_name;
    public String user_id;
    public String unique_id;
    public String name;
    public String mobile_number;
    public String country_code;
    public String gender;
    public String dob;
    public String email;
    public String address;
    public String profile_image;
    public String device_token;
    public KYC kyc;
    public String CD1;
    public String CD2;
    public String CD3;
    public String CD4;
    public String CD5;
    public String CD6;
    public String CD7;
    public String CD8;
    public String CD9;
    public String CD10;
    public String CD11;
    public String CD12;
    public String CD13;
    public String CD14;
    public String CD15;
    public String CD16;
    public String CD17;
    public String CD18;
    public String CD19;
    public String CD20;
    @CreatedDate
    public LocalDateTime created_date;
    @LastModifiedDate
    public LocalDateTime updated_date;
    class KYC{
        public String aadhaar;
        public String PAN;
        public String driving_license;
        public String passport;
        public String voter_id;
    }

}
