package com.madao.article.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.madao.api.dto.ArticleDTO;
import com.madao.api.entity.Article;
import com.madao.api.entity.ArticleCategory;
import com.madao.api.form.ArticleAddForm;
import com.madao.api.utils.KeyUtil;
import com.madao.article.bean.ArticleExample;
import com.madao.article.mapper.ArticleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

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

    public ArticleDTO getArticleDTO(Integer pageNum, Integer pageSize) {
        ArticleDTO articleDTO = new ArticleDTO();
        List<ArticleCategory> articleCategoryList = articleCategoryService.getAllCategory();
        articleDTO.setCategoryList(articleCategoryList);
        PageHelper.startPage(pageNum, pageSize);
        ArticleExample example = new ArticleExample();
        List<Article> articleList = articleMapper.selectByExample(example);
        PageInfo<Article> articlePageInfo = new PageInfo<>(articleList);
        articleDTO.setArticlePageInfo(articlePageInfo);
        return articleDTO;
    }

    public ArticleDTO getArticleDTOByCategoryId(Integer pageNum, Integer pageSize, Long categoryId) {
        ArticleDTO articleDTO = new ArticleDTO();
        List<ArticleCategory> articleCategoryList = articleCategoryService.getAllCategory();
        articleDTO.setCategoryList(articleCategoryList);
        PageHelper.startPage(pageNum, pageSize);
        ArticleExample example = new ArticleExample();
        ArticleExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<Article> articleList = articleMapper.selectByExample(example);
        PageInfo<Article> articlePageInfo = new PageInfo<>(articleList);
        articleDTO.setArticlePageInfo(articlePageInfo);
        return articleDTO;
    }
}
