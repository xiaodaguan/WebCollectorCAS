import java.io.UnsupportedEncodingException;

/**
 * Created by guanxiaoda on 6/8/16.
 */
public class StringSize {

    public static void main(String[] args) throws UnsupportedEncodingException {



        byte[] a = null;
        byte[] b = null;
        byte[] c = null;
        String str = "我们";
        a = str.getBytes("utf-8");
        b = str.getBytes("gb2312");
        c = str.getBytes();


        System.out.println("ok");

    }
}
