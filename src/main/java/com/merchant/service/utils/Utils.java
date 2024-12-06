package com.merchant.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.service.common.APIResponse;
import com.merchant.service.common.ErrorResponses;
import com.merchant.service.common.TransactionAPIResponse;
import com.merchant.service.enumclass.ErrorCode;
import com.merchant.service.enumclass.StatusCode;
import com.merchant.service.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static com.mysql.cj.util.TimeUtil.DATE_FORMATTER;

public class Utils {

    @Autowired
    public static FileService fileService = new FileService();

    public static String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extract token without "Bearer " prefix
        }
        return null;
    }

    public static void setUserNotFoundResponse(TransactionAPIResponse response) {
        response.setData(null);
        response.setStatus(false);
        ErrorResponses errorResponse = new ErrorResponses(ErrorCode.USER_NOT_FOUND);
        errorResponse.additionalInfo.excepText =ErrorCode.USER_NOT_FOUND.message;
        response.setError(errorResponse);
        response.setMsg("User Not Found");
        response.setCode(StatusCode.USER_NOT_FOUND.code);
    }

    public static Map<String, Object> loadJsonDataFromFile(MultipartFile file) {
        APIResponse apiResponse = new APIResponse();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Parse JSON content directly from MultipartFile input stream
            return objectMapper.readValue(file.getInputStream(), Map.class);
        } catch (IOException e) {
           // e.printStackTrace();
            apiResponse.setStatus(false);
            apiResponse.setData(null);
            apiResponse.setMsg(e.getMessage());
            return (Map<String, Object>) apiResponse;
        }
    }

    public static void fileWrite(String filename, String response) throws IOException {
        ZonedDateTime nowIst = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        String currentDate = nowIst.format(DATE_FORMATTER);
        fileService.writeToFile(filename, currentDate + "\t" +
                "response :: " + "\t" + response);
    }

    public static boolean validateEmail(String email) {
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,3}$";
        return email.matches(emailPattern); // true if valid, false if invalid
    }
    public static boolean validateMobileInput(String data) {
        return data.matches("\\d{10}");
    }
    public static boolean validateIntegerInput(String data) {
        return data.matches(".*[^0-9].*");
    }

    public static boolean validateText(String data) {
        return data.matches("^[a-zA-Z ]*$");
    }
}
