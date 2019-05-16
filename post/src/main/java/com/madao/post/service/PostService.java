package com.madao.post.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.madao.api.dto.ParentCategoryDTO;
import com.madao.api.dto.PostCommentDTO;
import com.madao.api.dto.PostDTO;
import com.madao.api.dto.PostSegmentDTO;
import com.madao.api.entity.*;
import com.madao.api.form.BaseForm;
import com.madao.api.form.ContentForm;
import com.madao.api.form.PostForm;
import com.madao.api.service.UserService;
import com.madao.api.utils.KeyUtil;
import com.madao.post.bean.PostCategoryExample;
import com.madao.post.bean.PostExample;
import com.madao.post.bean.PostSegmentExample;
import com.madao.post.bean.SegmentContentExample;
import com.madao.post.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private PostCommentService postCommentService;

    public static final int COMMENT_PAGE_SIZE = 10;

    //获取所有一级分类以及第一个分类的子分类
    public ParentCategoryDTO getParentCategoryInOrder() {
        PostCategoryExample example = new PostCategoryExample();
        PostCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentNodeEqualTo(-1L);
        example.setOrderByClause("category_order asc");
        List<PostCategory> parentCategoryList =  postCategoryMapper.selectByExample(example);

        Long firstParentId = parentCategoryList.get(0).getCategoryId();
        PostCategoryExample example1 = new PostCategoryExample();
        PostCategoryExample.Criteria criteria1 = example1.createCriteria();
        criteria1.andParentNodeEqualTo(firstParentId);
        List<PostCategory> childCategoryList = postCategoryMapper.selectByExample(example1);

        ParentCategoryDTO parentCategoryDTO = new ParentCategoryDTO();
        parentCategoryDTO.setParentCategoryList(parentCategoryList);
        parentCategoryDTO.setChildCategoryInFirstParentList(childCategoryList);
        return parentCategoryDTO;
    }

    //根据父分类的id获取子分类
    public List<PostCategory> getCategoryInOrderByParentId(Long parentId){
        PostCategoryExample example = new PostCategoryExample();
        PostCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentNodeEqualTo(parentId);
        example.setOrderByClause("category_order asc");
        return postCategoryMapper.selectByExample(example);
    }

    //根据分类分页获取帖子
    public PageInfo<PostDTO> getPostListByCategoryId(BaseForm form) {
        PageHelper.startPage(form.getPageNum(), form.getPageSize());
        PostExample example = new PostExample();
        PostExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(form.getId());
        List<Post> postList = postMapper.selectByExample(example);
        PageInfo<Post> postInfo = new PageInfo<>(postList);

        List<PostDTO> postDTOList = new ArrayList<>(postList.size());
        for(Post post: postList){
            User user = getUserInfoInCache(post.getUserId());
            PostDTO postDTO = new PostDTO();
            BeanUtils.copyProperties(post, postDTO);
            BeanUtils.copyProperties(user, postDTO);

            List<SegmentContent> contentList = getAbstractContentByPostId(post.getPostId());
            if (contentList.get(0)==null) {
                postDTO.setTextCount(0);
            }else{
                postDTO.setTextCount(1);
            }
            postDTO.setImgCount(contentList.size()-1);

            postDTO.setContentList(contentList);
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
    public List<SegmentContent> getAbstractContentByPostId(Long postId){
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

            User user = getUserInfoInCache(postSegmentDTO.getUserId());
            BeanUtils.copyProperties(user, postSegmentDTO);

            SegmentContentExample contentExample = new SegmentContentExample();
            SegmentContentExample.Criteria contentCriteria = contentExample.createCriteria();
            contentCriteria.andSegmentIdEqualTo(postSegment.getSegmentId());
            List<SegmentContent> segmentContentList = segmentContentMapper.selectByExampleWithBLOBs(contentExample);
            postSegmentDTO.setContentList(segmentContentList);

            PageInfo<PostCommentDTO> commentDTOPage = postCommentService.getCommentBySegmentId(postSegment.getSegmentId(), 1, COMMENT_PAGE_SIZE);
            System.out.println(commentDTOPage);
            postSegmentDTO.setPostComment(commentDTOPage);


            postSegmentDTOList.add(postSegmentDTO);
        }

        PageInfo<PostSegmentDTO> resultPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, resultPageInfo);
        resultPageInfo.setList(postSegmentDTOList);
        return resultPageInfo;
    }


    //添加帖子
    public void insertPost(PostForm postForm) {
        //添加Post
        Post post = new Post();
        post.setUserId(postForm.getUserId());
        post.setSegmentCount(1);
        post.setPostId(KeyUtil.genUniquKeyOnLong());
        post.setCategoryId(postForm.getCategoryId());
        postMapper.insertSelective(post);

        //添加一个Segment
        PostSegment postSegment = new PostSegment();
        postSegment.setUserId(postForm.getUserId());
        postSegment.setSegOrder(1);
        postSegment.setSegmentId(KeyUtil.genUniquKeyOnLong());
        postSegment.setPostId(post.getPostId());
        postSegment.setCommentCount(0);
        postSegmentMapper.insert(postSegment);

        int i = 1;
        for(ContentForm contentForm: postForm.getAnswerContentFormList()){
            SegmentContent segmentContent = new SegmentContent();
            segmentContent.setSegmentId(postSegment.getSegmentId());
            segmentContent.setContentOrder(i);
            segmentContent.setContent(contentForm.getContent());
            segmentContent.setType(contentForm.getType());
            segmentContent.setContentId(KeyUtil.genUniquKeyOnLong());
            segmentContentMapper.insertSelective(segmentContent);
            i++;
        }
    }

    public Map<Long,List<SegmentContent>> getContentByPostIdList(List<Long> postIdList) {
        Map<Long, List<SegmentContent>> resultMap = new HashMap<>(postIdList.size());
        for(long postId: postIdList){
            resultMap.put(postId, getAbstractContentByPostId(postId));
        }
        return resultMap;
    }
}
