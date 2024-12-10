package com.merchant.service.enumclass;

public enum ErrorCode {
    RESOURCE_NOT_FOUND("ER-1001", "Mandatory fields missing"),
    INVALID_CATEGORY("ER-1002", "Invalid characters found in input"),
    INVALID_USERID("ER-1003", "Invalid characters found in input"),
    INVALID_OPERATOR_ID("ER-1004", "Invalid characters found in input"),
    INVALID_CIRCLE_ID("ER-1005", "Invalid characters found in input"),
    INVALID_PLAN_TYPE("ER-1006", "Invalid characters found in input"),
    INTERNAL_SERVER_ERROR("ER-1007", "Internal server error"),
    MISSING_TOKEN("ER-1008", "Missing or Invalid Token"),
    INVALID_AMOUNT("ER-1009", "Invalid input value"),
    INVALID_OTP("ER-1010", "Invalid input value"),
    INVALID_MESSAGE_ID("ER-1011", "Invalid input value"),
    INVALID_CN("ER-1012", "Invalid input value"),
    INVALID_AADHAR("ER-1013", "Invalid input value"),
    INVALID_MOBILE_NUMBER("ER-1014", "Invalid input value"),
    REF_NUM_ALREADY_EXISTS("ER-1015", "Req id already exists"),
    INVALID_ID("ER-1016", "Invalid Request"),
    OPERATOR_NOT_FOUND("ER-1017", "Invalid Request"),
    USER_NOT_FOUND("ER-1018", "Invalid Request"),
    OTP_NOT_SEND("ER-1019", "Invalid Request"),
    OTP_NOT_VERIFIED("ER-1020", "Invalid Request"),
    ACCESS_DENIED("ER-1021", "Access Denied"),
    INVALID_RECHARGE_TYPE("ER-1022", "Invalid Request"),
    INVALID_RECHARGE_AMOUNT("ER-1023", "Invalid Request"),
    RECHARGE_TYPE_MISSING("ER-1024", "Recharge Type Missing"),
    BILL_NOT_FOUND("ER-1025", "Bill not found"),
    INVALID_TX_ID("ER-1026", "Invalid Request"),
    SOMETHING_WENT_WRONG("ER-1027", "Invalid Request"),
    MOBILE_NUMBER_MANDATORY("ER-1028","Mobile Number is Mandatory"),
    PROFILE_EMPTY("ER-1029","Neither name nor profile_image can be empty."),
    PLATFORM_TYPE("ER-1030","Platform Type is Mandatory."),
    //Emp.ID : NS00047
    DISPUTE_ID_NOT_FOUND("ER-1031","Dispute not found."),
    //Emp.ID : NS00063
    ACCOUNT_DETAILS_NOT_FOUND("ER-1032","Account details not found"),
    ACCOUNT_ALREADY_EXIST("ER-1033","Account Already Exist"),
    //NS00062
    NO_TRANSACTIONS_FOUND("ER-1030", "There are no transactions for the selected period."),
    NO_DATA_FOUND("ER-1031", "There are no data for the given request."),
    INVALID_DATE_FORMAT("ER-1031", "Invalid date format. Please use dd-MM-yyyy."),
    MISSING_CUSTOM_DATES("ER-1032", "Custom date range requires both startDate and endDate."),
    INVALID_DATE_RANGE("ER-1033", "Invalid date range provided."),
    FUTURE_END_DATE("ER-1034","End date cannot be a future date."),
    INVALID_AUTHENTICATION("ER-1003", "Invalid Authentication"),
    RESOURCE_EMPTY("ER-1035", "Mandatory field is empty"),
    MERCHANT_DUPLICATE("ER-1001", "Merchant with the given ID already exists."),
    INVALID_MERCHANT("ER-1002","Invalid Merchant."),
    CONFIGURATION_FAILED("ER-1035","Configuration failed and contact to admin"),
    CONFIGURATION_MISMATCH("ER-1036","Configuration Mismatch"),
    INVALID_VERIFICATION_ID("ER-1004","Invalid verification id.");

    public final String code;
    public final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
