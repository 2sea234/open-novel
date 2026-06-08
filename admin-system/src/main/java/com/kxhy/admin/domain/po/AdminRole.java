package com.kxhy.admin.domain.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data@AllArgsConstructor@NoArgsConstructor
public class AdminRole implements Serializable {

  private long id;
  private String roleCode;
  private String roleName;
  private long status;
  private long sort;
  private String remark;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
  private long createBy;
  private long updateBy;
  private long isDeleted;

}
