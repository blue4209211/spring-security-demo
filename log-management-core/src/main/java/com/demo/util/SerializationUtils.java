package com.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class SerializationUtils {
    private static final ObjectMapper OM = new ObjectMapper();

    public static final String toJSONString(Object o) throws Exception {
        return OM.writeValueAsString(o);
    }

    public static final <T> T fromJSONString(String json, Class<T> clazz) throws Exception {
        return OM.readValue(json, clazz);
    }

}
