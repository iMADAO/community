package com.madao.api.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class QuestionForm {
    @NotBlank(message = "问题标题不能为空")
    private String questionTitle;
    @NotBlank(message = "问题内容不能为空")
    private String questionContent;
    private Long userId;
}
