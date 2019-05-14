package com.madao.api.service;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class PostServiceFallbackFactory implements FallbackFactory<PostService> {
    @Override
    public PostService create(Throwable throwable) {
        return null;
    }
}
