package crawler;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import data.NewsData;
import data.SearchKeyInfo;
import db.DataPersistence;
import db.JDBCHelper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import util.MD5;
import util.re;
import util.time;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guanxiaoda on 5/25/16.
 */
public class BaiduNewsCrawler extends BaseCrawler<NewsData> {
/* <配置> */

    final private static String CRAWLER_NAME = "news_baidu_search";
    final private static String DB_URL = "172.18.79.3:1521/orcl";
    final private static String DB_USER = "jinrong";
    final private static String DB_PASSWORD = "jinrong";
    private static String DB_TABLE = "news_data";

    final private static String URL_TEMPLATE = "http://news.baidu.com/ns?word=<KEYWORD>&pn=0&cl=2&ct=0&tn=news&rn=20&ie=utf-8&bt=0&et=0&rsv_page=1";

    final private static String RUN_MODE = "test";//test or run
//    final private static String RUN_MODE = "run";

    /* </配置> */


    private static Logger logger = LoggerFactory.getLogger(BaiduNewsCrawler.class);

    private static JdbcTemplate jdbcTemplate = null;
    private List<String> crawledItems = null; // crawled items


    public BaiduNewsCrawler(String crawlPath, boolean autoParse) throws UnsupportedEncodingException {
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
            if (RUN_MODE.equals("test")) break;
        }
    }


    @Override
    protected List<SearchKeyInfo> loadSearchKeyInfos() {
        String SEARCH_KEY_SQL = "SELECT id,category_code,keyword,site_id,site_name FROM search_keyword WHERE type LIKE '%;1;%' AND status = 2 ";
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
        logger.info("loading crawled items ...");
        DB_TABLE = RUN_MODE.equals("run") ? DB_TABLE : DB_TABLE + "_test";
        crawledItems = DataPersistence.loadItemsCrawled(jdbcTemplate, DB_TABLE);
        logger.info("crawled item count: {}", crawledItems.size());
    }


    @Override
    protected void paging(Page page, CrawlDatums crawlDatums) {
        String currPnStr = re.rExtract(page.getUrl(), "&pn=\\d+");
        String nextUrl;
        if (currPnStr != null) {
            int currPn = Integer.parseInt(currPnStr.replace("&pn=", ""));
            nextUrl = page.getUrl().replace(currPnStr, "&pn=" + (currPn + 20));
        } else {
            nextUrl = page.getUrl() + "&pn=20";
        }

        CrawlDatum next = new CrawlDatum(nextUrl);
        next.meta("category_code", page.getMetaData().get("category_code"));
        next.meta("search_keyword", page.getMetaData().get("search_keyword"));
        next.meta("dbid", page.getMetaData().get("dbid"));
        next.meta("site_id", page.getMetaData().get("site_id"));
        crawlDatums.add(next);

    }

    /**
     * 默认每页20条
     *
     * @return
     */
    @Override
    protected Elements parseList(Page page, CrawlDatums crawlDatums) {
        Elements elements = page.doc().select(".result");
        for (Element element : elements) {

            String categoryCodeFromMeta = page.getMetaData().get("category_code");
            String searchKeywordFromMeta = page.getMetaData().get("search_keyword");

            String title = element.select("h3>a").text();
            String source_time = element.select("div>p").text();
            String url = element.select("h3>a").attr("href");
            String brief = element.select("div").text();
            String sameNumStr = element.select("div>span>a:nth-child(1)").text();

//台海网络电视台  5小时前
            String source = source_time.split("  ")[0];
            String timeStr = source_time.split("  ")[1];
            String sameUrl = null;
            sameNumStr = re.rExtract(sameNumStr, "\\d+");
            if (sameNumStr != null)//如果没有相同新闻数则不抽取相同新闻url
                sameUrl = "http://news.baidu.com" + element.select("div>span>a:nth-child(1)").attr("href");

            if (crawledItems.contains(MD5.MD5(url))) {
                logger.info("skip requesting crawled item {}", url);
                continue;
            }


            CrawlDatum detail = new CrawlDatum(url);
            detail.meta("title", title);
            detail.meta("brief", brief);
            detail.meta("source", source);
            detail.meta("pubtimeRaw", timeStr);
            detail.meta("same_num", sameNumStr != null ? sameNumStr : "0");
            detail.meta("same_url", sameUrl != null ? sameUrl : "");

            detail.meta("category_code", categoryCodeFromMeta);
            detail.meta("search_keyword", searchKeywordFromMeta);


            crawlDatums.add(detail);


        }
        return elements;
    }

    @Override
    protected NewsData parseDetailPage(Page page) {
        String title = page.getMetaData().get("title");
        String brief = page.getMetaData().get("brief");
        String source = page.getMetaData().get("source");
        String pubtimeRaw = page.getMetaData().get("pubtimeRaw");
        String sameNumStr = page.getMetaData().get("same_num");
        String sameUrl = page.getMetaData().get("same_url").length() < 5 ? null : page.getMetaData().get("same_url");

        String search_keyword = page.getMetaData().get("search_keyword");
        int category_code = Integer.parseInt(page.getMetaData().get("category_code"));
        String content = null;
        //parse
        try {
            content = ContentExtractor.getContentByHtml(page.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
        }

        java.util.Date pubdate = time.timeFormat(pubtimeRaw);

        NewsData nsData = new NewsData();
        nsData.setTitle(title);
        nsData.setUrl(page.getUrl());
        nsData.setContent(content);
        nsData.setBrief(brief);
        nsData.setSource(source);
        if (pubdate != null) nsData.setPubtime(new Date(pubdate.getTime()));
        nsData.setSameNum(Integer.parseInt(sameNumStr));
        nsData.setSameUrl(sameUrl);

        nsData.setSearchKeyword(search_keyword);
        nsData.setCategoryCode(category_code);
        nsData.setMd5(MD5.MD5(nsData.getUrl()));

        return nsData;
    }

    @Override
    protected boolean isDetailPage(Page page) {
        return !isListPage(page);//因为跳转连接均为外链,无法统一判断,所以认为不是列表页的都是详情页
    }

    @Override
    protected boolean isListPage(Page page) {
        return page.getUrl().contains("news.baidu.com/ns");
    }

    @Override
    protected void saveData(NewsData newsData) {
        synchronized (this) {
            if (!crawledItems.contains(newsData.getMd5())) {
                if (DataPersistence.insertData(jdbcTemplate, newsData, DB_TABLE)) {
                    logger.info("data inserted. {}", newsData.getTitle());
                    crawledItems.add(newsData.getMd5());

                } else logger.error("insert failed. {}", newsData.getTitle());
            } else logger.info("drop old item. {}", newsData.getTitle());
        }
    }

    public void visit(Page page, CrawlDatums next) {
        if (isListPage(page)) {
            Elements elements = parseList(page, next);


            // paging
            if (elements.size() > 0) {

                paging(page, next);

            }

        } else if (isDetailPage(page)) {
            // parse item
            NewsData newsData = parseDetailPage(page);

            //save
            if (newsData.getTitle() != null && newsData.getContent() != null) saveData(newsData);


        }
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            BaiduNewsCrawler crawler = new BaiduNewsCrawler(CRAWLER_NAME, true);
            crawler.setThreads(1);
            crawler.setExecuteInterval(3 * 1000);
            crawler.start(5);

            Thread.sleep(1000 * 3600 * 2);
        }
    }
}
