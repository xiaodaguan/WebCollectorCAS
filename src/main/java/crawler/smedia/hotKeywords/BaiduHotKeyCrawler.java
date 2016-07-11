package crawler.smedia.hotKeywords;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import com.mongodb.client.MongoCollection;
import data.HotkeyData;
import org.bson.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Re;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;


/**
 * Created by guanxiaoda on 7/8/16.
 */
public class BaiduHotKeyCrawler extends BreadthCrawler {
    private static Logger logger = LoggerFactory.getLogger(BaiduHotKeyCrawler.class);
    private final static String MONGOURL = "172.18.79.31:27017";
    private final static String MONGODB = "top_keywords_db";
    private final static String MONGOCOLL = "baidu_top_keywords";

    private static MongoCollection coll = null;

    public BaiduHotKeyCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        this.addSeed("http://top.baidu.com/category?c=513");

        MongoClient client = new MongoClient(MONGOURL);
        MongoDatabase db = client.getDatabase(MONGODB);

        coll = db.getCollection(MONGOCOLL);
    }

    public void visit(Page page, CrawlDatums next) {
        if ("http://top.baidu.com/category?c=513".equals(page.getUrl())) {
            Elements list = page.select("#sub-nav > li > a");
            for (Element el : list) {
                String cateUrl = el.attr("href");
                if (cateUrl == null) {
                    logger.error("分类url抓取失败！");
                    return;
                }
                if (!cateUrl.startsWith("http://")) cateUrl = "http://top.baidu.com/" + cateUrl;
                next.add(cateUrl);
            }
        } else if (page.getUrl().contains("/buzz")) {
            Elements keywordList = page.select("td.keyword > a.list-title");

            List<HotkeyData> results = new ArrayList<HotkeyData>();
            for (Element el : keywordList) {
                String keyword = el.text();
                String url = el.attr("href");
                String detailUrl = el.attr("href_top");

                HotkeyData data = new HotkeyData();
                data.setKeyword(keyword);
                data.setUrl(url);
                data.setTopUrl(detailUrl);
                results.add(data);
            }

            Elements indexList = page.select("tbody > tr > td.last > span");
            if (indexList.size() != results.size()) {
                logger.error("列表长度不一致: item/指数 {}/{}", results.size(), indexList.size());
            }
            int i = 0;
            for (Element el : indexList) {
                String value = el.text();
                if (Re.rMatches(value, "\\d+")) results.get(i).setSearchIndex(Integer.parseInt(value));

                if (el.hasClass("icon-rise")) results.get(i).setTrend(1);
                else if (el.hasClass("icon-fall")) results.get(i).setTrend(-1);
                else results.get(i).setTrend(0);

                results.get(i).setCrawlDate(new Date(System.currentTimeMillis()));
                i++;

            }


            logger.info("page parsed: --{} --{}", page.select("title").text(), page.getUrl());

            for (HotkeyData data : results) {
                coll.insertOne(new Document("keyword", data.getKeyword()).append("url", data.getUrl()).append("top_url", data.getTopUrl()).append("search_index", data.getSearchIndex()).append("trend", data.getTrend()).append("crawl_date", data.getCrawlDate()));
            }

            logger.info("page items inserted to {}/{}/{}", MONGOURL, MONGODB, MONGOCOLL);
        }

    }


    public static void main(String[] args) throws Exception {
        BaiduHotKeyCrawler hotKeyCrawler = new BaiduHotKeyCrawler("BaiduHotKey", false);
        hotKeyCrawler.setThreads(1);
        hotKeyCrawler.setExecuteInterval(1);
        hotKeyCrawler.start(10);
    }
}
