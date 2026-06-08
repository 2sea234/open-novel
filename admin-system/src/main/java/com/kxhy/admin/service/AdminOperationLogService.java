package com.kxhy.admin.service;

import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.AdminOperationLogQueryDTO;
import com.kxhy.admin.domain.po.AdminOperationLog;
import com.kxhy.admin.domain.vo.AdminOperationLogVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface AdminOperationLogService {

    void recordSuccess(String operationModule,
                       String operationType,
                       String operationDesc,
                       Long adminId,
                       String username);

    void recordFail(String operationModule,
                    String operationType,
                    String operationDesc,
                    Long adminId,
                    String username,
                    String errorMessage);

    PageInfo<AdminOperationLogVO> queryOperationLogList(AdminOperationLogQueryDTO query);
}
