package cn.mypro.swing.util.translateAPI;

import cn.mypro.swing.util.translateAPI.secondary.Answer;
import cn.mypro.swing.util.translateAPI.secondary.TransApi;
import cn.mypro.swing.util.translateAPI.secondary.TranslateResult;
import cn.mypro.utils.StringUtils;
import com.alibaba.fastjson.JSON;

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
        TranslateResult translateResult = runTranslate(text);
        if (translateResult == null) return null;
        List<Answer> trans_result = translateResult.getTrans_result();
        if (trans_result == null) return null;
        Answer answer = trans_result.get(0);
        if (answer == null) return null;
        return answer.getDst();
    }

    public static void main(String[] args) {
        String ss = BaiduTranslateUtil.translateAsString("息子の嫁が淫乱でした 夫に隠れて義父の精子を搾り取る妻 木下ひまり");
        System.out.println(ss);
    }

}
