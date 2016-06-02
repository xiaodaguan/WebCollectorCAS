package data;

import java.sql.Date;

/**
 * Created by guanxiaoda on 5/24/16.
 */
public class NewsData extends BasicData{
    private String brief;
    private String source;
    private Date pubtime;
    private int sameNum;
    private String sameUrl;
    private String content;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getPubtime() {
        return pubtime;
    }

    public void setPubtime(Date pubtime) {
        this.pubtime = pubtime;
    }

    public int getSameNum() {
        return sameNum;
    }

    public void setSameNum(int sameNum) {

        this.sameNum = sameNum;
    }

    public String getSameUrl() {
        return sameUrl;
    }

    public void setSameUrl(String sameUrl) {
        this.sameUrl = sameUrl;
    }
}
