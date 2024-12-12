package com.merchant.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.service.common.APIResponse;
import com.merchant.service.entity.Authentication;
import com.merchant.service.enumclass.ErrorCode;
import com.merchant.service.enumclass.StatusCode;
import com.merchant.service.model.AdminData;
import com.merchant.service.model.Merchant;
import com.merchant.service.repository.AdminUserRepository;
import com.merchant.service.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import static com.merchant.service.utils.Utils.loadJsonDataFromFile;

@Service
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    SequenceGeneratorService sequenceGeneratorService;


    public APIResponse updateSelectedFields(String id, MultipartFile file) {
        APIResponse apiResponse = new APIResponse();

        try {
            // Load JSON data from uploaded file
            Map<String, Object> jsonData = loadJsonDataFromFile(file);
            if (jsonData == null) {
                apiResponse.setStatus(false);
                apiResponse.setMsg("Failed to read JSON file.");
                return apiResponse;
            }

            // Retrieve merchant by ID
            Merchant merchant = merchantRepository.findByMid(id);
            if (merchant == null) {
                apiResponse.setStatus(false);
                apiResponse.setMsg(String.valueOf(ErrorCode.RESOURCE_EMPTY));
                return apiResponse;
            }

            merchant.setUser_register_data(new ObjectMapper().writeValueAsString(jsonData));
            merchantRepository.save(merchant);

            apiResponse.setStatus(true);
            apiResponse.setMsg("Merchant requirements updated successfully.");
        } catch (Exception e) {
            apiResponse.setStatus(false);
            apiResponse.setMsg("An error occurred: " + e.getMessage());
        }

        return apiResponse;
    }


    public APIResponse updateAdminData(MultipartFile file) {
        APIResponse apiResponse = new APIResponse();

        try {
            // Load JSON data from uploaded file
            Map<String, Object> jsonData = loadJsonDataFromFile(file);
            if (jsonData == null) {
                apiResponse.setStatus(false);
                apiResponse.setMsg("Failed to read JSON file.");
                return apiResponse;
            }

            // Retrieve merchant by ID
            AdminData adminData = new AdminData();
            adminData.setAdmin_user_data(new ObjectMapper().writeValueAsString(jsonData));
            adminUserRepository.save(adminData);
            apiResponse.setStatus(true);
            apiResponse.setMsg("Data updated successfully.");
        } catch (Exception e) {
            apiResponse.setStatus(false);
            apiResponse.setMsg("An error occurred: " + e.getMessage());
        }

        return apiResponse;
    }

    public APIResponse updateMerchant(Merchant updatedModel) {
        APIResponse apiResponse = new APIResponse();

        try {
            if (updatedModel == null) {
                apiResponse.setStatus(false);
                apiResponse.setMsg("Merchant not found for the given ID.");
                return apiResponse;
            }

            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> existingJsonData = new HashMap<>();
            if (updatedModel.getUser_register_data() != null && !updatedModel.getUser_register_data().isEmpty()) {
                existingJsonData = objectMapper.readValue(updatedModel.getUser_register_data(), Map.class);
            }

            Map<String, Object> updatedJsonData = objectMapper.readValue(updatedModel.getUser_register_data(), Map.class);
            existingJsonData.putAll(updatedJsonData);

            String updatedJsonString = objectMapper.writeValueAsString(existingJsonData);

            updatedModel.setUser_register_data(updatedJsonString);
            Merchant savedModel = merchantRepository.save(updatedModel);

            apiResponse.setStatus(true);
            apiResponse.setCode(200);
            apiResponse.setData(savedModel);
            apiResponse.setError(null);
            apiResponse.setMsg("Merchant updated successfully.");
        } catch (Exception e) {
            apiResponse.setStatus(false);
            apiResponse.setCode(500);
            apiResponse.setData(null);
            apiResponse.setError("INTERNAL_SERVER_ERROR");
            apiResponse.setMsg("An error occurred while updating the merchant: " + e.getMessage());
        }

        return apiResponse;
    }
    public APIResponse deleteMerchantKey(String mid, String key) {
        APIResponse response = new APIResponse();

        try {
            Merchant merchant = merchantRepository.findByMid(mid);

            if (merchant != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> jsonRequirements = objectMapper.readValue(merchant.getUser_register_data(), Map.class);

                if (jsonRequirements.containsKey(key)) {
                    jsonRequirements.remove(key);
                    merchant.setUser_register_data(objectMapper.writeValueAsString(jsonRequirements));
                    merchantRepository.save(merchant);

                    response.setCode(StatusCode.SUCCESS.code);
                    response.setStatus(true);
                    response.setMsg("Key '" + key + "' deleted successfully from merchant.");
                } else {
                    response.setCode(StatusCode.FAILURE.code);
                    response.setStatus(false);
                    response.setMsg("Key '" + key + "' not found in merchant json_requirements.");
                }
            } else {
                response.setCode(StatusCode.FAILURE.code);
                response.setStatus(false);
                response.setMsg("Merchant not found for mid: " + mid);
            }
        } catch (Exception e) {
            // Handle unexpected errors
            response.setCode(StatusCode.FAILURE.code);
            response.setStatus(false);
            response.setMsg("An error occurred while deleting the key: " + e.getMessage());
        }

        return response;
    }



}
