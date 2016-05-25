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


        while (true) {
            logger.info("start...");


            JdSearchCrawler jdsCrawler = new JdSearchCrawler(path + "eb_jd_search", true);
            jdsCrawler.setThreads(1);
            jdsCrawler.setExecuteInterval(15 * 1000);
            jdsCrawler.start(3);


            logger.info("wait...");
            long SLEEP_TIME = 1000 * 3600 * 2;
            Thread.sleep(SLEEP_TIME);

        }
    }

}
