package crawler.ipProxy;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by guanxiaoda on 17/5/3.
 * todo 代理打分机制：初始分80，扫描一次，失败-1，成功+1
 * todo 定期清扫掉60分以下的代理
 * todo 定期清扫掉时间太久的代理
 */
public class TestProxy {
    private static Logger logger = LoggerFactory.getLogger(TestProxy.class);
    private static MongoClient mc = new MongoClient("guanxiaoda.cn",27017);
    private static MongoDatabase mdb = mc.getDatabase("proxy");
    private static MongoCollection mcoll = mdb.getCollection("proxies");

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        final ExecutorService threadPool = Executors.newFixedThreadPool(5);

        FindIterable it = mcoll.find();
        final int[] count = {0};
        final List<Future> futures = new ArrayList<Future>();
        it.forEach(new Block<Document>() {
            public void apply(Document document) {
                String ip = (String)document.get("ip");
                int port = (Integer) document.get("port");

                logger.info("testing proxy: {}:{}",ip,port);
                Future f = threadPool.submit(new ProxyVerifier(ip,port));
                count[0]++;
                futures.add(f);



            }
        });


//        while(true) {
//            Thread.sleep(5 * 1000);
//
//            logger.info("{}", threadPool.toString());
//            logger.info("{}", threadPool.isTerminated());
//            logger.info("{}", threadPool.isShutdown());
//
//
//
//        }
        logger.info("total: {} proxies.",count[0]);
        Thread.sleep(2 * 1000);
        logger.info("{}", threadPool.toString());
        logger.info("shutting down");
        threadPool.shutdownNow();
        logger.info("{}", threadPool.toString());


        for(Future f: futures){
            f.cancel(true);
        }
        logger.info("all done");

    }
}
