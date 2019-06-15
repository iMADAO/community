package com.madao.web.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebConfig implements WebMvcConfigurer{
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorityBanInterceptor()).addPathPatterns("/**/admin/ban/**");
        registry.addInterceptor(new AuthorityGetInterceptor()).addPathPatterns("/**/admin/get/**");
    }
}
