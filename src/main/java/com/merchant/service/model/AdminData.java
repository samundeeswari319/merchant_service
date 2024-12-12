package com.merchant.service.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.merchant.service.common.JsonRequirementsDeserializer;
import com.merchant.service.common.JsonRequirementsSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("admin_user_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminData {
    @Transient
    public static final String SEQUENCE_NAME = "admin_sequence";

    @Id
    public long id;
    @JsonSerialize(using = JsonRequirementsSerializer.class)
    @JsonDeserialize(using = JsonRequirementsDeserializer.class)
    public String admin_user_data;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAdmin_user_data() {
        return admin_user_data;
    }

    public void setAdmin_user_data(String admin_user_data) {
        this.admin_user_data = admin_user_data;
    }
}
