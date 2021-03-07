package cn.mypro.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateFormatterHelp {
    public static final String LONG_TERM_DATE = "00000000";

    private static Map<String, SimpleDateFormat> formatMap = new HashMap<>();
    private static Map<String, DateTimeFormatter> formatMapLocal = new HashMap<>();


    public static String formatterDate(Date date, String formatterStr) {
        if (formatMap.containsKey(formatterStr)) {
            return formatMap.get(formatterStr).format(date);
        } else {
            formatMap.put(formatterStr, new SimpleDateFormat(formatterStr));
            return formatMap.get(formatterStr).format(date);
        }
    }

    public static Date parseDate(String dateStr, String formatterStr) throws ParseException {
        if (formatMap.containsKey(formatterStr)) {
            return formatMap.get(formatterStr).parse(dateStr);
        } else {
            formatMap.put(formatterStr, new SimpleDateFormat(formatterStr));
            return formatMap.get(formatterStr).parse(dateStr);
        }
    }

    public static LocalDate parseLocalDate(String dateStr, String formatterStr) {
        if (formatMapLocal.containsKey(formatterStr)) {
            return LocalDate.parse(dateStr, formatMapLocal.get(formatterStr));
        } else {
            formatMapLocal.put(formatterStr, DateTimeFormatter.ofPattern(formatterStr));
            return LocalDate.parse(dateStr, formatMapLocal.get(formatterStr));
        }
    }

    public static String toDurationTimeString(long durationTimeInMillis) {
        long d = durationTimeInMillis / 86400000;
        long h = (durationTimeInMillis - (d * 86400000)) / 3600000;
        long m = (durationTimeInMillis - (d * 86400000) - (h * 3600000)) / 60000;
        long s = (durationTimeInMillis - (d * 86400000) - (h * 3600000) - (m * 60000)) / 1000;
        long ms = durationTimeInMillis - (d * 86400000) - (h * 3600000) - (m * 60000) - (s * 1000);
        StringBuilder stringBuilder = new StringBuilder();
        if (d > 0) {
            stringBuilder.append(d).append("天");
        }
        if (h > 0) {
            stringBuilder.append(h).append("小时");
        }
        if (m > 0) {
            stringBuilder.append(m).append("分钟");
        }
        if (s > 0) {
            stringBuilder.append(s).append("秒");
        }
        if (ms > 0) {
            stringBuilder.append(ms).append("毫秒");
        }
        return stringBuilder.toString();
    }

    static {
        formatMap.put("yyyyMMdd", new SimpleDateFormat("yyyyMMdd"));
        formatMap.put("yyyyMMddHHmmss", new SimpleDateFormat("yyyyMMddHHmmss"));
    }

    static {
        formatMapLocal.put("yyyyMMdd", DateTimeFormatter.ofPattern("yyyyMMdd"));
        formatMapLocal.put("yyyyMMddHHmmss", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    public static String cleaningDate(String dateString) {
        if ("长期".equals(dateString)) {
            return LONG_TERM_DATE;
        }
        if (dateString == null || dateString.length() == 0) {
            return "";
        }
        String result = dateString.replaceAll("[^0-9]", "");
        if (result.length() > 8) {
            result = result.substring(0, 8);
        }
        return result;
    }
}
