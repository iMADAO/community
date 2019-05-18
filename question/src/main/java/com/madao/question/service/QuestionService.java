package com.madao.question.service;

import com.madao.api.dto.AnswerDTO;
import com.madao.api.dto.QuestionDTO;
import com.madao.api.entity.*;
import com.madao.api.enums.CollectTypeEnum;
import com.madao.api.enums.ContentTypeEnum;
import com.madao.api.enums.OperateEnum;
import com.madao.api.form.QuestionForm;
import com.madao.api.service.UserService;
import com.madao.api.utils.KeyUtil;
import com.madao.question.bean.AnswerContentExample;
import com.madao.question.bean.CollectExample;
import com.madao.question.bean.QuestionExample;
import com.madao.question.mapper.AnswerContentMapper;
import com.madao.question.mapper.AnswerMapper;
import com.madao.question.mapper.CollectMapper;
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

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CollectMapper collectMapper;

    @Value("${userPrefix}")
    private String USER_PREFIX;

    public Question addQuestion(QuestionForm form){
        //添加问题
        Question question = new Question();
        BeanUtils.copyProperties(form, question);
        question.setAnswerCount(0);
        question.setQuestionId(KeyUtil.genUniquKeyOnLong());
        questionMapper.insertSelective(question);

        //添加一个默认的空的回答，用于页面查询回答的时候将该问题查询出来
        Answer answer = new Answer();
        answer.setQuestionId(question.getQuestionId());
        answer.setAnswerId(KeyUtil.genUniquKeyOnLong());
        answerMapper.insertSelective(answer);
        return question;
    }

    //获取问题回答
    public List<AnswerDTO> getQuestion() {
        List<AnswerDTO> answerDTOList =  questionMapper.getAnswer();
        for(AnswerDTO answerDTO: answerDTOList) {
            if(answerDTO.getUserId()==null){
                continue;
            }
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

    //进入问题详情页的时候，查询问题和一个回答的信息
    public QuestionDTO getQuestionDTO(Long questionId, Long answerId) {
        Question question = questionMapper.selectByPrimaryKey(questionId);
        AnswerDTO answerDTO = questionMapper.getAnswerDTOById(answerId);
        AnswerContentExample example = new AnswerContentExample();
        AnswerContentExample.Criteria criteria = example.createCriteria();
        criteria.andAnswerIdEqualTo(answerId);
        List<AnswerContent> answerContentList = answerContentMapper.selectByExample(example);
        System.out.println("answerContentList ...." + answerContentList.size());
        if(answerContentList!=null && answerContentList.size()!=0) {
            Collections.sort(answerContentList, (x, y) -> {
                return y.getContentOrder() - x.getContentOrder();
            });
            answerDTO.setContent(answerContentList.get(0).getContent());
        }
        answerDTO.setAnswerContentList(answerContentList);
        User user = (User) redisTemplate.opsForValue().get(USER_PREFIX + answerDTO.getUserId());
        System.out.println("redis get user...." + user);
        if(user==null){
            user = userService.getUserById(answerDTO.getUserId());
            System.out.println("database get user" + user);
            redisTemplate.opsForValue().set(USER_PREFIX + answerDTO.getUserId(), user, 3600, TimeUnit.SECONDS);
        }
        answerDTO.setUserName(user.getUserName());
        answerDTO.setUserPic(user.getUserPic());
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setAnswerDTO(answerDTO);
        return questionDTO;
    }

    public QuestionDTO getQuestionDTOWithoutAnswer(Long questionId) {
        Question question = questionMapper.selectByPrimaryKey(questionId);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        return questionDTO;
    }

    public void collectQuestion(Long questionId, Long userId, Byte collectType) {
        CollectExample example = new CollectExample();
        CollectExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andTargetIdEqualTo(questionId);
        criteria.andTypeEqualTo(CollectTypeEnum.QUESTION.getCode());
        int count = collectMapper.countByExample(example);
        //如果是添加操作的话
        if(collectType.equals(OperateEnum.OPERATE.getCode())){
            //已经有记录就直接返回
            if(count > 0)
                return;
            //否则添加
            Collect collect = new Collect(KeyUtil.genUniquKeyOnLong(), userId, questionId, CollectTypeEnum.QUESTION.getCode());
            collectMapper.insertSelective(collect);
        }else if(collectType.equals(OperateEnum.CANCEL.getCode())){
            //如果是取消操作
            //没有记录就直接返回
            if(count==0)
                return;
            //否则进行删除
            collectMapper.deleteByExample(example);

        }
    }

    public boolean getUserCollectQuestionState(Long questionId, Long userId) {
        CollectExample example = new CollectExample();
        CollectExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andTargetIdEqualTo(questionId);
        criteria.andTypeEqualTo(CollectTypeEnum.QUESTION.getCode());

        int count = collectMapper.countByExample(example);
        return count > 0 ? true : false;
    }
}
