package com.kxhy.admin.service;

import com.kxhy.admin.domain.dto.menu.AdminMenuAddDTO;
import com.kxhy.admin.domain.dto.menu.AdminMenuStatusDTO;
import com.kxhy.admin.domain.dto.menu.AdminMenuUpdateDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleMenuAssignDTO;
import com.kxhy.admin.domain.vo.AdminMenuTreeVO;
import com.kxhy.admin.domain.vo.AdminMenuVO;

import java.util.List;

public interface AdminMenuService {

    /**
     * 根据用户id查询菜单列表
     * @return 菜单列表
     */
    List<AdminMenuVO> queryMenuListByAdminId(Long adminId);

    /**
     * 查询所有菜单树
     * @return 菜单树
     */
    List<AdminMenuTreeVO> queryAllMenuTree();

    /**
     * 添加菜单
     * @param dto dto
     * @param adminId 管理员id
     */
    void addMenu(AdminMenuAddDTO dto, Long adminId, String username);

    /**
     * 修改菜单信息
     * @param id id
     * @param dto dto
     * @param adminId 管理员id
     */
    void updateMenu(Long id, AdminMenuUpdateDTO dto, Long adminId, String username);

    void modifyMenuStatus(Long id, AdminMenuStatusDTO status, Long adminId, String username);

    void delMenu(Long id, Long adminId, String username);

    List<Long> queryRoleMenuIds(Long roleId);

    void assignMenus(Long roleId, AdminRoleMenuAssignDTO dto, Long adminId);

}
