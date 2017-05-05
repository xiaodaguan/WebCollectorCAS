package crawler.ipProxy;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mysql.cj.jdbc.util.TimeUtil;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by guanxiaoda on 17/5/3.
 *  代理打分机制：记录累计成功率
 *
 */
public class MarkProxy {
    private static Logger logger = LoggerFactory.getLogger(MarkProxy.class);
    private static MongoClient mc = new MongoClient("guanxiaoda.cn",27017);
    private static MongoDatabase mdb = mc.getDatabase("proxy");
    private static MongoCollection mcoll = mdb.getCollection("proxies");


    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static void mark() throws ExecutionException, InterruptedException {


        FindIterable it = mcoll.find();
        final int[] count = {0};
        final List<Future> futures = new ArrayList<Future>();
        it.forEach(new Block<Document>() {
            public void apply(Document document) {
                String ip = (String)document.get("ip");
                int port = (Integer) document.get("port");


                logger.info("testing proxy: {}:{}", ip, port);
                Future f = threadPool.submit(new ProxyVerifier(ip, port, mcoll, document));
                count[0]++;
                futures.add(f);



            }
        });


        logger.info("total: {} proxies.",count[0]);

        for(Future f: futures){
            f.get();
        }
        logger.info("all scored.");
    }

    public static void clean(){
        FindIterable it = mcoll.find();
        it.forEach(new Block<Document>() {
            public void apply(Document doc) {

                String ip = (String)doc.get("ip");
                int port = (Integer) doc.get("port");

                int score = (Integer) doc.get("score");

                if(score< 60){
                    logger.info("drop low score proxy:{}|{}",ip+":"+port,score);
                    mcoll.deleteOne(doc);
                }
            }
        });
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        while(true){
            logger.info("start marking proxies...");
            mark();
//            logger.info("start cleaning proxies...");
//            clean();
            logger.info("sleeping...");
            Thread.sleep(1000*60*1);
        }

    }
}
