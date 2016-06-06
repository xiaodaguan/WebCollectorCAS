package crawler.other;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import crawler.BaseCrawler;
import data.SimpleData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.xsoup.Xsoup;

/**
 * Created by guanxiaoda on 6/3/16.
 */
public class NgaCrawler extends BreadthCrawler {
    private static MongoCollection coll = null;
    private static Logger logger = LoggerFactory.getLogger(NgaCrawler.class);
    private static String cookie = "onlineTotal=37510; CNZZDATA30043604=cnzz_eid%3D1057483000-1464926535-%26ntime%3D1464926534; CNZZDATA30039253=cnzz_eid%3D1812571541-1464924242-%26ntime%3D1464927216; __utmt=1; __utma=240585808.812795462.1464927906.1464927906.1464931557.2; __utmb=240585808.1.10.1464931557; __utmc=240585808; __utmz=240585808.1464927906.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _i=qx%2FcAMqtadwh2P52DY6dm9OusKeTSKi8PkXbAFNWl1elgZo5JapBXO2WszDe5yaD_d3c17e60c6b8a059d5b61e4b063f2ceb_1464845170; ngacn0comUserInfo=slayerdark%09slayerdark%0939%0939%09%0910%09402%091%090%090%0961_5; ngacn0comUserInfoCheck=931123ff97636051673ca5acf02d1028; ngacn0comInfoCheckTime=1464931571; ngaPassportUid=2440823; ngaPassportUrlencodedUname=slayerdark; ngaPassportCid=b03a060f5f872703cea0970691e9ca3a28419eeb; lastvisit=1464931588; lastpath=/read.php?tid=9366799&_ff=189&_fp=2; bbsmisccookies=%7B%7D";

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
        this.addSeed("http://bbs.ngacn.cc/thread.php?fid=189&page=101");//暗影裂口^_^
        MongoClient client = new MongoClient("guanxiaoda.cn:27017");
        MongoDatabase db = client.getDatabase("ngadb");
        coll = db.getCollection("暗影裂口");
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
                String postUrl = "http://bbs.ngacn.cc" + element.attr("href");
                CrawlDatum link = new CrawlDatum(postUrl);
                next.add(link);
            }

            Element nextPage = page.select("a[title=下一页]").first();
            String nextUrl = "http://bbs.ngacn.cc" + nextPage.attr("href");
            CrawlDatum paging = new CrawlDatum(nextUrl);
            next.add(paging);


        } else if (page.getUrl().contains("/read.php")) {
//post
            String title = page.select("h1#postsubject").first().text();
            Document doc = Jsoup.parse(page.getHtml());
            String content = Xsoup.compile("//p[contains(@id,'postcontent')]//text()|//span[contains(@id,'postcontent')]//text()").evaluate(doc).get();
            coll.insertOne(new org.bson.Document().append("url", page.getUrl()).append("title", title).append("content", content)

            );
            logger.info("item inserted {}.", title);
        } else {
            ;
        }
    }

    public static void main(String[] args) throws Exception {
        NgaCrawler c = new NgaCrawler("NgaCrawler", true);
        c.setExecuteInterval(3);
        c.setThreads(3);
        c.start(Integer.MAX_VALUE);
    }
}
