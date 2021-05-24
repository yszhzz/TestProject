package cn.mypro.swing.util.webmagic;

import cn.mypro.swing.entity.HVAJapanAVM;
import cn.mypro.test.webDemo.d1.HupuNewsSpider;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebMagicUtil implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    public Site getSite() {
        return site;
    }

    private static String selectPath = "https://javdb9.com/search?q=%s&f=all";
    private String runCode = "GS-389";
    private Map<String,String> returnMap = null;


    @Override
    public void process(Page page) {

        returnMap = new HashMap<>();

        //该页面为详情页
        if (page.getUrl().regex("https://javdb9\\.com/v/\\S+").match()) {
            String[] s = page.getHtml().xpath("/html/body/section/div/h3/strong/text()").toString().split(" ");
            returnMap.put("CODE", s[0]);
            returnMap.put("NAME", s[1]);
            returnMap.put("COVER_URL", page.getHtml().xpath("/html/body/section/div/div[4]/div/div/div[1]/a/img/@src").toString());
            returnMap.put("TIME", page.getHtml().xpath("/html/body/section/div/div[4]/div/div/div[2]/nav/div[3]/span/text()").toString());
            returnMap.put("PRIDUCT_DATE", page.getHtml().xpath("/html/body/section/div/div[4]/div/div/div[2]/nav/div[2]/span/text()").toString());
            returnMap.put("DIR", page.getHtml().xpath("/html/body/section/div/div[4]/div/div/div[2]/nav/div[4]/span/a/text()").toString());
            returnMap.put("PRODUCTOR", page.getHtml().xpath("/html/body/section/div/div[4]/div/div/div[2]/nav/div[5]/span/a/text()").toString());
            returnMap.put("ACTOR", page.getHtml().xpath("/html/body/section/div/div[4]/div/div/div[2]/nav/div[7]/span/text()").toString());

            Selectable links = page.getHtml().xpath("/html/body/section/div/div[5]/div/article/div/div");

            List<String> all = links.xpath("//img[@src]/@data-src").all();

            int i = 1;
            for (String s1 : all) {
                returnMap.put("CUT"+i,s1);
            }

        } else {
        //该页面为列表页
            String url = page.getHtml().xpath("/html/body/section/div/div[5]/div/div[1]/a/@href").toString();
            String imgURL = page.getHtml().xpath("/html/body/section/div/div[5]/div/div[1]/a/div[1]/img/@src").toString();
            String code = page.getHtml().xpath("/html/body/section/div/div[5]/div/div[1]/a/div[2]/text()").toString();

            //page.putField("url", );
            //page.putField("imgURL", page.getHtml().xpath("/html/body/section/div/div[5]/div/div[1]/a/div[1]/img/@src").toString());
            //page.putField("code", page.getHtml().xpath("/html/body/section/div/div[5]/div/div[1]/a/div[2]/text()").toString());
            if (runCode.equals(code)) {
                //page.addTargetRequests(page.getHtml().links().regex("(https://javdb9\\.com/v/"+runCode+")").all());
                page.addTargetRequests(page.getHtml().xpath("/html/body/section/div/div[5]/div/div[1]/a/@href").all());
            }

        }

    }

    public HVAJapanAVM getHSources(String runCode, String ImgPath) {

        HVAJapanAVM hvaJapanAVM = null;

        String format = String.format(selectPath, runCode);
        this.runCode = runCode;
        Spider.create(new WebMagicUtil())
                .addUrl(format)
                //.addPipeline(new ConsolePipeline())
                .thread(3).run();

        if (returnMap != null) {
            hvaJapanAVM = new HVAJapanAVM();
            hvaJapanAVM.setOName(returnMap.get("NAME"));

        }

        return hvaJapanAVM;
    }

    public static void main(String[] args) {

        WebMagicUtil webMagicUtil = new WebMagicUtil();
    }




















}
