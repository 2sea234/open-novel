package com.kxhy.novel.service;

import com.kxhy.novel.domain.po.OperationLog;

/**
 * 操作日志服务
 */
public interface OperationLogService {

    /**
     * 异步保存操作日志
     * @param operationLog 操作日志
     */
    void saveAsync(OperationLog operationLog);

    /**
     * 同步保存操作日志
     * @param operationLog 操作日志
     */
    void save(OperationLog operationLog);

}
