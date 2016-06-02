import org.fusesource.jansi.Ansi;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by guanxiaoda on 5/26/16.
 * 查看数据库每天数据量
 */
public class Monitor {


    static {
        try {
            JdbcTemplate jdbcTemplateTire = db.JDBCHelper.createOracleTemplate("tire", "jdbc:oracle:thin:@172.18.79.3:1521/orcl", "tire", "tire2014", 5, 30);
        } catch (Exception e) {
            System.err.println("connect db failed.");
        }
        try {
            JdbcTemplate jdbcTemplateFinance = db.JDBCHelper.createOracleTemplate("jinrong", "jdbc:oracle:thin:@172.18.79.3:1521/orcl", "jinrong", "jinrong", 5, 30);
        } catch (Exception e) {
            System.err.println("connect db failed.");
        }
        try {
            JdbcTemplate jdbcTemplateTraffic = db.JDBCHelper.createOracleTemplate("traffic", "jdbc:oracle:thin:@117.132.15.89:1521/orcl", "qdtramgr", "qdtramgr", 5, 30);
        } catch (Exception e) {
            System.err.println("connect db failed.");
        }
        try {
            JdbcTemplate jdbcTemplateTech = db.JDBCHelper.createOracleTemplate("tech", "jdbc:oracle:thin:@172.18.79.3:1521/orcl", "qdtech2015", "qdtech2015", 5, 30);
        } catch (Exception e) {
            System.err.println("connect db failed.");
        }
    }

    private void test() {
        System.out.println("test");
    }

    private void show() {
        ArrayList<String> names = new ArrayList<String>();
//        names.add("tire");
//        names.add("jinrong");
        names.add("traffic");
//        names.add("tech");
        for (String jdbcTemplateName : names) {
            show24hInsertTime(jdbcTemplateName);
            showTodayInsertTime(jdbcTemplateName);
            show24hPubTime(jdbcTemplateName);
            showTodayPubTime(jdbcTemplateName);
        }

    }

    private void show24hInsertTime(String jdbcTemplateName) {
        System.out.println(Ansi.ansi().eraseScreen().render(">>" + jdbcTemplateName + " @|red 24hours inserttime|@:----------------------"));
        System.out.println("type\tcount\tmin(inserttime)\tmax(inserttime)");
        String SQL_NEWS = "select count(*),min(inserttime),max(inserttime) from news_data where trunc(inserttime) >= trunc(sysdate)-1";
        String SQL_BBS = "select count(*),min(insert_time),max(insert_time) from bbs_data where trunc(insert_time) >= trunc(sysdate)-1";
        String SQL_WEIBO = "select count(*),min(insert_time),max(insert_time) from weibo_data where trunc(insert_time) >= trunc(sysdate)-1";
        String SQL_WEIXIN = "select count(*),min(inserttime),max(inserttime) from weixin_data where trunc(inserttime) >= trunc(sysdate)-1";
        String SQL_EB = "select count(*),min(insert_time),max(insert_time) from eb_data where trunc(insert_time) >= trunc(sysdate)-1";
        String SQL_BLOG = "select count(*),min(inserttime),max(inserttime) from blog_data where trunc(inserttime) >= trunc(sysdate)-1";
        HashMap<String, String> sqlList = new HashMap<String, String>();
        sqlList.put("news", SQL_NEWS);
        sqlList.put("bbs", SQL_BBS);
        sqlList.put("weibo", SQL_WEIBO);
        sqlList.put("weixin", SQL_WEIXIN);
        if (jdbcTemplateName.equals("tire")) sqlList.put("eb", SQL_EB);
        sqlList.put("blog", SQL_BLOG);
        for (String type : sqlList.keySet()) {
            String[] news_count_today = queryForCountAndInfo(jdbcTemplateName, sqlList.get(type));

            System.out.println(type + ":\t[" + news_count_today[0] + "]\t[" + news_count_today[1] + "]\t[" + news_count_today[2] + "]");
        }
    }

    private void show24hPubTime(String jdbcTemplateName) {
        System.out.println(Ansi.ansi().eraseScreen().render(">>" + jdbcTemplateName + "  @|red 24hours pubtime|@:----------------------"));
        System.out.println("type\tcount\tmin(pubtime)\tmax(pubtime)");
        String SQL_NEWS = "select count(*),min(pubtime),max(pubtime) from news_data where trunc(pubtime) >= trunc(sysdate)-1";
        String SQL_BBS = "select count(*),min(pubtime),max(pubtime) from bbs_data where trunc(pubtime) >= trunc(sysdate)-1";
        String SQL_WEIBO = "select count(*),min(pubtime),max(pubtime) from weibo_data where trunc(pubtime) >= trunc(sysdate)-1";
        String SQL_WEIXIN = "select count(*),min(pubtime),max(pubtime) from weixin_data where trunc(pubtime) >= trunc(sysdate)-1";
        String SQL_EB = "select count(*),min(pubtime),max(pubtime) from eb_data where trunc(pubtime) >= trunc(sysdate)-1";
        String SQL_BLOG = "select count(*),min(pubtime),max(pubtime) from blog_data where trunc(pubtime) >= trunc(sysdate)-1";
        HashMap<String, String> sqlList = new HashMap<String, String>();
        sqlList.put("news", SQL_NEWS);
        sqlList.put("bbs", SQL_BBS);
        sqlList.put("weibo", SQL_WEIBO);
        sqlList.put("weixin", SQL_WEIXIN);
        if (jdbcTemplateName.equals("tire")) sqlList.put("eb", SQL_EB);
        sqlList.put("blog", SQL_BLOG);
        for (String type : sqlList.keySet()) {
            String[] news_count_today = queryForCountAndInfo(jdbcTemplateName, sqlList.get(type));

            System.out.println(type + ":\t[" + news_count_today[0] + "]\t[" + news_count_today[1] + "]\t[" + news_count_today[2] + "]");
        }
    }

    private void showTodayInsertTime(String jdbcTemplateName) {
        System.out.println(Ansi.ansi().eraseScreen().render(">>" + jdbcTemplateName + "  @|red today inserttime|@:----------------------"));
        System.out.println("type\tcount\tmin(inserttime)\tmax(inserttime)");
        String SQL_NEWS = "select count(*),min(inserttime),max(inserttime) from news_data where trunc(inserttime) = trunc(sysdate)";
        String SQL_BBS = "select count(*),min(insert_time),max(insert_time) from bbs_data where trunc(insert_time) = trunc(sysdate)";
        String SQL_WEIBO = "select count(*),min(insert_time),max(insert_time) from weibo_data where trunc(insert_time) = trunc(sysdate)";
        String SQL_WEIXIN = "select count(*),min(inserttime),max(inserttime) from weixin_data where trunc(inserttime) = trunc(sysdate)";
        String SQL_EB = "select count(*),min(insert_time),max(insert_time) from eb_data where trunc(insert_time) = trunc(sysdate)";
        String SQL_BLOG = "select count(*),min(inserttime),max(inserttime) from blog_data where trunc(inserttime) = trunc(sysdate)";
        HashMap<String, String> sqlList = new HashMap<String, String>();
        sqlList.put("news", SQL_NEWS);
        sqlList.put("bbs", SQL_BBS);
        sqlList.put("weibo", SQL_WEIBO);
        sqlList.put("weixin", SQL_WEIXIN);
        if (jdbcTemplateName.equals("tire")) sqlList.put("eb", SQL_EB);
        sqlList.put("blog", SQL_BLOG);
        for (String type : sqlList.keySet()) {
            String[] news_count_today = queryForCountAndInfo(jdbcTemplateName, sqlList.get(type));

            System.out.println(type + ":\t[" + news_count_today[0] + "]\t[" + news_count_today[1] + "]\t[" + news_count_today[2] + "]");
        }
    }

    private void showTodayPubTime(String jdbcTemplateName) {
        System.out.println(Ansi.ansi().eraseScreen().render(">>" + jdbcTemplateName + "  @|red today pubtime|@:----------------------"));
        System.out.println("type\tcount\tmin(pubtime)\tmax(pubtime)");
        String SQL_NEWS = "select count(*),min(pubtime),max(pubtime) from news_data where trunc(pubtime) = trunc(sysdate)";
        String SQL_BBS = "select count(*),min(pubtime),max(pubtime) from bbs_data where trunc(pubtime) = trunc(sysdate)";
        String SQL_WEIBO = "select count(*),min(pubtime),max(pubtime) from weibo_data where trunc(pubtime) = trunc(sysdate)";
        String SQL_WEIXIN = "select count(*),min(pubtime),max(pubtime) from weixin_data where trunc(pubtime) = trunc(sysdate)";
        String SQL_EB = "select count(*),min(pubtime),max(pubtime) from eb_data where trunc(pubtime) = trunc(sysdate)";
        String SQL_BLOG = "select count(*),min(pubtime),max(pubtime) from blog_data where trunc(pubtime) = trunc(sysdate)";
        HashMap<String, String> sqlList = new HashMap<String, String>();
        sqlList.put("news", SQL_NEWS);
        sqlList.put("bbs", SQL_BBS);
        sqlList.put("weibo", SQL_WEIBO);
        sqlList.put("weixin", SQL_WEIXIN);
        if (jdbcTemplateName.equals("tire")) sqlList.put("eb", SQL_EB);
        sqlList.put("blog", SQL_BLOG);
        for (String type : sqlList.keySet()) {
            String[] news_count_today = queryForCountAndInfo(jdbcTemplateName, sqlList.get(type));

            System.out.println(type + ":\t[" + news_count_today[0] + "]\t[" + news_count_today[1] + "]\t[" + news_count_today[2] + "]");
        }
    }

    private String[] queryForCountAndInfo(String jdbctemplateName, String sql) {

        JdbcTemplate jdbcTemplate = db.JDBCHelper.getJdbcTemplate(jdbctemplateName);
        return jdbcTemplate.queryForObject(sql, new RowMapper<String[]>() {
            public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
                String[] result = new String[3];
                result[0] = rs.getInt(1) + "";
                result[1] = rs.getString(2);
                result[2] = rs.getString(3);
                return result;
            }
        });
    }


    public static void main(String[] args) {
        Monitor monitor = new Monitor();
        monitor.show();
    }

}
