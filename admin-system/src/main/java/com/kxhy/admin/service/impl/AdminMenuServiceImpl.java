package com.kxhy.admin.service.impl;

import com.kxhy.admin.domain.dto.menu.AdminMenuAddDTO;
import com.kxhy.admin.domain.dto.menu.AdminMenuStatusDTO;
import com.kxhy.admin.domain.dto.menu.AdminMenuUpdateDTO;
import com.kxhy.admin.domain.dto.role.AdminRoleMenuAssignDTO;
import com.kxhy.admin.domain.vo.AdminMenuTreeVO;
import com.kxhy.admin.domain.vo.AdminMenuVO;
import com.kxhy.admin.mapper.AdminMenuMapper;
import com.kxhy.admin.mapper.AdminRoleMapper;
import com.kxhy.admin.mapper.AdminRoleMenuMapper;
import com.kxhy.admin.service.AdminMenuService;
import com.kxhy.admin.service.AdminOperationLogService;
import com.opennovel.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class AdminMenuServiceImpl implements AdminMenuService {

    private final AdminMenuMapper adminMenuMapper;
    private final AdminOperationLogService adminOperationLogService;
    private final AdminRoleMenuMapper adminRoleMenuMapper;
    private final AdminRoleMapper adminRoleMapper;


    /**
     * 根据id查询菜单列表
     * @param adminId id
     * @return 菜单列表
     */
    @Override
    public List<AdminMenuVO> queryMenuListByAdminId(Long adminId) {

        if (adminId == null) {
            throw new BizException(401, "管理员未登录");
        }

        List<AdminMenuVO> adminMenuVOS = adminMenuMapper.selectMenusByAdminId(adminId);
        return buildMenuTree(adminMenuVOS);
    }

    @Override
    public List<AdminMenuTreeVO> queryAllMenuTree() {

        List<AdminMenuTreeVO> adminMenuTreeVOS = adminMenuMapper.selectAllMenuList();
        Map<Long, AdminMenuTreeVO> menuMap = adminMenuTreeVOS.stream()
                .collect(Collectors.toMap(AdminMenuTreeVO::getId, item -> item));

        List<AdminMenuTreeVO> roots = new ArrayList<>();

        for (AdminMenuTreeVO menu : adminMenuTreeVOS) {
            if (menu.getParentId() == null || menu.getParentId().equals(0L)) {
                roots.add(menu);
                continue;
            }

            AdminMenuTreeVO parent = menuMap.get(menu.getParentId());

            if (parent != null) {
                parent.getChildren().add(menu);
            }

        }
        return roots;
    }

    @Override
    public void addMenu(AdminMenuAddDTO dto, Long adminId, String username) {

        String operationDesc = "新增菜单，菜单名称：" + (dto == null ? null : dto.getMenuName());
        try {
            if (adminId == null) {
                throw new BizException(401, "管理员未登录");
            }

            if (dto == null) {
                throw new BizException(400, "参数错误");
            }

            if (dto.getMenuName() == null || dto.getMenuName().isBlank()) {
                throw new BizException(400, "菜单名称不能为空");
            }

            if (dto.getMenuType() == null || (dto.getMenuType() != 1 && dto.getMenuType() != 2)) {
                throw new BizException(400, "菜单类型不正确");
            }

            if (dto.getParentId() == null) {
                dto.setParentId(0L);
            }

            if (dto.getSort() == null) {
                dto.setSort(0);
            }

            if (dto.getVisible() == null) {
                dto.setVisible(1);
            }

            if (dto.getStatus() == null) {
                dto.setStatus(1);
            }

            if (!dto.getParentId().equals(0L)) {
                // 校验父菜单存在
                Integer parentCount  = adminMenuMapper.countNormalMenuById(dto.getParentId());
                if (parentCount == null ||  parentCount <= 0) {
                    throw new BizException(400, "父级菜单不存在或已删除");
                }
            }

            if (dto.getMenuType() == 2) {
                if (dto.getPath() == null || dto.getPath().isBlank()) {
                    throw new BizException(400, "菜单路由不能为空");
                }
                if (dto.getComponent() == null || dto.getComponent().isBlank()) {
                    throw new BizException(400, "菜单组件不能为空");
                }
            }

            int insertMenu = adminMenuMapper.insertMenu(dto, adminId);
            if (insertMenu < 0) {
                throw new BizException(500, "新增菜单失败");
            }

            adminOperationLogService.recordSuccess("菜单管理",
                    "新增菜单",
                    operationDesc,
                    adminId,
                    username);

        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "菜单管理",
                    "新增",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage());
            throw e;
        }


    }

    @Override
    public void updateMenu(Long id, AdminMenuUpdateDTO dto, Long adminId, String username) {

        String operationDesc = "修改菜单，菜单名称：" + (dto == null ? null : dto.getMenuName());

        try {
            if (adminId == null) {
                throw new BizException(401, "管理员未登录");
            }

            if (id == null) {
                throw new BizException(400, "菜单id不能为空");
            }

            if (dto == null) {
                throw new BizException(400, "参数错误");
            }

            Integer menuCount = adminMenuMapper.countNormalMenuById(id);

            if (menuCount == null || menuCount <= 0) {
                throw new BizException(400, "菜单不存在或已删除");
            }

            if (dto.getMenuName() == null || dto.getMenuName().isBlank()) {
                throw new BizException(400, "菜单名称不能为空");
            }

            if (dto.getMenuType() == null || dto.getMenuType() != 1 && dto.getMenuType() != 2) {
                throw new BizException(400, "菜单类型不正确");
            }

            if (dto.getParentId() == null) {
                dto.setParentId(0L);
            }

            if (id.equals(dto.getParentId())) {
                throw new BizException(400, "父级菜单不能为当前菜单");
            }

            if (dto.getSort() == null) {
                dto.setSort(0);
            }

            if (dto.getVisible() !=0  && dto.getVisible() != 1) {
                throw new BizException(400, "菜单状态不正确");
            }

            if (!dto.getParentId().equals(0L)) {
                // 校验父菜单存在
                Integer parentCount = adminMenuMapper.countNormalMenuById(dto.getParentId());

                if (parentCount == null ||  parentCount <= 0) {
                    throw new BizException(400, "父级菜单不存在或已删除");
                }
            }

            if (dto.getMenuType() == 2) {
                if (dto.getPath() == null || dto.getPath().isBlank()) {
                    throw new BizException(400, "菜单路由不能为空");
                }

                if (dto.getComponent() == null || dto.getComponent().isBlank()) {
                    throw new BizException(400, "菜单组件不能为空");
                }
            }

            int rows = adminMenuMapper.updateMenuById(id, dto, adminId);
            if (rows <= 0) {
                throw new BizException(500, "更新菜单失败");
            }
            adminOperationLogService.recordSuccess(
                    "菜单管理",
                    "修改",
                    operationDesc,
                    adminId,
                    username
            );
        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "菜单管理",
                    "修改",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }


    }

    @Override
    public void modifyMenuStatus(Long id, AdminMenuStatusDTO dto, Long adminId, String username) {

        String operationDesc = "修改菜单状态，菜单id：" + id + "，菜单状态：" + (dto == null ? null : dto.getStatus());
        try {
            if (adminId == null) {
                throw new BizException(401, "管理员未登录");
            }

            if (id == null) {
                throw new BizException(400, "菜单id不能为空");
            }

            if (dto == null || dto.getStatus() == null) {
                throw new BizException(400, "菜单状态不能为空");
            }

            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BizException(400, "菜单状态不正确");
            }

            Integer menuCount = adminMenuMapper.countNormalMenuById(id);
            if (menuCount == null || menuCount <= 0) {
                throw new BizException(400, "菜单不存在或已删除");
            }

            int rows = adminMenuMapper.updateMenuStatus(id, dto, adminId);
            if (rows < 0) {
                throw new BizException(500, "更新菜单状态失败");
            }
            adminOperationLogService.recordSuccess(
                    "菜单管理",
                    "修改",
                    operationDesc,
                    adminId,
                    username
            );
        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "菜单管理",
                    "修改",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()
            );
            throw e;
        }


    }

    @Override
    public void delMenu(Long id, Long adminId, String username) {

        String operationDesc = "删除菜单，菜单id：" + id;

        try {
            if (adminId == null) {
                throw new BizException(401, "管理员未登录");
            }

            if (id == null) {
                throw new BizException(400, "菜单id不能为空");
            }

            Integer menuCount = adminMenuMapper.countNormalMenuById(id);
            if (menuCount == null || menuCount <= 0) {
                throw new BizException(404, "菜单不存在或已删除");
            }

            Integer childrenCount = adminMenuMapper.countChildrenByParentId(id);
            if (childrenCount != null && childrenCount > 0) {
                adminOperationLogService.recordFail(
                        "菜单管理",
                        "删除",
                        "删除菜单，菜单id：" + id,
                        adminId,
                        username,
                        "删除菜单失败"
                );
                throw new BizException(400, "菜单下有子菜单，请先删除子菜单");
            }

            Integer rows = adminMenuMapper.deleteMenuById(id, adminId);
            if (rows <= 0) {
                throw new BizException(500, "删除菜单失败");
            }

            adminOperationLogService.recordSuccess(
                    "菜单管理",
                    "删除",
                    operationDesc,
                    adminId,
                    username
            );
        } catch (BizException e) {
            adminOperationLogService.recordFail(
                    "菜单管理",
                    "删除",
                    operationDesc,
                    adminId,
                    username,
                    e.getMessage()

            );
            throw e;
        }

    }

    /**
     * 查询角色菜单
     * @param roleId 角色id
     * @return 角色菜单id列表
     */
    @Override
    public List<Long> queryRoleMenuIds(Long roleId) {

        if (roleId == null) {
            throw new BizException(400, "角色id不能为空");
        }

        Integer roleCount = adminRoleMapper.countNormalRoleById(roleId);

        if (roleCount == null || roleCount <= 0) {
            throw new BizException(400, "角色不存在或已删除");
        }

        return adminRoleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    /**
     * 分配菜单
     * @param roleId 角色id
     * @param dto 菜单信息
     * @param adminId 当前管理员id
     */
    @Override
    public void assignMenus(Long roleId, AdminRoleMenuAssignDTO dto, Long adminId) {

        if (adminId == null) {
            throw new BizException(401, "当前管理员未登录");
        }

        if (roleId == null) {
            throw new BizException(400, "角色id不能为空");
        }

        if (dto == null || dto.getMenuIds() == null) {
            throw new BizException(400, "菜单列表不能为空");
        }

        Integer roleCount = adminRoleMapper.countNormalRoleById(roleId);
        if (roleCount == null || roleCount <= 0) {
            throw new BizException(400, "这个角色有没有被管理员绑定");
        }

        // 保护内置超级管理员角色
        if (roleId.equals(1L)) {
            throw new BizException(400, "不能修改内置超级管理员菜单");
        }

        List<Long> menIds = dto.getMenuIds()
                .stream()
                .distinct()
                .toList();

        if (menIds.contains(null)) {
            throw new BizException(400, "菜单id不能为空");
        }

        if (!menIds.isEmpty()) {



            Integer menuCount = adminMenuMapper.countNormalMenusByIds(menIds);
            if (menuCount == null || menuCount <= 0) {
                throw new BizException(400, "菜单不存在或已删除");
            }
        }
        // 先校验全部通过，再删除旧绑定
        adminRoleMenuMapper.deleteMenusByRoleId(roleId);

        if (menIds.isEmpty()) {
            return;
        }

        int rows = adminRoleMenuMapper.batchInsertRoleMenus(roleId, menIds, adminId);
        if (rows <= 0) {
            throw new BizException(500, "分配菜单失败");
        }

    }


    /**
     * 把“平铺菜单列表”组装成“树结构”
     * @param menus 平铺菜单列表
     * @return  树结构菜单列表
     */
    private List<AdminMenuVO> buildMenuTree(List<AdminMenuVO> menus) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, AdminMenuVO> menuVOMap = new LinkedHashMap<>();

        for (AdminMenuVO menu : menus) {
            menu.setChildren(new ArrayList<>());
            menuVOMap.put(menu.getId(), menu);
        }

        List<AdminMenuVO> tree = new ArrayList<>();

        for (AdminMenuVO menu : menus) {
            Long parentId = menu.getParentId();

            if (parentId == null || parentId == 0) {
                tree.add(menu);
            }else {
                AdminMenuVO parent = menuVOMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }

        return tree;
    }
}
