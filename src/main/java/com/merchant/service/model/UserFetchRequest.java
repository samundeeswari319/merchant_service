package com.merchant.service.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFetchRequest {
    private String mid;
    private String app_id;

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
}
