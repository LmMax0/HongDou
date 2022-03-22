package com.lmdd.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来替换springboot自带的jackson
 * @author LM_MAX
 * @date 2022/2/25
 */
@Configuration
public class FastJsonConfigurer implements WebMvcConfigurer {
}
