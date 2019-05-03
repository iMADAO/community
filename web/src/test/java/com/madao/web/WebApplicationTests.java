package com.madao.web;

import com.madao.api.utils.MD5Encoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebApplicationTests {


	@Test
	public void contextLoads() throws UnsupportedEncodingException, NoSuchAlgorithmException {
//		String result = MD5Encoder.getEncryptedWithSalt("root123", "1");
//		System.out.println(result);
////		System.out.println(interval);
	}

}
