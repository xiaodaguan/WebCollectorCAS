package crawler.smedia;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import crawler.BaseCrawler;
import crawler.downloader.SeleniumWebDriverManager;
import data.WeixinData;
import org.jsoup.select.Elements;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by guanxiaoda on 6/7/16.
 *
 */
public class WeixinSearchSogou extends BaseCrawler<WeixinData> {
    /* <配置> */

    final private static String CRAWLER_NAME = WeiboSearchSina.class.getName();
    final private static String DB_URL = "172.18.79.3:1521/orcl";
    final private static String DB_USER = "jinrong";
    final private static String DB_PASSWORD = "jinrong";
    private static String DB_TABLE = "weixin_data";
    private static String DB_SEARCHKEYWORD_TABLE = "search_keyword";

    private static String URL_TEMPLATE = "";

    final private static String RUN_MODE = "test";//test or run
//    final private static String RUN_MODE = "run";

    /* </配置> */


    private static Logger logger = LoggerFactory.getLogger(WeixinSearchSogou.class);

    private static JdbcTemplate jdbcTemplate = null;

    private List<String> crawledItems = null; // crawled items


    public WeixinSearchSogou(String crawlPath, boolean autoParse) throws UnsupportedEncodingException {
        super(crawlPath, autoParse);

        SeleniumWebDriverManager driverManager = SeleniumWebDriverManager.getInstance();
        PhantomJSDriver driver = driverManager.getPhantomJSDriver();
    }

    @Override
    protected CrawlDatums generateSeeds(List list) throws UnsupportedEncodingException {
        return null;
    }

    @Override
    protected List<String> loadCrawledItems() {
        return null;
    }

    @Override
    protected WeixinData parseDetailPage(Page page) {
        return null;
    }

    @Override
    protected void paging(Page page, CrawlDatums crawlDatums) {

    }

    @Override
    protected Elements parseList(Page page, CrawlDatums crawlDatums) {
        return null;
    }

    @Override
    protected boolean isDetailPage(Page page) {
        return false;
    }

    @Override
    protected boolean isListPage(Page page) {
        return false;
    }

    @Override
    protected void saveData(WeixinData weixinData) {

    }


    public void visit(Page page, CrawlDatums next) {

    }
}
