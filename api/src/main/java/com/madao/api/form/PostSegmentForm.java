package com.madao.api.form;

import lombok.Data;

import java.util.List;

@Data
public class PostSegmentForm {
    private Long userId;
    private Long postId;
    private List<ContentForm> answerContentFormList;
}
