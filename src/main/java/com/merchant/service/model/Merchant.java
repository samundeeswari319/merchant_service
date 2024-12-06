package com.merchant.service.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.merchant.service.common.JsonRequirementsDeserializer;
import com.merchant.service.common.JsonRequirementsSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("merchant")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Merchant {

    @Transient
    public static final String SEQUENCE_NAME = "merchant_sequence";

    @Id
    public long id;
   // public String name;
    public String mid;
    @JsonSerialize(using = JsonRequirementsSerializer.class)
    @JsonDeserialize(using = JsonRequirementsDeserializer.class)
    public String user_register_data;
    //private String mobile_number;
    //private String token;
    private List<String> app_id;
    private String app_name;
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

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public List<String> getApp_id() {
        return app_id;
    }

    public void setApp_id(List<String> app_id) {
        this.app_id = app_id;
    }


    public String getUser_register_data() {
        return user_register_data;
    }

    public void setUser_register_data(String user_register_data) {
        this.user_register_data = user_register_data;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
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