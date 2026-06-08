package com.kxhy.admin.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;


public class OperationLogDescUtil {

    private OperationLogDescUtil() {}

    public static <T> void addChange(List<String> changes,
                                     String fieldName,
                                     T oldValue,
                                     T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            changes.add(fieldName + "：" + oldValue + " -> " + newValue);
        }
    }

    public static <T> void addChange(List<String> changes,
                                     String fieldName,
                                     T oldValue,
                                     T newValue,
                                     Function<T, String> formatter) {
        if (!Objects.equals(oldValue, newValue)) {
            changes.add(fieldName + "：" + formatter.apply(oldValue) + " -> " + formatter.apply(newValue));
        }
    }

    public static String buildUpdateDesc(String targetName, Long targetId, List<String> changes) {
        String base = "修改" + targetName + "，" + targetName + "id：" + targetId;

        if (changes == null || changes.isEmpty()) {
            return base + "，修改内容：无变化";
        }

        return base + "，修改内容：" + String.join("；", changes);
    }

    public static String buildAssignDesc(String prefix,
                                         String itemName,
                                         List<Long> oldIds,
                                         List<Long> newIds) {

        List<Long> oldList = oldIds == null ? new ArrayList<>() : oldIds;
        List<Long> newList = newIds == null ? new ArrayList<>() : newIds;

        Set<Long> oldSet = new LinkedHashSet<>(oldList);
        Set<Long> newSet = new LinkedHashSet<>(newList);

        List<Long> addedIds = newList.stream()
                .filter(id -> !oldSet.contains(id))
                .toList();

        List<Long> removedIds = oldList.stream()
                .filter(id -> !newSet.contains(id))
                .toList();

        List<String> changes = new ArrayList<>();

        changes.add("原" + itemName + "：" + oldList);
        changes.add("新" + itemName + "：" + newList);

        if (!addedIds.isEmpty()) {
            changes.add("新增" + itemName + "：" + addedIds);
        }

        if (!removedIds.isEmpty()) {
            changes.add("移除" + itemName + "：" + removedIds);
        }

        if (addedIds.isEmpty() && removedIds.isEmpty()) {
            changes.add(itemName + "未变化");
        }

        return prefix + "，" + String.join("；", changes);
    }

}
