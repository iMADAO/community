package com.madao.api.form;

import lombok.Data;

@Data
public class PostGetForm {
    private Long postId;
    private Integer pageNum;
    private Integer pageSize;
    private Integer commentPageSize;

    public PostGetForm(Long postId, Integer pageNum, Integer pageSize, Integer commentPageSize) {
        this.postId = postId;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.commentPageSize = commentPageSize;
    }

    public PostGetForm() {
    }
}
