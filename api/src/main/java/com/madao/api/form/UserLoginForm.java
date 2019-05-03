package com.madao.api.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginForm {
    @NotEmpty(message = "账户不能为空")
    private String account;
    @NotEmpty(message = "密码不能为空")
    private String password;
}
