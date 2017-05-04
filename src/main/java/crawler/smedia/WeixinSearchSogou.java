package crawler.smedia;

import cn.edu.hfut.dmic.webcollector.crawldb.DBManager;
import cn.edu.hfut.dmic.webcollector.crawler.Crawler;
import cn.edu.hfut.dmic.webcollector.fetcher.Executor;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BerkeleyDBManager;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by guanxiaoda on 6/7/16.
 * todo
 */
public class WeixinSearchSogou  {

    /* <配置> */

    final private static String CRAWLER_NAME = WeixinSearchSogou.class.getName();
    private static String URL_TEMPLATE_USER = "http://weixin.sogou.com/weixin?type=1&query=<keyword>";
    private static String URL_TEMPLATE_PAPER = "http://weixin.sogou.com/weixin?type=2&query=<keyword>";
    final private static String RUN_MODE = "run";
    private static Logger logger = LoggerFactory.getLogger(WeixinSearchSogou.class);
    static HtmlUnitDriver driver = new HtmlUnitDriver();

    static{

        driver.setJavascriptEnabled(true);
        driver.getBrowserVersion().setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");

    }

    public static void main(String[] args) {

        Executor userSearchExecutor = new Executor() {
            public void execute(CrawlDatum datum, CrawlDatums next) throws Exception {

                driver.get(datum.getUrl());
                logger.info("crawling user page");
                String title = driver.getTitle();
                String html = driver.getPageSource();
                while(title.contains("验证码")){
                    logger.error("爬虫被屏蔽，请手动输入cookie，回车结束：");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String str = reader.readLine();
                    reader.close();
                    driver.manage().addCookie(new Cookie("cookie",str));
                    Thread.sleep(10*1000);
                }


                if(datum.meta("pageType").equals("USER")) {

                    WebElement element = driver.findElementByCssSelector("div > div.txt-box > p.tit > a");

                    System.out.println(element.getText());
                    String url = element.getAttribute("href");
                    CrawlDatum list = new CrawlDatum(url);
                    list.meta("pageType","LIST");

                    next.add(list);

                }else if(datum.meta("pageType").equals("LIST")){
                    logger.info("crawling list page");

                    List<WebElement> elements = driver.findElementsByCssSelector("div.weui_media_bd > h4");
                    for(WebElement element: elements){
                        String url = element.getAttribute("hrefs");
                        CrawlDatum detail = new CrawlDatum("http://mp.weixin.qq.com"+url);
                        detail.meta("pageType","DETAIL");

                        next.add(detail);
                    }

                }else if(datum.meta("pageType").equals("DETAIL")){
                    logger.info("crawling detail page");
                }

            }
        };




        Executor paperSearchExecutor = new Executor() {
            public void execute(CrawlDatum datum, CrawlDatums next) throws Exception {

                driver.get(datum.getUrl());
                logger.info("crawling user page");
                String title = driver.getTitle();
                String html = driver.getPageSource();
                while(title.contains("验证码")){
                    logger.error("爬虫被屏蔽，请手动输入cookie，回车结束：");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String str = reader.readLine();
                    reader.close();
                    driver.manage().addCookie(new Cookie("cookie",str));
                    Thread.sleep(10*1000);
                }


                if(datum.meta("pageType").equals("LIST")){
                    logger.info("crawling list page");

                    List<WebElement> elements = driver.findElementsByCssSelector("div.weui_media_bd > h4");
                    for(WebElement element: elements){
                        String url = element.getAttribute("hrefs");
                        CrawlDatum detail = new CrawlDatum("http://mp.weixin.qq.com"+url);
                        detail.meta("pageType","DETAIL");

                        next.add(detail);
                    }
                    /* parse list page*/
                    //title
                    //url
                    //brief
                    //pubtime
                    //author
                    //img_brief



                }else if(datum.meta("pageType").equals("DETAIL")){
                    logger.info("crawling detail page");
                    /* parse detail page */
                    //images
                    //content
                    //qrCode
                    //readnum
                    //likenum
                }

            }
        };


        DBManager manager = new BerkeleyDBManager("sogouweixin");


        Crawler crawler = new Crawler(manager,paperSearchExecutor);
        CrawlDatum seed = new CrawlDatum(URL_TEMPLATE_PAPER.replace("<keyword>","%E9%9D%92%E5%B2%9B%E4%BA%A4%E9%80%9A"));// paper?user?
        seed.meta("pageType","LIST");// paper?user?
        crawler.addSeed(seed);
        crawler.setExecuteInterval(5*1000);
        try {
            crawler.start(5);
        } catch (Exception e) {
            e.printStackTrace();
        }





    }



}
