package com.gds.extractor.utils;

import android.support.annotation.Nullable;

import java.util.List;

public class TextUtils {


    /**
     * Sostituisce con un blank la stringa passata
     *
     * @param builder
     * @param text
     */
    public static void replaceLastString(StringBuilder builder, String text) {
        if (builder.indexOf(text) != -1) {
            builder.replace(builder.lastIndexOf(text), builder.length(), "");
        }
    }

    /**
     * Sostituisce con un blanck la stringa passata
     * @param value
     * @param text
     * @return
     */
    public static String replaceLastString(String value, String text) {
        StringBuilder builder = new StringBuilder(value);
        replaceLastString(builder, text);
        return builder.toString();

    }

    public static String completeWithPrefix(String value, char replacement, int max) {
        int i = max - value.length();
//        if (i > 0) {
        try {
            String replacementValue = new String(new char[i]).replace('\0', replacement);
            return replacementValue + value;
        } catch (NegativeArraySizeException e) {
            return value;
        }
//        }

//        return value;

    }

    public static String completeWithSuffix(String value, char replacement, int max) {
        int i = max - value.length();
        String replacementValue = new String(new char[i]).replace('\0', replacement);
        return value + replacementValue;
    }



    public static String getSingleString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(list)) {
            for (String s : list) {
                builder.append(s).append("; ");
            }

            builder.replace(builder.lastIndexOf("; "), builder.length(), "");
        }

        return builder.toString();
    }


    /**
     * Returns true if the string is null or 0-length.
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns true if the string is null or 0-length or contains "null".
     * @param str the string to be examined
     * @return true if str is null or zero length or contains "null"
     */
    public static boolean isEmptyNull(@Nullable CharSequence str) {
        return isEmpty(str) || "null".equals(str);
    }
}
