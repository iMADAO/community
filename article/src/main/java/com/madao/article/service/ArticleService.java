package com.madao.article.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.madao.api.Exception.ResultException;
import com.madao.api.dto.ArticleDTO;
import com.madao.api.dto.ArticlePageDTO;
import com.madao.api.entity.Article;
import com.madao.api.entity.ArticleCategory;
import com.madao.api.entity.User;
import com.madao.api.enums.StateEnum;
import com.madao.api.form.ArticleAddForm;
import com.madao.api.service.UserService;
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.ResultView;
import com.madao.article.bean.ArticleExample;
import com.madao.article.mapper.ArticleMapper;
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

    public ArticlePageDTO getArticleDTO(Integer pageNum, Integer pageSize) {
        ArticlePageDTO articlePageDTO = new ArticlePageDTO();
        List<ArticleCategory> articleCategoryList = articleCategoryService.getAllCategory();
        articlePageDTO.setCategoryList(articleCategoryList);

        PageHelper.startPage(pageNum, pageSize);
        ArticleExample example = new ArticleExample();
        List<Article> articleList = articleMapper.selectByExampleWithBLOBs(example);
        PageInfo<Article> articlePageInfo = new PageInfo<>(articleList);

        List<ArticleDTO> articleDTOList = new ArrayList<>();
        populateArticleDTO(articleList, articleDTOList);

        PageInfo<ArticleDTO> resultPage = new PageInfo<>();
        BeanUtils.copyProperties(articlePageInfo, resultPage);
        resultPage.setList(articleDTOList);

        articlePageDTO.setArticlePageInfo(resultPage);
        return articlePageDTO;
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

    public ArticlePageDTO getArticleDTOByCategoryId(Integer pageNum, Integer pageSize, Long categoryId) {
        ArticlePageDTO articlePageDTO = new ArticlePageDTO();
        List<ArticleCategory> articleCategoryList = articleCategoryService.getAllCategory();
        articlePageDTO.setCategoryList(articleCategoryList);

        PageHelper.startPage(pageNum, pageSize);
        ArticleExample example = new ArticleExample();
        ArticleExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<Article> articleList = articleMapper.selectByExampleWithBLOBs(example);
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
        List<Article> articleList = articleMapper.selectByExampleWithBLOBs(example);
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
}
