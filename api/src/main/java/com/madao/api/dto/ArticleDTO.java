package com.madao.api.dto;

import com.github.pagehelper.PageInfo;
import com.madao.api.entity.Article;
import com.madao.api.entity.ArticleCategory;
import lombok.Data;

import java.util.List;

@Data
public class ArticleDTO {
    private List<ArticleCategory> categoryList;
    private PageInfo<Article> articlePageInfo;
}
