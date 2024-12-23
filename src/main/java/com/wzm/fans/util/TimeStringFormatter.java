package com.wzm.fans.util;


import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class TimeStringFormatter {

    public static class Deserializer extends JsonDeserializer<Integer>{

        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            String text = p.getText();
            return DateUtils.toSecond(text);
        }
    }

    public static class Serializer extends JsonSerializer<Integer> {

        @Override
        public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(DateUtils.fromSecond(value));
        }
    }

}
