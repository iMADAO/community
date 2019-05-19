package com.madao.api.service;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ArticleServiceFallbackFactory implements FallbackFactory<ArticleService> {

    @Override
    public ArticleService create(Throwable throwable) {
        return null;
    }
}
