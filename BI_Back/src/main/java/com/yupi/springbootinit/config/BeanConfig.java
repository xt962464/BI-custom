//package com.yupi.springbootinit.config;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class BeanConfig {
//
//    @Bean
//    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
//        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(Long.class, ToStringSerializer.instance);
//        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
//        objectMapper.registerModule(module);
//        return objectMapper;
//    }
//
//
//
//}
