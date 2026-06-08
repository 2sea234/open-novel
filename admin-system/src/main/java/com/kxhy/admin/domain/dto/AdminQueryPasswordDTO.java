package com.kxhy.admin.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminQueryPasswordDTO implements Serializable {

    private final static long serialVersionUID = 1L;
    private String password;

}
