package com.madao.question;

import com.madao.api.entity.User;
import com.madao.api.enums.AgreeEnum;
import com.madao.question.mapper.AnswerMapper;
import com.madao.question.mapper.QuestionMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionApplicationTests {

	@Autowired
	private QuestionMapper mapper;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private AnswerMapper answerMapper;
	@Test
	public void contextLoads() {
//		Question question = mapper.selectByPrimaryKey(1L);
//		System.out.print(question);
		String str = stringRedisTemplate.opsForValue().get("aab");
		System.out.println(str);
		User user = (User) redisTemplate.opsForValue().get("user");
		System.out.println(user);
	}

//	@Test
//	public void test(){
//		int count = answerMapper.getCountInAgree(1L, 1L);
//		System.out.println(count);
//	}


//	@Test
//	public void test2(){
//		answerMapper.insertUserAgree(1L, 4L, AgreeEnum.AGREE.getCode());
//	}



}
