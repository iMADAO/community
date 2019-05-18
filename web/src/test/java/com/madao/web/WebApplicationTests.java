package com.madao.web;

import com.madao.api.entity.User;
import com.madao.api.utils.MD5Encoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebApplicationTests {

	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void contextLoads() throws UnsupportedEncodingException, NoSuchAlgorithmException {
//		String result = MD5Encoder.getEncryptedWithSalt("root123", "1");
//		System.out.println(result);
////		System.out.println(interval);
	}

	@Test
	public void test(){
		User user =new User();
		user.setUserName("madao");
		redisTemplate.opsForValue().set("aa", user);
	}

	@Test
	public void test2(){
//		Object obj = redisTemplate.opsForValue().get("aa");
//		System.out.println(obj);
		User user = (User) redisTemplate.opsForValue().get("aa");
		System.out.println(user);
	}

	@Test
	public void test3(){
		List<String> list = new ArrayList<>();
		list.add("");
		list.add("aa");
		list.add(null);
		System.out.println(list.size());
		for(String a: list)
			System.out.println(a);
	}
}
