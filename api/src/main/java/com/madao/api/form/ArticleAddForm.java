package com.madao.api.form;

import lombok.Data;

@Data
public class ArticleAddForm {
    private Long userId;

    private Long categoryId;

    private String accessUrl;

    private String filePath;

    private String originName;

    private String title;
}
