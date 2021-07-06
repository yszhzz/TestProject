package cn.mypro.swing.util.webmagic;

import cn.mypro.swing.dao.HVAJapanAVPersonDao;
import cn.mypro.swing.entity.HVAJapanAVM;
import cn.mypro.swing.entity.HVAJapanAVPersonM;
import cn.mypro.swing.util.file.MyFileUtils;
import cn.mypro.test.webDemo.d1.HupuNewsSpider;
import cn.mypro.utils.DataBaseUtils;
import cn.mypro.utils.DbName;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.swing.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class WebMagicOfSourcesUtil implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    public Site getSite() {
        return site;
    }

    public WebMagicOfSourcesUtil() {
    }

    private static String selectPath = "https://javdb9.com/search?q=%s&f=all";
    private String runCode = "";
    private Map<String,String> returnMap = null;
    private Connection serviceConn = null;

    @Override
    public void process(Page page)  {

        returnMap = new HashMap<>();

        //该页面为详情页
        if (page.getUrl().regex("https://javdb9\\.com/v/\\S+").match()) {
            String[] s = page.getHtml().xpath("/html/body/section/div/h2/strong/text()").toString().split(" ");
            returnMap.put("IF_CODE", s[0]);
            StringBuilder oname = new StringBuilder();
            for (int i = 1; i < s.length; i++) {
                oname.append(s[i]);
            }
            returnMap.put("ONAME", oname.toString());
            returnMap.put("COVER_URL", page.getHtml().xpath("/html/body/section/div/div[4]/div/div[1]/a/img/@src").toString());

            Selectable messageTable = page.getHtml().xpath("/html/body/section/div/div[4]/div/div[2]/nav/");
            List<Selectable> childNodes = messageTable.nodes();

            for (Selectable childNode : childNodes) {

                String tableName = childNode.xpath("strong/text()").toString();
                if (tableName == null) {

                } else if (tableName.startsWith("番號")) {
                    returnMap.put("TestCode",childNode.xpath("span/text()").toString());
                } else if (tableName.startsWith("日期")) {
                    returnMap.put("PRODUCT_DATE",childNode.xpath("span/text()").toString());
                }  else if (tableName.startsWith("導演")) {
                    returnMap.put("ACTOR",childNode.xpath("span/a/text()").toString());
                } else if (tableName.startsWith("片商")) {
                    returnMap.put("PRODUCTION_COMPANY",childNode.xpath("span/a/text()").toString());
                } else if (tableName.startsWith("發行")) {
                    returnMap.put("PUBLISH_COMPANY",childNode.xpath("span/a/text()").toString());
                } else if (tableName.startsWith("時長")) {
                    returnMap.put("TIME",childNode.xpath("span/text()").toString());
                } else if (tableName.startsWith("系列")) {
                    returnMap.put("SERIES",childNode.xpath("span/a/text()").toString());
                } else if (tableName.startsWith("類別")) {

                } else if (tableName.startsWith("演員")) {
                    /*Selectable personTables = childNode.xpath("span/");
                    List<Selectable> nodes = personTables.nodes();*/
                    List<String> all = childNode.xpath("//a/text()").all();
                    StringBuilder persons = new StringBuilder();
                    for (String s1 : all) {
                        if ("N/A".equals(s1)) continue;
                        persons.append(s1+"|");
                    }
                    if (persons.toString().length() > 0) returnMap.put("PERSONS",persons.toString());
                } else if (tableName.startsWith("評分")) {
                    returnMap.put("SCORE",childNode.xpath("span/text()").toString());
                } else {

                }
            }

            //returnMap.put("TIME", page.getHtml().xpath("/html/body/section/div/div[4]/div/div[2]/nav/div[3]/span/text()").toString());
            //returnMap.put("PRODUCT_DATE", page.getHtml().xpath("/html/body/section/div/div[4]/div/div[2]/nav/div[2]/span/text()").toString());
            //returnMap.put("PRODUCTOR", page.getHtml().xpath("/html/body/section/div/div[4]/div/div[2]/nav/div[5]/span/a/text()").toString());
            //returnMap.put("ACTOR", page.getHtml().xpath("/html/body/section/div/div[4]/div/div[2]/nav/div[4]/span/a/text()").toString());

            Selectable links = page.getHtml().xpath("/html/body/section/div/div[5]/div/article/div/div");

/*            List<String> all = links.xpath("//img[@src]/@src").all();//data-src

            int i = 0;
            for (String s1 : all) {
                returnMap.put("CUT"+i,s1);
                i++;
            }*/
            //List<String> all = links.xpath("//img[@data-src]/@data-src").all();
            List<String> all = links.xpath("//A[@href]/@href").all();
            int i = 1;
            for (String s1 : all) {
                if (!s1.startsWith("http")) continue;
                returnMap.put("CUT"+i,s1);
                i++;
            }

        } else {
        //该页面为列表页
            String url = page.getHtml().xpath("/html/body/section/div/div[6]/div/div[1]/a/@href").toString();
            String imgURL = page.getHtml().xpath("/html/body/section/div/div[6]/div/div[1]/a/div[1]/img/@src").toString();
            String code = page.getHtml().xpath("/html/body/section/div/div[6]/div/div[1]/a/div[2]/text()").toString();

            //page.putField("url", );
            //page.putField("imgURL", page.getHtml().xpath("/html/body/section/div/div[5]/div/div[1]/a/div[1]/img/@src").toString());
            //page.putField("code", page.getHtml().xpath("/html/body/section/div/div[5]/div/div[1]/a/div[2]/text()").toString());
            if (runCode.equals(code)) {
                //page.addTargetRequests(page.getHtml().links().regex("(https://javdb9\\.com/v/"+runCode+")").all());
                page.addTargetRequests(page.getHtml().xpath("/html/body/section/div/div[6]/div/div[1]/a/@href").all());
            }

        }

    }

    public HVAJapanAVM getHSources(String runCode, String imgPath, JProgressBar processBar, JTextArea messageRun) {

        HVAJapanAVM hvaJapanAVM = null;

        String format = String.format(selectPath, runCode);
        this.runCode = runCode;
        Spider.create(this)
                .addUrl(format)
                //.addPipeline(new ConsolePipeline())
                .thread(3).run();


        processBar.setValue(30);
        try {
            serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);

            if (returnMap != null) {

                hvaJapanAVM = new HVAJapanAVM();
                hvaJapanAVM.setOName(returnMap.get("ONAME"));
                hvaJapanAVM.setIf_Code(returnMap.get("IF_CODE"));
                if (returnMap.get("PRODUCTION_COMPANY") != null || returnMap.get("ACTOR") != null) hvaJapanAVM.setProduction_company(returnMap.get("PRODUCTION_COMPANY")+"|"+returnMap.get("ACTOR"));
                hvaJapanAVM.setPublish_company(returnMap.get("PUBLISH_COMPANY"));
                hvaJapanAVM.setPublish_time(returnMap.get("PRODUCT_DATE"));
                hvaJapanAVM.setSeries(returnMap.get("SERIES"));
                if (returnMap.get("TIME") != null) {
                    hvaJapanAVM.setDuration(Integer.valueOf(returnMap.get("TIME").split(" ")[0])*60);
                }
                long score = 0;
                if (returnMap.get("SCORE") != null) score = (long) (Float.valueOf(returnMap.get("SCORE").substring(1).replace(" ","").replace("分","").split(",")[0]) *100/5);
                hvaJapanAVM.setScore(score);

                processBar.setValue(40);

                if (returnMap.get("PERSONS") != null) {
                    List<String> personNames = Arrays.asList(returnMap.get("PERSONS").split("\\|"));
                    List<HVAJapanAVPersonM> persons = new ArrayList<>();
                    for (String name : personNames) {
                        HVAJapanAVPersonM hvaJapanAVPersonM = HVAJapanAVPersonDao.qryPersonByNames(serviceConn, name);
                        if (hvaJapanAVPersonM == null) {
                            messageRun.append("["+name+"] 无该人物，请手动添加任务信息！！\n");
                        } else {
                            persons.add(hvaJapanAVPersonM);
                        }
                    }
                    hvaJapanAVM.setPersons(persons);
                }
                processBar.setValue(50);

                MyFileUtils.downloadImgByNet(returnMap.get("COVER_URL"),imgPath,"cover.jpg");
                int picSize = returnMap.size();
                int every = 40 / picSize;
                for (String s : returnMap.keySet()) {
                    if (s.startsWith("CUT")) MyFileUtils.downloadImgByNet(returnMap.get(s),imgPath,s.toLowerCase()+".jpg");
                    processBar.setValue(processBar.getValue() + every);
                }
                //messageRun.append("图片下载成功！  \n");
                processBar.setValue(90);

            } else {
                messageRun.append("获取无数据！！\n");
                processBar.setValue(90);
            }
        } catch (SQLException e) {
            messageRun.append("网路导入出现错误！\n");
            e.printStackTrace();
        } finally {
            DataBaseUtils.closeQuietly(serviceConn);
        }

        return hvaJapanAVM;
    }

}
