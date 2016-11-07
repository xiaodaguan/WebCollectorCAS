package data;


import java.sql.Date;

/**
 * Created by guanxiaoda on 7/8/16.
 */
public class HotkeyData {

    private String keyword;
    private int cate;
    private String url;
    private String topUrl;
    private int searchIndex;
    private int trend;
    private Date crawlDate;
    private int siteId;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getCate() {
        return cate;
    }

    public void setCate(int cate) {
        this.cate = cate;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTopUrl() {
        return topUrl;
    }

    public void setTopUrl(String topUrl) {
        this.topUrl = topUrl;
    }

    public int getSearchIndex() {
        return searchIndex;
    }

    public void setSearchIndex(int searchIndex) {
        this.searchIndex = searchIndex;
    }

    public int getTrend() {
        return trend;
    }

    public void setTrend(int trend) {
        this.trend = trend;
    }

    public Date getCrawlDate() {
        return crawlDate;
    }

    public void setCrawlDate(Date crawlDate) {
        this.crawlDate = crawlDate;
    }
}
