package com.merchant.service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthTokenModel {
    public String mobile;
    public String user_id;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

}
