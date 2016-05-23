package db;

import data.EbData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by guanxiaoda on 5/20/16.
 * 数据持久化
 */
public class DataPersistence {


    /**
     * 读取采集过的items, 一般不需要重新实现
     * @param jdbcTemplate
     * @param tableName
     * @return
     */
    public static List<String> loadItemsCrawled(JdbcTemplate jdbcTemplate, String tableName) {
        String QUERY_SQL = "SELECT MD5 FROM " + tableName;
        return jdbcTemplate.queryForList(QUERY_SQL, String.class);
    }


    /**
     * 插入数据
     * 按照不同类型数据实现
     * @return 成功true 否则false
     */
    public static boolean insertData() {
        return false;
    }

    /**
     * EbData
     * @param jdbctemplate
     * @param data
     * @param tableName
     * @return
     */
    public static boolean insertData(JdbcTemplate jdbctemplate, final EbData data, final String tableName) {
        String INSERT_SQL = "INSERT INTO "+tableName+"(" +
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

                    ps.setInt(16, data.getCategory_code());
                    ps.setString(17, data.getSearch_keyword());
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

}
