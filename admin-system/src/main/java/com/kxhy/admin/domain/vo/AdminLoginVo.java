package com.kxhy.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String token;
    private String tokenType;
    private Long adminId;
    private String username;
    private String nickname;

}
