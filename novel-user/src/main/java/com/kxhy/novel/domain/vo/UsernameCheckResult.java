package com.kxhy.novel.domain.vo;

import lombok.Data;

/**
 * 用户名检查结果
 */
@Data
public class UsernameCheckResult {
    private boolean available;
    private String username;
    private String message;
    private String suggestion;
    private long timestamp;

    public static UsernameCheckResult available(String username) {
        UsernameCheckResult result = new UsernameCheckResult();
        result.setAvailable(true);
        result.setUsername(username);
        result.setMessage("用户名可用");
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public static UsernameCheckResult unavailable(String username, String message) {
        return unavailable(username, message, null);
    }

    public static UsernameCheckResult unavailable(String username, String message, String suggestion) {
        UsernameCheckResult result = new UsernameCheckResult();
        result.setAvailable(false);
        result.setUsername(username);
        result.setMessage(message);
        result.setSuggestion(suggestion);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
}