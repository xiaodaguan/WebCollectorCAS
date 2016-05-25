package crawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import data.EbData;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by guanxiaoda on 5/19/16.
 * <p/>
 * 列表页可以采集移动版orPC版
 * 移动版http://so.m.jd.com/ware/search.action?&keyword=%E8%BD%AE%E8%83%8E
 * PC版http://search.jd.com/Search?keyword=%E8%BD%AE%E8%83%8E
 * <p/>
 * 商品详情全部从详情页采集
 * <p/>
 * 由于爬虫是[列表页->详情页]模式,详情页不会获取到后续跟进url
 * 因此depth-1即是爬取页数,如果想爬取n页,将DEPTH设置为n+1即可
 * <p/>
 * 解析列表页时,会按照url过滤掉曾经爬取过的item
 * <p/>
 * 入库时,会再次判断item之前是否曾经爬取过
 * 使用MD5作为信息指纹,有些站点,item.url是动态生成的,如果想要去重则需要重新定义生成MD5的方法,例如去掉url中的动态参数(时间戳等)
 * <p/>
 * 数据库初始连接数没有指定,最大连接数30,按需更改
 */
public class JdSearchCrawler extends BaseCrawler<EbData> {

    /* <配置> */

    final private static String CRAWLER_NAME = "eb_jd_search";
    final private static String DB_URL = "172.18.79.3:1521/orcl";
    final private static String DB_USER = "tire";
    final private static String DB_PASSWORD = "tire2014";
    private static String DB_TABLE = "eb_data";

    private static String URL_TEMPLATE = "http://search.jd.com/s_new.php?keyword=<KEYWORD>&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&offset=3&page=1";

    //    final private static String RUN_MODE = "test";//test or run
    final private static String RUN_MODE = "run";

    /* </配置> */

    private static Logger logger = LoggerFactory.getLogger(JdSearchCrawler.class);

    private static JdbcTemplate jdbcTemplate = null;
    private List<String> crawledItems = null; // crawled items

    public JdSearchCrawler(String crawlPath, boolean autoParse) throws UnsupportedEncodingException {
        super(crawlPath, autoParse);
        jdbcTemplate = JDBCHelper.createOracleTemplate(CRAWLER_NAME, "jdbc:oracle:thin:@" + DB_URL, DB_USER, DB_PASSWORD, 5, 30);


        loadCrawledItems();
        List<SearchKeyInfo> searchKeyInfos = loadSearchKeyInfos();
        generateSeeds(searchKeyInfos);
    }


    @Override
    protected void generateSeeds(List<SearchKeyInfo> searchKeyInfos) throws UnsupportedEncodingException {/*generate seeds*/
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
    protected List<SearchKeyInfo> loadSearchKeyInfos() {/*load search keywords*/
        String SEARCH_KEY_SQL = "SELECT id,category_code,keyword,site_id,site_name FROM search_keyword WHERE type LIKE '%;13;%' AND status = 2 ";
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
    protected void loadCrawledItems() {/*load old items*/
        logger.info("loading crawled items...");
        DB_TABLE = RUN_MODE.equals("run") ? DB_TABLE : DB_TABLE + "_test";
        crawledItems = DataPersistence.loadItemsCrawled(jdbcTemplate, DB_TABLE);
        logger.info("crawled item count: {}", crawledItems.size());
    }

    public void visit(Page page, CrawlDatums crawlDatums) {
        if (isListPage(page)) {
            Elements elements = parseList(page, crawlDatums);


            // paging
            if (elements.size() > 0) {

                paging(page, crawlDatums);

            }

        } else if (isDetailPage(page)) {
            // parse item
            EbData ebData = parseDetailPage(page);


            saveData(ebData);


        }

    }

    @Override
    protected EbData parseDetailPage(Page page) {// from list page
        String price = page.getMetaData().get("price");
        String sale_num = page.getMetaData().get("commCount");
        String search_keyword = page.getMetaData().get("search_keyword");
        int category_code = Integer.parseInt(page.getMetaData().get("category_code"));

        // parse
        String brand = page.doc().select("#root-nav div > span+span >a[clstag]").text();
        String title = page.doc().select("head>title").text();
        String product_img = page.doc().select("div.p-img > img").attr("src");
        String diameter = "尺寸:0寸";
        String width = "胎面宽度:0";

        String info;
        String url = page.getUrl();
        String md5 = MD5.MD5(url);
        String code_num = "";
        String model = "";
        String year_month = getYearMonth();

        Elements params = page.doc().select("#parameter2 > li");
        StringBuilder stringBuilder = new StringBuilder();
        for (Element param : params) {
            String paramStr = param.text();
            if (paramStr.contains("尺寸")) diameter = param.attr("title");
            else if (paramStr.contains("宽度")) width = param.attr("title");
            else if (paramStr.contains("商品名称")) model = param.attr("title");
            else if (paramStr.contains("编号")) code_num = param.attr("title");

            stringBuilder.append(paramStr).append(";");
        }
        info = stringBuilder.toString();

        EbData ebData = new EbData();
        fillData(price, sale_num, search_keyword, category_code, brand, title, product_img, diameter, width, info, url, md5, code_num, model, year_month, ebData);
        return ebData;
    }

    @Override
    protected void paging(Page page, CrawlDatums crawlDatums) {
        CrawlDatum crawlDatum = new CrawlDatum(getPagingUrl(page.getUrl()));
        crawlDatum.meta("category_code", page.getMetaData().get("category_code"));
        crawlDatum.meta("search_keyword", page.getMetaData().get("search_keyword"));
        crawlDatum.meta("dbid", page.getMetaData().get("dbid"));
        crawlDatum.meta("site_id", page.getMetaData().get("site_id"));
        crawlDatums.add(crawlDatum);
    }

    @Override
    protected Elements parseList(Page page, CrawlDatums crawlDatums) {// parse list
        Elements elements = page.doc().select(" div.gl-i-wrap");
        for (Element element : elements) {

            String priceFromList = element.select("div.p-price strong[data-price]").attr("data-price");
            String commCountFromList = element.select("div.p-commit a[href]").text();
            String categoryCodeFromMeta = page.getMetaData().get("category_code");
            String searchKeywordFromMeta = page.getMetaData().get("search_keyword");

            String url = formatUrl(element.select("div.p-img>a").attr("href"));
            if (crawledItems.contains(MD5.MD5(url))) {
                logger.info("skip crawled item {}", url);
                continue;
            }

            CrawlDatum crawlDatum = new CrawlDatum(url);

            crawlDatum.meta("price", priceFromList);
            crawlDatum.meta("commCount", commCountFromList);
            crawlDatum.meta("category_code", categoryCodeFromMeta);
            crawlDatum.meta("search_keyword", searchKeywordFromMeta);

            crawlDatums.add(crawlDatum);
        }
        return elements;
    }

    @Override
    protected boolean isDetailPage(Page page) {return page.getUrl().contains("item.jd.com");}

    @Override
    protected boolean isListPage(Page page) {return page.getUrl().contains("search.jd.com");}

    @Override
    protected void saveData(EbData ebData) {
        synchronized (this) {
            if (!crawledItems.contains(ebData.getMd5())) {
                if (DataPersistence.insertData(jdbcTemplate, ebData, DB_TABLE)) {
                    logger.info("data inserted. {}", ebData.getTitle());
                    crawledItems.add(ebData.getMd5());
                } else logger.error("insert failed. {}", ebData.getTitle());
            } else {
                logger.info("drop old item. {}", ebData.getTitle());
            }
        }
    }

    protected void fillData(String price, String sale_num, String search_keyword, int category_code, String brand, String title, String product_img, String diameter, String width, String info, String url, String md5, String code_num, String model, String year_month, EbData ebData) {
        ebData.setUrl(url);
        ebData.setMd5(md5);
        ebData.setBrand(brand);
        ebData.setTitle(title);
        ebData.setProduct_img(product_img);
        ebData.setDiameter(diameter);
        ebData.setWidth(width);
        ebData.setPrice(price);
        ebData.setSale_num(sale_num);
        ebData.setInfo(info);
        ebData.setSearchKeyword(search_keyword);
        ebData.setCategory_code(category_code);
        ebData.setModel(model);
        ebData.setCode_num(code_num);
        ebData.setYear_month(year_month);
    }

    /**
     * 获取下一页url
     *
     * @param url 当前url
     * @return 下一页url
     */
    protected String getPagingUrl(String url) {
        String nextUrl;
        String pattern = "&page=\\d+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(url);
        if (m.find()) {
            String currPageStr = m.group().replace("&page=", "");
            int currPage = Integer.parseInt(currPageStr);
            nextUrl = url.replace("&page=" + currPage, "&page=" + (currPage + 1));
        } else nextUrl = url + "&page=2";
        return nextUrl;
    }

    /**
     * 当前时间前推一个月
     *
     * @return date
     */
    private String getYearMonth() {
        String year_month;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        year_month = sdf.format(new Date(cal.getTimeInMillis()));
        return year_month;
    }

    /**
     * add http*
     *
     * @param url url
     * @return formatted
     */
    private String formatUrl(String url) {
        url = url.startsWith("http") ? url : "http:" + url;
        return url;
    }


    public static void main(String[] args) throws Exception {
        while (true) {
            logger.info("start...");

            JdSearchCrawler jdsCrawler = new JdSearchCrawler(CRAWLER_NAME, true);
            jdsCrawler.setThreads(1);
            jdsCrawler.setExecuteInterval(20 * 1000);
            jdsCrawler.start(1);

            logger.info("wait...");
            long SLEEP_TIME = 1000 * 3600 * 2;
            Thread.sleep(SLEEP_TIME);

        }
    }
}
