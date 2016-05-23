package db;

import data.SearchKeyInfo;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;

/**
 * Created by guanxiaoda on 5/20/16.
 *
 */
public class JDBCHelper {

    /*可能需要多个数据源     */
    public static HashMap<String, JdbcTemplate> templateMap = new HashMap<String, JdbcTemplate>();

    /**
     * 创建jdbctemplate oracle
     * @param templateName
     * @param url
     * @param uname
     * @param pwd
     * @param initialSize
     * @param maxActive
     * @return
     */
    public static JdbcTemplate createOracleTemplate(String templateName, String url, String uname, String pwd, int initialSize, int maxActive) {

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUrl(url);
        dataSource.setUsername(uname);
        dataSource.setPassword(pwd);
        dataSource.setMaxActive(maxActive);

        JdbcTemplate template = new JdbcTemplate(dataSource);
        templateMap.put(templateName, template);

        return template;
    }


    public static JdbcTemplate getJdbcTemplate(String templateName) {
        return templateMap.get(templateName);
    }


}
