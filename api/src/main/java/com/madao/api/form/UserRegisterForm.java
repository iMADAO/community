package com.madao.api.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRegisterForm {
//    private String userName;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "电话号码不能为空")
    private String phone;
    @NotBlank(message = "验证码不能为空")
    private String code;
}
