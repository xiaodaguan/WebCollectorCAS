package util;


import java.net.*;

/**
 * Created by guanxiaoda on 17/5/3.
 */
public class ProxyUtil {



    public static boolean verify(String ip, int port){

        SocketAddress socketAddress = new InetSocketAddress(ip,port);

        Proxy proxy = new Proxy(Proxy.Type.HTTP,socketAddress);
        try{
            URL url = new URL("https://www.baidu.com");
            URLConnection conn = url.openConnection(proxy);
            conn.setConnectTimeout(6000);
            conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)");
            conn.getContent();
            return true;
        }catch (Exception e){
//            e.printStackTrace();
        }
        try{
            URL url = new URL("https://www.sogou.com/");
            URLConnection conn = url.openConnection(proxy);
            conn.setConnectTimeout(6000);
            conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)");
            conn.getContent();
            return true;
        }catch (Exception e){
//            e.printStackTrace();
        }
        try{
            URL url = new URL("https://www.taobao.com/");
            URLConnection conn = url.openConnection(proxy);
            conn.setConnectTimeout(6000);
            conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)");
            conn.getContent();
            return true;
        }catch (Exception e){
//            e.printStackTrace();
        }
        try{
            URL url = new URL("http://www.qq.com/");
            URLConnection conn = url.openConnection(proxy);
            conn.setConnectTimeout(6000);
            conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)");
            conn.getContent();
            return true;
        }catch (Exception e){
//            e.printStackTrace();
        }


        return false;
    }
}
