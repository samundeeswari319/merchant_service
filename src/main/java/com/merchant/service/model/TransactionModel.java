package com.merchant.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document("transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionModel {
    @Transient
    public static final String SEQUENCE_NAME = "transaction_sequence";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Id
    private long id;
    private long user_id;
    private String mobile;
    private String bbps_type;
    private String consumer_number;
    private String description;
    private String amount;
    //NS00062
    @CreatedDate
    private LocalDateTime transaction_date;
    @LastModifiedDate
    private LocalDateTime completion_date;
    @LastModifiedDate
    private LocalDateTime updated_date;
    private String status;
    @NonNull
    private String reference_number;
    @NonNull
    private String transaction_ref_number;
    private String transfer_type;
    private String platform_type;
    public String opRefNo;
    public String qpayStamp;
    public long plan_id;
    public String mid;
    public String app_id;


    public OperatorDetails operatorDetails;
    public ConsumerDetails consumerDetails;
    public PaymentDetails paymentDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperatorDetails {
        private long operator_id;
        private String operator_name;
        private String operator_icon;

        public long getOperator_id() {
            return operator_id;
        }

        public void setOperator_id(long operator_id) {
            this.operator_id = operator_id;
        }

        public String getOperator_name() {
            return operator_name;
        }

        public void setOperator_name(String operator_name) {
            this.operator_name = operator_name;
        }

        public String getOperator_icon() {
            return operator_icon;
        }

        public void setOperator_icon(String operator_icon) {
            this.operator_icon = operator_icon;
        }
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConsumerDetails{
        private String consumer_name;
        private String consumer_number;
        private LocalDateTime bill_date;
        private String bill_number;
        private LocalDateTime due_date;

        public String getConsumer_name() {
            return consumer_name;
        }

        public void setConsumer_name(String consumer_name) {
            this.consumer_name = consumer_name;
        }

        public String getConsumer_number() {
            return consumer_number;
        }

        public void setConsumer_number(String consumer_number) {
            this.consumer_number = consumer_number;
        }

        public LocalDateTime getBill_date() {
            return bill_date;
        }

        public void setBill_date(LocalDateTime bill_date) {
            this.bill_date = bill_date;
        }

        public String getBill_number() {
            return bill_number;
        }

        public void setBill_number(String bill_number) {
            this.bill_number = bill_number;
        }

        public LocalDateTime getDue_date() {
            return due_date;
        }

        public void setDue_date(LocalDateTime due_date) {
            this.due_date = due_date;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentDetails{
        private String amount;
        @CreatedDate
        private LocalDateTime transaction_date;
        @LastModifiedDate
        private LocalDateTime completion_date;
        private String payment_method;
        private String payment_status;
        private String utr_number;
        private String order_id;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public LocalDateTime getTransaction_date() {
            return transaction_date;
        }

        public void setTransaction_date(LocalDateTime transaction_date) {
            this.transaction_date = transaction_date;
        }

        public LocalDateTime getCompletion_date() {
            return completion_date;
        }

        public void setCompletion_date(LocalDateTime completion_date) {
            this.completion_date = completion_date;
        }

        public String getPayment_method() {
            return payment_method;
        }

        public void setPayment_method(String payment_method) {
            this.payment_method = payment_method;
        }

        public String getPayment_status() {
            return payment_status;
        }

        public void setPayment_status(String payment_status) {
            this.payment_status = payment_status;
        }

        public String getUtr_number() {
            return utr_number;
        }

        public void setUtr_number(String utr_number) {
            this.utr_number = utr_number;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBbps_type() {
        return bbps_type;
    }

    public void setBbps_type(String bbps_type) {
        this.bbps_type = bbps_type;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getReference_number() {
        return reference_number;
    }

    public void setReference_number(String reference_number) {
        this.reference_number = reference_number;
    }

    public String getTransaction_ref_number() {
        return transaction_ref_number;
    }

    public void setTransaction_ref_number(String transaction_ref_number) {
        this.transaction_ref_number = transaction_ref_number;
    }


    public String getTransfer_type() {
        return transfer_type;
    }

    public void setTransfer_type(String transfer_type) {
        this.transfer_type = transfer_type;
    }

    public String getPlatform_type() {
        return platform_type;
    }

    public void setPlatform_type(String platform_type) {
        this.platform_type = platform_type;
    }


    public LocalDateTime getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(LocalDateTime updated_date) {
        this.updated_date = updated_date;
    }

    public String getOpRefNo() {
        return opRefNo;
    }

    public void setOpRefNo(String opRefNo) {
        this.opRefNo = opRefNo;
    }

    public String getQpayStamp() {
        return qpayStamp;
    }

    public void setQpayStamp(String qpayStamp) {
        this.qpayStamp = qpayStamp;
    }

    public String getConsumer_number() {
        return consumer_number;
    }

    public void setConsumer_number(String consumer_number) {
        this.consumer_number = consumer_number;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(LocalDateTime transaction_date) {
        this.transaction_date = transaction_date;
    }

    public LocalDateTime getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(LocalDateTime completion_date) {
        this.completion_date = completion_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(long plan_id) {
        this.plan_id = plan_id;
    }

    public OperatorDetails getOperatorDetails() {
        return operatorDetails;
    }

    public void setOperatorDetails(OperatorDetails operatorDetails) {
        this.operatorDetails = operatorDetails;
    }

    public ConsumerDetails getConsumerDetails() {
        return consumerDetails;
    }

    public void setConsumerDetails(ConsumerDetails consumerDetails) {
        this.consumerDetails = consumerDetails;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
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
}
