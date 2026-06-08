package com.kxhy.admin.domain.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminUserAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String nickname;
    private Integer status;


}
