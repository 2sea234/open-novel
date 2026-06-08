package com.kxhy.admin.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data@AllArgsConstructor@NoArgsConstructor
public class AdminUserQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String nickname;
    private Integer status;

}
