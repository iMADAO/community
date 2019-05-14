package com.madao.api.form;

import lombok.Data;

@Data
public class PostGetForm {
    private Long categoryId;
    private Integer pageNum;
    private Integer pageSize;
}
