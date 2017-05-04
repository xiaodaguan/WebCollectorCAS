package data;

/**
 * Created by guanxiaoda on 5/20/16.
 * 关键词
 */
public class SearchKeyInfo {
    private int dbOriginalId;
    private String keyword;
    private int category_code;
    private String site_id;
    private String site_name;

    /* scholar paper */

    private String startDate;//search time period <from>
    private String endDate;//search time period <to>


    @Override
    public String toString(){
        return keyword;
    }

    public int getDbOriginalId() {
        return dbOriginalId;
    }

    public void setDbOriginalId(int dbOriginalId) {
        this.dbOriginalId = dbOriginalId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getCategory_code() {
        return category_code;
    }

    public void setCategory_code(int category_code) {
        this.category_code = category_code;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }
}
