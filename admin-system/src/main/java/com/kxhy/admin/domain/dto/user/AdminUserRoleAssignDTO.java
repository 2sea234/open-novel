package com.kxhy.admin.domain.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AdminUserRoleAssignDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Long> roleIds;

}
