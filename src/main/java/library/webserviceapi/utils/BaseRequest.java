package library.webserviceapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Created by BABEL Sistemas de Información.
 */
public abstract class BaseRequest {

    public static String getJson(Object aClass) throws JsonProcessingException {
        ObjectWriter sObjectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return sObjectWriter.writeValueAsString(aClass);
    }


}
