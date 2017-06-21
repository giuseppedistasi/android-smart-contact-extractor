package com.gds.extractor.utils;

import java.util.Collection;
import java.util.Map;

public class CollectionUtils{

    public static boolean isEmpty(Collection list) {
        return list == null || list.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Collection list) {
        return !isEmpty(list);
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    public static String toString(Object[] array) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (array != null && array.length > 0) {

            for (Object o : array) {
                stringBuilder.append(o).append("\n");
            }


            stringBuilder.replace(stringBuilder.lastIndexOf("\n"), stringBuilder.length(), "");
        }

        return stringBuilder.toString();
    }
}
