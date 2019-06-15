package com.madao.api.form;

import lombok.Data;

@Data
public class PasswordChangeForm {
    private String password;
    private String newPassword;
}
