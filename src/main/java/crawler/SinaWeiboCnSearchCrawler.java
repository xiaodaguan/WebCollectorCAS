package crawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import data.SearchKeyInfo;
import data.WeiboData;
import db.DataPersistence;
import db.JDBCHelper;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guanxiaoda on 5/25/16.
 * 新浪微博移动版
 */
public class SinaWeiboCnSearchCrawler extends BaseCrawler<WeiboData> {

        /* <配置> */

    final private static String CRAWLER_NAME = "weibo_sina_search";
    final private static String DB_URL = "172.18.79.3:1521/orcl";
    final private static String DB_USER = "jinrong";
    final private static String DB_PASSWORD = "jinrong";
    private static String DB_TABLE = "weibo_data";

    private static String URL_TEMPLATE = "http://weibo.cn/search/mblog?hideSearchFrame=&keyword=<keyword>&page=1&vt=4";

    final private static String RUN_MODE = "test";//test or run
//    final private static String RUN_MODE = "run";

    /* </配置> */


    private static Logger logger = LoggerFactory.getLogger(SinaWeiboCnSearchCrawler.class);

    private static JdbcTemplate jdbcTemplate = null;
    private List<String> crawledItems = null; // crawled items

    public SinaWeiboCnSearchCrawler(String crawlPath, boolean autoParse) throws UnsupportedEncodingException {
        super(crawlPath, autoParse);
        jdbcTemplate = JDBCHelper.createOracleTemplate(CRAWLER_NAME, "jdbc:oracle:thin:@" + DB_URL, DB_USER, DB_PASSWORD, 5, 30);
        loadCrawledItems();
        List<SearchKeyInfo> searchKeyInfos = loadSearchKeyInfos();
        generateSeeds(searchKeyInfos);
    }

    @Override
    protected void generateSeeds(List<SearchKeyInfo> searchKeyInfos) throws UnsupportedEncodingException {
        for (SearchKeyInfo ski : searchKeyInfos) {
            String url = URL_TEMPLATE.replace("<KEYWORD>", URLEncoder.encode(ski.getKeyword(), "utf-8"));
            CrawlDatum cd = new CrawlDatum(url);

            cd.meta("dbid", String.valueOf(ski.getDbOriginalId()));
            cd.meta("category_code", String.valueOf(ski.getCategory_code()));
            cd.meta("search_keyword", ski.getKeyword());
            cd.meta("site_id", ski.getSite_id());
            cd.meta("site_name", ski.getSite_name());

            this.addSeed(cd);
        }
    }

    @Override
    protected List<SearchKeyInfo> loadSearchKeyInfos() {
        String SEARCH_KEY_SQL = "SELECT id,category_code,keyword,site_id,site_name FROM search_keyword WHERE type LIKE '%;7;%' AND status = 2 ";
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

    @Override
    protected void loadCrawledItems() {
        logger.info("loading crawled items...");
        DB_TABLE = RUN_MODE.equals("run") ? DB_TABLE : DB_TABLE + "_test";
        crawledItems = DataPersistence.loadItemsCrawled(jdbcTemplate, DB_TABLE);
        logger.info("crawled item count: {}", crawledItems.size());
    }

    @Override
    protected WeiboData parseDetailPage(Page page) {
        // TODO: 5/25/16
        return null;
    }

    @Override
    protected void paging(Page page, CrawlDatums crawlDatums) {
// TODO: 5/25/16
    }

    @Override
    protected Elements parseList(Page page, CrawlDatums crawlDatums) {
        // TODO: 5/25/16
        return null;
    }

    @Override
    protected boolean isDetailPage(Page page) {
// TODO: 5/25/16
        return false;
    }

    @Override
    protected boolean isListPage(Page page) {

        return page.getUrl().contains("weibo.cn/search/");
    }

    @Override
    protected void saveData(WeiboData weiboData) {
        // TODO: 5/25/16
    }

    public void visit(Page page, CrawlDatums next) {
        //url, pubtime, insert_time, md5, user_id, comment_count, rtt_count, mid, comment_url, rtt_url, author, author_url, search_keyword, category_code, author_img, content, source, img_url, gps, like_count
        if (isListPage(page)) {

            Elements elements = parseList(page, next);


            // paging
            if (elements.size() > 0) {

                paging(page, next);

            }

        } else if (isDetailPage(page)) {
            // parse item
            WeiboData wbData = parseDetailPage(page);

            //save
            if (wbData.getPubtime() != null && wbData.getContent() != null) saveData(wbData);


        }
    }

    public static void main(String[] args) throws Exception {
        while(true) {
            logger.info("cycle start...");
            SinaWeiboCnSearchCrawler crawler = new SinaWeiboCnSearchCrawler(CRAWLER_NAME,true);
            crawler.setThreads(1);
            crawler.setExecuteInterval(25*1000);
            crawler.start(31);

            logger.info("wait...");
            long SLEEP = 1000*3600*2;
            Thread.sleep(SLEEP);
        }
    }
}
