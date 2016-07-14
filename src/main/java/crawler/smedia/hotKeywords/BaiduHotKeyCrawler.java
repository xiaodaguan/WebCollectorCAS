package crawler.smedia.hotKeywords;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import data.HotkeyData;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import util.Re;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by guanxiaoda on 7/8/16.
 */
public class BaiduHotKeyCrawler extends BreadthCrawler {
    private static Logger logger = LoggerFactory.getLogger(BaiduHotKeyCrawler.class);
    private final static String ORACLEURL = "jdbc:oracle:thin:@172.18.79.3:1521/orcl";
    private final static String ORACLEUSER = "TOPSEARCH";
    private final static String ORACLEPASS = "TOPSEARCH";

    private final static JdbcTemplate JDBC_TEMPLATE = db.JDBCHelper.createOracleTemplate(BaiduHotKeyCrawler.class.getName(), ORACLEURL, ORACLEUSER, ORACLEPASS, 5, 30);
    private final static String DATA_TABLE = "search_keyword";
    private final static String CATE_TABLE = "category_scheme";

    private static List<String> crawledItems = null;
    private static Map<String, Integer> cateCode = new HashMap<String, Integer>();

    public BaiduHotKeyCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        this.addSeed("http://top.baidu.com/category?c=513");

        crawledItems = JDBC_TEMPLATE.queryForList("select distinct(keyword) from " + DATA_TABLE, String.class);
        logger.info("{} crawled items.", crawledItems.size());

        List rows = JDBC_TEMPLATE.queryForList("select id,name from " + CATE_TABLE);
        int count = 0;
        for (Object row : rows) {
            Map map = (Map) row;
            cateCode.put((String) map.get("name"), ((BigDecimal) map.get("id")).intValue());
            count++;
        }
        logger.info("{} cates.", count);


    }

    public void visit(Page page, CrawlDatums next) {
        if ("http://top.baidu.com/category?c=513".equals(page.getUrl())) {
            Elements list = page.select("#sub-nav > li > a");
            for (Element el : list) {
                String cateUrl = el.attr("href");
                if (cateUrl == null) {
                    logger.error("分类url抓取失败！");
                    return;
                }
                if (!cateUrl.startsWith("http://")) cateUrl = "http://top.baidu.com/" + cateUrl;
                next.add(cateUrl);
            }
        } else if (page.getUrl().contains("/buzz")) {
            Element cateE = page.select("#main > div.mainBody > div > div > h2").first();
            String cateStr = cateE.text();
            int categoryCode = cateCode.containsKey(cateStr) ? cateCode.get(cateStr) : 0;

            Elements keywordList = page.select("td.keyword > a.list-title");

            List<HotkeyData> results = new ArrayList<HotkeyData>();
            for (Element el : keywordList) {
                String keyword = el.text();
                String url = el.attr("href");
                String detailUrl = el.attr("href_top");

                HotkeyData data = new HotkeyData();
                data.setKeyword(keyword);
                data.setUrl(url);
                data.setTopUrl(detailUrl);
                data.setCate(categoryCode);
                results.add(data);
            }

            Elements indexList = page.select("tbody > tr > td.last > span");
            if (indexList.size() != results.size()) {
                logger.error("列表长度不一致: item/指数 {}/{}", results.size(), indexList.size());
            }
            int i = 0;
            for (Element el : indexList) {
                String value = el.text();
                if (Re.rMatches(value, "\\d+")) results.get(i).setSearchIndex(Integer.parseInt(value));

                if (el.hasClass("icon-rise")) results.get(i).setTrend(1);
                else if (el.hasClass("icon-fall")) results.get(i).setTrend(-1);
                else results.get(i).setTrend(0);

                results.get(i).setCrawlDate(new Date(System.currentTimeMillis()));
                i++;

            }


            logger.info("page parsed: --{} --{}", page.select("title").text(), page.getUrl());

            /**
             * items
             */
            int count = 0;
            for (final HotkeyData data : results) {
                if (crawledItems.contains(data.getKeyword())) {
                    logger.error("drop old item {}", data.getKeyword());
                    continue;
                }
                count++;
                JDBC_TEMPLATE.update("insert into " + DATA_TABLE + "(keyword, search_index, propose_time, category_code, status, type) values(?,?,?,?,?,?)", new PreparedStatementSetter() {
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setString(1, data.getKeyword());
                        ps.setInt(2, data.getSearchIndex());
                        ps.setTimestamp(3, new Timestamp(data.getCrawlDate().getTime()));
                        ps.setInt(4, data.getCate());
                        ps.setInt(5, 2);
                        ps.setString(6, ";1;2;4;8;");
                    }
                });
            }

            logger.info("page items inserted to {}/{}:[{}]", ORACLEURL, DATA_TABLE, count);
        }

    }


    public static void main(String[] args) throws Exception {
        BaiduHotKeyCrawler hotKeyCrawler = new BaiduHotKeyCrawler("BaiduHotKey", false);
        hotKeyCrawler.setThreads(1);
        hotKeyCrawler.setExecuteInterval(1);
        hotKeyCrawler.start(10);
    }
}
