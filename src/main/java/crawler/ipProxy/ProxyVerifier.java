package crawler.ipProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.rmi.CORBA.Util;

/**
 * Created by guanxiaoda on 17/5/3.
 */
public class ProxyVerifier implements Runnable {

    Logger logger = LoggerFactory.getLogger(ProxyVerifier.class);

    private String ip;
    private int port;

    public ProxyVerifier(String ip, int port ){
        this.ip = ip;
        this.port = port;

    }

    public String getName(){
        return ip+port;
    }

    public void run() {
        if(util.ProxyUtil.verify(ip,port)){
            logger.info("[√]proxy alive: {}:{}",ip,port);
        }else{

            logger.info("[x]proxy down: {}:{}",ip,port);
        }
    }
}
