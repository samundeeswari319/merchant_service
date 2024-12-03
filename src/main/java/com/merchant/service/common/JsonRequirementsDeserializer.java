package com.merchant.service.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class JsonRequirementsDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.readTree(p);
        return node.toString();
    }
}
