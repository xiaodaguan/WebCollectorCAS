package crawler.other;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import crawler.BaseCrawler;
import data.SearchKeyInfo;
import data.WowItemData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.StringUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by guanxiaoda on 17/3/31.
 */
public class WowheadCNCrawler extends BaseCrawler<WowItemData>{

    private Logger logger = LoggerFactory.getLogger(WowheadCNCrawler.class);
    private static MongoCollection coll = null;
    private static HashSet<String> crawled = new HashSet<String>();

    public WowheadCNCrawler(String crawlPath, boolean autoParse) throws UnsupportedEncodingException {
        super(crawlPath, autoParse);
        generateSeeds(null);
        MongoClient client = new MongoClient("localhost:27017");
        MongoDatabase db = client.getDatabase("wowhead");
        coll = db.getCollection("items");
        FindIterable iter = coll.find();
        iter.forEach(new Block<org.bson.Document>() {
            public void apply(org.bson.Document document) {
//                System.out.println(document.get("title"));
                crawled.add((String) document.get("url"));
            }


        });
    }

    protected CrawlDatums generateSeeds(List<SearchKeyInfo> searchKeyInfos) throws UnsupportedEncodingException {
        List<String> seeds = new ArrayList<String>();
//        this.addSeed(new CrawlDatum("http://cn.wowhead.com/leather-armor/min-level:800/quality:3:4:5/slot:3"));
//        this.addSeed(new CrawlDatum("http://cn.wowhead.com/leather-armor/min-level:800/quality:3:4:5/slot:8"));
//        this.addSeed(new CrawlDatum("http://cn.wowhead.com/leather-armor/min-level:800/quality:3:4:5/slot:10"));
//        this.addSeed(new CrawlDatum("http://cn.wowhead.com/leather-armor/min-level:800/quality:3:4:5/slot:9"));
//        this.addSeed(new CrawlDatum("http://cn.wowhead.com/leather-armor/min-level:800/quality:3:4:5/slot:1"));
//        this.addSeed(new CrawlDatum("http://cn.wowhead.com/leather-armor/min-level:800/quality:3:4:5/slot:7"));
//        this.addSeed(new CrawlDatum("http://cn.wowhead.com/leather-armor/min-level:800/quality:3:4:5/slot:5"));
//        this.addSeed(new CrawlDatum("http://cn.wowhead.com/leather-armor/min-level:800/quality:3:4:5/slot:6"));
        this.addSeed(new CrawlDatum("http://cn.wowhead.com/amulets/min-level:800/quality:3:4:5"));
//        this.addSeed(new CrawlDatum("http://cn.wowhead.com/rings/min-level:800/quality:3:4:5"));
//        this.addSeed(new CrawlDatum("http://cn.wowhead.com/trinkets/min-level:800/quality:3:4:5"));
        return null;
    }

    protected List<String> loadCrawledItems() {
        return null;
    }

    protected void paging(Page page, CrawlDatums crawlDatums) {

    }


    protected void saveData(WowItemData wowItemData) {

    }

    public void visit(Page page, CrawlDatums next) {

        String title = page.select("head > title").first().text();
        logger.info("visiting page {}...", title);

        if(!page.getUrl().contains("/item=")) {
            List<String> itemIds = StringUtil.rExtractList(page.getHtml(), "\"id\":\\d+,\"level\":");
            for (String itemId : itemIds) {
                String id = StringUtil.rExtract(itemId, "\\d+");
                String url = "http://cn.wowhead.com/item=" + id;

                if(crawled.contains(url)){
                    logger.info("drop crawled post!. {}", url);
                    continue;
                }else {
                    next.add(url);
                }
            }
        }else{

            String name = title;
            String tooltip = StringUtil.rExtract(page.getHtml(),"tooltip_zhcn = '.*'");
            String getResource = StringUtil.rExtract(page.getHtml(),"new Listview.*created-by.*;");
            if (getResource == null) {
                getResource = StringUtil.rExtract(page.getHtml(),"new Listview.*reward-from.*;");
            }else{
                getResource = "制造:"+getResource;
            }
            if (getResource == null) {
                getResource = StringUtil.rExtract(page.getHtml(),"new Listview.*dropped-by.*;");
            }{
                getResource = "任务:"+getResource;
            }

            if(getResource!=null){
                getResource = "掉落:"+getResource;
            }
//            Document doc = null;
//            try {
//                doc = (new SAXBuilder()).build(new InputSource(new StringReader(tooltip)));
//            } catch (JDOMException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            WowItemData wid = new WowItemData();
            wid.setName(name);
            wid.setUrl(page.getUrl());
            wid.setDescription(tooltip);
            wid.setLoot(getResource);

            logger.info("item [{}] parsed.", name);

            coll.insertOne(new org.bson.Document()
                    .append("url", page.getUrl())
                    .append("name", wid.getName())
                    .append("description", wid.getDescription())
                    .append("loot",wid.getLoot()));


            logger.info("item [{}] inserted.", name);

        }




        logger.info("o.");
    }


    public static void main(String[] args) throws Exception {



        WowheadCNCrawler crawler = new WowheadCNCrawler("WowheadCN",false);
        crawler.setExecuteInterval(5);
        crawler.setThreads(1);

        crawler.start(95588);
    }
}
