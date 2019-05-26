package com.madao.post.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.madao.api.Exception.ResultException;
import com.madao.api.dto.*;
import com.madao.api.entity.*;
import com.madao.api.enums.CollectTypeEnum;
import com.madao.api.enums.OperateEnum;
import com.madao.api.enums.StateEnum;
import com.madao.api.form.BaseForm;
import com.madao.api.form.ContentForm;
import com.madao.api.form.PostForm;
import com.madao.api.form.PostSegmentForm;
import com.madao.api.service.UserService;
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.ResultView;
import com.madao.post.bean.*;
import com.madao.post.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.swing.text.Segment;
import java.util.*;
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
    private CollectMapper collectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PostCommentService postCommentService;

    public static final int COMMENT_PAGE_SIZE = 2;

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

    //根据分类分页获取可见的帖子
    public PageInfo<PostDTO> getPostListByCategoryId(BaseForm form) {
        PageHelper.startPage(form.getPageNum(), form.getPageSize());
        PostExample example = new PostExample();
        PostExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(form.getId());
        criteria.andStateEqualTo(StateEnum.VISIBLE.getCode());
        List<Post> postList = postMapper.selectByExample(example);
        PageInfo<Post> postInfo = new PageInfo<>(postList);

        List<PostDTO> postDTOList = new ArrayList<>(postList.size());
        populatePostDTO(postList, postDTOList);
        PageInfo<PostDTO> postDTOPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(postInfo, postDTOPageInfo);
        postDTOPageInfo.setList(postDTOList);
        return postDTOPageInfo;
    }

    //不分类获取可见的帖子信息
    public PageInfo<PostDTO> getPostList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PostExample example = new PostExample();
        PostExample.Criteria criteria = example.createCriteria();
        criteria.andStateEqualTo(StateEnum.VISIBLE.getCode());
        List<Post> postList = postMapper.selectByExample(example);
        PageInfo<Post> postInfo = new PageInfo<>(postList);

        List<PostDTO> postDTOList = new ArrayList<>(postList.size());
        populatePostDTO(postList, postDTOList);
        PageInfo<PostDTO> postDTOPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(postInfo, postDTOPageInfo);
        postDTOPageInfo.setList(postDTOList);
        return postDTOPageInfo;
    }


    //尝试从缓存中获取用户信息，如果没有，从数据库获取，并缓存
    public User getUserInfoInCache(Long userId){
        User user = null;
        try {
           user  = (User) redisTemplate.opsForValue().get(USER_PREFIX + userId);
            System.out.println("redis get user...." + user);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(user==null){
            user = userService.getUserById(userId);
            System.out.println("database get user" + user);
            try {
                redisTemplate.opsForValue().set(USER_PREFIX + userId, user, 3600, TimeUnit.SECONDS);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return user;
    }

    //获取每条发帖的第一层的第一个文本信息和前三个图片信息
    public List<SegmentContent> getAbstractContentByPostId(Long postId){
        Long firstSegmentId = postSegmentMapper.getFirstSegmentId(postId);
        List<SegmentContent> segmentContentList = new ArrayList<>(4);

        SegmentContent segmentContent = segmentContentMapper.getFirstTextContentBySegmentId(firstSegmentId);
        segmentContentList.add(segmentContent);

        List<SegmentContent> picSegmentContent = segmentContentMapper.getPicContentBySegmentId(firstSegmentId);
        for(SegmentContent content: picSegmentContent) {
            segmentContentList.add(content);
        }
        return segmentContentList;
    }

    //获取每条发帖的第一层的第一个文本信息和前三个图片信息 仅返回字符串形式的内容
    public List<String> getAbstractContentStringByPostId(Long postId){
        Long firstSegmentId = postSegmentMapper.getFirstSegmentId(postId);
        List<String> resultList = new ArrayList<>(4);

        SegmentContent segmentContent = segmentContentMapper.getFirstTextContentBySegmentId(firstSegmentId);
        if(segmentContent!=null){
            resultList.add(segmentContent.getContent());
        }else{
            resultList.add("");
        }

        List<SegmentContent> picSegmentContent = segmentContentMapper.getPicContentBySegmentId(firstSegmentId);
        for(SegmentContent content: picSegmentContent) {
            if(content!=null) {
                resultList.add(content.getContent());
            }else{
                resultList.add("");
            }
        }

        int count = 0;
        if(picSegmentContent==null){
            count = 3;
        }else if(picSegmentContent.size()<3){
            count = 3 - picSegmentContent.size();
        }

        for(int i=0; i<count; i++){
            resultList.add("");
        }

        System.out.println("resultSize-----" + resultList.size());
        for(String str: resultList){
            System.out.println(str + "------------------------------------------");
        }
        return resultList;
    }


    //获取帖子详情分页,包括每层和该层的内容
    public PageInfo<PostSegmentDTO> getPostSegmentByPostId(Long postId, Integer pageNum, Integer pageSize, Integer commentPageSize) {
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

            PageInfo<PostCommentDTO> commentDTOPage = postCommentService.getCommentBySegmentId(postSegment.getSegmentId(), 1, commentPageSize);
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
    public Post insertPost(PostForm postForm) {
        //添加Post
        Post post = new Post();
        post.setUserId(postForm.getUserId());
        post.setSegmentCount(1);
        post.setPostId(KeyUtil.genUniquKeyOnLong());
        post.setCategoryId(postForm.getCategoryId());
        postMapper.insertSelective(post);

        PostSegment postSegment = new PostSegment();
        postSegment.setCommentCount(0);
        postSegment.setPostId(post.getPostId());
        postSegment.setSegmentId(KeyUtil.genUniquKeyOnLong());
        postSegment.setSegOrder(1);
        postSegment.setUserId(postForm.getUserId());
        postSegmentMapper.insertSelective(postSegment);

        int order = 1;
        for(ContentForm contentForm: postForm.getAnswerContentFormList()) {
            SegmentContent content = new SegmentContent();
            content.setContentId(KeyUtil.genUniquKeyOnLong());
            content.setContent(contentForm.getContent());
            content.setType(contentForm.getType());
            content.setSegmentId(postSegment.getSegmentId());
            content.setContentOrder(order);
            segmentContentMapper.insertSelective(content);
            order++;
        }


        return post;
    }

    public PostSegment insertPostSegment(PostSegmentForm postForm) {
        PostSegmentExample example = new PostSegmentExample();
        PostSegmentExample.Criteria criteria = example.createCriteria();
        criteria.andPostIdEqualTo(postForm.getPostId());
        int count = postSegmentMapper.countByExample(example);
        int segmentOrder = count+1;
        //添加一个Segment
        PostSegment postSegment = new PostSegment();
        postSegment.setUserId(postForm.getUserId());
        postSegment.setSegOrder(segmentOrder);
        postSegment.setSegmentId(KeyUtil.genUniquKeyOnLong());
        postSegment.setPostId(postForm.getPostId());
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
        return postSegment;
    }

    public List<List<String>> getContentByPostIdList(List<Long> postIdList) {
        List<List<String>> resultList = new ArrayList<>();
        for(long postId: postIdList){
            resultList.add(getAbstractContentStringByPostId(postId));
        }
        return resultList;
    }


    public PageInfo<CollectDTO<PostDTO>> getCollectPost(Long userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        CollectExample collectExample = new CollectExample();
        CollectExample.Criteria criteria  = collectExample.createCriteria();
        criteria.andTypeEqualTo(CollectTypeEnum.POST.getCode());
        criteria.andUserIdEqualTo(userId);
        List<Collect> collectionList = collectMapper.selectByExample(collectExample);
        PageInfo<Collect> pageInfo = new PageInfo<>(collectionList);

        List<CollectDTO<PostDTO>> collectDTOList = new ArrayList<>(collectionList.size());
        for(Collect collect: collectionList){
            CollectDTO<PostDTO> collectDTO = new CollectDTO<>();
            BeanUtils.copyProperties(collect, collectDTO);
            PostDTO postDTO = getPostDTO(collect.getTargetId());
            System.out.println(postDTO);
            collectDTO.setData(postDTO);
            collectDTOList.add(collectDTO);
        }

        PageInfo<CollectDTO<PostDTO>> collectPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, collectPageInfo);
        collectPageInfo.setList(collectDTOList);
        return collectPageInfo;
    }

    private PostDTO getPostDTO(Long postId) {
        Post post = postMapper.selectByPrimaryKey(postId);
        if(post==null)
            return null;
        PostDTO postDTO = new PostDTO();
        BeanUtils.copyProperties(post, postDTO);

        User user = getUserInfoInCache(post.getUserId());
        BeanUtils.copyProperties(user, postDTO);

        List<String> conentList = getAbstractContentStringByPostId(post.getPostId());
        postDTO.setContentList(conentList);
        return postDTO;
    }

    public boolean getUserCollectPostState(Long userId, Long postId) {
        CollectExample example = new CollectExample();
        CollectExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andTargetIdEqualTo(postId);
        criteria.andTypeEqualTo(CollectTypeEnum.POST.getCode());
        int count = collectMapper.countByExample(example);
        return count==0 ? false : true;
    }

    //添加或者取消帖子收藏
    public void updatePostCollectState(Long userId, Long postId, Byte operate) {
        CollectExample example = new CollectExample();
        CollectExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andTargetIdEqualTo(postId);
        criteria.andTypeEqualTo(CollectTypeEnum.POST.getCode());
        int count = collectMapper.countByExample(example);
        if(operate.equals(OperateEnum.CANCEL.getCode())){
            if(count==0)
                return;
            collectMapper.deleteByExample(example);
        }else if(operate.equals(OperateEnum.OPERATE.getCode())){
            if(count!=0)
                return;

            Collect collect = new Collect();
            collect.setType(CollectTypeEnum.POST.getCode());
            collect.setTargetId(postId);
            collect.setUserId(userId);
            collect.setCollectId(KeyUtil.genUniquKeyOnLong());
            collectMapper.insertSelective(collect);
        }
    }

    public PostCategory getCategoryByName(String categoryName) {
        PostCategoryExample example = new PostCategoryExample();
        PostCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryNameEqualTo(categoryName);
        List<PostCategory> postCategoryList = postCategoryMapper.selectByExample(example);
        if(postCategoryList==null || postCategoryList.size()==0)
            throw new ResultException("该类别不存在");
        return postCategoryList.get(0);
    }


    //根据分类分页获取所有帖子
    public PageInfo<PostDTO> getPostListByCategoryIdInAllState(BaseForm form) {
        PageHelper.startPage(form.getPageNum(), form.getPageSize());
        PostExample example = new PostExample();
        PostExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(form.getId());
        criteria.andStateEqualTo(StateEnum.VISIBLE.getCode());
        List<Post> postList = postMapper.selectByExample(example);
        PageInfo<Post> postInfo = new PageInfo<>(postList);

        List<PostDTO> postDTOList = new ArrayList<>(postList.size());
        populatePostDTO(postList, postDTOList);
        PageInfo<PostDTO> postDTOPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(postInfo, postDTOPageInfo);
        postDTOPageInfo.setList(postDTOList);
        return postDTOPageInfo;
    }

    //不分类获取全部状态的帖子信息
    public PageInfo<PostDTO> getPostListInAllState(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PostExample example = new PostExample();
        List<Post> postList = postMapper.selectByExample(example);
        PageInfo<Post> postInfo = new PageInfo<>(postList);

        List<PostDTO> postDTOList = new ArrayList<>(postList.size());
        populatePostDTO(postList, postDTOList);
        PageInfo<PostDTO> postDTOPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(postInfo, postDTOPageInfo);
        postDTOPageInfo.setList(postDTOList);
        return postDTOPageInfo;
    }


    public void disablePost(Long postId) {
        Post post = postMapper.selectByPrimaryKey(postId);
        if(post.getState()==StateEnum.INVISIBLE.getCode())
            return ;
        post.setState(StateEnum.INVISIBLE.getCode());
        postMapper.updateByPrimaryKeySelective(post);
    }

    public PageInfo<PostDTO> getPostListByUserId(Long userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PostExample example = new PostExample();
        PostExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<Post> postList = postMapper.selectByExample(example);
        PageInfo<Post> postInfo = new PageInfo<>(postList);

        List<PostDTO> postDTOList = new ArrayList<>(postList.size());
        populatePostDTO(postList, postDTOList);
        PageInfo<PostDTO> postDTOPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(postInfo, postDTOPageInfo);
        postDTOPageInfo.setList(postDTOList);
        return postDTOPageInfo;
    }

    private void populatePostDTO(List<Post> postList, List<PostDTO> postDTOList){
        for(Post post: postList){
            PostDTO postDTO = new PostDTO();
            User user = getUserInfoInCache(post.getUserId());
            BeanUtils.copyProperties(user, postDTO);

            //这里的user和post都有state属性，如果放在user前会被user的state属性父该覆盖
            BeanUtils.copyProperties(post, postDTO);

            List<String> conentList = getAbstractContentStringByPostId(post.getPostId());
            postDTO.setContentList(conentList);
            postDTOList.add(postDTO);
        }
    }

    //用户操作帖子状态
    public void operatePost(Long userId, Long postId, Byte operate) {
        Post post = postMapper.selectByPrimaryKey(postId);
        if(!userId.equals(post.getUserId())){
            throw new ResultException("该帖子不属于当前用户");
        }
        //如果已经是操作过了的状态，直接返回
        if(operate.equals(post.getState()))
            return;
        //如果在合法操作内的话
        if(operate.equals(StateEnum.VISIBLE.getCode()) || operate.equals(StateEnum.INVISIBLE.getCode())){
            post.setState(operate);
            postMapper.updateByPrimaryKeySelective(post);
        }
    }

    //获取用户收藏的帖子，带分页
    public PageInfo<PostDTO> getPostListByUserCollected(Long userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        CollectExample collectExample = new CollectExample();
        CollectExample.Criteria criteria = collectExample.createCriteria();
        criteria.andTypeEqualTo(CollectTypeEnum.POST.getCode());
        criteria.andUserIdEqualTo(userId);
        List<Collect> collectList = collectMapper.selectByExample(collectExample);
        PageInfo<Collect> collectPageInfo = new PageInfo<>(collectList);

        List<Post> postList = new ArrayList<>();
        for(Collect collect: collectList){
            Post post = postMapper.selectByPrimaryKey(collect.getTargetId());
            postList.add(post);
        }
        List<PostDTO> postDTOList = new ArrayList<>();
        populatePostDTO(postList, postDTOList);

        PageInfo<PostDTO> postDTOPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(collectPageInfo, postDTOPageInfo);
        postDTOPageInfo.setList(postDTOList);

        return postDTOPageInfo;
    }
}
