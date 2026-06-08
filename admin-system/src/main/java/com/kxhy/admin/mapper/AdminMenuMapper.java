package com.kxhy.admin.mapper;

import com.kxhy.admin.domain.dto.menu.AdminMenuAddDTO;
import com.kxhy.admin.domain.dto.menu.AdminMenuStatusDTO;
import com.kxhy.admin.domain.dto.menu.AdminMenuUpdateDTO;
import com.kxhy.admin.domain.vo.AdminMenuTreeVO;
import com.kxhy.admin.domain.vo.AdminMenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMenuMapper {

    /**
     * 根据用户id查询菜单
     * @param adminId id
     * @return 菜单列表
     */
    List<AdminMenuVO> selectMenusByAdminId(@Param("adminId") Long adminId);

    /**
     * 查询所有菜单树
     * @return 菜单树
     */
    List<AdminMenuTreeVO> selectAllMenuList();

    /**
     * 先根据id查询菜单数量判断菜单是否存在 之后再执行插入操作
     * @param id id
     * @return 数量
     */
    Integer countNormalMenuById(@Param("id") Long id);

    /**
     * 添加菜单
     * @param dto dto
     * @param adminId 管理员id
     * @return 影响行数
     */
    int insertMenu(@Param("dto")AdminMenuAddDTO dto, @Param("adminId") Long adminId);

    /**
     * 修改菜单
     * @param id id
     * @param dto dto
     * @param adminId 管理员id
     * @return 影响行数
     */
    int updateMenuById(@Param("id") Long id, @Param("dto")AdminMenuUpdateDTO dto, @Param("adminId") Long adminId);

    int updateMenuStatus(@Param("id") Long id, @Param("dto") AdminMenuStatusDTO dto, @Param("adminId") Long adminId);

    Integer countChildrenByParentId(@Param("parentId") Long parentId);
    Integer deleteMenuById(@Param("id") Long id, @Param("adminId") Long adminId);


    /**
     * 校验菜单有效数量
     * @param menuIds 菜单id列表
     * @return  数量
     */
    Integer countNormalMenusByIds(@Param("menuIds") List<Long> menuIds);


}
