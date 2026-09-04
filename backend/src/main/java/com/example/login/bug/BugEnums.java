package com.example.login.bug;

import java.util.Set;

public final class BugEnums {

    private BugEnums() {
    }

    public static final Set<String> TYPES = Set.of("缺陷", "新功能");
    public static final Set<String> STATUSES = Set.of("打开", "处理中", "已修复", "已关闭");
    public static final Set<String> SEVERITIES = Set.of("轻微", "一般", "严重", "致命");

    public static boolean isValidType(String type) {
        return type != null && TYPES.contains(type);
    }

    public static boolean isValidStatus(String status) {
        return status != null && STATUSES.contains(status);
    }

    public static boolean isValidSeverity(String severity) {
        return severity != null && SEVERITIES.contains(severity);
    }
}
