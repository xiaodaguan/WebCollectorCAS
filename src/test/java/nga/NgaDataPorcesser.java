package nga;

import com.google.gson.Gson;
import com.mongodb.*;
import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.util.exception.LoadModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by guanxiaoda on 6/3/16.
 */
public class NgaDataPorcesser {
    private static CNFactory factory = null;
    private static Logger logger = LoggerFactory.getLogger(NgaDataPorcesser.class);

    public NgaDataPorcesser() throws LoadModelException {

        factory = CNFactory.getInstance("models");
    }

    public HashMap<String, Integer> loadItemsAndWordcount() {
        MongoClient client = new MongoClient("guanxiaoda.cn:27017");

        DB db = client.getDB("ngadb");
        DBCollection coll = db.getCollection("暗影裂口");

        DBCursor cursor = coll.find();

        HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

        int num = 0;
        int total = cursor.count();
        while (cursor.hasNext()) {
            if (++num % 10 == 0) {
                logger.info("loading: {}/{}...", num, total);
            }
            DBObject obj = cursor.next();

            String title = (String) obj.get("title");
            String content = (String) obj.get("content");

            String[] seg = factory.seg(title + content);
            for (String word : seg) {
                if (wordCount.containsKey(word)) {
                    wordCount.put(word, wordCount.get(word) + 1);
                } else {
                    wordCount.put(word, 1);
                }
            }
//            System.out.println(obj.toString());
        }

        return wordCount;
    }


    public void filterStopWords(String srcFile, String tarFile, String stopWordFile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stopWordFile)));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tarFile)));
        Set<String> stopWords = new HashSet<String>();
        String line = null;

        while ((line = reader.readLine()) != null) {
            stopWords.add(line);
        }
        reader.close();
        logger.info("load {} stopwords.", stopWords.size());

        reader = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile)));
        while ((line = reader.readLine()) != null) {
            if (!line.contains("\t")) continue;
            String word = line.split("\t")[0];
            if (stopWords.contains(word)) continue;

            writer.write(line + "\n");
            writer.flush();

        }
        writer.close();
        reader.close();

    }

    public static void main(String[] args) throws LoadModelException, IOException {


        NgaDataPorcesser pro = new NgaDataPorcesser();
//        HashMap<String, Integer> wordcount = pro.loadItemsAndWordcount();
//        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(wordcount.entrySet());
//        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
//            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
//                return o1.getValue() - o2.getValue();
//            }
//        });
//
//        BufferedWriter write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("wordcount.txt")));
//        for (int i = list.size() - 1; i >= 0; i--) {
//            System.out.println(list.get(i).getKey() + ":" + list.get(i).getValue());
//            write.write(list.get(i).getKey() + "\t" + list.get(i).getValue() + "\n");
//            write.flush();
//        }
//        write.close();
        pro.filterStopWords("wordcount.txt","wordcount_filter.txt","models/stopwords/StopWords.txt");


    }
}
