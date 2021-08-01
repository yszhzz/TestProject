package cn.mypro.swing.util.webmagic;

import cn.mypro.swing.constant.LabelConstant;
import cn.mypro.swing.dao.HVAJapanAVPersonDao;
import cn.mypro.swing.entity.HVAJapanAVM;
import cn.mypro.swing.entity.HVAJapanAVPersonM;
import cn.mypro.swing.util.file.MyFileUtils;
import cn.mypro.test.webDemo.d1.HupuNewsSpider;
import cn.mypro.utils.DataBaseUtils;
import cn.mypro.utils.DbName;
import cn.mypro.utils.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WebMagicOfSourcesUtil implements PageProcessor {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    public Site getSite() {
        return site;
    }

    public WebMagicOfSourcesUtil() {
    }

    private static String selectPath = "https://javdb9.com/search?q=%s&f=all";
    private String runCode = "";
    private String clearCode = "";
    private Map<String,String> returnMap = null;
    private Connection serviceConn = null;

    @Override
    public void process(Page page)  {

        returnMap = new HashMap<>();

        //该页面为详情页
        if (page.getUrl().regex("https://javdb9\\.com/v/\\S+").match()) {
            String[] s = page.getHtml().xpath("/html/body/section/div/h2/strong/text()").toString().split(" ");
            //returnMap.put("IF_CODE", s[0]);
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
            returnMap.put("CUTCOUNT",i -1 + "");

        } else {
        //该页面为列表页
            String url = page.getHtml().xpath("/html/body/section/div/div[6]/div/div[1]/a/@href").toString();
            String imgURL = page.getHtml().xpath("/html/body/section/div/div[6]/div/div[1]/a/div[1]/img/@src").toString();
            String code = page.getHtml().xpath("/html/body/section/div/div[6]/div/div[1]/a/div[2]/text()").toString();

            //page.putField("url", );
            //page.putField("imgURL", page.getHtml().xpath("/html/body/section/div/div[5]/div/div[1]/a/div[1]/img/@src").toString());
            //page.putField("code", page.getHtml().xpath("/html/body/section/div/div[5]/div/div[1]/a/div[2]/text()").toString());
            if (code != null) {
                if (runCode.equals(code) || runCode.contains(code) || clearCode.equals(code) || clearCode.contains(code) || code.contains(clearCode) || code.contains(runCode)) {
                    //page.addTargetRequests(page.getHtml().links().regex("(https://javdb9\\.com/v/"+runCode+")").all());
                    page.addTargetRequests(page.getHtml().xpath("/html/body/section/div/div[6]/div/div[1]/a/@href").all());
                }
            }


        }

    }

    public HVAJapanAVM getHSources(String runCode, String imgPath, JProgressBar processBar, JTextArea messageRun) {

        HVAJapanAVM hvaJapanAVM = null;
        this.runCode = runCode;
        this.clearCode = clearCode(runCode);
        String format = String.format(selectPath, clearCode);

        Spider.create(this)
                .addUrl(format)
                //.addPipeline(new ConsolePipeline())
                .thread(3).run();


        processBar.setValue(30);
        try {
            serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);

            if (returnMap != null) {

                runMessagePrint(messageRun,"数据爬取完毕！！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);

                hvaJapanAVM = new HVAJapanAVM();
                hvaJapanAVM.setOName(returnMap.get("ONAME"));
                //hvaJapanAVM.setIf_Code(returnMap.get("IF_CODE"));
                hvaJapanAVM.setIf_Code(runCode);
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
                            runMessagePrint(messageRun,"["+name+"] 无该人物，请手动添加人物信息！！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                        } else {
                            persons.add(hvaJapanAVPersonM);
                        }
                    }
                    hvaJapanAVM.setPersons(persons);
                }
                processBar.setValue(50);
                runMessagePrint(messageRun,"开始获取封面图片！！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                MyFileUtils.downloadImgByNet(returnMap.get("COVER_URL"),imgPath,"cover.jpg");
                int picSize = 1;
                try {
                    picSize = Integer.valueOf(returnMap.get("CUTCOUNT"));
                } catch (Exception es) {
                    es.printStackTrace();
                }

                int every = picSize == 0 ? 0:40 / picSize;
                runMessagePrint(messageRun,"开始获取截图，共["+picSize+"]个！！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                for (String s : returnMap.keySet()) {
                    if (s.startsWith("CUT")) MyFileUtils.downloadImgByNet(returnMap.get(s),imgPath,s.toLowerCase()+".jpg");
                    processBar.setValue(processBar.getValue() + every);
                }
                //messageRun.append("图片下载成功！  \n");
                processBar.setValue(90);

            } else {
                runMessagePrint(messageRun,"获取无数据！！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                processBar.setValue(90);
            }
        } catch (SQLException e) {
            runMessagePrint(messageRun,"网路导入出现错误！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
        } finally {
            DataBaseUtils.closeQuietly(serviceConn);
        }

        if (returnMap != null) returnMap.clear();
        return hvaJapanAVM;
    }

    public HVAJapanAVM getHSourceBatch(String runCode, String imgPath, JProgressBar processBar, JTextArea messageRun) {

        HVAJapanAVM hvaJapanAVM = null;

        this.runCode = runCode;
        this.clearCode = clearCode(runCode);
        String format = String.format(selectPath, clearCode);

        Spider.create(this)
                .addUrl(format)
                //.addPipeline(new ConsolePipeline())
                .thread(3).run();


        processBar.setValue(20);
        try {
            serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);

            if (returnMap != null && returnMap.size() != 0) {

                runMessagePrint(messageRun,"数据爬取完毕！！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);

                hvaJapanAVM = new HVAJapanAVM();
                hvaJapanAVM.setOName(returnMap.get("ONAME"));
                //hvaJapanAVM.setIf_Code(returnMap.get("IF_CODE"));
                hvaJapanAVM.setIf_Code(runCode);
                if (returnMap.get("PRODUCTION_COMPANY") != null || returnMap.get("ACTOR") != null) hvaJapanAVM.setProduction_company(returnMap.get("PRODUCTION_COMPANY")+"|"+returnMap.get("ACTOR"));
                hvaJapanAVM.setPublish_company(returnMap.get("PUBLISH_COMPANY"));
                if (returnMap.get("PRODUCT_DATE") != null)  hvaJapanAVM.setPublish_time(returnMap.get("PRODUCT_DATE").replace("-",""));
                hvaJapanAVM.setSeries(returnMap.get("SERIES"));
                if (returnMap.get("TIME") != null) {
                    hvaJapanAVM.setDuration(Integer.valueOf(returnMap.get("TIME").split(" ")[0])*60);
                }
                long score = 0;
                if (returnMap.get("SCORE") != null) score = (long) (Float.valueOf(returnMap.get("SCORE").substring(1).replace(" ","").replace("分","").split(",")[0]) *100/5);
                hvaJapanAVM.setScore(score);

                processBar.setValue(30);

                if (returnMap.get("PERSONS") != null) {
                    List<String> personNames = Arrays.asList(returnMap.get("PERSONS").split("\\|"));
                    List<HVAJapanAVPersonM> persons = new ArrayList<>();
                    List<String> notExistsPersons = new ArrayList<>();
                    for (String name : personNames) {
                        HVAJapanAVPersonM hvaJapanAVPersonM = HVAJapanAVPersonDao.qryPersonByNames(serviceConn, name);
                        if (hvaJapanAVPersonM == null) {
                            runMessagePrint(messageRun,"["+name+"] 无该人物，请手动添加人物信息！！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                            notExistsPersons.add(name);
                        } else {
                            persons.add(hvaJapanAVPersonM);
                        }
                    }
                    hvaJapanAVM.setPersons(persons);
                    hvaJapanAVM.setNotExistsPerson(notExistsPersons);
                }
                processBar.setValue(40);

                boolean cover_success = MyFileUtils.downloadImgByNet(returnMap.get("COVER_URL"), imgPath, "cover.jpg");
                if (cover_success) {
                    File coverFile = new File(LabelConstant.DEFAULT_FILE_PATH + "\\" + hvaJapanAVM.getIf_Code().replace(" ", "") + "\\" + "cover.jpg");
                    if (coverFile.exists() && coverFile.isFile()) {
                        hvaJapanAVM.setCover(MyFileUtils.getBytesFromFile(coverFile));
                        //System.out.println("已赋值："+hvaJapanAVM.getCover());
                    }
                }

                runMessagePrint(messageRun,"开始获取封面图片！！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                MyFileUtils.downloadImgByNet(returnMap.get("COVER_URL"), imgPath, "cover.jpg");

                int picSize = 1;
                try {
                    picSize = Integer.valueOf(returnMap.get("CUTCOUNT"));
                } catch (Exception es) {
                    es.printStackTrace();
                }
                int every = picSize == 0 ? 0:40 / picSize;
                runMessagePrint(messageRun,"开始获取截图，共["+picSize+"]个！！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                int i = 0;
                for (String s : returnMap.keySet()) {
                    if (s.startsWith("CUT")) {
                        boolean cut_success = MyFileUtils.downloadImgByNet(returnMap.get(s), imgPath, s.toLowerCase() + ".jpg");

                        if (cut_success && i <= 8) {
                            File cutFile = new File(LabelConstant.DEFAULT_FILE_PATH + "\\" + hvaJapanAVM.getIf_Code().replace(" ", "") + "\\" + s.toLowerCase() + ".jpg");
                            if (cutFile.exists() && cutFile.isFile()) {
                                if (i == 0) hvaJapanAVM.setCut1(MyFileUtils.getBytesFromFile(cutFile));
                                if (i == 1) hvaJapanAVM.setCut2(MyFileUtils.getBytesFromFile(cutFile));
                                if (i == 2) hvaJapanAVM.setCut3(MyFileUtils.getBytesFromFile(cutFile));
                                if (i == 3) hvaJapanAVM.setCut4(MyFileUtils.getBytesFromFile(cutFile));
                                if (i == 4) hvaJapanAVM.setCut5(MyFileUtils.getBytesFromFile(cutFile));
                                if (i == 5) hvaJapanAVM.setCut6(MyFileUtils.getBytesFromFile(cutFile));
                                if (i == 6) hvaJapanAVM.setCut7(MyFileUtils.getBytesFromFile(cutFile));
                                if (i == 7) hvaJapanAVM.setCut8(MyFileUtils.getBytesFromFile(cutFile));
                                if (i == 8) hvaJapanAVM.setCut9(MyFileUtils.getBytesFromFile(cutFile));

                                i++;
                            }

                        }
                    }
                    processBar.setValue(processBar.getValue() + every);
                }
                //messageRun.append("图片下载成功！  \n");
                processBar.setValue(80);

            } else {
                runMessagePrint(messageRun,"获取无数据！！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                processBar.setValue(90);
            }
        } catch (SQLException e) {
            runMessagePrint(messageRun,"网路导入出现错误！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
            e.printStackTrace();
        } catch (IOException e) {
            runMessagePrint(messageRun,"IO错误！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
            e.printStackTrace();
        } finally {
            DataBaseUtils.closeQuietly(serviceConn);
        }
        if (returnMap != null) returnMap.clear();
        return hvaJapanAVM;
    }

    private void runMessagePrint(JTextArea textArea,String message,String model) {
        String time_text = sdf.format(System.currentTimeMillis());
        if (LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT.equals(model)) {
            textArea.append(message);
        } else if (LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT.equals(model)){
            textArea.append("["+time_text+"]: ");
            textArea.append(message);
        }
        textArea.paintImmediately(textArea.getBounds());
    }

    private String clearCode(String code) {

        if (StringUtils.isEmpty(code)) return null;


        if (code.startsWith(LabelConstant.IF_COD_ERROR_PREFIX_1PONDO)) {
            return code.split("-")[1];
        } else if (code.startsWith(LabelConstant.IF_COD_ERROR_PREFIX_10MUSUME)) {
            return code.split("-")[1];
        } else if (code.startsWith(LabelConstant.IF_COD_ERROR_PREFIX_CARIBBEAN)) {
            return code.split("-")[1];
        } else if (code.startsWith(LabelConstant.IF_COD_ERROR_PREFIX_FC2)) {
            if (code.contains("PPV")) return code.split("-")[2];
            return code.split("-")[1];
        }/* else if (code.startsWith(LabelConstant.IF_COD_ERROR_PREFIX_HEYZO)) {
            return code.split("-")[1];
        }*/ else {

        }
        return code;
    }

}
