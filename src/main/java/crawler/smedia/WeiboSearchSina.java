package crawler.smedia;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import crawler.BaseCrawler;
import downloader.SeleniumWebDriverManager;
import data.SearchKeyInfo;
import data.WeiboData;
import db.DataPersistence;
import db.JDBCHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import util.File;
import util.MD5;
import util.Re;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by guanxiaoda on 5/25/16.
 * 采集新浪微博pc版
 * * 初始化时统一登录若干微博账号
 * * 生成seed时,为seed随机指定cookie,伴随到该关键词采集完成(即一个关键词使用一个账号)
 * * 列表页只获取url,其他信息到详情页采集
 * * 生成crawldatum时要从meta继承'account', 'cookie'
 */
public class WeiboSearchSina extends BaseCrawler<WeiboData> {

        /* <配置> */

    final private static String CRAWLER_NAME = WeiboSearchSina.class.getName();
    final private static String DB_URL = "172.18.79.3:1521/orcl";
    final private static String DB_USER = "jinrong";
    final private static String DB_PASSWORD = "jinrong";
    private static String DB_TABLE = "weibo_data";
    private static String DB_SEARCHKEYWORD_TABLE = "search_keyword";
    private static String DB_ACCOUNT_TABLE = "crawler_account";

    private static String URL_TEMPLATE = "http://s.weibo.com/weibo/<KEYWORD>&page=1&nodup=1";

    final private static String RUN_MODE = "test";//test or run
//    final private static String RUN_MODE = "run";

    /* </配置> */


    private static Logger logger = LoggerFactory.getLogger(WeiboSearchSina.class);

    private static JdbcTemplate jdbcTemplate = null;
    private List<String> crawledItems = null; // crawled items

    private static HashMap<String, String> userCookies;// cookie pool

    public WeiboSearchSina(String crawlPath, boolean autoParse) throws UnsupportedEncodingException {
        super(crawlPath, autoParse);
        jdbcTemplate = JDBCHelper.createOracleTemplate(CRAWLER_NAME, "jdbc:oracle:thin:@" + DB_URL, DB_USER, DB_PASSWORD, 5, 30);

        weiboLogin();

        DB_TABLE = RUN_MODE.equals("test") ? DB_TABLE + "_test" : DB_TABLE;
        crawledItems = RUN_MODE.equals("run") ? loadCrawledItems() : new ArrayList<String>();
        List<SearchKeyInfo> searchKeyInfos = new ArrayList<SearchKeyInfo>();
        if (RUN_MODE.equals("test")) {
            SearchKeyInfo testSk = new SearchKeyInfo();
            testSk.setCategory_code(0);
            testSk.setDbOriginalId(1);
            testSk.setSite_name("sina");
            testSk.setKeyword("互联网 金融");
            testSk.setSite_id("sina");
            searchKeyInfos.add(testSk);
        } else {
            searchKeyInfos = DataPersistence.loadSearchKeyInfos(jdbcTemplate, DB_SEARCHKEYWORD_TABLE, 4);

        }

        CrawlDatums seeds = generateSeeds(searchKeyInfos);
        this.addSeed(seeds);
    }

    private void weiboLogin() {
        if (RUN_MODE.equals("run")) {
            HashMap<String, String> userPass = DataPersistence.loadUserPass(jdbcTemplate, DB_ACCOUNT_TABLE, 8);
            if (userPass.size() == 0) {
                logger.error("没有采集账户!");
                System.exit(-1);
            }


            for (String uname : userPass.keySet()) {
                String passwd = userPass.get(uname);

                String cookie = userLogin(uname, passwd);
                if (cookie == null) continue;//login failed
                if (userCookies == null) userCookies = new HashMap<String, String>();//init map
                userCookies.put(uname, cookie);//push cookie


            }
        } else {
            String uname = "zoo_1@sina.com";
            String passwd = "zoo_1@sina.com";
            String cookie = userLogin(uname, passwd);
            if (userCookies == null) userCookies = new HashMap<String, String>();//init map
            userCookies.put(uname, cookie);
        }

    }

    private String userLogin(String uname, String passwd) {
        logger.info("user [{}] signing in...", uname);
//        FirefoxProfile profile = new FirefoxProfile();
//        profile.setAcceptUntrustedCertificates(true);
//        profile.setAssumeUntrustedCertificateIssuer(false);
//        FirefoxDriver driver = new FirefoxDriver();
        PhantomJSDriver driver = SeleniumWebDriverManager.getInstance().getPhantomJSDriver();

        driver.get("http://weibo.com/login.php");
        WebDriverWait driverWait = new WebDriverWait(driver, 10);

        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[suda-uatrack*=ordinary_login]")));
        try {
            File.writeTxt("tmphtml/tmp.html", driver.getPageSource());
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver.findElementByCssSelector("a[suda-uatrack*=ordinary_login]").click();

        driver.findElementByCssSelector("#pl_login_form > div > div.info_header > div > a:nth-child(2)").click();
        driver.findElementByCssSelector("#loginname").clear();
        driver.findElementByCssSelector("#loginname").sendKeys(uname);
        driver.findElementByCssSelector("div.info_list.password > div > input").sendKeys(passwd);


        driver.findElementByCssSelector("#pl_login_form > div > div:nth-child(3) > div.info_list.login_btn > a").click();
        driverWait = new WebDriverWait(driver, 20);
        try {
            driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#skin_cover_s > div")));
        } catch (org.openqa.selenium.TimeoutException e) {

        }
        String result = null;
        if (driver.getTitle().contains("我的首页")) {
            logger.info("user [{}] login succeed.", uname);
            StringBuilder sb = new StringBuilder();
            for (Cookie cookie : driver.manage().getCookies()) {
                sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
            }
            result = sb.toString();

        } else {
            logger.error("user [{}] login failed.", uname);
        }

        driver.quit();
        return result;
    }

    @Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);

        String uName = crawlDatum.getMetaData().get("account");
        String cookie = crawlDatum.getMetaData().get("cookie");

        logger.info("requsting via account: >" + uName + "<");
        request.setCookie(cookie);

        return request.getResponse();
    }

    private void setRandomCookieForCrawlDatum(CrawlDatum crawlDatum) {
        List<String> uNameList = new ArrayList<String>(userCookies.keySet());
        int random = (int) Math.floor(Math.random() * uNameList.size());
        String uName = uNameList.get(random);
        String cookie = userCookies.get(uName);
        crawlDatum.meta("account", uName);
        crawlDatum.meta("cookie", cookie);
    }

    @Override
    protected CrawlDatums generateSeeds(List<SearchKeyInfo> searchKeyInfos) throws UnsupportedEncodingException {
        CrawlDatums seeds = new CrawlDatums();
        for (SearchKeyInfo ski : searchKeyInfos) {
            String url = URL_TEMPLATE.replace("<KEYWORD>", URLEncoder.encode(ski.getKeyword(), "utf-8"));
            CrawlDatum cd = new CrawlDatum(url);

            cd.meta("dbid", String.valueOf(ski.getDbOriginalId()));
            cd.meta("category_code", String.valueOf(ski.getCategory_code()));
            cd.meta("search_keyword", ski.getKeyword());
            cd.meta("site_id", ski.getSite_id());
            cd.meta("site_name", ski.getSite_name());
            setRandomCookieForCrawlDatum(cd);

            seeds.add(cd);
        }
        return seeds;
    }


    @Override
    protected List<String> loadCrawledItems() {
        logger.info("loading crawled items...");
        DB_TABLE = RUN_MODE.equals("run") ? DB_TABLE : DB_TABLE + "_test";
        List<String> crawled = DataPersistence.loadItemsCrawled(jdbcTemplate, DB_TABLE);
        logger.info("crawled item count: {}", crawled.size());
        return crawled;
    }

    @Override
    protected WeiboData parseDetailPage(Page page) {
        // TODO: 5/25/16
        String pageUrl = page.getUrl();
        String pageSource = page.getHtml();
        //发布时间
        Element timeE = page.select("div.WB_detail > div > a[date]").first();
        assert timeE != null : "select pubtime failed!!!";
        String pubtime = timeE.attr("title");
        String timstampStr = timeE.attr("date");
        Date pubdate = new Date(Long.parseLong(timstampStr));
        //用户
        String uid = null;
        String userUrl = null;
        String userName = null;
        Element userE = page.select("div.WB_feed_detail.clearfix > div.WB_detail > div.WB_info > a").first();
        if (userE != null) {
            uid = Re.rExtract(userE.attr("usercard"), "id=\\d+").replace("id=", "");
            userUrl = userE.attr("href");
            userName = userE.text();
        }

        //发布来源
        Element sourceE = page.select("div.WB_detail > div > a[action-type=app_source]").first();
        String source = sourceE != null ? sourceE.text() : null;
        //内容
        Element contentE = page.select("div.WB_feed_detail.clearfix > div.WB_detail > div[node-type=feed_list_content]").first();
        String content = contentE != null ? contentE.text() : null;

        //收藏,转发,评论,赞
        Element collE = page.select("div.WB_feed_handle > div > ul > li:nth-child(1) > a > span > span > span > em:nth-child(2)").first();
        Element rttE = page.select("div.WB_feed_handle > div > ul > li:nth-child(2) > a > span > span > span > em:nth-child(2)").first();
        Element commE = page.select("div.WB_feed_handle > div > ul > li:nth-child(3) > a > span > span > span > em:nth-child(2)").first();
        Element likeE = page.select("div.WB_feed_handle > div > ul > li:nth-child(4) > a > span > span > span > em:nth-child(2)").first();

        String collCountStr = collE != null ? collE.text() : null;
        String rttCountStr = rttE != null ? rttE.text() : null;
        String commCountStr = commE != null ? commE.text() : null;
        String likeCountStr = likeE != null ? likeE.text() : null;

        int collCount = collCountStr != null ? Re.rMatches(collCountStr, "\\d+") ? Integer.parseInt(collCountStr) : 0 : 0;
        int rttCount = rttCountStr != null ? Re.rMatches(rttCountStr, "\\d+") ? Integer.parseInt(rttCountStr) : 0 : 0;
        int commCount = commCountStr != null ? Re.rMatches(commCountStr, "\\d+") ? Integer.parseInt(commCountStr) : 0 : 0;
        int likeCount = likeCountStr != null ? Re.rMatches(likeCountStr, "\\d+") ? Integer.parseInt(likeCountStr) : 0 : 0;
        //用户头像
        Element userImgE = page.select("div.WB_face.W_fl > div.face > a > img").first();
        String userImg = userImgE != null ? userImgE.attr("src") : null;
        //微博图片
        Elements imgEs = page.select("div.WB_media_wrap.clearfix > div > ul > li > img");
        StringBuilder sb = new StringBuilder();
        for (Element imgE : imgEs) {
            sb.append(imgE.attr("src")).append(";");
        }
        String imgUrl = sb.toString();
        //位置信息
        Element gpsE = page.select("div.WB_detail > div.WB_text.W_f14>a:last-child").first();
        String gps = gpsE != null ? gpsE.attr("title") : null;
        //微博id
        Element midE = page.select("div[mid]").first();
        String mid = midE != null ? midE.attr("mid") : null;


        WeiboData wd = new WeiboData();
        wd.setUrl(pageUrl);
        wd.setPubtime(pubdate);
        wd.setMd5(MD5.MD5(page.getUrl()));
        wd.setUserId(Long.parseLong(uid));
        wd.setCommentCount(commCount);
        wd.setRttCount(rttCount);
        wd.setMid(mid);
        wd.setCommentUrl("http://weibo.com/aj/comment/big?id=" + mid);
        wd.setRttUrl("http://weibo.com/aj/mblog/info/big?id=" + mid);
        wd.setAuthor(userName);
        wd.setAuthorUrl(userUrl);
        wd.setSearchKeyword(page.getMetaData().get("search_keyword"));
        wd.setCategoryCode(Integer.parseInt(page.getMetaData().get("category_code")));
        wd.setAuthorImg(userImg);
        wd.setContent(content);
        wd.setSource(source);
        wd.setImgUrl(imgUrl);
        wd.setGps(gps);
        wd.setLike_count(likeCount);


        return wd;
    }

    @Override
    protected void paging(Page page, CrawlDatums crawlDatums) {
        if (page.getHtml().contains("抱歉") && page.getHtml().contains("未找到") && page.getHtml().contains("相关结果")) return;

        String url = page.getUrl();

        String pattern = "&page=\\d+";
        String currPageStr = Re.rExtract(url, pattern);
        String numStr = Re.rExtract(currPageStr, "\\d+");

        assert numStr != null : "paging err.";
        int currPageNum = Integer.parseInt(numStr);
        int nextPageNum = currPageNum + 1;

        assert currPageStr != null : "paging err.";
        String nextUrl = url.replace(currPageStr, "&page=" + nextPageNum);

        CrawlDatum next = new CrawlDatum(nextUrl);
        next.meta("account", page.getMetaData().get("account"));
        next.meta("cookie", page.getMetaData().get("cookie"));
        next.meta("category_code", page.getMetaData().get("category_code"));
        next.meta("search_keyword", page.getMetaData().get("search_keyword"));
        crawlDatums.add(next);

    }

    @Override
    protected Elements parseList(Page page, CrawlDatums crawlDatums) {
        //
        String account = page.getMetaData().get("account");
        String cookie = page.getMetaData().get("cookie");
        String category_code = page.getMetaData().get("category_code");
        String search_keyword = page.getMetaData().get("search_keyword");

        Elements elements = page.select("div.content.clearfix > div.feed_from.W_textb > a.W_textb");
        for (Element element : elements) {
            String url = element.attr("href");

            if (crawledItems.contains(MD5.MD5(url))) continue;
            CrawlDatum link = new CrawlDatum(url);
            link.meta("account", account);
            link.meta("cookie", cookie);
            link.meta("category_code", category_code);
            link.meta("search_keyword", search_keyword);
            crawlDatums.add(link);

        }
        return elements;
    }

    @Override
    protected boolean isDetailPage(Page page) {
        return !isListPage(page);
    }

    @Override
    protected boolean isListPage(Page page) {
        return page.getUrl().contains("s.weibo.com/");
    }

    @Override
    protected void saveData(WeiboData weiboData) {
        synchronized (this) {
            if (!crawledItems.contains(weiboData.getMd5())) {
                if (DataPersistence.insertData(jdbcTemplate, weiboData, DB_TABLE)) {
                    logger.info("data inserted. {}", weiboData.getContent());
                    crawledItems.add(weiboData.getMd5());
                } else logger.error("insert failed. {}", weiboData.getContent());
            } else {
                logger.info("drop old item. {}", weiboData.getContent());
            }
        }
    }

    public void visit(Page page, CrawlDatums next) {
        //url, pubtime, insert_time, md5, user_id, comment_count, rtt_count, mid, comment_url, rtt_url, author, author_url, search_keyword, category_code, author_img, content, source, img_url, gps, like_count
        if (isListPage(page)) {
            List<String> scripts = Re.rExtractList(page.getHtml(), "<script>STK.*STK.*pageletM.*pageletM.*view.*<\\/script>");
            StringBuilder sb = new StringBuilder();
            for (String script : scripts) {
                JSONObject jo = new JSONObject(script.substring(script.indexOf("{"), script.lastIndexOf("}") + 1));
                sb.append(jo.getString("html"));
            }

            page.setHtml(sb.toString());
            Elements elements = parseList(page, next);


            // paging
            if (elements != null) {

                paging(page, next);

            }


        } else if (isDetailPage(page)) {
            // parse item

            List<String> scripts = Re.rExtractList(page.getHtml(), "<script>FM\\.view.*<\\/script>");
            StringBuilder sb = new StringBuilder();
            for (String script : scripts) {
                JSONObject jo = new JSONObject(script.substring(script.indexOf("{"), script.lastIndexOf("}") + 1));
                try {
                    sb.append(jo.getString("html"));
                } catch (JSONException e) {//可能不一定都包含html
                    continue;
                }
            }

            page.setHtml(sb.toString());

            WeiboData wbData = parseDetailPage(page);

            //save
            if (wbData.getPubtime() != null && wbData.getContent() != null) saveData(wbData);


        }
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            logger.info("cycle start...");
            WeiboSearchSina crawler = new WeiboSearchSina(CRAWLER_NAME, true);
            crawler.setThreads(1);
            crawler.setExecuteInterval(25 * 1000);
            crawler.start(31);

            logger.info("wait...");
            long SLEEP = 1000 * 3600 * 2;
            Thread.sleep(SLEEP);
        }
    }
}
