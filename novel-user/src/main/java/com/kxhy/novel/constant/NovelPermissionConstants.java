package com.kxhy.novel.constant;

/**
 *  Novel权限常量
 * @author kxh
 * @date 2023/09/05 13:48
 */
public class NovelPermissionConstants {

    // 基础权限
    public static final String PERM_READ_NOVEL = "novel:read";
    public static final String PERM_COMMENT = "comment:add";
    public static final String PERM_BOOKMARK = "novel:bookmark";
    public static final String PERM_DOWNLOAD = "novel:download";

    // 管理权限（如果需要）
    public static final String PERM_MANAGE_NOVEL = "novel:manage";
    public static final String PERM_MANAGE_USER = "user:manage";
    public static final String PERM_MANAGE_COMMENT = "comment:manage";

    // 角色代码
    public static final String ROLE_READER = "reader";
    public static final String ROLE_AUTHOR = "author";
    public static final String ROLE_ADMIN = "admin";

    // 角色关系层级
    public static boolean hasHigherOrEqualRole(String userRole, String requiredRole) {
        // 角色层级: admin > author > reader
        switch (requiredRole) {
            case ROLE_READER:
                return true;
            case ROLE_AUTHOR:
                return ROLE_AUTHOR.equals(userRole) || ROLE_ADMIN.equals(userRole);
            case ROLE_ADMIN:
                return ROLE_ADMIN.equals(userRole);
            default:
                return false;
        }

    }
}
