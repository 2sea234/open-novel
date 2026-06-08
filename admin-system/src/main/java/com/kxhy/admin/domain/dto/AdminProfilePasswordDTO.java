package com.kxhy.admin.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminProfilePasswordDTO implements Serializable {

    private final static long serialVersionUID = 1L;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

}
