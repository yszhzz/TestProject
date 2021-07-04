package cn.mypro.swing.util.webmagic;

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

            Map<String,String> returnMap = new HashMap<>();

            String h2 = page.getHtml().xpath("/html/body/div[1]/div[3]/div/h2[1]/text()").toString();
            String[] split = h2.split("\\(");
            returnMap.put("PersonOName", split[0]);
            returnMap.put("PersonEName", split[1].split("\\)")[0].split("/")[0]);
            returnMap.put("PersonAge", split[1].split("\\)")[0].split("/")[1]);

            
            List<String> namesList = page.getHtml().xpath("//span[@itemprop=additionalName]/text()").all();

            System.out.println(namesList.toString());
            //List<String> namesList = names.xpath("span/text()").all();
            StringBuilder nss = new StringBuilder();
            for (String s : namesList) {
                nss.append(s).append("|");
            }
            returnMap.put("PersonOtherNames",nss.toString());

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
            StringBuilder movies= new StringBuilder();
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

            for (String o : returnMap.keySet()) {
                System.out.println(o + ":" + returnMap.get(o) + "\n");
            }

        } else {
            //该页面为列表页
            //List<String> all = page.getHtml().xpath("/html/body/ul/").xpath("//a[@href]/@href").all();
            page.addTargetRequests(page.getHtml().xpath("/html/body/ul/").xpath("//a[@href]/@href").all());

        }

    }

/*
    public List<HVAJapanAVPersonM> getHSources(String runCode, String imgPath, JProgressBar processBar, JTextArea messageRun) {

        List<HVAJapanAVPersonM> lists = null;

        String format = String.format(selectPath, runCode);
        this.runName = runCode;

        Spider.create(this)
                .addUrl(format)
                //.addPipeline(new ConsolePipeline())
                .thread(3).run();


        processBar.setValue(40);
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

//                        "PersonBirthday",
//                        "PersonSW",
//                        "PersonZB",
//                        "PersonOutTime",
//                        "PersonStar",
//                        "PersonBlood",
//                        "PersonHigh",
//                        "PersonCountry",
//                        "PersonDescribe",

                        personM.setOname(returnMap.get("ONAME"));
                        hvaJapanAVM.setIf_Code(returnMap.get("IF_CODE"));
                        if (returnMap.get("PRODUCTION_COMPANY") != null || returnMap.get("ACTOR") != null)
                            hvaJapanAVM.setProduction_company(returnMap.get("PRODUCTION_COMPANY") + "|" + returnMap.get("ACTOR"));
                        hvaJapanAVM.setPublish_company(returnMap.get("PUBLISH_COMPANY"));
                        hvaJapanAVM.setPublish_time(returnMap.get("PRODUCT_DATE"));
                        hvaJapanAVM.setSeries(returnMap.get("SERIES"));
                        if (returnMap.get("TIME") != null) {
                            hvaJapanAVM.setDuration(Integer.valueOf(returnMap.get("TIME").split(" ")[0]) * 60);
                        }
                        long score = 0;
                        if (returnMap.get("SCORE") != null)
                            score = (long) (Float.valueOf(returnMap.get("SCORE").substring(1).replace(" ", "").replace("分", "").split(",")[0]) * 100 / 5);
                        hvaJapanAVM.setScore(score);

                        processBar.setValue(50);
                        System.out.println(returnMap.get("PERSONS"));
                        if (returnMap.get("PERSONS") != null) {
                            List<String> personNames = Arrays.asList(returnMap.get("PERSONS").split("\\|"));
                            List<HVAJapanAVPersonM> persons = new ArrayList<>();
                            for (String name : personNames) {
                                HVAJapanAVPersonM hvaJapanAVPersonM = HVAJapanAVPersonDao.qryPersonByNames(serviceConn, name);
                                if (hvaJapanAVPersonM == null) {
                                    messageRun.append("[" + name + "] 无该人物，请手动添加任务信息！！\n");
                                } else {
                                    persons.add(hvaJapanAVPersonM);
                                }
                            }
                            hvaJapanAVM.setPersons(persons);
                        }
                        processBar.setValue(60);

                        MyFileUtils.downloadImgByNet(returnMap.get("COVER_URL"), imgPath, "cover.jpg");
                        for (String s : returnMap.keySet()) {
                            if (s.startsWith("CUT"))
                                MyFileUtils.downloadImgByNet(returnMap.get(s), imgPath, s.toLowerCase() + ".jpg");
                        }
                        messageRun.append("图片下载成功!\n");
                        processBar.setValue(90);

                    }
                }

            }

        } catch (SQLException e) {
            messageRun.append("网路导入出现错误！\n");
            e.printStackTrace();
        } finally {
            DataBaseUtils.closeQuietly(serviceConn);
        }

        return hvaJapanAVM;
    }
*/

    public static void main(String[] args) {

        WebMagicOfPersonsUtil util = new WebMagicOfPersonsUtil();
        String runName = "明里つむぎ";
        String format = String.format(selectPath, runName);
        Spider.create(util)
                .addUrl(format)
                //.addPipeline(new ConsolePipeline())
                .thread(3).run();

    }

}
