package cn.mypro.swing.util;

import cn.mypro.swing.util.translateAPI.TransApi;
import cn.mypro.swing.util.translateAPI.TranslateResult;
import cn.mypro.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BaiduTranslateUtil {

    private static final String APP_ID = "20210418000788077";
    private static final String SECURITY_KEY = "qh0A3qZZuVtz6b4umtsz";

    public static TranslateResult runTranslate(String text) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String transResult = api.getTransResult(text, "auto", "zh");
        TranslateResult translateResult = JSON.parseObject(transResult, TranslateResult.class);
        return translateResult;
    }

    public static String translateAsString(String text) {
        if (StringUtils.isEmpty(text)) return null;
        return runTranslate(text).getTrans_result().get(0).getDst();
    }

    public static void main(String[] args) {
        String ss = BaiduTranslateUtil.translateAsString("はたの ゆい");
        System.out.println(ss);
    }

}
