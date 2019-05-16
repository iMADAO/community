package com.madao.api.form;

import lombok.Data;

import java.util.List;

@Data
public class PostForm {
    private Long userId;
    private Long categoryId;
    private List<ContentForm> answerContentFormList;
}
