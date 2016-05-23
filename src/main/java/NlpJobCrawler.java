import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

/**
 * Created by guanxiaoda on 5/19/16.
 */
public class NlpJobCrawler extends BreadthCrawler{

    public NlpJobCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        this.addSeed("http://www.nlpjob.com/jobs/big-data/");
        this.addRegex("http://");

    }

    public void visit(Page page, CrawlDatums crawlDatums) {
        if(page.getUrl().contains("/jobs/")){
            System.out.println("列表页");
        }else if(page.getUrl().contains("/job/")) {
            System.out.println("详情页");
        }
    }


    public static void main(String[] args) throws Exception {
        NlpJobCrawler cra = new NlpJobCrawler("NlpJobCrawler",true);
        cra.setThreads(1);
        cra.setTopN(99999);
        cra.start(2);
    }
}
