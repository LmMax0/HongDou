package com.lmdd.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author LM_MAX
 * @date 2022/3/18
 */
@Configuration
@ConfigurationProperties(prefix = "mooc" )
public class AppConfig {

    @Getter
    @Setter
    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Jwt {
        private String header = "Authorization";
        private String prefix = "Bearer ";
        private long  accessTokenExpireTime = 60_000L;
        private long refreshTokenExpireTime = 30 * 24 * 3600 * 1000L;
    }
}
