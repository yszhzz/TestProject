package cn.mypro.utils;

import cn.com.jit.pki.netcert.main.StringSm3Util;
import cn.mypro.utils.exception.SM3CalculateException;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class StringUtils {
    public static boolean equal(String s1, String s2) {
        if (isEmpty(s1)) {
            return isEmpty(s2);
        }
        return s1.equals(s2);
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isBlank(String s) {
        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    public static boolean isGB18030String(String str) {
        if (isBlank(str)) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!isGB18030Char(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isGB18030Char(char c) {
        if (c == '�') {
            return false;
        }
        byte[] bytes = String.valueOf(c).getBytes(Charset.forName("GB18030"));
        int len = bytes.length;
        switch (len) {
            case 1:
                int n = bytes[0] & 0xFF;
                if (n < 0x20 || n > 0x7E) {
                    return false;
                }
                break;
            case 2:
                int d1 = bytes[0] & 0xFF;
                int d2 = bytes[1] & 0xFF;
                if (d1 < 0x81 || d1 > 0xFE) {
                    return false;
                }
                if (d2 < 0x40 || d2 > 0xFE) {
                    return false;
                }
                break;
            case 4:
                int q1 = bytes[0] & 0xFF;
                int q2 = bytes[1] & 0xFF;
                int q3 = bytes[2] & 0xFF;
                int q4 = bytes[3] & 0xFF;
                if (q1 < 0x81 || q1 > 0xFE) {
                    return false;
                }
                if (q2 < 0x30 || q2 > 0x39) {
                    return false;
                }
                if (q3 < 0x81 || q3 > 0xFE) {
                    return false;
                }
                if (q4 < 0x30 || q4 > 0x39) {
                    return false;
                }
                break;
            default:
                return false;
        }
        return true;
    }

    public static String getSM3(String... strings) throws SM3CalculateException {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : strings) {
                stringBuilder.append(string);
            }
            return StringSm3Util.strToSm3UTF16LE(stringBuilder.toString());
        } catch (Exception e) {
            throw new SM3CalculateException("SM3计算出现错误。", e);
        }
    }

    public static String getHexStringSM3(String hexString) throws SM3CalculateException {
        try {
            return StringSm3Util.hexStrToSm3(hexString);
        } catch (Exception e) {
            throw new SM3CalculateException("SM3计算出现错误。", e);
        }
    }

    public static boolean isHexString(String str) {
        return isHexString(str, null);
    }

    public static boolean isHexString(String str, Boolean isUpperCase) {
        if (isEmpty(str)) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (c >= '0' && c <= '9') {
                continue;
            }
            if (isUpperCase == null) {
                c = Character.toUpperCase(c);
                if (c >= 'A' && c <= 'F') {
                    continue;
                }
            } else {
                if (isUpperCase) {
                    if (c >= 'A' && c <= 'F') {
                        continue;
                    }
                } else {
                    if (c >= 'a' && c <= 'f') {
                        continue;
                    }
                }
            }
            return false;
        }
        return true;
    }

    public static boolean isGBKString(String str) {
        return isCharsetString(str, "GBK");
    }

    public static boolean isCharsetString(String string, Charset charset) {
        if (string == null) {
            return false;
        }
        if (string.length() == 0) {
            return false;
        }
        if (string.trim().length() == 0) {
            return false;
        }
        try {
            CharsetDecoder charsetDecoder = charset.newDecoder();
            ByteBuffer byteBuffer = ByteBuffer.wrap(string.getBytes(charset));
            CharBuffer charBuffer = charsetDecoder.decode(byteBuffer);
            String name = charBuffer.toString();
            return name.equals(string);
        } catch (CharacterCodingException e) {
            return false;
        }
    }

    public static boolean isCharsetString(String string, String charsetName) {
        Charset charset = Charset.forName(charsetName);

        return isCharsetString(string, charset);
    }

    public static int getChineseNameCharacterQuality(char c) {
        //Unicode用户自定义区，GB18030未查到具体位置，暂定由Unicode范围确定。
        if (c >= '\uE000' && c <= '\uF8FF') {
            return 70;
        }
        if ('·' == c) {
            return 82;
        }
        if ('.' == c) {
            return 81;
        }
        byte[] bytes = String.valueOf(c).getBytes(Charset.forName("GB18030"));
        int len = bytes.length;
        switch (len) {
            case 1:
                int n = bytes[0] & 0xFF;
                if (n < 0x20 || n > 0x7E) {
                    return 30;
                } else {
                    return 50;
                }
            case 2:
                int d1 = bytes[0] & 0xFF;
                int d2 = bytes[1] & 0xFF;
                //GB2312汉字区
                if (d1 >= 0xB0 && d1 <= 0xF7 && d2 >= 0xA1 && d2 <= 0xFE) {
                    return 96;
                }
                //GBK汉字区a
                if (d1 >= 0x81 && d1 <= 0xA0 && d2 >= 0x40 && d2 <= 0xFE) {
                    return 92;
                }
                //GBK汉字区b
                if (d1 >= 0xAA && d1 <= 0xFE && d2 >= 0x40 && d2 <= 0xA0) {
                    return 92;
                }
                //GBK自造字区(1) AAA1-AFFE，码位 564 个。
                if (d1 >= 0xAA && d1 <= 0xAF && d2 >= 0xA1 && d2 <= 0xFE) {
                    return 70;
                }
                //GBK自造字区(2) F8A1-FEFE，码位 658 个。
                if (d1 >= 0xF8 && d1 <= 0xFE && d2 >= 0xA1 && d2 <= 0xFE) {
                    return 70;
                }
                //GBK自造字区(3) A140-A7A0，码位 672 个。
                if (d1 >= 0xA1 && d1 <= 0xA7 && d2 >= 0x40 && d2 <= 0xA0) {
                    return 70;
                }
                //GB2312符号区A1A1-A9FE
                if (d1 >= 0xA1 && d1 <= 0xA9 && d2 >= 0xA1 && d2 <= 0xFE) {
                    return 50;
                }
                //GBK符号区 A840-A9A0
                if (d1 >= 0xA8 && d1 <= 0xA9 && d2 >= 0x40 && d2 <= 0xA0) {
                    return 50;
                }
                return 30;
            case 4:
                int q1 = bytes[0] & 0xFF;
                int q2 = bytes[1] & 0xFF;
                int q3 = bytes[2] & 0xFF;
                int q4 = bytes[3] & 0xFF;
                //GB18030 CJK统一汉字扩充A
                if (q1 >= 0x81 && q1 <= 0x82 && q2 >= 0x30 && q2 <= 0x39 && q3 >= 0x81 && q3 <= 0xFE && q4 >= 0x30 && q4 <= 0x39) {
                    return 88;
                }
                //GB18030 CJK统一汉字扩充B
                if (q1 >= 0x95 && q1 <= 0x98 && q2 >= 0x30 && q2 <= 0x39 && q3 >= 0x81 && q3 <= 0xFE && q4 >= 0x30 && q4 <= 0x39) {
                    return 84;
                }
                return 30;
            default:
                return 30;
        }
    }
}
