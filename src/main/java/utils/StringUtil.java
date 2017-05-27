package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guanxiaoda on 5/20/16.
 */
public class StringUtil {

    public static String MD5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("StringUtil");
            byte[] array = md.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(MD5("dklajf;la"));
    }

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

    public static List<String> rExtractList(String input, String pattern){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        List<String> results = new ArrayList<String>();
        while(m.find()){

            results.add(m.group());
        }
        return results;
    }
}
