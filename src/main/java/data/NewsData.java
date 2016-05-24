package data;

/**
 * Created by guanxiaoda on 5/24/16.
 */
public class NewsData extends BasicData{
    private String brief;
    private String source;
    private String pubtime;
    private String sameNum;

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

    public String getPubtime() {
        return pubtime;
    }

    public void setPubtime(String pubtime) {
        this.pubtime = pubtime;
    }

    public String getSameNum() {
        return sameNum;
    }

    public void setSameNum(String sameNum) {
        this.sameNum = sameNum;
    }
}
