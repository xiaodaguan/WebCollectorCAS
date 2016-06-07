package crawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import crawler.smedia.EbSearchJd;
import data.SearchKeyInfo;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by guanxiaoda on 5/24/16.
 *
 */
public abstract class BaseCrawler<T> extends BreadthCrawler {

    /* <配置> */

    final private static String CRAWLER_NAME = "";
    final private static String DB_URL = "";//172.18.79.3:1521/orcl
    final private static String DB_USER = "";//tire
    final private static String DB_PASSWORD = "";//tire2014
    private static String DB_TABLE = "";//eb_data
    private static String DB_SEARCHKEYWORD_TABLE = "";

    final private static String URL_TEMPLATE = "";//http://search.jd.com/s_new.php?keyword=<KEYWORD>&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&offset=3&page=1

    final private static String RUN_MODE = "";//test or run
    //    final private static String RUN_MODE = "run";

    /* </配置> */

    private static Logger logger = LoggerFactory.getLogger(EbSearchJd.class);

    private static JdbcTemplate jdbcTemplate = null;
    private List<String> crawledItems = null; // crawled items

    public BaseCrawler(String crawlPath, boolean autoParse) throws UnsupportedEncodingException {
        super(crawlPath, autoParse);

    }

    protected abstract CrawlDatums generateSeeds(List<SearchKeyInfo> searchKeyInfos) throws UnsupportedEncodingException;


    protected abstract List<String> loadCrawledItems();


    protected abstract T parseDetailPage(Page page);

    protected abstract void paging(Page page, CrawlDatums crawlDatums);

    protected abstract Elements parseList(Page page, CrawlDatums crawlDatums);

    protected abstract boolean isDetailPage(Page page);

    protected abstract boolean isListPage(Page page);

    protected abstract void saveData(T t);

}
