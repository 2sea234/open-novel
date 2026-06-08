package com.kxhy.admin.domain.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminUserRoleQueryDTO implements Serializable {

    private final static long serialVersionUID = 1L;
    private Long userId;

}
