package com.madao.question.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.madao.api.Exception.ResultException;
import com.madao.api.dto.AnswerCommentDTO;
import com.madao.api.entity.*;
import com.madao.api.enums.AgreeEnum;
import com.madao.api.enums.CommentEnum;
import com.madao.api.enums.StateEnum;
import com.madao.api.form.ContentForm;
import com.madao.api.form.AnswerForm;
import com.madao.api.service.UserService;
import com.madao.api.utils.KeyUtil;
import com.madao.question.bean.AgreeExample;
import com.madao.question.bean.AnswerCommentExample;
import com.madao.question.bean.AnswerContentExample;
import com.madao.question.bean.AnswerExample;
import com.madao.question.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AnswerService {
    @Autowired
    private AnswerContentMapper answerContentMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private AgreeMapper agreeMapper;

    @Autowired
    private AnswerCommentMapper answerCommentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Value("${userPrefix}")
    private String USER_PREFIX;

    public List<AnswerContent> getAnswerContentByAnswerId(Long answerId){
        AnswerContentExample example = new AnswerContentExample();
        AnswerContentExample.Criteria criteria = example.createCriteria();
        criteria.andAnswerIdEqualTo(answerId);
        List<AnswerContent> answerContentList = answerContentMapper.selectByExample(example);
        return answerContentList;
    }

    public int addAgreeOnAnswer(Long userId, Long answerId) {
        System.out.println(userId+"---" + answerId);
        AgreeExample agreeExample = new AgreeExample();
        AgreeExample.Criteria criteria = agreeExample.createCriteria();
        criteria.andAnswerIdEqualTo(answerId);
        criteria.andUserIdEqualTo(userId);
        int count = agreeMapper.countByExample(agreeExample);
        int result = 0;
        //如果已经点赞或者不赞同
        if(count>0){
            System.out.println("已经有记录了");

            return result;
        }else{
            //如果还没有点赞
            //更新点赞数
            Answer answer = answerMapper.selectByPrimaryKey(answerId);
            if(answer==null)
                throw new ResultException("该回答不存在");
            answer.setAgreeCount(answer.getAgreeCount()+1);
            result = answer.getAgreeCount();
            answerMapper.updateByPrimaryKeySelective(answer);

            //添加用户点赞的记录
            Agree agree = new Agree(userId, answerId, AgreeEnum.AGREE.getCode());
            System.out.println("agree:---" + agree);
            agreeMapper.insertSelective(agree);
        }
        return result;
    }

    public int cancelAgreeOnAnswer(Long  userId, Long answerId){
        AgreeExample example = new AgreeExample();
        AgreeExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andAnswerIdEqualTo(answerId);

        int result = 0;

        //查看是否有点赞记录
        List<Agree> agreeList = agreeMapper.selectByExample(example);
        if(agreeList==null || agreeList.size()==0){
            return result;
        }else {
            agreeMapper.deleteByExample(example);
        }


        //将该回答的点赞数或不赞同数减一
        Answer answer = answerMapper.selectByPrimaryKey(answerId);
        Byte type = agreeList.get(0).getType();
        if(type.equals(AgreeEnum.AGREE.getCode())){
            result = answer.getAgreeCount();
            result--;
            result = result<0 ? 0 : result;
            answer.setAgreeCount(result);

        }else if(type.equals(AgreeEnum.DISAGREE.getCode())){
            result = answer.getDisagreeCount();
            result--;
            result = result< 0 ? 0 : result;
            answer.setDisagreeCount(result);
        }
        answerMapper.updateByPrimaryKeySelective(answer);
        return result;
    }

    public int addDisagreeOnAnswer(Long userId, Long answerId) {
        System.out.println(userId+"---" + answerId);

        AgreeExample example = new AgreeExample();
        AgreeExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andAnswerIdEqualTo(answerId);

        int count = agreeMapper.countByExample(example);
        int result = 0;
        //如果已经点赞或者不赞同
        if(count>0){
            System.out.println("已经有记录了");
            return result;
        }else{
            //如果还没有点赞
            //更新不赞同的数量
            Answer answer = answerMapper.selectByPrimaryKey(answerId);
            if(answer==null)
                throw new ResultException("该回答不存在");
            answer.setDisagreeCount(answer.getDisagreeCount()+1);
            result = answer.getDisagreeCount();
            answerMapper.updateByPrimaryKeySelective(answer);

            Agree agree = new Agree(userId, answerId, AgreeEnum.DISAGREE.getCode());
            System.out.println("Disagree:---" + agree);
            agreeMapper.insert(agree);
        }
        return result;
    }

    public List<Byte> getAgreeTypeByUserId(Long userId, List<Long> answerIdList) {
        List<Byte> result = new ArrayList<>();
        for(Long answerId: answerIdList){
            AgreeExample example = new AgreeExample();
            AgreeExample.Criteria criteria = example.createCriteria();
            criteria.andUserIdEqualTo(userId);
            criteria.andAnswerIdEqualTo(answerId);
            List<Agree> agreeList = agreeMapper.selectByExample(example);
            if(agreeList==null || agreeList.size()==0)
                result.add((byte)-1);
            else{
                result.add(agreeList.get(0).getType());
            }
        }
        return result;
    }

    public PageInfo<AnswerCommentDTO> getCommentByAnswerid(Long answerId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        AnswerCommentExample example = new AnswerCommentExample();
        AnswerCommentExample.Criteria criteria = example.createCriteria();
        criteria.andAnswerIdEqualTo(answerId);
        criteria.andIsVisibleEqualTo(CommentEnum.VISIBLE.getCode());
        List<AnswerComment> answerCommentList = answerCommentMapper.selectByExample(example);
        PageInfo<AnswerComment> pageInfo = new PageInfo<>(answerCommentList);

        List<AnswerCommentDTO> answerCommentDTOList = new ArrayList<>(answerCommentList.size());
        for(AnswerComment answerComment: answerCommentList){
            User user = (User) redisTemplate.opsForValue().get(USER_PREFIX + answerComment.getUserId());
            System.out.println("redis get user...." + user);
            if(user==null){
                user = userService.getUserById(answerComment.getUserId());
                System.out.println("database get user" + user);
                redisTemplate.opsForValue().set(USER_PREFIX + answerComment.getUserId(), user, 3600, TimeUnit.SECONDS);
            }
            AnswerCommentDTO answerCommentDTO = new AnswerCommentDTO();
            BeanUtils.copyProperties(answerComment, answerCommentDTO);
            answerCommentDTO.setUserName(user.getUserName());
            answerCommentDTO.setUserPic(user.getUserPic());
            answerCommentDTOList.add(answerCommentDTO);
        }
        PageInfo<AnswerCommentDTO> pageInfoDTO = new PageInfo<>();
        System.out.println(pageInfo);
        System.out.println(pageInfoDTO);
        BeanUtils.copyProperties(pageInfo, pageInfoDTO);
        pageInfoDTO.setList(answerCommentDTOList);
        return pageInfoDTO;
    }


    public PageInfo<AnswerCommentDTO> getCommentByAnswerid2(Long answerId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        AnswerCommentExample example = new AnswerCommentExample();
        AnswerCommentExample.Criteria criteria = example.createCriteria();
        criteria.andAnswerIdEqualTo(answerId);
        criteria.andIsVisibleEqualTo(CommentEnum.VISIBLE.getCode());
        List<AnswerComment> answerCommentList = answerCommentMapper.selectByExample(example);
        List<AnswerCommentDTO> answerCommentDTOList = new ArrayList<>(answerCommentList.size());
        for(AnswerComment answerComment: answerCommentList){
            AnswerCommentDTO answerCommentDTO = new AnswerCommentDTO();
            BeanUtils.copyProperties(answerComment, answerCommentDTO);
            answerCommentDTOList.add(answerCommentDTO);
        }
        PageInfo<AnswerCommentDTO> pageInfo = new PageInfo<>(answerCommentDTOList);
        return pageInfo;
    }

    public AnswerComment addAnswerComment(Long answerId, Long userId, String commentContent) {
        AnswerComment comment = new AnswerComment();
        comment.setCommentId(KeyUtil.genUniquKeyOnLong());
        comment.setCommentContent(commentContent);
        comment.setUserId(userId);
        comment.setIsVisible(CommentEnum.VISIBLE.getCode());
        comment.setAnswerId(answerId);
        comment.setCreateTime(new Date());
        answerCommentMapper.insertSelective(comment);

        //在回答中更新评论条数
        Answer answer = answerMapper.selectByPrimaryKey(answerId);
        answer.setCommentCount(answer.getCommentCount()+1);
        answerMapper.updateByPrimaryKeySelective(answer);
        return comment;
    }

    public void addAnswer(AnswerForm form) {
        Answer answer = new Answer();
        answer.setAnswerId(KeyUtil.genUniquKeyOnLong());
        answer.setQuestionId(form.getQuestionId());
        answer.setUserId(form.getUserId());
        answerMapper.insertSelective(answer);

        int i = 1;
        for(ContentForm answerContentForm: form.getAnswerContentFormList()){
            AnswerContent answerContent = new AnswerContent();
            BeanUtils.copyProperties(answerContentForm, answerContent);
            answerContent.setAnswerId(answer.getAnswerId());
            answerContent.setContentId(KeyUtil.genUniquKeyOnLong());
            answerContent.setContentOrder(i);
            answerContent.setType(answerContentForm.getType().intValue());
            i++;

            answerContentMapper.insertSelective(answerContent);
        }

        //更新问题的回答数量 删除默认回答
        Question question = questionMapper.selectByPrimaryKey(form.getQuestionId());
        question.setAnswerCount(question.getAnswerCount()+1);
        questionMapper.updateByPrimaryKeySelective(question);

        AnswerExample example = new AnswerExample();
        AnswerExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(-1L);
        answerMapper.deleteByExample(example);
    }

    public void operateAnswer(Long userId, Long answerId, Byte operate) {
        Answer answer = answerMapper.selectByPrimaryKey(answerId);
        if(answer==null){
            throw new ResultException("该回答不存在");
        }
        if(!answer.getUserId().equals(userId)){
            throw new ResultException("该回答不属于用户");
        }
        if(!operate.equals(StateEnum.VISIBLE.getCode()) && !operate.equals(StateEnum.INVISIBLE.getCode())){
            throw new ResultException("操作不正确");
        }

        if(answer.getState().equals(operate)){
            return;
        }
        //更新状态
        answer.setState(operate);
        answerMapper.updateByPrimaryKeySelective(answer);

    }
}
