package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guanxiaoda on 5/25/16.
 */
public class Re {

    /**
     * 从字符串中抽取pattern
     *
     * @param input
     * @param pattern
     * @return
     */
    public static String rExtract(String input, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    /**
     * 是否匹配pattern
     *
     * @param input
     * @param pattern
     * @return
     */
    public static boolean rMatches(String input, String pattern) {
        Pattern p = Pattern.compile(pattern);
        return p.matches(pattern, input);
    }

    public static List<String> rExtractList(String input, String pattern){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        List<String> results = new ArrayList<String>();
        while(m.find()){

            results.add(m.group());
        }
        return results;
    }

    public static void main(String[] args) {
//        System.out.println(rExtract("10条12相同新闻", "\\d+"));
//        System.out.println(rMatches("10天前", "\\d+天前"));
//        System.out.println(rMatches("10天前", "\\d+[\\u4e00-\\u9fa5]前"));
//        System.out.println(rMatches("10分钟前", "\\d+[\\u4e00-\\u9fa5]+前"));
        String cont = "<noscript><img width=\"0\" height=\"0\" src=\"http://beacon.sina.com.cn/a.gif?noScript\" border=\"0\" alt=\"\" /></noscript>\n" +
                "<!-- SUDA_CODE_END -->\n" +
                "\n" +
                "<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_common_searchTop\",\"js\":[\"apps\\/search_v6\\/js\\/pl\\/common\\/searchTop.js?version=20160527122500\"],\"css\":[],\"html\":\"<div class=\\\"search_head_formbox\\\">\\n <div class=\\\"search_logo\\\"><a suda-data=\\\"key=tblog_search_weibo&value=weibo_logo\\\" href=\\\"\\/?Refer=STopic_icon\\\" class=\\\"logo_img\\\"><\\/a><\\/div>\\n <!-- <ul class=\\\"formbox_tab clearfix formbox_tab2\\\" node-type=\\\"searchItems\\\"> -->\\n <ul class=\\\"formbox_tab clearfix formbox_tab2\\\" node-type=\\\"searchItems\\\">\\n\\t<li>\\n\\t <a action-type=\\\"searchItem\\\" suda-data=\\\"key=tblog_search_weibo&value=weibo_weibo\\\" href=\\\"javascript:void(0);\\\" class=\\\"cur\\\">\\u7efc\\u5408<\\/a>\\n\\t \\t <a action-type=\\\"searchItem\\\" suda-data=\\\"key=tblog_search_weibo&value=weibo_user\\\" href=\\\"\\/user\\/%25E7%258B%25AC%25E7%2594%259F%25E5%25AD%2590%25E5%25A5%25B3%25E6%258A%25A4%25E7%2590%2586%25E5%2581%2587&Refer=weibo_user\\\" >\\u627e\\u4eba<\\/a>\\n\\t \\t \\t <a action-type=\\\"searchItem\\\" suda-data=\\\"key=tblog_search_weibo&value=weibo_pic\\\" href=\\\"\\/pic\\/%25E7%258B%25AC%25E7%2594%259F%25E5%25AD%2590%25E5%25A5%25B3%25E6%258A%25A4%25E7%2590%2586%25E5%2581%2587&Refer=weibo_pic\\\" >\\u56fe\\u7247<\\/a>\\n\\t <a action-type=\\\"searchItem\\\" suda-data=\\\"key=tblog_search_weibo&value=weibo_interest\\\" href=\\\"\\/apps\\/%25E7%258B%25AC%25E7%2594%259F%25E5%25AD%2590%25E5%25A5%25B3%25E6%258A%25A4%25E7%2590%2586%25E5%2581%2587&Refer=weibo_apps\\\" >\\u5174\\u8da3\\u4e3b\\u9875<\\/a>\\t \\t \\n\\t \\t<\\/li>\\n <\\/ul>\\n <!-- search_input -->\\n <div class=\\\"search_input clearfix\\\">\\n  <div class=\\\"search_input_wrap\\\">\\n <div class=\\\"searchBtn_box\\\"><a href=\\\"\\/weibo\\/%25E7%258B%25AC%25E7%2594%259F%25E5%25AD%2590%25E5%25A5%25B3%25E6%258A%25A4%25E7%2590%2586%25E5%2581%2587\\\" class=\\\"searchBtn\\\" node-type=\\\"submit\\\" suda-data=\\\"key=tblog_search_weibo&value=weibo_search\\\">\\u641c\\u7d22<\\/a><\\/div>\\n <div class=\\\"searchInp_box\\\">\\n  <div class=\\\"searchInp_auto\\\">\\n   <input class=\\\"searchInp_form\\\" type=\\\"text\\\" value=\\\"\\u72ec\\u751f\\u5b50\\u5973\\u62a4\\u7406\\u5047\\\" node-type=\\\"text\\\" maxlength=\\\"40\\\">\\n  <\\/div>\\n <\\/div>\\n  <\\/div>\\n <\\/div>\\n <!-- \\/search_input --> \\n<\\/div>\\n<div class=\\\"search_head_action\\\">\\n <p class=\\\"action_txt\\\">\\n   <a href=\\\"javascript:void(0);\\\" class=\\\"adv_settiong\\\" node-type=\\\"advsearch\\\" suda-data=\\\"key=tblog_search_weibo&value=weibo_profes\\\">\\u9ad8\\u7ea7\\u641c\\u7d22<\\/a>\\n  <a href=\\\"\\/preferences\\\" class=\\\"setting\\\" target=\\\"_blank\\\" suda-data=\\\"key=tblog_search_weibo&value=weibo_set\\\">\\u8bbe\\u7f6e<\\/a>\\n <a href=\\\"http:\\/\\/help.weibo.com\\/newtopic\\/search\\\" target=\\\"_blank\\\" suda-data=\\\"key=tblog_search_weibo&value=weibo_help\\\" class=\\\"adv_settiong\\\">\\u5e2e\\u52a9<\\/a>\\n <\\/p>\\n<\\/div>\\n\"})</script>\n" +
                "<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"plc_main\",\"js\":[\"apps\\/search_v6\\/js\\/pl\\/common\\/unLogin.js?version=20160527122500\",\"apps\\/search_v6\\/js\\/pl\\/common\\/loginBox.js?version=20160527122500\"],\"css\":[],\"html\":\"<div class=\\\"S_content clearfix\\\">\\n <div class=\\\"S_content_l\\\">\\n  <div id=\\\"pl_common_pinyinerr\\\"><\\/div>\\n  <div id=\\\"pl_weibo_shareres\\\"><\\/div>\\n  <div id=\\\"pl_weibo_directtop\\\" smartconf=\\\"type=1\\\" class=\\\"clearfix\\\"><\\/div>\\n <div id=\\\"pl_weibo_filtertab\\\"><\\/div>\\n  <div id=\\\"pl_weibo_direct\\\"><\\/div>\\n  <div id=\\\"pl_weibo_relation\\\"><\\/div>\\n  <div id=\\\"pl_common_bottomInput\\\" style=\\\"display:none;\\\"><\\/div>\\n  <div id=\\\"pl_gs_christmas2013\\\" class=\\\"clearfix\\\"><\\/div>\\n <\\/div>\\n <div class=\\\"S_content_r\\\">\\n <!--  <div id=\\\"pl_weibo_race\\\"><\\/div> -->\\n  <div id=\\\"pl_weibo_directright\\\"><\\/div>\\n  <div id=\\\"pl_common_ad\\\"><\\/div>\\n  <!-- <div id=\\\"pl_common_recommend\\\"><\\/div> -->\\n  <div id=\\\"pl_common_shishi\\\"><\\/div>\\n  <div id=\\\"pl_weibo_hotband\\\"><\\/div>\\n  <div id=\\\"pl_common_ali\\\"><\\/div>\\n  <div id=\\\"pl_common_subscribe\\\"><\\/div>\\n  <div class=\\\"WB_cardwrap S_bg2\\\"><div class=\\\"module_history\\\" id=\\\"pl_common_searchHistory\\\"><\\/div><\\/div>\\n  <div id=\\\"pl_common_alisecond\\\"><\\/div>\\n <\\/div>\\n<\\/div>\"})</script>\n" +
                "<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_weibo_race\",\"js\":[\"apps\\/search_v6\\/js\\/pl\\/weibo\\/race.js?version=20160527122500\"],\"css\":[\"appstyle\\/searchV45\\/css_v6\\/module\\/race_table.css?version=20160527122500\"],\"html\":\"\"})</script>\n" +
                "<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_weibo_directright\",\"js\":[\"apps\\/search_v6\\/js\\/pl\\/weibo\\/directright.js?version=20160527122500\",\"apps\\/search_v6\\/js\\/pl\\/weibo\\/weblist.js?version=20160527122500\",\"apps\\/search_v6\\/js\\/pl\\/weibo\\/directplayer.js?version=20160527122500\"],\"css\":[],\"html\":\"   <div class=\\\"WB_cardwrap S_bg2 wbs_interest_wrap\\\"><div class=\\\"pl_gspage_r\\\">\\n  <div class=\\\"search_title_a\\\">\\n   <a suda-data=\\\"key=tblog_search_weibo&value=open_page_more\\\" href=\\\"\\/apps\\/%25E7%258B%25AC%25E7%2594%259F%25E5%25AD%2590%25E5%25A5%25B3%25E6%258A%25A4%25E7%2590%2586%25E5%2581%2587&city=10&page_interest=weihuati_zhuchi\\\" class=\\\"W_fr change\\\">\\u66f4\\u591a<em href=\\\"\\/apps\\/%25E7%258B%25AC%25E7%2594%259F%25E5%25AD%2590%25E5%25A5%25B3%25E6%258A%25A4%25E7%2590%2586%25E5%2581%2587&city=10&page_interest=weihuati_zhuchi\\\" class=\\\"W_ficon ficon_arrow_right S_ficon\\\">a<\\/em><\\/a>\\n   <h4><span class=\\\"W_fb\\\">\\u76f8\\u5173\\u5174\\u8da3\\u4e3b\\u9875<\\/span><\\/h4>\\n\\t<\\/div>\\n<div class=\\\"card_scroll\\\">\\n<div class=\\\"wbs_relevant_interest\\\"> \\n \\n \\n    \\n    \\t<div class=\\\"content_topic\\\">\\n  <a suda-data=\\\"key=tblog_search_weibo&value=open_page_topic_pic\\\" target=\\\"_blank\\\" href=\\\"http:\\/\\/weibo.com\\/p\\/1008081b7f6d4eda5857cfe1210d2b1da5d096\\\"><img src=\\\"http:\\/\\/ww2.sinaimg.cn\\/thumbnail\\/631f7dfegw1f4ded8qtnnj2050050dfz.jpg\\\" alt=\\\"\\u72ec\\u751f\\u5b50\\u5973\\u62a4\\u7406\\u5047\\\" ><\\/a>\\n  <div>\\n  <h2><i class=\\\"interset_icon topic_icon\\\"><\\/i><a suda-data=\\\"key=tblog_search_weibo&value=open_page_topic_title\\\" target=\\\"_blank\\\" href=\\\"http:\\/\\/weibo.com\\/p\\/1008081b7f6d4eda5857cfe1210d2b1da5d096\\\" title=\\\"\\u72ec\\u751f\\u5b50\\u5973\\u62a4\\u7406\\u5047\\\">\\u72ec\\u751f\\u5b50\\u5973\\u62a4\\u7406\\u5047<\\/a><\\/h2>  <p>\\u968f\\u7740\\u4e00\\u4ee3\\u4ee3\\u72ec\\u751f\\u5b50\\u5973\\u957f\\u5927\\u6210...<\\/p>  <span>77\\u4e07\\u9605\\u8bfb<\\/span>  <\\/div>\\n  <\\/div>\\n \\n    \\n <\\/div>\\n<\\/div> \\n<\\/div>\\n<\\/div>\\n\"})</script>\n";
        System.out.println( rExtractList(cont,"<script>STK.*STK.*pageletM.*pageletM.*view.*<\\/script>"));
    }
}
