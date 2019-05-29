package com.madao.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class ArticleDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long articleId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String userPic;

    private Date createTime;

    private Date updateTime;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    private String accessUrl;

    private String filePath;

    private Integer downloadCount;

    private String originName;

    private String title;

    private Byte state;

    private Byte collectState = 0;
}
