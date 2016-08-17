package library.webserviceapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;

public class Parser {

    public static String getJson(Object aClass) throws JsonProcessingException {
        ObjectWriter sObjectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return sObjectWriter.writeValueAsString(aClass);
    }

    public static Object parseResult(String json, Class<?> aClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return objectMapper.readValue(json, aClass);
        } catch (IOException e) {
        }
        return null;
    }

}
