package com.madao.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class ReportDTO<T> {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long reportId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String userPic;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long targetId;

    private Byte type;

    private Byte state;

    private Date createTime;

    private Date updateTime;

    private String reason;

    private T content;

    private String accessUrl;
}
