package crawler.ipProxy;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import crawler.BaseCrawler;
import data.SimpleData;
import org.bson.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by guanxiaoda on 17/5/3.
 */
public class IPProxyCrawler extends BreadthCrawler {
    public static void main(String[] args) throws Exception {
        IPProxyCrawler crawler = new IPProxyCrawler("crawler.ipproxy", false);
        crawler.setExecuteInterval(3);
        crawler.setThreads(1);
        crawler.start(2);
    }
    Logger logger = LoggerFactory.getLogger(IPProxyCrawler.class);
    /**
     * db: {ip,port,inserttime,type}
     * str(ip+port)去重
     */

    MongoClient mc = new MongoClient("guanxiaoda.cn",27017);
    MongoDatabase mdb = mc.getDatabase("proxy");
    MongoCollection mcoll = mdb.getCollection("proxies");

    List<String> crawled = loadCrawledItems();

    public IPProxyCrawler(String crawlPath, boolean autoParse) throws UnsupportedEncodingException {
        super(crawlPath, autoParse);
        generateSeeds();
    }

    protected CrawlDatums generateSeeds() throws UnsupportedEncodingException {
        CrawlDatums seeds = new CrawlDatums();
        /**
         * 代理网站添加在这里
         */
        seeds.add("http://www.kuaidaili.com/free/inha/");
        seeds.add("http://www.kuaidaili.com/free/inha/2");
        seeds.add("http://www.kuaidaili.com/free/inha/3");
        seeds.add("http://www.kuaidaili.com/free/inha/4");
        seeds.add("http://www.kuaidaili.com/free/inha/5");
        seeds.add("http://www.kuaidaili.com/free/inha/6");
        seeds.add("http://www.kuaidaili.com/free/inha/7");
        seeds.add("http://www.kuaidaili.com/free/outha/");
        seeds.add("http://www.kuaidaili.com/free/outha/2");
        seeds.add("http://www.kuaidaili.com/free/outha/3");
        seeds.add("http://www.kuaidaili.com/free/outha/4");
        seeds.add("http://www.kuaidaili.com/free/outha/5");
        seeds.add("http://www.kuaidaili.com/free/outha/6");
        seeds.add("http://www.kuaidaili.com/free/outha/7");
        this.addSeed(seeds);
        return seeds;
    }

    protected List<String> loadCrawledItems() {
        final List<String> crawled = new ArrayList<String>();
        FindIterable it = mcoll.find();
        it.forEach(new Block<Document>() {
            public void apply(Document document) {
                crawled.add((String)document.get("ip")+document.get("port"));
            }
        });
        logger.info("load {} proxies.",crawled.size());
        return crawled;
    }

    protected void paging(Page page, CrawlDatums crawlDatums) {

    }

    protected void saveData(Object o) {

    }



    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public void visit(Page page, CrawlDatums next) {
        logger.info("processing page: {} ...", page.getUrl());
        /**
         * 页面抽取
         */
        if(page.getUrl().contains("kuaidaili")){
            page.getHtml();
            Elements eles = page.select("table > tbody > tr");
            for(Element ele : eles){

                try {
                    String ip = ele.select("td:nth-child(1)").first().text();
                    String port = ele.select("td:nth-child(2)").first().text();
                    String anonymity = ele.select("td:nth-child(3)").first().text();
                    String type = ele.select("td:nth-child(4)").first().text();
                    String loc = ele.select("td:nth-child(5)").first().text();
                    String speed = ele.select("td:nth-child(6)").first().text();

                    if(util.Re.rMatches(ip,"\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                        if (crawled.contains(ip + port)) {
                            logger.info("duplicate item: {}:{}.", ip, port);
                        } else {
                            mcoll.insertOne(new Document().append("rate","0/0").append("score", 0.0).append("ip", ip).append("port", Integer.parseInt(port)).append("inserttime", sdf.format(new Date())).append("type", type));
                            logger.info("inserted item: {}:{}.", ip, port);
                        }
                    }
                }catch (Exception e){
                    logger.error("err parsing: {}",page.getUrl());
                }

            }
            logger.debug("kuaidaili");

        }

    }
}
