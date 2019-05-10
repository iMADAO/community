package com.madao.user;

import com.madao.api.entity.User;
import com.madao.api.utils.KeyUtil;
import com.madao.user.bean.UserExample;
import com.madao.user.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserApplicationTests {
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;


	@Test
	public void contextLoads() {
		UserExample example = new UserExample();
		UserExample.Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(1L);
		List<User> user = userMapper.selectByExample(example);
		System.out.println(user);
	}

	@Test
	public void testRedis(){
//		redisTemplate.opsForValue().set("abcd", "def", 1200L);
		stringRedisTemplate.opsForValue().set("abcd", "def", 1200, TimeUnit.SECONDS);
	}

	@Value("${code-send-interval}")
	private String interval;

	@Test
	public void test(){
//		String str = KeyUtil.genStringCode(100);
//		System.out.println(str);
//		System.out.println(interval);

		Long expire = redisTemplate.getExpire("abc");
		System.out.println(expire);
	}

	@Test
	public void test1(){
//		Long expire = redisTemplate.getExpire("abcd");
//		System.out.println(expire);
//		Long expire = stringRedisTemplate.getExpire("abcde");
//		System.out.println(expire);
//		System.out.println(3 * 60 * 60 * 24);
		com.madao.api.entity.User user = (com.madao.api.entity.User) redisTemplate.opsForValue().get("user");
		System.out.println(user);
	}

}
