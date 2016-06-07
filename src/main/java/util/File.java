package util;

import java.io.*;

/**
 * Created by guanxiaoda on 6/7/16.
 *
 */
public class File {


    public synchronized static void writeTxt(String fileName, String content) throws IOException {

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, false)));
        writer.write(content);
        writer.close();

    }

    public synchronized static void writeTxtAppend(String fileName, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
        writer.write(content);
        writer.close();
    }
}
