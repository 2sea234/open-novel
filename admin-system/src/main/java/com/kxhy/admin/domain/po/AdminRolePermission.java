package com.kxhy.admin.domain.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data@NoArgsConstructor@AllArgsConstructor
public class AdminRolePermission implements Serializable {

  private static final long serialVersionUID = 1L;
  private long id;
  private long roleId;
  private long permissionId;
  private LocalDateTime createTime;
  private long createBy;
}
