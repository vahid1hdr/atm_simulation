package com.egs.eval.client.external;

import feign.Logger;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class DefaultFeignErrorDecoder implements ErrorDecoder {

    public Exception decode(String s, Response response) {
        try {
            return new FeignGeneralException(response.status(), response.body() == null ? "" : IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8.toString()));
        } catch (IOException var4) {
            return new FeignGeneralException(0);
        }
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}