package crawler.other;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import crawler.BaseCrawler;
import data.SimpleData;
import org.bson.BsonDocument;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.xsoup.Xsoup;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;

/**
 * Created by guanxiaoda on 6/3/16.
 */
public class NgaCrawler extends BreadthCrawler {
    private static MongoCollection coll = null;
    private static Logger logger = LoggerFactory.getLogger(NgaCrawler.class);
    private static String cookie = "CNZZDATA1256638915=1824191923-1481525388-null%7C1481525388; CNZZDATA1256638869=633873567-1484278768-http%253A%252F%252Fbbs.ngacn.cc%252F%7C1484278768; UM_distinctid=15ac518c9ef7a1-0b2f46ba99d733-1d3e6850-13c680-15ac518c9f01f5; CNZZDATA1261107574=1616349212-1489990649-http%253A%252F%252Fbbs.ngacn.cc%252F%7C1489990649; __utmz=240585808.1490168355.427.60.utmcsr=bing|utmccn=(organic)|utmcmd=organic|utmctr=15%E5%A4%A7%E7%A7%98%E5%A2%83%E8%A3%85%E7%AD%89; Hm_lvt_60031dda34b454306f907cbac1fb2081=1490600143,1490668684,1490772516,1490937195; Hm_lpvt_60031dda34b454306f907cbac1fb2081=1491447513; ngaPassportUid=2440823; ngaPassportUrlencodedUname=slayerdark; ngaPassportCid=b03a060f5f872703cea0970691e9ca3a28419eeb; CNZZDATA1256638820=2139585352-1479783369-http%253A%252F%252Fbbs.ngacn.cc%252F%7C1492503096; CNZZDATA1256638874=1777120526-1477468497-http%253A%252F%252Fbbs.ngacn.cc%252F%7C1492759285; CNZZDATA1256638919=170363960-1493096770-http%253A%252F%252Fbbs.ngacn.cc%252F%7C1493096770; __utma=240585808.180360142.1462261792.1493167987.1493171431.486; __utmc=240585808; __utma=225257221.1456946196.1493171567.1493171567.1493171567.1; __utmc=225257221; __utmz=225257221.1493171567.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); ngacn0comUserInfo=slayerdark%09slayerdark%0939%0939%09%0910%09602%091%090%090%0961_15; ngacn0comUserInfoCheck=4f83b5ef51882fd2dfefd6c974e5cb0d; ngacn0comInfoCheckTime=1493196408; CNZZDATA1256638858=996643932-1463973808-http%253A%252F%252Fbbs.ngacn.cc%252F%7C1493192503; bbsmisccookies=%7B%22uisetting%22%3A%7B0%3A131138%2C1%3A1498696761%7D%2C%22insad_refreshid%22%3A%7B0%3A%22/0a001e494662a12b19fdb530e318%22%2C1%3A1493692108%7D%2C%22pv_count_for_insad%22%3A%7B0%3A-58%2C1%3A1493226110%7D%2C%22insad_views%22%3A%7B0%3A2%2C1%3A1493226110%7D%7D; CNZZDATA30043604=cnzz_eid%3D2067195796-1462260140-%26ntime%3D1493192429; CNZZDATA30039253=cnzz_eid%3D1314639973-1462259489-%26ntime%3D1493196052";

    private static HashSet<String> crawled = new HashSet<String>();

    /**
     * 构造一个基于伯克利DB的爬虫
     * 伯克利DB文件夹为crawlPath，crawlPath中维护了历史URL等信息
     * 不同任务不要使用相同的crawlPath
     * 两个使用相同crawlPath的爬虫并行爬取会产生错误
     *
     * @param crawlPath 伯克利DB使用的文件夹
     * @param autoParse 是否根据设置的正则自动探测新URL
     */
    public NgaCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        this.addSeed("http://bbs.ngacn.cc/thread.php?fid=189&page=1");//暗影裂口^_^
        MongoClient client = new MongoClient("guanxiaoda.cn:27017");
        MongoDatabase db = client.getDatabase("ngadb");
        coll = db.getCollection("暗影裂口");
        FindIterable iter = coll.find();
        iter.forEach(new Block<Document>() {
            public void apply(Document document) {
//                System.out.println(document.get("title"));
                crawled.add((String) document.get("url"));
            }


        });

        logger.info("load {} crawled items.", crawled.size());
    }

    @Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        request.setCookie(cookie);
        return request.getResponse();
    }

    public void visit(Page page, CrawlDatums next) {
        if (page.getUrl().contains("/thread.php")) {
//list
            String url = page.getUrl();
            String html = page.getHtml();

            Elements links = page.select("tbody>tr > td.c2> a");
            for (Element element : links) {
                Element eleRep = element.parent().previousElementSibling();
                String repCount = eleRep.select("a").first().text();

                Element elePub = element.parent().nextElementSibling();
                String pubtime = elePub.select("span").first().text();

                String postUrl = "http://bbs.ngacn.cc" + element.attr("href");
                if (crawled.contains(postUrl)) {
                    logger.info("drop crawled post!. {}", postUrl);
                    continue;
                } else {
                    CrawlDatum link = new CrawlDatum(postUrl);
                    /**
                     * properties from list page...
                     */
                    link.meta("repCount",repCount);
                    link.meta("pubtime", pubtime);
                    /**
                     *
                     */
                    next.add(link);
                }
            }

            Element nextPage = page.select("a[title=下一页]").first();
            String nextUrl = "http://bbs.ngacn.cc" + nextPage.attr("href");
            CrawlDatum paging = new CrawlDatum(nextUrl);
            next.add(paging);


        } else if (page.getUrl().contains("/read.php")) {

//post
            /**
             * properties from list page...
             */
            String repCount = page.meta("repCount");
            Integer repNum = Integer.parseInt(repCount);
            String pubtime = page.meta("pubtime");
            pubtime = util.Time.timeFormat(pubtime).toString();
            /**
             *
             */
            String title = page.select("head > title").first().text();
            org.jsoup.nodes.Document doc = Jsoup.parse(page.getHtml());
            String content = Xsoup.compile("//p[contains(@id,'postcontent')]//text()|//span[contains(@id,'postcontent')]//text()").evaluate(doc).get();

            coll.insertOne(
                    new org.bson.Document()
                            .append("url", page.getUrl())
                            .append("title", title)
                            .append("content", content)
                            .append("pubtime",pubtime)
                    .append("reply",repNum)
            );
            logger.info("item inserted {}.", title);
        } else {
            ;
        }
    }



    public static void main(String[] args) throws Exception {
        NgaCrawler c = new NgaCrawler("NgaCrawler", true);
        c.setExecuteInterval(3);
        c.setThreads(1);
        c.start(Integer.MAX_VALUE);
    }
}
