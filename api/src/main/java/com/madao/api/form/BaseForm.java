package com.madao.api.form;

import lombok.Data;

@Data
public class BaseForm {
    private Long id;
    private Integer pageNum;
    private Integer pageSize;

    public BaseForm() {
    }

    public BaseForm(Long id, Integer pageNum, Integer pageSize) {

        this.id = id;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
