package com.madao.api.service;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class QuestionServiceFallbackFactory implements FallbackFactory<QuestionService>{
    @Override
    public QuestionService create(Throwable throwable) {
        System.out.println("Fall BAcking......................................");
        return null;
    }
}
