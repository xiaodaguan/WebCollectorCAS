import crawler.JdSearchCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by guanxiaoda on 5/23/16.
 * entrance
 */
public class Start {


    private static Logger logger = LoggerFactory.getLogger(Start.class);

    public static void main(String[] args) throws Exception {
        String path = "crawlers/";

        JdSearchCrawler jdsCrawler = new JdSearchCrawler(path + "eb_jd_search", true);
        jdsCrawler.setThreads(1);
        jdsCrawler.setExecuteInterval(5 * 1000);


        while (true) {
            logger.info("start...");
            jdsCrawler.start(3);

            logger.info("wait...");
            try {
                Thread.sleep(1000 * 60);
            } catch (InterruptedException ie) {
                break;
            }

        }
    }

}
