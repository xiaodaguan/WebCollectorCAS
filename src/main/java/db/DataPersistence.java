package db;

import data.EbData;
import data.NewsData;
import data.SearchKeyInfo;
import data.WeiboData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.RowSet;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by guanxiaoda on 5/20/16.
 * 数据持久化
 */
public class DataPersistence {

    private static Logger logger = LoggerFactory.getLogger(DataPersistence.class);

    /**
     * 读取采集账号
     *
     * @param jdbcTemplate
     * @param accountTableName
     * @param type
     * @return
     */
    public static HashMap<String, String> loadUserPass(JdbcTemplate jdbcTemplate, String accountTableName, int type) {
        logger.info("loading crawler accounts...");
        String SQL = "select name, pass from " + accountTableName + " where site_id = " + type + " and valid = 1";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL);
        HashMap<String, String> result = new HashMap<String, String>();
        while (rs.next()) {
            result.put(rs.getString(1), rs.getString(2));
        }
        logger.info("{} accounts loaded.", result.size());
        return result;
    }

    /**
     * 读取采集过的items, 一般不需要重新实现
     *
     * @param jdbcTemplate
     * @param tableName
     * @return
     */
    public static List<String> loadItemsCrawled(JdbcTemplate jdbcTemplate, String tableName) {
        String QUERY_SQL = "SELECT MD5 FROM " + tableName;
        return jdbcTemplate.queryForList(QUERY_SQL, String.class);
    }

    public static List<SearchKeyInfo> loadSearchKeyInfos(JdbcTemplate jdbcTemplate, String searchKeywordTable, int type) {
        String SEARCH_KEY_SQL = "SELECT id,category_code,keyword,site_id,site_name FROM " + searchKeywordTable + " WHERE type LIKE '%;" + type + ";%' AND status = 2 ";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SEARCH_KEY_SQL);
        List<SearchKeyInfo> searchKeyInfos = new ArrayList<SearchKeyInfo>();
        try {
            searchKeyInfos = db.ORM.searchKeyInfoMapRow(rs);
            logger.info("read {} search keywords.\n{}", searchKeyInfos.size(), searchKeyInfos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchKeyInfos;
    }


    /**
     * 插入数据
     * 按照不同类型数据实现
     *
     * @return 成功true 否则false
     */
    public static boolean insertData() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        String INSERT_SQL = "";
        try {
            jdbcTemplate.update(INSERT_SQL, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {

                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean insertData(JdbcTemplate jdbctemplate, final EbData data, final String tableName) {
        String INSERT_SQL = "INSERT INTO " + tableName + "(" +
                "BRAND, TITLE, CONTENT, PRODUCT_IMG, INFO_IMG, " +
                "INSERT_TIME, DIAMETER, WIDTH, PRICE, SALE_NUM, " +
                "NAME, URL, SOURE, INFO, PUBTIME, " +
                "CATEGORY_CODE, SEARCH_KEYWORD, SITE_ID, YEAR_MONTH, OWNER, " +
                "MODEL, CODE_NUM, BRAND_FULL, COMPANY, MD5) VALUES(" +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?)";
        try {
            jdbctemplate.update(INSERT_SQL, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setString(1, data.getBrand());
                    ps.setString(2, data.getTitle());
                    ps.setString(3, data.getContent());
                    ps.setString(4, data.getProduct_img());
                    ps.setString(5, data.getInfo_img());

                    ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                    ps.setString(7, data.getDiameter());
                    ps.setString(8, data.getWidth());
                    ps.setString(9, data.getPrice());
                    ps.setString(10, data.getSale_num());

                    ps.setString(11, data.getName());
                    ps.setString(12, data.getUrl());
                    ps.setString(13, data.getSource());
                    ps.setString(14, data.getInfo());
                    ps.setTimestamp(15, data.getPubtime() != null ? new Timestamp(data.getPubtime().getTime()) : null);

                    ps.setInt(16, data.getCategoryCode());
                    ps.setString(17, data.getSearchKeyword());
                    ps.setInt(18, data.getSite_id());
                    ps.setString(19, data.getYear_month());
                    ps.setInt(20, data.getOwner());

                    ps.setString(21, data.getModel());
                    ps.setString(22, data.getCode_num());
                    ps.setString(23, data.getBrand_full());
                    ps.setString(24, data.getCompany());
                    ps.setString(25, data.getMd5());


                }
            });
            return true;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }

    }

    public static boolean insertData(JdbcTemplate jdbcTemplate, final NewsData data, final String tableName) {
        String INSERT_SQL = "INSERT INTO " + tableName + "(" +
                "BRIEF, CATEGORY_CODE, CONTENT, IMG_URL, INSERTTIME, " +
                "MD5, PUBTIME, SAME_NUM, SAME_URL, SEARCH_KEYWORD, " +
                "SOURCE, TITLE, URL) VALUES(" +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?)";
        try {
            jdbcTemplate.update(INSERT_SQL, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setString(1, data.getBrief());
                    ps.setInt(2, data.getCategoryCode());
                    ps.setString(3, data.getContent());
                    ps.setString(4, null);
                    ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

                    ps.setString(6, data.getMd5());
                    ps.setTimestamp(7, data.getPubtime() != null ? new Timestamp(data.getPubtime().getTime()) : null);
                    ps.setInt(8, data.getSameNum());
                    ps.setString(9, data.getSameUrl());
                    ps.setString(10, data.getSearchKeyword());

                    ps.setString(11, data.getSource());
                    ps.setString(12, data.getTitle());
                    ps.setString(13, data.getUrl());
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertData(JdbcTemplate jdbcTemplate, final WeiboData data, String dbTable) {
        String INSERT_SQL = "INSERT INTO " + dbTable +
                "(" +
                "URL, PUBTIME, INSERT_TIME, MD5, USER_ID, " +
                "COMMENT_COUNT, RTT_COUNT, MID, COMMENT_URL, RTT_URL, " +
                "AUTHOR, AUTHOR_URL, SEARCH_KEYWORD, CATEGORY_CODE, AUTHOR_IMG, " +
                "CONTENT, SOURCE, SITE_ID, IMG_URL, GPS, " +
                "LIKE_COUNT" +
                ") " +
                "VALUES" +
                "(" +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?" +
                ")";
        try {
            jdbcTemplate.update(INSERT_SQL, new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {

                    ps.setString(1, data.getUrl());
                    ps.setTimestamp(2, new Timestamp(data.getPubtime().getTime()));
                    ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    ps.setString(4, data.getMd5());
                    ps.setLong(5, data.getUserId());

                    ps.setInt(6, data.getCommentCount());
                    ps.setInt(7, data.getRttCount());
                    ps.setString(8, data.getMid());
                    ps.setString(9, data.getCommentUrl());
                    ps.setString(10, data.getRttUrl());

                    ps.setString(11, data.getAuthor());
                    ps.setString(12, data.getAuthorUrl());
                    ps.setString(13, data.getSearchKeyword());
                    ps.setInt(14, data.getCategoryCode());
                    ps.setString(15, data.getAuthorImg());

                    ps.setString(16, data.getContent());
                    ps.setString(17, data.getSource());
                    ps.setInt(18, data.getSiteId());
                    ps.setString(19, data.getImgUrl());
                    ps.setString(20, data.getGps());

                    ps.setInt(21, data.getLike_count());

                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
