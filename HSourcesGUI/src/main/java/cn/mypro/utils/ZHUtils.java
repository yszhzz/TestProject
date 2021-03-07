package cn.mypro.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

public class ZHUtils {

    private static final int[] SD1_MIN = {0xAA, 0xA1}; //0xAAA1
    private static final int[] SD1_MAX = {0xAF, 0xFE}; //0xAFFE

    private static final int[] SD2_MIN = {0xF8, 0xA1}; //0xF8A1
    private static final int[] SD2_MAX = {0xFE, 0xFE}; //0xFEFE

    private static final int[] SD3_MIN = {0xA1, 0x40}; //0xA140
    private static final int[] SD3_MAX = {0xA7, 0xA0}; //0xA7A0

    private static final Properties properties = new Properties();

    static {
        InputStream is = ZHUtils.class.getClassLoader().getResourceAsStream("zht2s.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            System.out.println("繁体转简体字库加载失败");
            e.printStackTrace();
        }
    }

    public static String questionMarkFilter(String str){
        return str.replaceAll("\\?\\?", "?").replaceAll("？" , "?");
    }
    /**
     * GBK字符过滤 过滤非GBK字符集符号为半角?
     *
     * @param str 需过滤的字符串
     * @return 过滤后字符串
     */
    public static String gbkFilter(String str) {
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            String s = Character.toString(c);
            byte[] bytes = s.getBytes(Charset.forName("GB18030"));
            if (bytes.length > 2) {
                //GB18030四字节字符非GBK字符，替换为半角问号
                sb.append('?');
            } else if (bytes.length == 2) {
                //GBK自定义区汉字替换为半角问号
                int b1 = bytes[0] > 0 ? bytes[0] : (bytes[0] & 0xFF);
                int b2 = bytes[1] > 0 ? bytes[1] : (bytes[1] & 0xFF);
                if (isSelfDefinitionGBK(b1, b2)) {
                    sb.append('?');
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 是否GBK用户自定义区
     *
     * @param b1 GBK字符第一个字节值
     * @param b2 GBK字符第二个字节值
     * @return true or false
     */
    private static boolean isSelfDefinitionGBK(int b1, int b2) {
        if (b1 >= SD1_MIN[0] && b1 <= SD1_MAX[0] && b2 >= SD1_MIN[1] && b2 <= SD1_MAX[1]) {
            return true;
        } else if (b1 >= SD2_MIN[0] && b1 <= SD2_MAX[0] && b2 >= SD2_MIN[1] && b2 <= SD2_MAX[1]) {
            return true;
        } else if (b1 >= SD3_MIN[0] && b1 <= SD3_MAX[0] && b2 >= SD3_MIN[1] && b2 <= SD3_MAX[1]) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 繁体字转换为简体字
     *
     * @param str 待转换字符串
     * @return 转换后字符串
     */
    public static String ZH2Simplified(String str) {
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (properties.containsKey(String.valueOf(c))) {
                sb.append(properties.get(String.valueOf(c)));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 先转换为简体后过滤GBK字符集
     *
     * @param str 待转换字符串
     * @return 转换后字符串
     */
    public static String smplAndGBKFltr(String str) {
        return gbkFilter(ZH2Simplified(str));
    }

    public static String ASCFilter(String str) {

        char[] chars = str.toUpperCase().toCharArray();
        String foreignerXm = "";
        for (char aChar : chars) {

            if (aChar >65 && aChar <=90) foreignerXm = foreignerXm+aChar;
        }

        return foreignerXm;

    }
}
