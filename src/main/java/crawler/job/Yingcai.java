package crawler.job;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guanxiaoda on 6/27/16.
 */
public class Yingcai extends BreadthCrawler{

    private static Logger logger = LoggerFactory.getLogger(Yingcai.class);
    private static String MONGO_URL = "guanxiaoda.cn:27017";


    public Yingcai(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
//        this.addSeed();
    }

    public void visit(Page page, CrawlDatums next) {

    }
}
