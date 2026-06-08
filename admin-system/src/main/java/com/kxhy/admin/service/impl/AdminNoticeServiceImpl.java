package com.kxhy.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.notice.AdminNoticeAddDTO;
import com.kxhy.admin.domain.dto.notice.AdminNoticeQueryDTO;
import com.kxhy.admin.domain.dto.notice.AdminNoticeStatusDTO;
import com.kxhy.admin.domain.dto.notice.AdminNoticeUpdateDTO;
import com.kxhy.admin.domain.vo.AdminNoticeVO;
import com.kxhy.admin.mapper.AdminNoticeMapper;
import com.kxhy.admin.service.AdminNoticeService;
import com.kxhy.admin.service.AdminOperationLogService;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminNoticeServiceImpl implements AdminNoticeService {

    private final AdminNoticeMapper adminNoticeMapper;
    private final AdminOperationLogService adminOperationLogService;

    @Override
    public PageInfo<AdminNoticeVO> queryNoticePage(AdminNoticeQueryDTO query) {

        if (query == null) {

            query = new AdminNoticeQueryDTO();
        }

        if (query.getPageNum() == null || query.getPageNum() <= 0) {
            query.setPageNum(1);
        }

        if (query.getPageSize() == null || query.getPageSize() <= 0) {
            query.setPageSize(10);
        }


        PageHelper.startPage(query.getPageNum(), query.getPageSize());

        List<AdminNoticeVO> adminNoticeVOS = adminNoticeMapper.selectNoticePage(query);

        return new PageInfo<>(adminNoticeVOS);
    }

    @Override
    public void addNotice(AdminNoticeAddDTO dto, Long adminId, String username) {

        String operationDesc = "新增通知公告,标题：" + (dto == null ? null : dto.getTitle());

        try {
            if (adminId == null) {
                throw new BizException(401, "管理员账户未登录");
            }


            if (dto ==  null) {
                throw new BizException(400, "参数不能为空");
            }

            if (dto.getTitle() == null || dto.getTitle().isBlank()) {
                throw new BizException(400, "标题不能为空");
            }

            if (dto.getContent() == null || dto.getContent().isBlank()) {
                throw new BizException(400, "内容不能为空");
            }

            if (dto.getNoticeType() == null || dto.getNoticeType() != 1 && dto.getNoticeType() != 2) {
                throw new BizException(400, "类型不正确");
            }

            if (dto.getStatus() == null) {
                dto.setStatus(0);
            }

            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BizException(400, "公告状态不正确");
            }

            if (dto.getSort() == null) {
                dto.setSort(0);
            }

            int rows = adminNoticeMapper.insertNotice(dto, adminId);
            if (rows <= 0) {
                throw new BizException(500, "添加失败");
            }

            adminOperationLogService.recordSuccess(
                    "公告管理",
                    "新增",
                    operationDesc,
                    adminId,
                    username
            );

        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "公告管理",
                    "新增",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage());
            throw e;
        }

    }

    @Override
    public void updateNoticeById(Long id, AdminNoticeUpdateDTO dto, Long adminId, String username) {

//        String operationDesc = "修改通知公告，公告id为：" + id + "的记录,标题：" + (dto == null ? null : dto.getTitle());
        String operationDesc = "修改通知公告，公告id为：" + id;

        try {
            if (adminId == null) {
                throw new BizException(401, "管理员账户未登录");
            }

            if (id == null) {
                throw new BizException(400, "id 不能为空");
            }

            if (dto == null) {
                throw new BizException(400, "参数不能为空");
            }

            Integer noticeCount = adminNoticeMapper.countNormalNoticeById(id);
            if (noticeCount == null || noticeCount <= 0) {
                throw new BizException(400, "该公告不存在或已删除");
            }

            if (dto.getTitle() == null || dto.getTitle().isBlank()) {
                throw new BizException(400, "标题不能为空");
            }

            if (dto.getContent() == null || dto.getContent().isBlank()) {
                throw new BizException(400, "内容不能为空");
            }

            if (dto.getStatus() == null || dto.getStatus() !=0 && dto.getStatus() != 1) {
                throw new BizException(400, "公告状态不正确");
            }

            if (dto.getNoticeType() == null || dto.getNoticeType() != 1 && dto.getNoticeType() != 2) {
                throw new BizException(400, "类型不正确");
            }

            if (dto.getSort() == null) {
                dto.setSort(0);
            }



            int rows = adminNoticeMapper.updateNoticeById(id, dto, adminId);
            if (rows <= 0) {
                throw new BizException(500, "修改失败");
            }
            adminOperationLogService.recordSuccess(
                    "公告管理",
                    "修改",
                    operationDesc,
                    adminId,
                    username
            );

        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "公告管理",
                    "修改",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage());
            throw e;
        }

    }

    @Override
    public void modifyStatusById(Long id, AdminNoticeStatusDTO dto, Long adminId, String username) {

        String operationType = dto != null && dto.getStatus() != null && dto.getStatus() == 1 ? "发布" : "下架";
        String operationDesc = operationType + "通知公告，公告id：" + id;
        try {
            if (adminId == null) {
                throw new BizException(401, "管理员账户未登录");
            }

            if (id == null) {
                throw new BizException(400, "id 不能为空");
            }

            if (dto == null) {
                throw new BizException(400, "参数不能为空");
            }

            if (dto.getStatus() == null || dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BizException(400, "公告状态不正确");
            }

            Integer noticeCount = adminNoticeMapper.countNormalNoticeById(id);
            if (noticeCount == null || noticeCount <= 0) {
                throw new BizException(400, "该公告不存在或已删除");
            }

            int rows = adminNoticeMapper.updateNoticeStatus(id, dto.getStatus(), adminId);
            if (rows <= 0) {
                throw new BizException(500, "修改失败");
            }

            adminOperationLogService.recordSuccess(
                    "公告管理",
                    operationType,
                    operationDesc,
                    adminId,
                    username
            );

        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "公告管理",
                    operationType,
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage());
            throw e;
        }

    }

    @Override
    public void deleteNoticeById(Long id, Long adminId, String username) {

        String operationDesc = "删除通知公告，公告id为：" + id;

        try {
            if (adminId == null) {
                throw new BizException(401, "管理员账户未登录");
            }

            if (id == null) {
                throw new BizException(400, "id 不能为空");
            }

            Integer noticeCount = adminNoticeMapper.countNormalNoticeById(id);
            if (noticeCount == null || noticeCount <=0 ) {
                throw new BizException(400, "该公告不存在或已删除");
            }

            int rows = adminNoticeMapper.deleteNoticeById(id, adminId);
            if (rows <= 0) {
                throw new BizException(500, "删除公告失败");
            }

            adminOperationLogService.recordSuccess(
                    "公告管理",
                    "删除",
                    operationDesc,
                    adminId,
                    username
            );

        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "公告管理",
                    "删除",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage());
            throw e;
        }

    }
}
