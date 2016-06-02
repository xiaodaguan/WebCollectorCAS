package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by guanxiaoda on 5/25/16.
 */
public class time {

    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HHmmss");
    private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy/MM/dd");
    private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy/MM/dd HHmmss");
    private static SimpleDateFormat sdf7 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    private static SimpleDateFormat sdf8 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdf9 = new SimpleDateFormat("yyyyMMdd HHmmss");

    private static ArrayList<SimpleDateFormat> sdfs = new ArrayList<SimpleDateFormat>();

    static {
        sdfs.add(sdf1);
        sdfs.add(sdf2);
        sdfs.add(sdf3);
        sdfs.add(sdf4);
        sdfs.add(sdf5);
        sdfs.add(sdf6);
        sdfs.add(sdf7);
        sdfs.add(sdf8);
        sdfs.add(sdf9);


    }

    /**
     * 暂时只能处理阿拉伯数字
     *
     * @param timeRaw
     * @return
     */
    public static Date timeFormat(String timeRaw) {
        if (re.rMatches(timeRaw, "\\d+[\\u4e00-\\u9fa5]+前")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int num = Integer.parseInt(re.rExtract(timeRaw, "\\d+"));
            String unit = re.rExtract(timeRaw, "[\\u4e00-\\u9fa5]+前").replace("前", "");
            if (unit.equals("秒") || unit.equals("秒钟")) calendar.add(Calendar.SECOND, -num);
            if (unit.equals("分") || unit.equals("分钟")) calendar.add(Calendar.MINUTE, -num);
            if (unit.equals("天")) calendar.add(Calendar.DAY_OF_YEAR, -num);
            if (unit.equals("周") || unit.equals("星期")) calendar.add(Calendar.WEEK_OF_YEAR, -num);
            return calendar.getTime();
        } else {

            for (SimpleDateFormat sdf : sdfs) {
                try {
                    synchronized (time.class) {
                        return sdf.parse(timeRaw);
                    }
                } catch (ParseException e) { }

            }
            return null;


        }


    }

    public static void main(String[] args) {
        System.out.println(timeFormat("8天前"));
        System.out.println(timeFormat("8分钟前"));

        System.out.println(timeFormat("8秒前"));
        System.out.println(timeFormat("2014-05-23"));
        System.out.println(timeFormat("2014-05-23 23:15:12"));
        System.out.println(timeFormat("2014-05-23 1:15:12"));
        System.out.println(timeFormat("20140523"));
        System.out.println(timeFormat("2014/05/23 23:15:12"));
        System.out.println(timeFormat("20140523 110512"));
    }
}
