package com.wzm.fans.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {

    public static final int SECONDS_PER_MINUTES = 60;
    public static final int SECONDS_PER_HOURS = 3600;
    public static final int SECONDS_PER_DAYS = 86400;

    /**
     * 将 1m,3d这样的字符串转换为 秒
     * @param str 输出字符串
     * @return 返回字符串代表的s
     */

    public static int toSecond(String str) {
        // 正则表达式：匹配数字和单位（s, m, h, d）
        Pattern pattern = Pattern.compile("(\\d+)([smhd])");
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            // 根据单位转换为秒
            return switch (unit) {
                case "s" -> value;
                case "m" -> value * SECONDS_PER_MINUTES;
                case "h" -> value * SECONDS_PER_HOURS;
                case "d" -> value * SECONDS_PER_DAYS;
                default -> throw new IllegalArgumentException("Unsupported time unit: " + unit);
            };
        } else {
            throw new IllegalArgumentException("Unsupported time string: " + str);
        }
    }

    public static String fromSecond(int second){
        if (second % SECONDS_PER_DAYS == 0)
            return String.format("%dd",second/SECONDS_PER_DAYS);
        if (second % SECONDS_PER_HOURS == 0)
            return String.format("%dh",second/SECONDS_PER_HOURS);
        if (second % SECONDS_PER_MINUTES == 0)
            return String.format("%dm",second/SECONDS_PER_MINUTES);
        if (second < SECONDS_PER_MINUTES && second >=0)
            return String.format("%ds",second);
        throw new IllegalArgumentException("非法的second:" + second);
    }

    public static void main(String[] args) {
        System.out.println( fromSecond(68400));
    }

}
