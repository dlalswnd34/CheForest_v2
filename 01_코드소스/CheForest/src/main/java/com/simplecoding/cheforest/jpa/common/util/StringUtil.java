package com.simplecoding.cheforest.jpa.common.util;

import java.util.List;

public class StringUtil {  // 재료 처리 목적으로 만든 클래스
    public static String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(",", list);
    }
}
