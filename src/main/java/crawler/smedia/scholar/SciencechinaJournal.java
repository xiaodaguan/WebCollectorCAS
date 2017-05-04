package crawler.smedia.scholar;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import crawler.BaseCrawler;
import crawler.smedia.EbSearchJd;
import data.EbData;
import data.ScholarData;
import data.SearchKeyInfo;
import db.DataPersistence;
import db.JDBCHelper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import util.MD5;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guanxiaoda on 17/3/20.
 */
public class SciencechinaJournal extends BaseCrawler<ScholarData> {
    public static void main(String[] args) throws Exception {
        logger.info("start...");

        SciencechinaJournal sjCrawler = new SciencechinaJournal(CRAWLER_NAME, true);
        sjCrawler.setThreads(1);
        sjCrawler.setExecuteInterval(15 * 1000);
        sjCrawler.start(999999999);


    }

    final private static String CRAWLER_NAME = SciencechinaJournal.class.getName();
    final private static String DB_URL = "172.18.79.3:3306/2017-02-17-xuekefazhan?characterEncoding=utf-8";
    final private static String DB_USER = "root";
    final private static String DB_PASSWORD = "root";
    private static String DB_TABLE = "journal_data";
    final private static String URL_TEMPLATE = "http://sciencechina.cn/search_outline_sou.jsp";


    private List<String> searchKeys = new ArrayList<String>() {
    };

    //        final private static String RUN_MODE = "test";//test or run
    final private static String RUN_MODE = "run";

    private static Logger logger = LoggerFactory.getLogger(SciencechinaJournal.class);
    private static JdbcTemplate jdbcTemplate = null;
    private List<String> crawledItems = null; // crawled items

    public SciencechinaJournal(String crawlPath, boolean autoParse) throws UnsupportedEncodingException {
        super(crawlPath, autoParse);
        jdbcTemplate = JDBCHelper.createMysqlTemplate(CRAWLER_NAME, "jdbc:mysql://" + DB_URL, DB_USER, DB_PASSWORD, 5, 30);

        crawledItems = loadCrawledItems();

        searchKeys.add("控制理论与应用#2013#2016");
//        searchKeys.add("控制工程#2013#2016");
//        searchKeys.add("信息与控制#2013#2016");
//        searchKeys.add("中国科学F辑#2013#2016");
//        searchKeys.add("模式识别与人工智能#2013#2016");
//
//        searchKeys.add("自动化学报#2013#2016");
//        searchKeys.add("计算机学报#2013#2016");
//        searchKeys.add("仪器仪表学报#2013#2016");
//        searchKeys.add("传感技术学报#2013#2016");
//        searchKeys.add("计算机集成制造系统#2013#2016");
//
//        searchKeys.add("计算机测量与控制#2013#2016");
//        searchKeys.add("测控技术#2013#2016");
//        searchKeys.add("传感器与微系统#2013#2016");
//        searchKeys.add("系统工程理论与实践#2013#2016");
//        searchKeys.add("控制与决策#2013#2016");
//
//        searchKeys.add("系统工程学报#2013#2016");
//        searchKeys.add("中国惯性技术学报#2013#2016");
//        searchKeys.add("宇航学报#2013#2016");
//        searchKeys.add("航空学报#2013#2016");
//        searchKeys.add("中国空间科学与技术#2013#2016");
//
//        searchKeys.add("航天控制#2013#2016");
//        searchKeys.add("空间控制技术与应用#2013#2016");
//        searchKeys.add("导航与控制#2013#2016");

        CrawlDatums seeds = generateSeeds(searchKeys);
        this.addSeed(seeds);

    }

    protected CrawlDatums generateSeeds(List list) throws UnsupportedEncodingException {
        HttpRequest indexRequest = null;
        String indexCookie = "";
        try {
            indexRequest = new HttpRequest("http://sciencechina.cn/index.jsp");
            HttpResponse response = indexRequest.getResponse();
            List<String> cookies = response.getHeader("Set-Cookie");
            for (String cookie : cookies) {
                indexCookie += cookie.split(";")[0] + ";";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        CrawlDatums seeds = new CrawlDatums();
        for (String ski : searchKeys) {
            String url = URL_TEMPLATE + "?key=" + ski;
            String keyword = ski.split("#")[0];
            String from = ski.split("#")[1];
            String to = ski.split("#")[2];
            CrawlDatum cd = new CrawlDatum(url);
            cd.meta("searchKeyInfo", ski);
            cd.meta("method", "POST");
            cd.meta("outputData", "searchword=%28JOURNAL_NAME_GF_EX%3A%22" + keyword +// keyword
                    "%22+AND+YEAR%3A%5B" + from +// from
                    "+TO+" + to +// to
                    "%5D%29+AND+CITE%3A1+&searchword_tra=%E5%88%8A%E5%90%8D_%3D%22" + keyword +// keyword
                    "%22+*+%E5%B9%B4%E4%BB%BD%3D" + from +// from
                    "+-+" + to +// to
                    "&searchmode=1&dbtype=1&port=1&field_searchword=JOURNAL_NAME_GF_EX%3A%22" + keyword +// keyword
                    "%22+AND+YEAR%3A%5B" + from +// from
                    "+TO+" + to +// to
                    "%5D&Submit=%E6%A3%80%E7%B4%A2");


//            cd.meta("outputData","searchword=%28JOURNAL_NAME_GF_EX%3A%22%E8%87%AA%E5%8A%A8%E5%8C%96%E5%AD%A6%E6%8A%A5%22%29+AND+CITE%3A1+&searchword_tra=%E5%88%8A%E5%90%8D_%3D%22%E8%87%AA%E5%8A%A8%E5%8C%96%E5%AD%A6%E6%8A%A5%22&searchmode=1&dbtype=1&port=1&field_searchword=JOURNAL_NAME_GF_EX%3A%22%E8%87%AA%E5%8A%A8%E5%8C%96%E5%AD%A6%E6%8A%A5%22&Submit=%E6%A3%80%E7%B4%A2");
            cd.meta("cookie", indexCookie);
            cd.meta("referer", "http://sciencechina.cn/advancedsearch.jsp");
//            cd.meta("ua","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3041.0 Safari/537.36");
            cd.meta("contentType", "application/x-www-form-urlencoded");
            cd.meta("pageType", "LIST");


            seeds.add(cd);
        }
        logger.info("[generage seeds]count:" + seeds.size());
        return seeds;
    }

    protected List<String> loadCrawledItems() {
        logger.info("loading crawled items...");
        DB_TABLE = RUN_MODE.equals("run") ? DB_TABLE : DB_TABLE + "_test";
        List<String> crawled = DataPersistence.loadItemsCrawled(jdbcTemplate, DB_TABLE);
        logger.info("crawled item count: {}", crawled.size());
        return crawled;
    }

    @Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        String cookie = crawlDatum.meta("cookie");

        HttpRequest preSearch = new HttpRequest("http://sciencechina.cn/advancedsearch.jsp");
        preSearch.setMethod("GET");

        if (cookie != null) {
            preSearch.setCookie(cookie);
        }

        if (crawlDatum.meta("pageType").equals("LIST")) {
            HttpResponse preSearchRes = preSearch.getResponse();
            String preSearchContent = new String(preSearchRes.getContent());
        }


        HttpRequest request = new HttpRequest(crawlDatum.getUrl());
        request.setMethod(crawlDatum.meta("method"));

        if (cookie != null) {
            request.setCookie(cookie);
        }
        String outputData = crawlDatum.meta("outputData");
        if (outputData != null) {
            request.setOutputData(outputData.getBytes("utf-8"));
        }
        String referer = crawlDatum.meta("referer");
        if (referer != null) {
            request.addHeader("Referer", referer);
        }
        String ua = crawlDatum.meta("ua");
        if (ua != null) {
            request.setUserAgent(ua);
        }
        String contentType = crawlDatum.meta("contentType");
        if (contentType != null) {
            request.addHeader("Content-Type", contentType);
        }
        return request.getResponse();
    }


    protected void paging(Page page, CrawlDatums crawlDatums) {

    }


    protected boolean isDetailPage(Page page) {
        if (page.getUrl().contains("detail.jsp")) return true;
        else return false;
    }

    protected boolean isListPage(Page page) {
        if (page.getUrl().contains("search_outline_sou")) return true;
        else return false;
    }

    @Override
    protected void saveData(ScholarData sd) {
        synchronized (this) {
            if (!crawledItems.contains(sd.getMd5())) {
                if (DataPersistence.insertData(jdbcTemplate, sd, DB_TABLE)) {
                    logger.info("data inserted. {}", sd.getTitle());
                    crawledItems.add(sd.getMd5());
                } else logger.error("insert failed. {}", sd.getTitle());
            } else {
                logger.info("drop old item. {}", sd.getTitle());
            }
        }
    }

    public void visit(Page page, CrawlDatums next) {
        String content = new String(page.getResponse().getContent());

        if (isListPage(page)) {

            if (content.contains("共检索到") && content.contains("条结果")) logger.info("list page response ok.");
            else {
                logger.warn("list page response failed, maybe no search result.");
                return;
            }

            Elements elements = parseList(page, next);


            //get set-cookie from response and set cookie for detail page crawl datum

            // paging
            if (elements != null) {

                paging(page, next);

            }

        } else if (isDetailPage(page)) {
            // parse item
            ScholarData scholarData = parseDetailPage(page);

            //save
            if (scholarData.getTitle() != null && scholarData.getUrl() != null) saveData(scholarData);

            logger.info("[detail] detail page saved.");
        }
    }


    protected Elements parseList(Page page, CrawlDatums crawlDatums) {

        String listCookie = page.getCrawlDatum().meta("cookie");
        String ski = page.getCrawlDatum().meta("searchKeyInfo");
        String keyword = ski.split("#")[0];
        String from = ski.split("#")[1];
        String to = ski.split("#")[2];

//        List<String> cookies = page.getResponse().getHeader("Set-Cookie");
//        for (String cookie: cookies
//                ) {
//            listCookie += cookie.split(";")[0]+";";
//        }


        Elements elements = page.doc().select("#main_tit2 > h1 > strong");// a row
        String totalStr = elements.get(0).text();

        int total = Integer.parseInt(totalStr);
        for (int i = 0; i < total; i++) {

//
//            String priceFromList = element.select("div.p-price strong[data-price]").attr("data-price");
//            String commCountFromList = element.select("div.p-commit a[href]").text();
//            String categoryCodeFromMeta = page.getMetaData().get("category_code");
//            String searchKeywordFromMeta = page.getMetaData().get("search_keyword");
//
////            String url = formatUrl(element.select("div.p-img>a").attr("href"));
//            if (crawledItems.contains(MD5.MD5(url))) {
//                logger.info("skip crawled item {}", url);
//                continue;
//            }
//
//


            /**
             *       详情页POST属性
             *       header: 1. referer  2. cookie  3. Content-Type
             *       body:
             *       page=&searchmode=&dbtype=&searchword=%28JOURNAL_NAME_GF_EX%3A%22%E8%87%AA%E5%8A%A8%E5%8C%96%E5%AD%A6%E6%8A%A5%22%29+AND+CITE%3A1+&sortfld=&order=&group_num=&detailType=2&loc=2
             */

            String url = "http://sciencechina.cn/detail.jsp?i=" + i + "#key="+ski;
            CrawlDatum crawlDatum = new CrawlDatum(url);
            crawlDatum.meta("method", "POST");
            crawlDatum.meta("cookie", listCookie);
            crawlDatum.meta("referer", page.getUrl());
            crawlDatum.meta("contentType", "application/x-www-form-urlencoded");
            String outputData = "page=&searchmode=&dbtype=" + "&searchword=%28JOURNAL_NAME_GF_EX%3A%22" + keyword + "%22+AND+YEAR%3A%5B" + from + "+TO+" + to + "%5D%29+AND+CITE%3A1+" + "&sortfld=&order=&group_num=&detailType=2&loc=" + i;

            crawlDatum.meta("outputData", outputData);
            crawlDatum.meta("pageType", "DETAIL");
            crawlDatum.meta("searchKeywordInfo", ski);

            if (crawledItems.contains(MD5.MD5(url))) {
                logger.info("drop crawled item[{}]", url);
            }
            else {
                crawlDatums.add(crawlDatum);
                logger.info("[list] add detail task {}", i);
            }

        }

        logger.info("[searchKey]:" + ski + "list page parsed, item count{}", total);

        return elements;
    }


    protected ScholarData parseDetailPage(Page page) {
        Element title = page.select("td#body_con_cl > h1", 0);
        String pageTitle = title.text();

        Element authorE = page.select("div#authorbar", 0);
        String author = authorE.text();

        Element citeE = page.select("#body_con_cr > table.table_s2 > thead > tr > th ", 0);
        String citeStr = citeE.text();
        String countStr = util.Re.rExtract(citeStr, "\\d+");
        int citeCount = Integer.parseInt(countStr);
        String citeUrl = citeE.attr("href");


        Elements detailsE = page.select("#textresize > tbody > tr ");
        String summary = "";
        String enSummary = "";
        String source = "";
        String doi = "";
        String keywords = "";
        String address = "";
        String language = "";
        String issn = "";
        String subject = "";
        String fund = "";


        for (Element element : detailsE) {
            if (element.select("td").text().contains("文摘") && !element.select("td").text().contains("其他语种文摘")) {
                summary = element.select("td").text();
            } else if (element.select("td").text().contains("来源")) {
                source = element.select("td").text();
            } else if (element.select("td").text().contains("DOI")) {
                doi = element.select("td").text();
            } else if (element.select("td").text().contains("关键词")) {
                keywords = element.select("td").text();
            } else if (element.select("td").text().contains("地址")) {
                address = element.select("td").text();
            } else if (element.select("td").text().contains("语种")) {
                language = element.select("td").text();
            } else if (element.select("td").text().contains("ISSN")) {
                issn = element.select("td").text();
            } else if (element.select("td").text().contains("学科")) {
                subject = element.select("td").text();
            } else if (element.select("td").text().contains("基金")) {
                fund = element.select("td").text();
            }

        }


        ScholarData sd = new ScholarData();
        sd.setTitle(pageTitle);
        sd.setSearchKeyword(page.getCrawlDatum().meta("searchKeywordInfo"));
        sd.setTpCode("");
        sd.setInserttime(new Date(System.currentTimeMillis()));
        sd.setUrl(page.getUrl());
        sd.setAuthor(author);
        sd.setBerefer_num(citeCount);
        sd.setBerefer_url(citeUrl);
        sd.setSummary(summary + "\n" + enSummary);
        sd.setJournal(source);
        sd.setDoi(doi);
        sd.setKeywords(keywords);
        sd.setAddress(address);
        sd.setLanguage(language);
        sd.setIssn(issn);
        sd.setSubject(subject);
        sd.setFund(fund);

        sd.setMd5(MD5.MD5(sd.getUrl()));

        logger.info("[detail] paper{} parsed.", pageTitle);
        return sd;
    }
}
