package com.kxhy.admin.domain.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor@AllArgsConstructor
public class AdminPermission implements Serializable {

  private static final long serialVersionUID = 1L;
  private long id;
  private long menuId;
  private String permissionCode;
  private String permissionName;
  private long permissionType;
  private String requestMethod;
  private String requestPath;
  private long status;
  private String remark;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
  private long createBy;
  private long updateBy;
  private long isDeleted;
}
