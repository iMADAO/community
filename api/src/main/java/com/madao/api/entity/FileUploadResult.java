package com.madao.api.entity;

import lombok.Data;

@Data
public class FileUploadResult {
    private String imgPath;
    private String imgName;
    private Integer num;
}
