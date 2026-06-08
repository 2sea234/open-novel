package com.kxhy.novel.domain.enm;

public enum Status {

    NORMAL("1", "正常"),
    LOCK("0", "锁定"),
    DISABLED("2", "禁用");

    private final String value;

    Status(String value, String description) {
        this.value = value;
    }

    // 添加根据value获取枚举的方法
    public static Status fromValue(String value) {
        for (Status status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
