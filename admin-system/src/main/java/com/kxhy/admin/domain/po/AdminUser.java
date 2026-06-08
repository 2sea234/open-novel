package com.kxhy.admin.domain.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data@AllArgsConstructor@NoArgsConstructor
public class AdminUser implements Serializable {

  private static final long serialVersionUID = 1L;
  private Long id;
  private String username;
  private String password;
  private String nickname;
  private Integer status;
  private LocalDateTime lastLoginTime;
  private String lastLoginIp;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
  private Long createBy;
  private Long updateBy;
  private Integer isDeleted;

}
