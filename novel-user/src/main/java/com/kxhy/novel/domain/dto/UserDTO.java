package com.kxhy.novel.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {

    // 默认序列化
    private static final long serialVersionUID = 1L;
    private Long userId;
    private String username;

}
