package com.merchant.service.controller;

import com.merchant.service.common.APIResponse;
import com.merchant.service.common.ErrorResponses;
import com.merchant.service.common.TransactionAPIResponse;
import com.merchant.service.enumclass.ErrorCode;
import com.merchant.service.enumclass.StatusCode;
import com.merchant.service.model.Sample;
import com.merchant.service.model.TransactionModel;
import com.merchant.service.model.TransactionRequest;
import com.merchant.service.model.TransactionResponseDTO;
import com.merchant.service.repository.TransactionRepo;
import com.merchant.service.services.SequenceGeneratorService;
import com.merchant.service.utils.DateRangeUtil;
import com.merchant.service.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    SequenceGeneratorService sequenceGeneratorService;
    private static final int DEFAULT_PAGE_SIZE = 10; //

    // NS00049
    @PostMapping("/getTransaction")
    public TransactionAPIResponse getTransactionData(@RequestParam(defaultValue = "0") int page, @RequestBody(required = false) TransactionRequest transactionRequest) {
        TransactionAPIResponse response = new TransactionAPIResponse();

        if (transactionRequest == null || transactionRequest.getMid() == null || transactionRequest.getMid().isEmpty()) {
            response.setStatus(false);
            response.setCode(400);
            response.setData(null);
            response.setError(ErrorCode.SOMETHING_WENT_WRONG.message);
            response.setMsg("MID is required to fetch data.");
        }else{
                // If date_range is provided, calculate the start and end dates
                if (transactionRequest.getDate_range() != null) {
                    try {
                        String[] dateRange = DateRangeUtil.calculateDateRange(
                                transactionRequest.getDate_range(),
                                transactionRequest.getStartDate(),
                                transactionRequest.getEndDate()
                        );
                        transactionRequest.setStartDate(dateRange[0]);
                        transactionRequest.setEndDate(dateRange[1]);
                    } catch (IllegalArgumentException e) {
                        response.setStatus(false);
                        response.setMsg(e.getMessage());
                        response.setData(Collections.emptyList());
                        return response; // Handle invalid date range
                    }
                }

                Page<TransactionModel> model = transactionRepo.findByUserIdAndOptionalTransactionDate(
                        transactionRequest.getRef_number(),
                        transactionRequest.getMid(),
                        transactionRequest.getApp_id(),
                        transactionRequest.getStartDate(),
                        transactionRequest.getEndDate(),
                        transactionRequest.getStatus(),
                        transactionRequest.getPayment_method(),
                        page,
                        DEFAULT_PAGE_SIZE
                );

                if (model != null) {
                    if (model.isEmpty()) {
                        response.setData(null);
                        response.setStatus(false);
                        response.setMsg("no transactions for the selected criteria.");
                        response.setCode(StatusCode.SUCCESS.code);
                        ErrorResponses errorResponse = new ErrorResponses(ErrorCode.NO_TRANSACTIONS_FOUND);
                        errorResponse.additionalInfo.excepText = String.valueOf(ErrorCode.NO_TRANSACTIONS_FOUND);
                        response.setError(errorResponse);
                        return response;
                    } else {
                        List<TransactionResponseDTO> transactionList = model.getContent().stream().map(transaction -> {
                            TransactionResponseDTO responseDTO = new TransactionResponseDTO();
                            responseDTO.setId(transaction.getId());
                            responseDTO.setBbps_type(transaction.getBbps_type());
                            responseDTO.setDescription(transaction.getDescription());
                            responseDTO.setMid(transaction.getMid());
                            responseDTO.setApp_id(transaction.getApp_id());
                            // time format
                            responseDTO.setReference_number(transaction.getReference_number());
                            responseDTO.setTransaction_ref_number(transaction.getTransaction_ref_number());
                            responseDTO.setTransfer_type(transaction.getTransfer_type());
                            responseDTO.setPlatform_type(transaction.getPlatform_type());
                            responseDTO.setPlan_id(transaction.getPlan_id());

                            TransactionResponseDTO.OperatorDetails operatorDetails = new TransactionResponseDTO.OperatorDetails(
                                    transaction.operatorDetails.getOperator_id() > 0 ? transaction.operatorDetails.getOperator_id() : null,
                                    transaction.operatorDetails.getOperator_name(),
                                    transaction.operatorDetails.getOperator_icon()
                            );
                            responseDTO.setOperator_details(operatorDetails);

                            if ("postpaid".equals(transaction.getBbps_type()) || "electricity".equals(transaction.getBbps_type()) ||
                                    "landline".equals(transaction.getBbps_type()) || "creditcardpay".equals(transaction.getBbps_type()) ||
                                    "insurance".equals(transaction.getBbps_type()) || "lpgbooking".equals(transaction.getBbps_type()) ||
                                    "gas".equals(transaction.getBbps_type()) || "broadband".equals(transaction.getBbps_type()) ||
                                    "broadbandpostpaid".equals(transaction.getBbps_type()) || "emi".equals(transaction.getBbps_type())) {
                                TransactionResponseDTO.ConsumerDetails consumerDetails = new TransactionResponseDTO.ConsumerDetails(
                                        transaction.consumerDetails != null ? transaction.consumerDetails.getConsumer_name() : null,
                                        transaction.paymentDetails != null ? transaction.paymentDetails.getUtr_number() : null,
                                        transaction.consumerDetails != null ? transaction.consumerDetails.getBill_date() : null,
                                        transaction.consumerDetails != null ? transaction.consumerDetails.getBill_number() : null,
                                        transaction.consumerDetails != null ? transaction.consumerDetails.getDue_date() : null
                                );
                                responseDTO.setConsumerDetails(consumerDetails);
                            } else {
                                TransactionResponseDTO.ConsumerDetails consumerDetails = new TransactionResponseDTO.ConsumerDetails(
                                        transaction.consumerDetails != null ? transaction.consumerDetails.getConsumer_name() : null,
                                        transaction.consumerDetails != null ? transaction.consumerDetails.getConsumer_number() : null,
                                        null,
                                        null,
                                        null
                                );
                                responseDTO.setConsumerDetails(consumerDetails);
                            }TransactionResponseDTO.PaymentDetails paymentDetails = new TransactionResponseDTO.PaymentDetails(
                                    transaction.paymentDetails.getAmount(),
                                    transaction.paymentDetails.getTransaction_date() != null ? transaction.paymentDetails.getTransaction_date().toString() : null,
                                    transaction.paymentDetails.getCompletion_date() != null ? transaction.paymentDetails.getCompletion_date().toString() : null,
                                    transaction.paymentDetails.getPayment_method(),
                                    transaction.paymentDetails.getPayment_status(),
                                    transaction.paymentDetails.getUtr_number()
                            );
                            responseDTO.setPaymentDetails(paymentDetails);

                            return responseDTO;
                        }).collect(Collectors.toList());

                        // Prepare successful response
                        response.setData(transactionList);
                        response.setStatus(true);
                        response.setTotalPages(model.getTotalPages());
                        response.setTotalElements(model.getTotalElements());
                        response.setPageNumber(model.getPageable().getPageNumber());
                        response.setPageData((model.getPageable().getPageNumber() * DEFAULT_PAGE_SIZE) + DEFAULT_PAGE_SIZE);
                        response.setMsg("Transaction details fetched successfully.");
                        response.setCode(StatusCode.SUCCESS.code);
                    }
                }else {
                    Utils.setUserNotFoundResponse(response);
                    return response;
                }
        }
        return response;
    }
    // NS00062
    @PostMapping("/addTransaction")
    public APIResponse addTransaction(@RequestBody Sample sample){
        APIResponse apiResponse = new APIResponse();
        TransactionModel transactionModel = addTransactionDetails(sample);
        apiResponse.setCode(StatusCode.SUCCESS.code);
        apiResponse.setMsg("SUCCESS");
        apiResponse.setData(transactionModel);
        return apiResponse;
    }


    private TransactionModel addTransactionDetails(Sample sample) {
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setId(sequenceGeneratorService.generateSequence(TransactionModel.SEQUENCE_NAME));
        transactionModel.setUser_id(23);
        transactionModel.setMobile("1234567890");
        transactionModel.setBbps_type("Prepaid");
        transactionModel.setStatus("INITIATE");
        transactionModel.setTransfer_type("BBPS");// need changes
        transactionModel.setPlatform_type("Android");
        transactionModel.setDescription("description");
        transactionModel.setReference_number(sample.getRef_number());
        transactionModel.setPlan_id(sample.getPlan_id());
        transactionModel.setMid(sample.getMid());
        transactionModel.setApp_id(sample.getApp_id());
        //  transactionModel.paymentDetails.setOrder_id("0"); /// need changes

        TransactionModel.OperatorDetails operatorDetails = new TransactionModel.OperatorDetails();
        operatorDetails.setOperator_id(Long.parseLong("1"));
        operatorDetails.setOperator_name("Airtel");

        operatorDetails.setOperator_icon("https://static.mobikwik.com/appdata/operator_icons/op1.png");
        transactionModel.setOperatorDetails(operatorDetails);

        TransactionModel.ConsumerDetails consumerDetails = new TransactionModel.ConsumerDetails();
        consumerDetails.setConsumer_number("7358221354");
        consumerDetails.setConsumer_name("Electricity");
        consumerDetails.setBill_number("Mobile Number (+91)");
        transactionModel.setConsumerDetails(consumerDetails);

        TransactionModel.PaymentDetails paymentDetails = new TransactionModel.PaymentDetails();
        paymentDetails.setAmount("22.0");
        LocalDateTime nowIst = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        paymentDetails.setTransaction_date(nowIst);
        paymentDetails.setPayment_method("UPI");
        paymentDetails.setPayment_status("Success");
        paymentDetails.setUtr_number("UTR123456789");
        paymentDetails.setOrder_id("213"); //need changes
        transactionModel.setPaymentDetails(paymentDetails);
        return transactionRepo.save(transactionModel);
    }
}
