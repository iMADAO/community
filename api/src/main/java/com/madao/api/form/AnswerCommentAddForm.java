package com.madao.api.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AnswerCommentAddForm {
    @NotBlank(message = "评论内容不能为空")
    private String commentContent;

    @NotNull(message = "该回答不存在")
    private Long answerId;
}
