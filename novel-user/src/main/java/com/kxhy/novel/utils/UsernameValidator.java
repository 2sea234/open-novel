package com.kxhy.novel.utils;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class UsernameValidator {

    // 不允许使用的用户名列表
    private static final Set<String> RESERVED_USERNAMES = new HashSet<>();

    static {
        // 系统保留用户名
        RESERVED_USERNAMES.add("admin");
        RESERVED_USERNAMES.add("administrator");
        RESERVED_USERNAMES.add("root");
        RESERVED_USERNAMES.add("system");
        RESERVED_USERNAMES.add("guest");
        RESERVED_USERNAMES.add("test");
        RESERVED_USERNAMES.add("novel");
        RESERVED_USERNAMES.add("noveladmin");
        RESERVED_USERNAMES.add("manager");
        RESERVED_USERNAMES.add("superuser");

        // 脏话/敏感词列表
        RESERVED_USERNAMES.add("fuck");
        RESERVED_USERNAMES.add("shit");
        RESERVED_USERNAMES.add("asshole");
        RESERVED_USERNAMES.add("bastard");
        RESERVED_USERNAMES.add("bitch");
    }

    /**
     * 验证用户名格式
     * @param username 用户名
     * @return 验证结果
     */
    public ValidationResult validate(String username) {
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.error("用户名不能为空");
        }

        String trimmedUsername = username.trim();

        // 1. 长度验证
        if (trimmedUsername.length() < 3) {
            return ValidationResult.error("用户名至少3个字符");
        }
        if (trimmedUsername.length() > 20) {
            return ValidationResult.error("用户名最多20个字符");
        }

        // 2. 字符范围验证
        String usernameRegex = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
        if (!Pattern.matches(usernameRegex, trimmedUsername)) {
            return ValidationResult.error("用户名只能包含中文、字母、数字和下划线");
        }

        // 3. 检查是否包含中文和英文/数字混合
        if (containsChineseAndEnglish(trimmedUsername)) {
            return ValidationResult.error("用户名不能同时包含中文和英文/数字");
        }

        // 4. 检查是否以数字开头
        if (Pattern.matches("^\\d.*", trimmedUsername)) {
            return ValidationResult.error("用户名不能以数字开头");
        }

        // 5. 检查是否以下划线开头或结尾
        if (trimmedUsername.startsWith("_") || trimmedUsername.endsWith("_")) {
            return ValidationResult.error("用户名不能以下划线开头或结尾");
        }

        // 6. 检查连续下划线
        if (trimmedUsername.contains("__")) {
            return ValidationResult.error("用户名不能包含连续下划线");
        }

        // 7. 检查保留用户名
        String lowerUsername = trimmedUsername.toLowerCase();
        if (RESERVED_USERNAMES.contains(lowerUsername)) {
            return ValidationResult.error("该用户名不可用，请换一个");
        }

        // 8. 检查包含保留词
        for (String reserved : RESERVED_USERNAMES) {
            if (lowerUsername.contains(reserved)) {
                return ValidationResult.error("用户名包含不允许使用的词汇");
            }
        }

        // 9. 检查特殊模式（如重复字符）
        if (isRepeatingPattern(trimmedUsername)) {
            return ValidationResult.error("用户名不能使用重复模式");
        }

        // 10. 检查纯数字
        if (Pattern.matches("^\\d+$", trimmedUsername)) {
            return ValidationResult.error("用户名不能全是数字");
        }

        // 11. 检查纯下划线
        if (Pattern.matches("^_+$", trimmedUsername)) {
            return ValidationResult.error("用户名不能全是下划线");
        }

        return ValidationResult.success();
    }

    /**
     * 是否同时包含中文和英文/数字
     */
    private boolean containsChineseAndEnglish(String username) {
        boolean hasChinese = Pattern.matches(".*[\u4e00-\u9fa5].*", username);
        boolean hasEnglishOrDigit = Pattern.matches(".*[a-zA-Z0-9].*", username);
        return hasChinese && hasEnglishOrDigit;
    }

    /**
     * 检查是否是重复模式（如aaa, abcabc）
     */
    private boolean isRepeatingPattern(String username) {
        // 检查连续相同字符
        if (Pattern.matches("(.)\\1{2,}", username)) {
            return true; // 超过2个连续相同字符
        }

        // 检查重复模式
        for (int i = 1; i <= username.length() / 2; i++) {
            String pattern = username.substring(0, i);
            if (username.matches("(" + Pattern.quote(pattern) + "){2,}")) {
                return true;
            }
        }

        return false;
    }

    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private boolean valid;
        private String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, "用户名格式正确");
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}