package com.merchant.service.impl;

import com.merchant.service.common.InvalidException;
import com.merchant.service.enumclass.ErrorCode;
import com.merchant.service.model.AuthTokenModel;
import com.merchant.service.model.TransactionModel;
import com.merchant.service.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

@Repository
public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {

    @Autowired
    MerchantRepository userRepo;

    @Autowired
    AuthTokenModel authTokenModel;


    private final MongoTemplate mongoTemplate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public TransactionRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<TransactionModel> findByUserIdAndOptionalTransactionDate(String ref_number, String mid, String app_id,String startDate, String endDate, String status, String payment_method, int page, int pageSize) {
        Query query = new Query();

        if (mid!= null && !mid.isEmpty()) {
            query.addCriteria(Criteria.where("mid").is(mid));
        }

        if (app_id!= null && !app_id.isEmpty()) {
            query.addCriteria(Criteria.where("app_id").is(app_id));
        }

        if (status != null && !status.isEmpty()) {
            query.addCriteria(Criteria.where("paymentDetails.payment_status").regex("^[" + status.toLowerCase() + status.toUpperCase() + "]+$"));
        }
        if (payment_method != null && !payment_method.isEmpty()) {
            query.addCriteria(Criteria.where("paymentDetails.payment_method").regex("^[" + payment_method.toLowerCase() + payment_method.toUpperCase() + "]+$"));
        }

        if(ref_number != null && !ref_number.isEmpty()){
            query.addCriteria(Criteria.where("reference_number").is(ref_number));
        }

        // NS00062
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.of("Asia/Kolkata"));

            LocalDate startLocalDate;
            LocalDate endLocalDate;

            try {
                startLocalDate = LocalDate.parse(startDate, formatter);
                endLocalDate = LocalDate.parse(endDate, formatter);
            } catch (DateTimeParseException e) {
                throw new InvalidException(ErrorCode.INVALID_DATE_FORMAT);
            }

            if (startLocalDate.isAfter(endLocalDate)) {
                throw new InvalidException(ErrorCode.NO_TRANSACTIONS_FOUND);
            }

            ZonedDateTime startZonedDateTime = startLocalDate.atStartOfDay(ZoneId.of("Asia/Kolkata")).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime endZonedDateTime = endLocalDate.atTime(LocalTime.MAX).atZone(ZoneId.of("Asia/Kolkata")).withZoneSameInstant(ZoneId.of("UTC"));

            Date startDateTime = Date.from(startZonedDateTime.toInstant());
            Date endDateTime = Date.from(endZonedDateTime.toInstant());

            query.addCriteria(Criteria.where("paymentDetails.transaction_date").gte(startDateTime).lte(endDateTime));
        }
// NS00062
        query.with(Sort.by(Sort.Direction.DESC, "paymentDetails.transaction_date"));

        Pageable pageable = PageRequest.of(page, pageSize);
        long totalRecords = mongoTemplate.count(query, TransactionModel.class); // Count total records
        query.skip(pageable.getOffset()); // skip = (page - 1) * size
        query.limit(pageable.getPageSize()); // limit = size

        // Execute the query with pagination
        List<TransactionModel> transactions = mongoTemplate.find(query, TransactionModel.class);

        // Return paginated result as a Page object
        return new PageImpl<>(transactions, pageable, totalRecords);

    }
}
