package com.madao.article.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.madao.api.Exception.ResultException;
import com.madao.api.dto.AnswerDTO;
import com.madao.api.dto.ArticleDTO;
import com.madao.api.dto.ArticlePageDTO;
import com.madao.api.entity.*;
import com.madao.api.enums.*;
import com.madao.api.form.ArticleAddForm;
import com.madao.api.service.UserService;
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import com.madao.article.bean.ArticleExample;
import com.madao.article.bean.CollectExample;
import com.madao.article.bean.ReportExample;
import com.madao.article.mapper.ArticleMapper;
import com.madao.article.mapper.CollectMapper;
import com.madao.article.mapper.ReportMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ArticleService {

    @Value("userPrefix")
    private String USER_PREFIX;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ArticleCategoryService articleCategoryService;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private ReportMapper reportMapper;

    public Article getArticleById(Long articleId) {
       return articleMapper.selectByPrimaryKey(articleId);
    }

    public void increaseDownloadCount(Long articleId) {
        Article article = articleMapper.selectByPrimaryKey(articleId);
        if(article==null)
            return;
        article.setDownloadCount(article.getDownloadCount()+1);
        articleMapper.updateByPrimaryKeySelective(article);
    }

    public Article addArticle(ArticleAddForm form) {
        Article article = new Article();
        BeanUtils.copyProperties(form, article);
        article.setDownloadCount(0);
        article.setArticleId(KeyUtil.genUniquKeyOnLong());
        articleMapper.insertSelective(article);
        return article;
    }

    public ArticlePageDTO getArticleDTOVisible(Integer pageNum, Integer pageSize) {
        ArticlePageDTO articlePageDTO = new ArticlePageDTO();
        List<ArticleCategory> articleCategoryList = articleCategoryService.getAllCategory();
        articlePageDTO.setCategoryList(articleCategoryList);

        PageHelper.startPage(pageNum, pageSize);
        ArticleExample example = new ArticleExample();
        ArticleExample.Criteria criteria = example.createCriteria();
        criteria.andStateEqualTo(StateEnum.VISIBLE.getCode());
        List<Article> articleList = articleMapper.selectByExample(example);
        PageInfo<Article> articlePageInfo = new PageInfo<>(articleList);

        List<ArticleDTO> articleDTOList = new ArrayList<>();
        populateArticleDTO(articleList, articleDTOList);

        PageInfo<ArticleDTO> resultPage = new PageInfo<>();
        BeanUtils.copyProperties(articlePageInfo, resultPage);
        resultPage.setList(articleDTOList);

        articlePageDTO.setArticlePageInfo(resultPage);
        return articlePageDTO;
    }

    public PageInfo<ArticleDTO> getArticleDTOInAllState(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        ArticleExample example = new ArticleExample();
        List<Article> articleList = articleMapper.selectByExample(example);
        PageInfo<Article> articlePageInfo = new PageInfo<>(articleList);

        List<ArticleDTO> articleDTOList = new ArrayList<>();
        populateArticleDTO(articleList, articleDTOList);

        PageInfo<ArticleDTO> resultPage = new PageInfo<>();
        BeanUtils.copyProperties(articlePageInfo, resultPage);
        resultPage.setList(articleDTOList);
        return resultPage;
    }



    public void populateArticleDTO(List<Article> articleList, List<ArticleDTO> articleDTOList){
        for(Article article: articleList){
            ArticleDTO articleDTO = new ArticleDTO();
            Long userId = article.getUserId();
            if(userId!=null) {
                User user = getUserInfoInCache(article.getUserId());
                if(user!=null) {
                    BeanUtils.copyProperties(user, articleDTO);
                }
            }

            BeanUtils.copyProperties(article, articleDTO);
            articleDTOList.add(articleDTO);
        }
    }

    public ArticlePageDTO getArticleDTOByCategoryIdVisible(Integer pageNum, Integer pageSize, Long categoryId) {
        ArticlePageDTO articlePageDTO = new ArticlePageDTO();
        List<ArticleCategory> articleCategoryList = articleCategoryService.getAllCategory();
        articlePageDTO.setCategoryList(articleCategoryList);

        PageHelper.startPage(pageNum, pageSize);
        ArticleExample example = new ArticleExample();
        ArticleExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        criteria.andStateEqualTo(StateEnum.VISIBLE.getCode());
        List<Article> articleList = articleMapper.selectByExample(example);
        PageInfo<Article> articlePageInfo = new PageInfo<>(articleList);

        List<ArticleDTO> articleDTOList = new ArrayList<>();
        for(Article article: articleList){
            ArticleDTO articleDTO = new ArticleDTO();
            BeanUtils.copyProperties(article, articleDTO);
            Long userId = article.getUserId();
            if(userId!=null) {
                User user = getUserInfoInCache(article.getUserId());
                if(user!=null) {
                    BeanUtils.copyProperties(user, articleDTO);
                }
            }
            articleDTOList.add(articleDTO);
        }

        PageInfo<ArticleDTO> resultPage = new PageInfo<>();
        BeanUtils.copyProperties(articlePageInfo, resultPage);
        resultPage.setList(articleDTOList);

        articlePageDTO.setArticlePageInfo(resultPage);
        return articlePageDTO;
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

    //获取某个用户的文章
    public PageInfo<ArticleDTO> getArticleDTOByPerson(Long userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        ArticleExample example = new ArticleExample();
        ArticleExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<Article> articleList = articleMapper.selectByExample(example);
        PageInfo<Article> pageInfo = new PageInfo<>(articleList);

        List<ArticleDTO> articleDTOList = new ArrayList<>(articleList.size());
        populateArticleDTO(articleList, articleDTOList);

        PageInfo<ArticleDTO> resultPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, resultPageInfo);
        resultPageInfo.setList(articleDTOList);
        return resultPageInfo;
    }

    public void operateArticle(Long userId, Long articleId, Byte operate) {
        Article article = articleMapper.selectByPrimaryKey(articleId);
        if(article==null){
            throw new ResultException("该回答不存在");
        }
        if(!article.getUserId().equals(userId)){
            throw new ResultException("该回答不属于用户");
        }
        if(!operate.equals(StateEnum.VISIBLE.getCode()) && !operate.equals(StateEnum.INVISIBLE.getCode())){
            throw new ResultException("操作不正确");
        }

        if(article.getState().equals(operate)){
            return;
        }
        //更新状态
        article.setState(operate);
        articleMapper.updateByPrimaryKeySelective(article);
    }

    public PageInfo<ArticleDTO> getArticleDTOByUserCollected(Long userId, Integer pageNum, Integer pageSize) {
        CollectExample collectExample = new CollectExample();
        CollectExample.Criteria criteria = collectExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andTypeEqualTo(CollectTypeEnum.ARTICLE.getCode());
        List<Collect> collectList = collectMapper.selectByExample(collectExample);
        PageInfo<Collect> collectPageInfo = new PageInfo<>(collectList);

        List<Article> articleList = new ArrayList<>();
        List<ArticleDTO> articleDTOList = new ArrayList<>();
        for(Collect collect: collectList){
            Article article = articleMapper.selectByPrimaryKey(collect.getTargetId());
            articleList.add(article);
        }
        populateArticleDTO(articleList, articleDTOList);

        PageInfo<ArticleDTO> articleDTOPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(collectPageInfo, articleDTOPageInfo);
        articleDTOPageInfo.setList(articleDTOList);
        return articleDTOPageInfo;
    }

    public void populateCollectStateByUser(Long userId, List<ArticleDTO> list){
        List<Long> targetIdList = collectMapper.getTargetIdByUser(userId, CollectTypeEnum.ARTICLE.getCode());
        for(ArticleDTO articleDTO: list){
            Long articleId = articleDTO.getArticleId();
            if(targetIdList.contains(articleId)){
                articleDTO.setCollectState(CollectStateEnum.COLLECTED.getCode());
            }else{
                articleDTO.setCollectState(CollectStateEnum.UN_COLLECTED.getCode());
            }
        }
    }

    public void collectArticle(Long userId, Long articleId, Byte operate) {
        CollectExample collectExample = new CollectExample();
        CollectExample.Criteria criteria = collectExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andTargetIdEqualTo(articleId);
        criteria.andTypeEqualTo(CollectTypeEnum.ARTICLE.getCode());
        int count = collectMapper.countByExample(collectExample);

        if(operate.equals(OperateEnum.OPERATE.getCode())){
            if(count==0){
                Collect collect = new Collect();
                collect.setCollectId(KeyUtil.genUniquKeyOnLong());
                collect.setUserId(userId);
                collect.setTargetId(articleId);
                collect.setType(CollectTypeEnum.ARTICLE.getCode());
                collectMapper.insertSelective(collect);
            }
        }else if(operate.equals(OperateEnum.CANCEL.getCode())){
            if(count!=0){
                collectMapper.deleteByExample(collectExample);
            }
        }
    }

    public void banArticle(Long articleId, Byte operate) {
        Article article = articleMapper.selectByPrimaryKey(articleId);
        if(article==null)
            return;

        if(!operate.equals(StateEnum.VISIBLE.getCode()) && !operate.equals(StateEnum.INVISIBLE.getCode())&& !operate.equals(StateEnum.BAN.getCode())){
            throw new ResultException("操作不正确");
        }

        if(article.getState().equals(operate)){
            return;
        }
        article.setState(operate);
        articleMapper.updateByPrimaryKeySelective(article);
    }

    public void reportArticle(Long userId, Long articleId, String reason) {
        ReportExample reportExample = new ReportExample();
        ReportExample.Criteria criteria = reportExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andTargetIdEqualTo(articleId);
        criteria.andTypeEqualTo(TypeEnum.ARTICLE.getCode());
        int count = reportMapper.countByExample(reportExample);
        if(count > 0)
            throw new ResultException("已经举报过了");
        Report report = new Report();
        report.setReportId(KeyUtil.genUniquKeyOnLong());
        report.setType(TypeEnum.ARTICLE.getCode());
        report.setUserId(userId);
        report.setTargetId(articleId);
        report.setState(ReportStateEnum.REPORTED.getCode());
        report.setReason(reason);
        reportMapper.insertSelective(report);
    }

    public ArticleDTO getArticleDTOById(Long articleId) {
        Article article = articleMapper.selectByPrimaryKey(articleId);
        ArticleDTO articleDTO = new ArticleDTO();
        if(article!=null) {
            BeanUtils.copyProperties(article, articleDTO);
            System.out.println("userId....." + article.getUserId());
            if(article.getUserId()!=null) {
                User user = getUserInfoInCache(article.getUserId());
                if (user != null) {
                    articleDTO.setUserName(user.getUserName());
                    articleDTO.setUserPic(user.getUserPic());
                }
            }
        }
        return articleDTO;
    }

    public ArticlePageDTO searchArticleBytTitle(String searchContent, Integer pageNum, Integer pageSize) {
        ArticlePageDTO articlePageDTO = new ArticlePageDTO();
        PageHelper.startPage(pageNum, pageSize);
        ArticleExample articleExample = new ArticleExample();
        ArticleExample.Criteria criteria = articleExample.createCriteria();
        criteria.andTitleLike("%" + searchContent + "%");
        List<Article> articleList = articleMapper.selectByExample(articleExample);
        PageInfo<Article> articlePageInfo = new PageInfo<>();

        List<ArticleDTO> articleDTOList = new ArrayList<>(articleList.size());
        for(Article article: articleList){
            ArticleDTO articleDTO = new ArticleDTO();
            BeanUtils.copyProperties(article, articleDTO);
            User user = getUserInfoInCache(article.getUserId());
            articleDTO.setUserPic(user.getUserPic());
            articleDTO.setUserName(user.getUserName());
            articleDTOList.add(articleDTO);
        }
        PageInfo<ArticleDTO> articleDTOPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(articlePageInfo, articleDTOPageInfo);
        articleDTOPageInfo.setList(articleDTOList);
        articlePageDTO.setArticlePageInfo(articleDTOPageInfo);
        return articlePageDTO;
    }
}
