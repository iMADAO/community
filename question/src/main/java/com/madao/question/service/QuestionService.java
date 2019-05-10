package com.madao.question.service;

import com.madao.api.dto.AnswerDTO;
import com.madao.api.entity.AnswerContent;
import com.madao.api.entity.Question;
import com.madao.api.entity.User;
import com.madao.api.enums.ContentTypeEnum;
import com.madao.api.form.QuestionForm;
import com.madao.api.service.UserService;
import com.madao.api.utils.KeyUtil;
import com.madao.question.bean.AnswerContentExample;
import com.madao.question.mapper.AnswerContentMapper;
import com.madao.question.mapper.AnswerMapper;
import com.madao.question.mapper.QuestionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerContentMapper answerContentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private AnswerMapper answerMapper;
    @Value("${userPrefix}")
    private String USER_PREFIX;

    public Question addQuestion(QuestionForm form){
        Question question = new Question();
        BeanUtils.copyProperties(form, question);
        question.setAnswerCount(0);
        question.setQuestionId(KeyUtil.genUniquKeyOnLong());
        questionMapper.insertSelective(question);
        return question;
    }

    public List<AnswerDTO> getQuestion() {
        List<AnswerDTO> answerDTOList =  questionMapper.getAnswer();
        for(AnswerDTO answerDTO: answerDTOList) {
            AnswerContentExample example = new AnswerContentExample();
            AnswerContentExample.Criteria criteria = example.createCriteria();
            criteria.andAnswerIdEqualTo(answerDTO.getAnswerId());
            criteria.andTypeEqualTo(ContentTypeEnum.TEXT.getCode().intValue());
            List<AnswerContent> answerContentList = answerContentMapper.selectByExample(example);
            System.out.println("answerContentList ...." + answerContentList.size());
            if(answerContentList!=null && answerContentList.size()!=0) {
                Collections.sort(answerContentList, (x, y) -> {
                    return y.getContentOrder() - x.getContentOrder();
                });
                answerDTO.setContent(answerContentList.get(0).getContent());
            }
            User user = (User) redisTemplate.opsForValue().get(USER_PREFIX + answerDTO.getUserId());
            System.out.println("redis get user...." + user);
            if(user==null){
                user = userService.getUserById(answerDTO.getUserId());
                System.out.println("database get user" + user);
                redisTemplate.opsForValue().set(USER_PREFIX + answerDTO.getUserId(), user, 3600, TimeUnit.SECONDS);
            }
            answerDTO.setUserName(user.getUserName());
            answerDTO.setUserPic(user.getUserPic());

        }
        return answerDTOList;
    }
}
