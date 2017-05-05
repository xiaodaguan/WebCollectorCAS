package crawler.ipProxy;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.rmi.CORBA.Util;

/**
 * Created by guanxiaoda on 17/5/3.
 * score: succ/total
 *
 */
public class ProxyVerifier implements Runnable {

    Logger logger = LoggerFactory.getLogger(ProxyVerifier.class);

    private String ip;
    private int port;
    private Document doc;
    private MongoCollection coll;

    public ProxyVerifier(String ip, int port , MongoCollection coll, Document doc){
        this.ip = ip;
        this.port = port;
        this.coll = coll;
        this.doc = doc;
    }

    public String getName(){
        return ip+port;
    }

    public void run() {
        String rate = (String)doc.get("rate");
        int succCount = Integer.parseInt(rate.split("/")[0]);
        int testCount = Integer.parseInt(rate.split("/")[1]);

        if(util.ProxyUtil.verify(ip,port)){
            logger.info("[√]proxy alive: {}:{}",ip,port);
            succCount++;
        }else{
            logger.info("[x]proxy down: {}:{}",ip,port);

        }

        testCount++;
        String newRateStr = succCount+"/"+testCount;
        Document newScore = new Document("score",(double)succCount/testCount).append("rate",newRateStr);
        coll.updateMany(doc,new Document("$set",newScore));


    }
}
