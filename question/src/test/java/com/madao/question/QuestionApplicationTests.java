package com.madao.question;

import com.madao.api.dto.ReportDTO;
import com.madao.api.entity.Answer;
import com.madao.api.entity.Question;
import com.madao.api.entity.User;
import com.madao.api.enums.AgreeEnum;
import com.madao.api.service.QuestionService;
import com.madao.api.service.UserService;
import com.madao.api.utils.ResultView;
import com.madao.question.bean.QuestionExample;
import com.madao.question.mapper.AnswerMapper;
import com.madao.question.mapper.QuestionMapper;
import com.madao.question.mapper.ReportMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionApplicationTests {

	@Autowired
	private UserService userService;

	@Autowired
	private QuestionMapper mapper;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private AnswerMapper answerMapper;

	@Autowired
	private ReportMapper reportMapper;

	@Autowired
	private QuestionService questionService;
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


	@Test
	public void test3(){
		List<Long> answerIdList = answerMapper.getAnswerIdListByQuestionIdOrderbyAgree(1558362893298165237L, 0, 10);
		answerIdList.stream().forEach(System.out::println);
	}

	@Test
	public void test4(){
//		User user = userService.getUserById(1L);
//		System.out.println(user);
		Answer answer = answerMapper.selectByPrimaryKey(1558341914119102247L);
		System.out.println(answer);
	}

	@Autowired
	QuestionMapper questionMapper;
	@Test
	public void test5(){
//		List<ReportDTO> reportDTOList = reportMapper.getReportDTOList();
//		reportDTOList.stream().forEach(System.out::println);
//		ResultView resultView = questionService.searchByQuestion("iphone", 1, 1);
//		System.out.println(resultView);

		QuestionExample example = new QuestionExample();
		QuestionExample.Criteria criteria = example.createCriteria();
		criteria.andQuestionTitleLike("%iphone%");
		List<Question> questionList = questionMapper.selectByExample(example);
		questionList.stream().forEach(System.out::println);
	}

}
