package cn.mypro.swing.util.webmagic;

import cn.mypro.swing.constant.LabelConstant;
import cn.mypro.swing.dao.HVAJapanAVPersonDao;
import cn.mypro.swing.entity.HVAJapanAVM;
import cn.mypro.swing.entity.HVAJapanAVPersonM;
import cn.mypro.swing.util.file.MyFileUtils;
import cn.mypro.utils.DataBaseUtils;
import cn.mypro.utils.DbName;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.*;

public class WebMagicOfPersonsUtil implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    public Site getSite() {
        return site;
    }

    public WebMagicOfPersonsUtil() {
        returnMaps = new ArrayList<>();
    }

    private static String selectPath = "https://xslist.org/search?query=%s&lg=zh";

    private String runName = "";
    private List<Map<String, String>> returnMaps = null;

    private Connection serviceConn = null;

    @Override
    public void process(Page page) {

        //该页面为详情页
        if (page.getUrl().regex("https://xslist\\.org/zh/model/.*").match()) {

            Map<String, String> returnMap = new HashMap<>();

            String h2 = page.getHtml().xpath("/html/body/div[1]/div[3]/div/h2[1]/text()").toString();
            String[] split = h2.split("\\(");
            returnMap.put("PersonOName", split[0]);
            String[] split2 = split[1].split("\\)")[0].split("/");
            if (split2.length > 0) returnMap.put("PersonEName", split2[0]);
            if (split2.length > 1) returnMap.put("PersonAge", split2[1]);

            List<String> namesList = page.getHtml().xpath("//span[@itemprop=additionalName]/text()").all();

            StringBuilder nss = new StringBuilder();
            for (String s : namesList) {
                nss.append(s).append("|");
            }
            returnMap.put("PersonOtherNames", nss.toString());

            String mess = page.getHtml().xpath("/html/body/div[1]/div[3]/div/p[1]/text()").toString();

            String[] replaces = (mess.trim().replace("出生:", "|")
                    .replace("三围:", "|")
                    .replace("罩杯:", "|")
                    .replace("出道日期:", "|")
                    .replace("星座:", "|")
                    .replace("血型:", "|")
                    .replace("身高:", "|")
                    .replace("国籍:", "|") + " ").split("\\|");

            returnMap.put("PersonBirthday", replaces[1]);
            returnMap.put("PersonSW", replaces[2]);
            returnMap.put("PersonZB", replaces[3]);
            returnMap.put("PersonOutTime", replaces[4]);
            returnMap.put("PersonStar", replaces[5]);
            returnMap.put("PersonBlood", replaces[6]);
            returnMap.put("PersonHigh", page.getHtml().xpath("/html/body/div[1]/div[3]/div/p[1]/span[1]/text()").toString());
            returnMap.put("PersonCountry", page.getHtml().xpath("/html/body/div[1]/div[3]/div/p[1]/span[2]/text()").toString());
            returnMap.put("PersonDescribe", page.getHtml().xpath("/html/body/div[1]/div[3]/div/p[2]/text()").toString());

            Selectable avs = page.getHtml().xpath("/html/body/div[1]/div[3]/div/table/tbody/");

            List<Selectable> nodes = avs.nodes();
            StringBuilder movies = new StringBuilder();
            for (Selectable node : nodes) {
                String if_code = node.xpath("tr/td[1]/strong/text()").toString();
                String av_oname = node.xpath("tr/td[2]/text()").toString();
                String publish_time = node.xpath("tr/td[3]/text()").toString();
                movies.append(if_code + ":" + av_oname + ":" + publish_time + "|");
            }
            returnMap.put("PersonMovies", movies.toString());

            Selectable pictureTable = page.getHtml().xpath("/html/body/div[1]/div[3]/div/div[1]/div");
            List<String> pictures = pictureTable.xpath("//a[@href]/@href").all();
            int i = 0;
            for (String s1 : pictures) {
                if (!s1.startsWith("http")) continue;
                returnMap.put("(" + i + ")", s1);
                i++;
            }
            returnMaps.add(returnMap);

/*            for (String o : returnMap.keySet()) {
                System.out.println(o + ":" + returnMap.get(o) + "\n");
            }*/

        } else {
            //该页面为列表页
            //List<String> all = page.getHtml().xpath("/html/body/ul/").xpath("//a[@href]/@href").all();
            page.addTargetRequests(page.getHtml().xpath("/html/body/ul/").xpath("//a[@href]/@href").all());

        }

    }

    public List<HVAJapanAVPersonM> getHPerson(String namelike, JProgressBar processBar, JTextArea messageRun) {

        List<HVAJapanAVPersonM> lists = null;

        this.runName = namelike;
        String format = String.format(selectPath, runName);

        Spider.create(this)
                .addUrl(format)
                //.addPipeline(new ConsolePipeline())
                .thread(3).run();

        processBar.setValue(10);

        try {
            serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);

            if (returnMaps == null || returnMaps.size() == 0) {
                messageRun.append("获取无数据！！\n");
                return null;
            }

            lists = new ArrayList<>();

            for (Map<String, String> returnMap : returnMaps) {

                HVAJapanAVPersonM personM = null;

                try {
                    if (returnMap != null) {

                        personM = new HVAJapanAVPersonM();

                        personM.setValuesAsNormal();

                        personM.setNames(returnMap.get("PersonOName"));
                        personM.setCname("");
                        personM.setOname(returnMap.get("PersonEName") + "|" + returnMap.get("PersonOtherNames"));
                        personM.setGender("1");
                        personM.setStart_time(returnMap.get("PersonOutTime"));

                        processBar.setValue(20);

                        personM.setPhtot_1(MyFileUtils.getImgByteByNet(returnMap.get("(0)")));
                        personM.setPhtot_2(MyFileUtils.getImgByteByNet(returnMap.get("(1)")));

                        processBar.setValue(60);

                        String[] personSW = null;
                        if (returnMap.get("PersonSW") != null) personSW = returnMap.get("PersonSW").replace(" ","").split("/");

                        String date_info = LabelConstant.personRegexFirstText
                                .replace("(T-Content)", returnMap.get("PersonBirthday") != null?returnMap.get("PersonBirthday"):"")
                                .replace("(Birthday-Content)", returnMap.get("PersonBirthday"))
                                .replace("(B-Content)", (personSW != null && personSW.length > 0 && personSW[0] != null)?personSW[0].replace("B",""):"")
                                .replace("(W-Content)", (personSW != null && personSW.length > 1 && personSW[0] != null)?personSW[1].replace("W",""):"")
                                .replace("(H-Content)", (personSW != null && personSW.length > 2 && personSW[0] != null)?personSW[2].replace("H",""):"")
                                .replace("(C-Content)", returnMap.get("PersonZB") != null?returnMap.get("PersonZB").replace(" ","").replace("Cup",""):"")
                                .replace("(Star-Content)", returnMap.get("PersonStar") != null?returnMap.get("PersonStar"):"")
                                .replace("(Blood-Content)", returnMap.get("PersonBlood") != null?returnMap.get("PersonBlood"):"")
                                .replace("(County-Content)", returnMap.get("PersonCountry") != null?returnMap.get("PersonCountry"):"")
                                .replace("(Company1-Content)", returnMap.get("") != null?returnMap.get(""):"")
                                .replace("(Company2-Content)", returnMap.get("") != null?returnMap.get(""):"")
                                .replace("(AV Count-Content)", returnMap.get("") != null?returnMap.get(""):"");

                        personM.setDeta_info(date_info);
                        personM.setOther_info(returnMap.get("PersonDescribe"));

                        personM.setScores(1L);
                        personM.setLevels("F");
                        lists.add(personM);

                        processBar.setValue(80);
                        messageRun.append(personM.getNames() + "网络导入完成！");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            processBar.setValue(90);

        } catch (Exception e) {
            messageRun.append("网路导入出现错误！\n");
            e.printStackTrace();
        } finally {
            DataBaseUtils.closeQuietly(serviceConn);
        }

        return lists;
    }

    public List<HVAJapanAVPersonM> getHPerson(String namelike, JTextArea messageRun) {

        List<HVAJapanAVPersonM> lists = null;

        this.runName = namelike;
        String format = String.format(selectPath, runName);

        Spider.create(this)
                .addUrl(format)
                //.addPipeline(new ConsolePipeline())
                .thread(3).run();


        try {
            serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);

            if (returnMaps == null || returnMaps.size() == 0) {
                messageRun.append("获取无数据！！\n");
                return null;
            }

            lists = new ArrayList<>();

            for (Map<String, String> returnMap : returnMaps) {

                HVAJapanAVPersonM personM = null;

                try {
                    if (returnMap != null) {

                        personM = new HVAJapanAVPersonM();

                        personM.setValuesAsNormal();

                        personM.setNames(returnMap.get("PersonOName"));
                        personM.setCname("");
                        personM.setOname(returnMap.get("PersonEName") + "|" + returnMap.get("PersonOtherNames"));
                        personM.setGender("1");
                        personM.setStart_time(returnMap.get("PersonOutTime"));

                        personM.setPhtot_1(MyFileUtils.getImgByteByNet(returnMap.get("(0)")));
                        personM.setPhtot_2(MyFileUtils.getImgByteByNet(returnMap.get("(1)")));

                        String[] personSW = null;
                        if (returnMap.get("PersonSW") != null) personSW = returnMap.get("PersonSW").replace(" ","").split("/");

                        String date_info = LabelConstant.personRegexFirstText
                                .replace("(T-Content)", returnMap.get("PersonBirthday") != null?returnMap.get("PersonBirthday"):"")
                                .replace("(Birthday-Content)", returnMap.get("PersonBirthday"))
                                .replace("(B-Content)", (personSW != null && personSW.length > 0 && personSW[0] != null)?personSW[0].replace("B",""):"")
                                .replace("(W-Content)", (personSW != null && personSW.length > 1 && personSW[0] != null)?personSW[1].replace("W",""):"")
                                .replace("(H-Content)", (personSW != null && personSW.length > 2 && personSW[0] != null)?personSW[2].replace("H",""):"")
                                .replace("(C-Content)", returnMap.get("PersonZB") != null?returnMap.get("PersonZB").replace(" ","").replace("Cup",""):"")
                                .replace("(Star-Content)", returnMap.get("PersonStar") != null?returnMap.get("PersonStar"):"")
                                .replace("(Blood-Content)", returnMap.get("PersonBlood") != null?returnMap.get("PersonBlood"):"")
                                .replace("(County-Content)", returnMap.get("PersonCountry") != null?returnMap.get("PersonCountry"):"")
                                .replace("(Company1-Content)", returnMap.get("") != null?returnMap.get(""):"")
                                .replace("(Company2-Content)", returnMap.get("") != null?returnMap.get(""):"")
                                .replace("(AV Count-Content)", returnMap.get("") != null?returnMap.get(""):"");

                        personM.setDeta_info(date_info);
                        personM.setOther_info(returnMap.get("PersonDescribe"));

                        personM.setScores(1L);
                        personM.setLevels("F");
                        lists.add(personM);

                        messageRun.append(personM.getNames() + "网络导入完成！");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            messageRun.append("网路导入出现错误！\n");
            e.printStackTrace();
        } finally {
            DataBaseUtils.closeQuietly(serviceConn);
        }

        return lists;
    }

    public HVAJapanAVPersonM getHPersonSimple(String namelike, JProgressBar processBar, JTextArea messageRun) {

        HVAJapanAVPersonM personM = null;

        this.runName = namelike;
        String format = String.format(selectPath, runName);

        Spider.create(this)
                .addUrl(format)
                //.addPipeline(new ConsolePipeline())
                .thread(3).run();

        processBar.setValue(10);

        try {
            serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);

            if (returnMaps == null || returnMaps.size() == 0) {
                messageRun.append("获取无数据！！\n");
                return null;
            }
            Map<String, String> returnMap = returnMaps.get(0);

            try {
                if (returnMap != null) {

                    personM = new HVAJapanAVPersonM();

                    personM.setValuesAsNormal();

                    personM.setNames(returnMap.get("PersonOName"));
                    personM.setCname("");
                    personM.setOname(returnMap.get("PersonEName") + "|" + returnMap.get("PersonOtherNames"));
                    personM.setGender("1");
                    personM.setStart_time(returnMap.get("PersonOutTime"));
                    processBar.setValue(20);

                    personM.setPhtot_1(MyFileUtils.getImgByteByNet(returnMap.get("(0)")));
                    personM.setPhtot_2(MyFileUtils.getImgByteByNet(returnMap.get("(1)")));
                    processBar.setValue(60);

                    String[] personSW = null;
                    if (returnMap.get("PersonSW") != null) personSW = returnMap.get("PersonSW").replace(" ","").split("/");

                    String date_info = LabelConstant.personRegexFirstText
                            .replace("(T-Content)", returnMap.get("PersonBirthday") != null?returnMap.get("PersonBirthday"):"")
                            .replace("(Birthday-Content)", returnMap.get("PersonBirthday"))
                            .replace("(B-Content)", (personSW != null && personSW.length > 0 && personSW[0] != null)?personSW[0].replace("B",""):"")
                            .replace("(W-Content)", (personSW != null && personSW.length > 1 && personSW[0] != null)?personSW[1].replace("W",""):"")
                            .replace("(H-Content)", (personSW != null && personSW.length > 2 && personSW[0] != null)?personSW[2].replace("H",""):"")
                            .replace("(C-Content)", returnMap.get("PersonZB") != null?returnMap.get("PersonZB").replace(" ","").replace("Cup",""):"")
                            .replace("(Star-Content)", returnMap.get("PersonStar") != null?returnMap.get("PersonStar"):"")
                            .replace("(Blood-Content)", returnMap.get("PersonBlood") != null?returnMap.get("PersonBlood"):"")
                            .replace("(County-Content)", returnMap.get("PersonCountry") != null?returnMap.get("PersonCountry"):"")
                            .replace("(Company1-Content)", returnMap.get("") != null?returnMap.get(""):"")
                            .replace("(Company2-Content)", returnMap.get("") != null?returnMap.get(""):"")
                            .replace("(AV Count-Content)", returnMap.get("") != null?returnMap.get(""):"");

                    personM.setDeta_info(date_info);
                    personM.setOther_info(returnMap.get("PersonDescribe"));

                    personM.setScores(1L);
                    personM.setLevels("F");

                    processBar.setValue(80);
                    messageRun.append(personM.getNames() + "网络导入完成！");

                }

                processBar.setValue(90);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            messageRun.append("网路导入出现错误！\n");
            e.printStackTrace();
        } finally {
            DataBaseUtils.closeQuietly(serviceConn);
        }

        return personM;
    }

    public HVAJapanAVPersonM getHPersonSimple(String namelike, JTextArea messageRun) {

        HVAJapanAVPersonM personM = null;

        this.runName = namelike;
        String format = String.format(selectPath, runName);

        Spider.create(this)
                .addUrl(format)
                //.addPipeline(new ConsolePipeline())
                .thread(3).run();


        try {
            serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);

            if (returnMaps == null || returnMaps.size() == 0) {
                messageRun.append("获取无数据！！\n");
                return null;
            }
            Map<String, String> returnMap = returnMaps.get(0);

            try {
                if (returnMap != null) {

                    personM = new HVAJapanAVPersonM();

                    personM.setValuesAsNormal();

                    personM.setNames(returnMap.get("PersonOName"));
                    personM.setCname("");
                    personM.setOname(returnMap.get("PersonEName") + "|" + returnMap.get("PersonOtherNames"));
                    personM.setGender("1");
                    personM.setStart_time(returnMap.get("PersonOutTime"));

                    personM.setPhtot_1(MyFileUtils.getImgByteByNet(returnMap.get("(0)")));
                    personM.setPhtot_2(MyFileUtils.getImgByteByNet(returnMap.get("(1)")));

                    String[] personSW = null;
                    if (returnMap.get("PersonSW") != null) personSW = returnMap.get("PersonSW").replace(" ","").split("/");

                    String date_info = LabelConstant.personRegexFirstText
                            .replace("(T-Content)", returnMap.get("PersonBirthday") != null?returnMap.get("PersonBirthday"):"")
                            .replace("(Birthday-Content)", returnMap.get("PersonBirthday"))
                            .replace("(B-Content)", (personSW != null && personSW.length > 0 && personSW[0] != null)?personSW[0].replace("B",""):"")
                            .replace("(W-Content)", (personSW != null && personSW.length > 1 && personSW[0] != null)?personSW[1].replace("W",""):"")
                            .replace("(H-Content)", (personSW != null && personSW.length > 2 && personSW[0] != null)?personSW[2].replace("H",""):"")
                            .replace("(C-Content)", returnMap.get("PersonZB") != null?returnMap.get("PersonZB").replace(" ","").replace("Cup",""):"")
                            .replace("(Star-Content)", returnMap.get("PersonStar") != null?returnMap.get("PersonStar"):"")
                            .replace("(Blood-Content)", returnMap.get("PersonBlood") != null?returnMap.get("PersonBlood"):"")
                            .replace("(County-Content)", returnMap.get("PersonCountry") != null?returnMap.get("PersonCountry"):"")
                            .replace("(Company1-Content)", returnMap.get("") != null?returnMap.get(""):"")
                            .replace("(Company2-Content)", returnMap.get("") != null?returnMap.get(""):"")
                            .replace("(AV Count-Content)", returnMap.get("") != null?returnMap.get(""):"");

                    personM.setDeta_info(date_info);
                    personM.setOther_info(returnMap.get("PersonDescribe"));

                    personM.setScores(1L);
                    personM.setLevels("F");

                    messageRun.append(personM.getNames() + "网络导入完成！");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            messageRun.append("网路导入出现错误！\n");
            e.printStackTrace();
        } finally {
            DataBaseUtils.closeQuietly(serviceConn);
        }

        return personM;
    }

    public HVAJapanAVPersonM getHPersonSimple(String namelike) {

        HVAJapanAVPersonM personM = null;

        this.runName = namelike;
        String format = String.format(selectPath, runName);

        Spider.create(this)
                .addUrl(format)
                //.addPipeline(new ConsolePipeline())
                .thread(3).run();


        try {
            //serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);

            if (returnMaps == null || returnMaps.size() == 0) {
                return null;
            }
            Map<String, String> returnMap = returnMaps.get(0);

            try {
                if (returnMap != null) {

                    personM = new HVAJapanAVPersonM();

                    personM.setValuesAsNormal();

                    personM.setNames(returnMap.get("PersonOName"));
                    personM.setCname("");
                    personM.setOname(returnMap.get("PersonEName") + "|" + returnMap.get("PersonOtherNames"));
                    personM.setGender("1");
                    personM.setStart_time(returnMap.get("PersonOutTime"));

                    personM.setPhtot_1(MyFileUtils.getImgByteByNet(returnMap.get("(0)")));
                    personM.setPhtot_2(MyFileUtils.getImgByteByNet(returnMap.get("(1)")));

                    String[] personSW = null;
                    if (returnMap.get("PersonSW") != null) personSW = returnMap.get("PersonSW").replace(" ","").split("/");

                    String date_info = LabelConstant.personRegexFirstText
                            .replace("(T-Content)", returnMap.get("PersonBirthday") != null?returnMap.get("PersonBirthday"):"")
                            .replace("(Birthday-Content)", returnMap.get("PersonBirthday"))
                            .replace("(B-Content)", (personSW != null && personSW.length > 0 && personSW[0] != null)?personSW[0].replace("B",""):"")
                            .replace("(W-Content)", (personSW != null && personSW.length > 1 && personSW[0] != null)?personSW[1].replace("W",""):"")
                            .replace("(H-Content)", (personSW != null && personSW.length > 2 && personSW[0] != null)?personSW[2].replace("H",""):"")
                            .replace("(C-Content)", returnMap.get("PersonZB") != null?returnMap.get("PersonZB").replace(" ","").replace("Cup",""):"")
                            .replace("(Star-Content)", returnMap.get("PersonStar") != null?returnMap.get("PersonStar"):"")
                            .replace("(Blood-Content)", returnMap.get("PersonBlood") != null?returnMap.get("PersonBlood"):"")
                            .replace("(County-Content)", returnMap.get("PersonCountry") != null?returnMap.get("PersonCountry"):"")
                            .replace("(Company1-Content)", returnMap.get("") != null?returnMap.get(""):"")
                            .replace("(Company2-Content)", returnMap.get("") != null?returnMap.get(""):"")
                            .replace("(AV Count-Content)", returnMap.get("") != null?returnMap.get(""):"");

                    personM.setDeta_info(date_info);
                    personM.setOther_info(returnMap.get("PersonDescribe"));

                    personM.setScores(1L);
                    personM.setLevels("F");


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DataBaseUtils.closeQuietly(serviceConn);
        }

        return personM;
    }


    public static void main(String[] args) {

        WebMagicOfPersonsUtil util = new WebMagicOfPersonsUtil();
        HVAJapanAVPersonM pp = util.getHPersonSimple("はたの ゆい");

        System.out.println(pp.toString());
        System.out.println(pp.getAllMessageString());

    }

}
