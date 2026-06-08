package com.kxhy.admin.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminLoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

}
