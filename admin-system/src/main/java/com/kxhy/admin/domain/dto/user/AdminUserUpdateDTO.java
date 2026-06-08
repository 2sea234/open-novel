package com.kxhy.admin.domain.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminUserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nickname;
    private Integer status;
}
