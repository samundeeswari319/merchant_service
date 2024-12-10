package com.merchant.service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_manager")
public class AppManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "mid")
    public String mid;
    @Column(name = "web_url")
    public String web_url;
    @Column(name = "web_redirect_url")
    public String web_redirect_url;
    @Column(name = "bundelid")
    public String bundelid;
    @Column(name = "intent_redirect")
    public String intent_redirect;
    @Column(name = "whitelist_type")
    public String whitelist_type;
    @Column(name = "operating_system")
    public String operating_system;
    @Column(name = "nickname")
    public String nickname;
    @Column(name = "created_date")
    public String created_date;
    @Column(name = "updated_date")
    public String updated_date;
}
