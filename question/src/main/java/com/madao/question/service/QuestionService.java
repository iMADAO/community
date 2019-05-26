package com.madao.question.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.madao.api.dto.AnswerDTO;
import com.madao.api.dto.QuestionDTO;
import com.madao.api.entity.*;
import com.madao.api.enums.*;
import com.madao.api.form.QuestionForm;
import com.madao.api.service.UserService;
import com.madao.api.utils.KeyUtil;
import com.madao.question.bean.*;
import com.madao.question.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private AgreeMapper agreeMapper;

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

    //获取可见的回答分页
    public PageInfo<AnswerDTO> getQuestionPageVisible(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<AnswerDTO> answerDTOList =  questionMapper.getAnswerByState(StateEnum.VISIBLE.getCode());
        PageInfo<AnswerDTO> resultPage = new PageInfo<>(answerDTOList);
        setAnswerContentByAnswerDTOList(answerDTOList);
        return resultPage;
    }

    //获取用户信息
    public User getUserFromCache(Long userId){
        User user = null;
        try {
            user = (User) redisTemplate.opsForValue().get(USER_PREFIX + userId);
            System.out.println("redis get user...." + user);
        }catch (Exception e){
            System.out.println("redis---error");
        }

        if(user==null){
            user = userService.getUserById(userId);
            System.out.println("database get user" + user);
            try {
                redisTemplate.opsForValue().set(USER_PREFIX + userId, user, 3600, TimeUnit.SECONDS);
            }catch (Exception e){
                System.out.println("reids Fall.....");
            }
        }
        return user;
    }

    //获取下一个回答
    public AnswerDTO getNextAnswer(Long questionId, List<Long> answerIdList) {
        List<Long> answerIdListData = getAnswerIdByPosition(questionId, 0, answerIdList.size()+1);
        System.out.println("answerIdListDataSize-----" + answerIdListData.size());
        if(answerIdListData.size()<=answerIdList.size()){
            return null;
        }
        Long targetAnswerId = null;
        for(Long answerId: answerIdListData){
            if(!answerIdList.contains(answerId)){
                targetAnswerId = answerId;
                break;
            }
        }
        System.out.println("targetAnswerId----" + targetAnswerId);
        if(targetAnswerId==null)
            return null;
        Answer answer = answerMapper.selectByPrimaryKey(targetAnswerId);
        AnswerDTO answerDTO = new AnswerDTO();
        User user = getUserFromCache(answer.getUserId());
        BeanUtils.copyProperties(user, answerDTO);
        BeanUtils.copyProperties(answer, answerDTO);

        AnswerContentExample example = new AnswerContentExample();
        AnswerContentExample.Criteria criteria = example.createCriteria();
        criteria.andAnswerIdEqualTo(answer.getAnswerId());
        List<AnswerContent> answerContentList = answerContentMapper.selectByExample(example);
        answerDTO.setAnswerContentList(answerContentList);
        return answerDTO;
    }

    public List<Long> getAnswerIdByPosition(Long questionId, Integer startRow, Integer size){
        List<Long> answerIdList = answerMapper.getAnswerIdListByQuestionIdOrderbyAgree(questionId, startRow, size);
        return answerIdList;
    }

    //检查用户是否点赞、收藏回答，并添加结果
    public void addAnswerState(AnswerDTO answerDTO, Long userId) {
        List<Byte> resultList = new ArrayList<>();

        AgreeExample agreeExample = new AgreeExample();
        AgreeExample.Criteria agreeExampleCriteria = agreeExample.createCriteria();
        agreeExampleCriteria.andUserIdEqualTo(userId);
        agreeExampleCriteria.andAnswerIdEqualTo(answerDTO.getAnswerId());
        List<Agree> agreeList = agreeMapper.selectByExample(agreeExample);
        if(agreeList!=null && agreeList.size()!=0){
            resultList.add(agreeList.get(0).getType());
        }else{
            resultList.add(AgreeEnum.DEFAULT.getCode());
        }


        CollectExample collectExample = new CollectExample();
        CollectExample.Criteria criteria = collectExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andTargetIdEqualTo(answerDTO.getAnswerId());
        criteria.andTypeEqualTo(CollectTypeEnum.ANSWER.getCode());
        int count = collectMapper.countByExample(collectExample);
        if(count > 0){
            resultList.add(OperateEnum.OPERATE.getCode());
        }else{
            resultList.add(OperateEnum.CANCEL.getCode());
        }
        System.out.println("resultList------");
        resultList.stream().forEach(System.out::println);
        answerDTO.setUserOperateState(resultList);
    }

    public PageInfo<AnswerDTO> getQuestionByPerson(Long userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<AnswerDTO> answerDTOList =  questionMapper.getAnswerByUserId(userId);
        PageInfo<AnswerDTO> resultPage = new PageInfo<>(answerDTOList);
        setAnswerContentByAnswerDTOList(answerDTOList);
        return resultPage;
    }

    public void setAnswerContentByAnswerDTOList(List<AnswerDTO> answerDTOList){
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
            User user  = getUserFromCache(answerDTO.getUserId());
            answerDTO.setUserName(user.getUserName());
            answerDTO.setUserPic(user.getUserPic());

        }
    }

    //获取用户收藏的回答
    public PageInfo<AnswerDTO> getQuestionByPersonCollected(Long userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        CollectExample collectExample = new CollectExample();
        CollectExample.Criteria criteria = collectExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andTypeEqualTo(CollectTypeEnum.ANSWER.getCode());
        List<Collect> collectList = collectMapper.selectByExample(collectExample);
        PageInfo<Collect> pageInfo = new PageInfo<>(collectList);

        List<AnswerDTO> answerDTOList = new ArrayList<>();
        for(Collect collect: collectList){
            AnswerDTO answerDTO = questionMapper.getAnswerDTOById(collect.getTargetId());
        }
        PageInfo<AnswerDTO> answerDTOPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(collectList, answerDTOList);
        answerDTOPageInfo.setList(answerDTOList);
        return answerDTOPageInfo;
    }
}
