package com.merchant.service.impl;


import com.merchant.service.model.TransactionModel;
import org.springframework.data.domain.Page;



public interface TransactionRepositoryCustom {
    Page<TransactionModel> findByUserIdAndOptionalTransactionDate(String ref_number, String mid, String app_id,String startDate, String endDate, String status, String payment_method, int page, int pageSize);

}
