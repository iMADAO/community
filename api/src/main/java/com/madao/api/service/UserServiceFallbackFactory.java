package com.madao.api.service;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFallbackFactory implements FallbackFactory<UserService> {
    @Override
    public UserService create(Throwable throwable) {
        System.out.println("Fall BAcking......................................");
        return null;
    }
}
