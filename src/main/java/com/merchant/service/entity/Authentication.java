package com.merchant.service.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "authentication")
public class Authentication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "token")
    public String token;
    @Column(name = "status")
    public String status;
    @Column(name = "merchant_id")
    public String merchant_id;
    @Column(name = "encrypted")
    public int encrypted;
    @Column(name = "white_list_ip")
    public String white_list_ip;
    @Column(name = "whitelist_status")
    public int whitelist_status;
    @Column(name = "session_id")
    public String session_id;
    @Column(name = "payin_status")
    public String payin_status;
    @Column(name = "payout_status")
    public String payout_status;
    @Column(name = "session_expiry")
    public String session_expiry;
    @Column(name = "ekyc")
    public String ekyc;
    @Column(name = "connecting_banking")
    public String connecting_banking;
  /*  @Column(name = "is_service")
    public boolean is_service;*/
}