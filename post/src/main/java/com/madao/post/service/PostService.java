package com.madao.post.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.madao.api.dto.PostDTO;
import com.madao.api.dto.PostSegmentDTO;
import com.madao.api.entity.*;
import com.madao.api.enums.ContentTypeEnum;
import com.madao.api.form.PostGetForm;
import com.madao.api.service.UserService;
import com.madao.post.bean.PostCategoryExample;
import com.madao.post.bean.PostExample;
import com.madao.post.bean.PostSegmentExample;
import com.madao.post.bean.SegmentContentExample;
import com.madao.post.mapper.PostCategoryMapper;
import com.madao.post.mapper.PostMapper;
import com.madao.post.mapper.PostSegmentMapper;
import com.madao.post.mapper.SegmentContentMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PostService {
    @Value("userPrefix")
    private String USER_PREFIX;

    @Autowired
    private PostCategoryMapper postCategoryMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostSegmentMapper postSegmentMapper;

    @Autowired
    private SegmentContentMapper segmentContentMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    public List<PostCategory> getParentCategoryInOrder() {
        PostCategoryExample example = new PostCategoryExample();
        PostCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentNodeEqualTo(-1L);
        example.setOrderByClause("category_order asc");
        return postCategoryMapper.selectByExample(example);
    }

    public List<PostCategory> getCategoryInOrderByParentId(Long parentId){
        PostCategoryExample example = new PostCategoryExample();
        PostCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentNodeEqualTo(parentId);
        example.setOrderByClause("category_order asc");
        return postCategoryMapper.selectByExample(example);
    }

    //根据分类分页获取帖子
    public PageInfo<PostDTO> getPostListByCategoryId(PostGetForm form) {
        PageHelper.startPage(form.getPageNum(), form.getPageSize());
        PostExample example = new PostExample();
        PostExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(form.getCategoryId());
        List<Post> postList = postMapper.selectByExample(example);
        PageInfo<Post> postInfo = new PageInfo<>(postList);

        List<PostDTO> postDTOList = new ArrayList<>(postList.size());
        for(Post post: postList){
            User user = getUserInfoInCache(post.getUserId());
            PostDTO postDTO = new PostDTO();
            BeanUtils.copyProperties(post, postDTO);
            BeanUtils.copyProperties(user, postDTO);
            postDTO.setContentList(getContentByPostId(post.getPostId()));
            postDTOList.add(postDTO);
        }
        PageInfo<PostDTO> postDTOPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(postInfo, postDTOPageInfo);
        postDTOPageInfo.setList(postDTOList);
        return postDTOPageInfo;
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

    //获取每条发帖的第一层的第一个文本信息和前三个图片信息
    public List<SegmentContent> getContentByPostId(Long postId){
        Long firstSegmentId = postSegmentMapper.getFirstSegmentId(postId);
        List<SegmentContent> segmentContentList = new ArrayList<>(4);

        SegmentContent segmentContent = segmentContentMapper.getFirstTextContentBySegmentId(firstSegmentId);
        List<SegmentContent> picSegmentContent = segmentContentMapper.getPicContentBySegmentId(firstSegmentId);

        segmentContentList.add(segmentContent);
        for(SegmentContent content: picSegmentContent) {
            segmentContentList.add(content);
        }
        return segmentContentList;
    }


    //获取帖子详情分页,包括每层和该层的内容
    public PageInfo<PostSegmentDTO> getPostSegmentByPostId(Long postId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PostSegmentExample example = new PostSegmentExample();
        PostSegmentExample.Criteria criteria = example.createCriteria();
        criteria.andPostIdEqualTo(postId);
        example.setOrderByClause("seg_order asc");
        List<PostSegment> postSegmentList = postSegmentMapper.selectByExample(example);
        PageInfo<PostSegment> pageInfo = new PageInfo<>(postSegmentList);

        List<PostSegmentDTO> postSegmentDTOList = new ArrayList<>(postSegmentList.size());
        for(PostSegment postSegment: postSegmentList){
            PostSegmentDTO postSegmentDTO = new PostSegmentDTO();
            BeanUtils.copyProperties(postSegment, postSegmentDTO);

            SegmentContentExample contentExample = new SegmentContentExample();
            SegmentContentExample.Criteria contentCriteria = contentExample.createCriteria();
            contentCriteria.andSegmentIdEqualTo(postSegment.getSegmentId());
            List<SegmentContent> segmentContentList = segmentContentMapper.selectByExample(contentExample);

            postSegmentDTO.setContentList(segmentContentList);
            postSegmentDTOList.add(postSegmentDTO);
        }

        PageInfo<PostSegmentDTO> resultPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, resultPageInfo);
        resultPageInfo.setList(postSegmentDTOList);
        return resultPageInfo;
    }


    //todo 查询并返回评论
    public void getCommentBySegmentId(Long segmentId, Integer pageNum, Integer pageSize) {
    }
}
