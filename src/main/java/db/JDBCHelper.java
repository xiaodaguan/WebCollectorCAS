package db;

import data.SearchKeyInfo;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static JdbcTemplate createMysqlTemplate(String templateName, String url, String uname, String pwd, int initialSize, int maxActive){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(uname);
        dataSource.setPassword(pwd);
        dataSource.setMaxActive(maxActive);

        JdbcTemplate template = new JdbcTemplate(dataSource);
        templateMap.put(templateName,template);

        return template;
    }


    public static JdbcTemplate getJdbcTemplate(String templateName) {
        return templateMap.get(templateName);
    }

    public static void main(String[] args) throws IOException {
        JdbcTemplate template = createOracleTemplate("weixinExport","jdbc:oracle:thin:@172.18.79.3:1521/orcl","QDTRAMGRRED","qdtramgrred",5,100);
        List<Object> txts = template.query("select content from weixin_data where content is not null", new RowMapper<Object>() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                String txt = rs.getString(1);

                return txt;
            }
        });

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out/weixin.txt"),"utf-8"));
        for(Object txt:txts){
            String cont = txt.toString().replace("\n","。").replace(" ","");
            while(cont.contains("。。"))
                cont = cont.replace("。。","。");
            if(cont.length()>50)
            writer.write(cont);
            writer.write("\r\n");
            writer.flush();

        }
        writer.close();
        System.out.println("o.");
    }
}
