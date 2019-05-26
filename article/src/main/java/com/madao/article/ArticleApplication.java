package com.madao.article;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.madao"})
@MapperScan(basePackages = "com.madao.article.mapper")
@ComponentScan(basePackages = {"com.madao"})
@SpringBootApplication
public class ArticleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArticleApplication.class, args);
	}

}
