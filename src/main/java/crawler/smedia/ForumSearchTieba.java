package crawler.smedia;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import crawler.BaseCrawler;
import data.ForumData;
import db.DataPersistence;
import db.JDBCHelper;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import util.File;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by guanxiaoda on 5/26/16.
 * todo
 */
public class ForumSearchTieba extends BaseCrawler<ForumData> {

    /* <配置> */

    final private static String CRAWLER_NAME = ForumSearchTieba.class.getName();
    final private static String DB_URL = "172.18.79.3:1521/orcl";
    final private static String DB_USER = "jinrong";
    final private static String DB_PASSWORD = "jinrong";
    private static String DB_TABLE = "bbs_data";
    private static String DB_SEARCHKEYWORD_TABLE = "search_keyword";
    final private static String DB_ACCOUNT_TABLE = "crawler_account";

    final private static String URL_TEMPLATE = "";

    final private static String RUN_MODE = "test";//test or run
//    final private static String RUN_MODE = "run";

    /* </配置> */

    private static Logger logger = LoggerFactory.getLogger(ForumSearchTieba.class);

    private static JdbcTemplate jdbcTemplate = null;
    private List<String> crawledItems = null; // crawled items
    private static HashMap<String, String> userCookies = new HashMap<String, String>();


    public ForumSearchTieba(String crawlPath, boolean autoParse) throws UnsupportedEncodingException {
        super(crawlPath, autoParse);
        jdbcTemplate = JDBCHelper.createOracleTemplate(CRAWLER_NAME, "jdbc:oracle:thin:@" + DB_URL, DB_USER, DB_PASSWORD, 5, 30);

        login();
    }

    private void login() {
        if (RUN_MODE.equals("run")) {
            HashMap<String, String> userPass = DataPersistence.loadUserPass(jdbcTemplate, DB_ACCOUNT_TABLE, 4);
            if (userPass.size() == 0) {
                logger.error("no crawler account!");
                System.exit(-1);
            }

            for (String uname : userPass.keySet()) {
                String passwd = userPass.get(uname);
                String cookie = userLogin(uname, passwd);
                if (cookie == null) continue;
                if (userCookies == null) userCookies = new HashMap<String, String>();
                userCookies.put(uname, cookie);

            }
        } else {
            String name = "xiaoda_guan";
            String passwd = "gxd327";
            String cookie = userLogin(name, passwd);
            if (userCookies == null) userCookies = new HashMap<String, String>();
            userCookies.put(name, cookie);
        }
    }

    private String userLogin(String uname, String passwd) {
        logger.info("user [{}] signing in...");
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs/osx/phantomjs");

        PhantomJSDriver driver = new PhantomJSDriver(caps);
        driver.get("https://passport.baidu.com/v2/?login");

        WebDriverWait wait = new WebDriverWait(driver,20);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#TANGRAM__PSP_3__userName")));

        WebElement inputUser = driver.findElementByCssSelector("#TANGRAM__PSP_3__userName");
        WebElement inputPass = driver.findElementByCssSelector("#TANGRAM__PSP_3__password");
        WebElement submit = driver.findElementByCssSelector("#TANGRAM__PSP_3__submit");

        inputUser.clear();
        inputUser.sendKeys(uname);
        inputPass.clear();
        inputPass.sendKeys(passwd);
        submit.click();

        String cookies = null;
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("dd.mod-account-info > p > a")));
            logger.info("user [{}] login succeed.", uname);
            StringBuilder sb = new StringBuilder();
            for(Cookie cookie : driver.manage().getCookies()){
                sb.append(cookie.getName()+"="+cookie.getValue()+";");
            }
            cookies =  sb.toString();
        }catch (TimeoutException te){
            logger.error("user [{}] login failed!", uname);
            cookies = null;
        }
        driver.quit();
        return cookies;


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
    protected Object parseDetailPage(Page page) {
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
    protected void saveData(ForumData forumData) {

    }


    public void visit(Page page, CrawlDatums next) {

    }

    public static void main(String[] args) throws Exception {
        ForumSearchTieba fst = new ForumSearchTieba(CRAWLER_NAME, true);
        fst.setExecuteInterval(3);
        fst.setThreads(1);
        fst.start(5);
    }
}

