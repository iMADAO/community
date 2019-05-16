package com.madao.post.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.madao.api.dto.PostCommentDTO;
import com.madao.api.entity.PostComment;
import com.madao.api.entity.User;
import com.madao.api.service.UserService;
import com.madao.api.utils.KeyUtil;
import com.madao.post.bean.PostCommentExample;
import com.madao.post.mapper.PostCommentMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PostCommentService {
    @Autowired
    private PostCommentMapper postCommentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("userPrefix")
    private String USER_PREFIX;

    @Autowired
    private UserService userService;

    public PageInfo<PostCommentDTO> getCommentBySegmentId(Long segmentId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PostCommentExample example = new PostCommentExample();
        PostCommentExample.Criteria criteria = example.createCriteria();
        criteria.andSegmentIdEqualTo(segmentId);
        List<PostComment> postCommentList = postCommentMapper.selectByExampleWithBLOBs(example);
        PageInfo<PostComment> pageInfo = new PageInfo<>(postCommentList);

        List<PostCommentDTO> postCommentDTOList = new ArrayList<>(postCommentList.size());
        for(PostComment postComment: postCommentList){
            PostCommentDTO postCommentDTO = new PostCommentDTO();
            BeanUtils.copyProperties(postComment, postCommentDTO);

            User user = getUserInfoInCache(postComment.getUserId());
            BeanUtils.copyProperties(user, postCommentDTO);

            postCommentDTOList.add(postCommentDTO);
        }

        PageInfo<PostCommentDTO> resultPage = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, resultPage);
        resultPage.setList(postCommentDTOList);
        return resultPage;
    }

    public void addComment(Long segmentId, Long userId, String commentContent){
        PostComment comment = new PostComment();
        comment.setCommentId(KeyUtil.genUniquKeyOnLong());
        comment.setSegmentId(segmentId);
        comment.setUserId(userId);
        comment.setCommentContent(commentContent);
        postCommentMapper.insertSelective(comment);
    }


    //尝试从缓存中获取用户信息，如果没有，从数据库获取，并缓存
    public User getUserInfoInCache(Long userId){
        User user = (User) redisTemplate.opsForValue().get(USER_PREFIX + userId);
        System.out.println("redis get user...." + user);
        if(user==null){
            user = userService.getUserById(userId);
            System.out.println("database get user" + user);
            redisTemplate.opsForValue().set(USER_PREFIX + userId, user, 3600, TimeUnit.SECONDS);
        }
        return user;
    }

}
