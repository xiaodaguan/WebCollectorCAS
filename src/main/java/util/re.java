package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guanxiaoda on 5/25/16.
 */
public class re {

    /**
     * 从字符串中抽取pattern
     *
     * @param input
     * @param pattern
     * @return
     */
    public static String rExtract(String input, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    /**
     * 是否匹配pattern
     *
     * @param input
     * @param pattern
     * @return
     */
    public static boolean rMatches(String input, String pattern) {
        Pattern p = Pattern.compile(pattern);
        return p.matches(pattern, input);
    }

    public static void main(String[] args) {
//        System.out.println(rExtract("10条12相同新闻", "\\d+"));
        System.out.println(rMatches("10天前", "\\d+天前"));
        System.out.println(rMatches("10天前", "\\d+[\\u4e00-\\u9fa5]前"));
        System.out.println(rMatches("10分钟前", "\\d+[\\u4e00-\\u9fa5]+前"));
    }
}
