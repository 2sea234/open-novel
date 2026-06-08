package com.kxhy.admin.service;

import com.github.pagehelper.PageInfo;
import com.kxhy.admin.domain.dto.notice.AdminNoticeAddDTO;
import com.kxhy.admin.domain.dto.notice.AdminNoticeQueryDTO;
import com.kxhy.admin.domain.dto.notice.AdminNoticeStatusDTO;
import com.kxhy.admin.domain.dto.notice.AdminNoticeUpdateDTO;
import com.kxhy.admin.domain.vo.AdminNoticeVO;

public interface AdminNoticeService {

    /**
     * 获取公告列表
     * @param query 查询参数
     * @return 公告列表
     */
    PageInfo<AdminNoticeVO> queryNoticePage(AdminNoticeQueryDTO query);

    /**
     * 添加公告
     * @param dto 添加参数
     * @param adminId 管理员id
     * @param username 管理员名称
     */
    void addNotice(AdminNoticeAddDTO dto, Long adminId, String username);

    /**
     * 修改公告
     * @param id 公告id
     * @param dto 修改参数
     * @param adminId 管理员id
     * @param username 管理员名称
     */
    void updateNoticeById(Long id, AdminNoticeUpdateDTO dto, Long adminId, String username);

    /**
     * 修改公告状态
     * @param id 公告id
     * @param status 状态
     * @param adminId 管理员id
     * @param username 管理员名称
     */
    void modifyStatusById(Long id, AdminNoticeStatusDTO dto, Long adminId, String username);

    /**
     * 删除公告
     * @param id 删除id
     * @param adminId 管理员id
     * @param username 管理员名称
     */
    void deleteNoticeById(Long id, Long adminId, String username);

}
