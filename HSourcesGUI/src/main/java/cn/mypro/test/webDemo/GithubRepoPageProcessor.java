package cn.mypro.test.webDemo;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;


public class GithubRepoPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(2).setSleepTime(100);

    private static String selectPath = "https://javdb9.com/search?q=%s&f=all";

    @Override
    public void process(Page page) {

/*
        page.putField("selectCode", page.getHtml().xpath("//div[@class='uid']/text()"));
        page.putField("path", page.getHtml().xpath("//div[@class='uid']/text()"));

        String name = page.getResultItems().get("name");
        System.out.println(name);
        if (name != null) {
            if ("GS-389".equals(name)) {
                page.addTargetRequests(page.getHtml().links().regex("(https://javdb9\\.com/v/"++")").all());
            }
        }


        page.putField("ifcode", page.getHtml().xpath("//h1[@class='title is-4']/strong/a/text()"));
*/



        //后续发现的url进行抽取
        page.addTargetRequests(page.getHtml().links().regex("(https://javdb9\\.com/v/\\w+)").all());
        page.putField("author", page.getUrl().regex("https://javdb9.com").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        String format = String.format(selectPath, "GS-389");

        Spider.create(new GithubRepoPageProcessor())
                .addUrl(format)
                .addPipeline(new ConsolePipeline())
                .thread(5)
                .run();
    }
}