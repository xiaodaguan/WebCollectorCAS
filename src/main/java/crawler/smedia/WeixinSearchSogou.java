package crawler.smedia;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import crawler.BaseCrawler;
import data.WeixinData;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by guanxiaoda on 6/7/16.
 *
 *
 */
public class WeixinSearchSogou extends BaseCrawler<WeixinData> {

    public WeixinSearchSogou(String crawlPath, boolean autoParse) throws UnsupportedEncodingException {
        super(crawlPath, autoParse);
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
    protected WeixinData parseDetailPage(Page page) {
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
    protected void saveData(WeixinData weixinData) {

    }



    public void visit(Page page, CrawlDatums next) {

    }
}
