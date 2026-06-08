package com.kxhy.novel.service.impl;

import com.kxhy.novel.domain.po.OperationLog;
import com.kxhy.novel.mapper.OperationMapper;
import com.kxhy.novel.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationLogService {

    private final OperationMapper operationMapper;

    @Async("taskExecutor")
    @Override
    public void saveAsync(OperationLog operationLog) {
        try {
            operationMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }

    @Override
    public void save(OperationLog operationLog) {

        try {
            operationMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }

    }
}
