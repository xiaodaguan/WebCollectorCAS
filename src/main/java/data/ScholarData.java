package data;

import java.sql.Date;

/**
 * Created by guanxiaoda on 17/3/21.
 */
public class ScholarData extends BasicData {
    private String tpCode;
    private String author;
    private String pubtime;
    private Date inserttime;
    private String summary;
    private String keywords;
    private String address;
    private String down_url;
    private String berefer_url;
    private int berefer_num;
    private String en_author;
    private String fund;
    private String journal;
    private String refer_url;
    private String database;
    private String doi;
    private String issn;
    private String language;
    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getTpCode() {
        return tpCode;
    }

    public void setTpCode(String tpCode) {
        this.tpCode = tpCode;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPubtime() {
        return pubtime;
    }

    public void setPubtime(String pubtime) {
        this.pubtime = pubtime;
    }

    public Date getInserttime() {
        return inserttime;
    }

    public void setInserttime(Date inserttime) {
        this.inserttime = inserttime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDown_url() {
        return down_url;
    }

    public void setDown_url(String down_url) {
        this.down_url = down_url;
    }

    public String getBerefer_url() {
        return berefer_url;
    }

    public void setBerefer_url(String berefer_url) {
        this.berefer_url = berefer_url;
    }

    public int getBerefer_num() {
        return berefer_num;
    }

    public void setBerefer_num(int berefer_num) {
        this.berefer_num = berefer_num;
    }

    public String getEn_author() {
        return en_author;
    }

    public void setEn_author(String en_author) {
        this.en_author = en_author;
    }

    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getRefer_url() {
        return refer_url;
    }

    public void setRefer_url(String refer_url) {
        this.refer_url = refer_url;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
