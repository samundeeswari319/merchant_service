package com.merchant.service.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionResponseDTO {
    private long id;
    private String bbps_type;
    private String description;
    private String reference_number;
    private String transaction_ref_number;
    private String transfer_type;
    private String platform_type;
    private  long plan_id;
    private String mid;
    private String app_id;
    private OperatorDetails operator_details;
    private ConsumerDetails consumerDetails;
    private PaymentDetails paymentDetails;

    @Data
    @NoArgsConstructor
    public static class OperatorDetails {
        private Long operator_id;
        private String operator_name;
        private String operator_icon;

        public OperatorDetails(Long operator_id, String operator_name, String operator_icon) {
            this.operator_id = operator_id;
            this.operator_name = operator_name;
            this.operator_icon = operator_icon;
        }

        public Long getOperator_id() {
            return operator_id;
        }

        public void setOperator_id(Long operator_id) {
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
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ConsumerDetails{
        private String consumer_name;
        private String consumer_number;
        private LocalDateTime bill_date;
        private String bill_number;
        private LocalDateTime due_date;

        public ConsumerDetails(String consumer_name, String consumer_number, LocalDateTime bill_date, String bill_number, LocalDateTime due_date) {
            this.consumer_name = consumer_name;
            this.consumer_number = consumer_number;
            this.bill_date = bill_date;
            this.bill_number = bill_number;
            this.due_date = due_date;
        }

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
    @NoArgsConstructor
    public static class PaymentDetails{
        private String amount;
        private String transaction_date;
        private String completion_date;
        private String payment_method;
        private String payment_status;
        private String utr_number;

        public PaymentDetails(String amount, String transaction_date, String completion_date, String payment_method, String payment_status, String utr_number) {
            this.amount = amount;
            this.transaction_date = transaction_date;
            this.completion_date = completion_date;
            this.payment_method = payment_method;
            this.payment_status = payment_status;
            this.utr_number = utr_number;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTransaction_date() {
            return transaction_date;
        }

        public void setTransaction_date(String transaction_date) {
            this.transaction_date = transaction_date;
        }

        public String getCompletion_date() {
            return completion_date;
        }

        public void setCompletion_date(String completion_date) {
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
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(long plan_id) {
        this.plan_id = plan_id;
    }

    public OperatorDetails getOperator_details() {
        return operator_details;
    }

    public void setOperator_details(OperatorDetails operator_details) {
        this.operator_details = operator_details;
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
