package cn.mypro.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;

public class DateManipulateUtil {
    public static String dateAddYears(String sDate, int n) {
        Date dt;
        try {
            dt = DateFormatterHelp.parseDate(sDate, "yyyyMMdd");
        } catch (ParseException e) {
            return null;
        }
        return dateAddYears(dt, n);
    }

    public static String dateAddYears(Date dt, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.YEAR, n);//日期加n年
        return DateFormatterHelp.formatterDate(calendar.getTime(), "yyyyMMdd");
    }

    public static String dateAddMonths(String sDate, int n) {
        Date dt;
        try {
            dt = DateFormatterHelp.parseDate(sDate, "yyyyMM");
        } catch (ParseException e) {
            return null;
        }
        return dateAddMonths(dt, n);
    }

    public static String dateAddMonths(Date dt, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.MONTH, n);//日期加n年
        return DateFormatterHelp.formatterDate(calendar.getTime(), "yyyyMM");
    }


    public static String dateAddDays(String sDate, int n) {
        Date dt;
        try {
            dt = DateFormatterHelp.parseDate(sDate, "yyyyMMdd");
        } catch (ParseException e) {
            return null;
        }
        return dateAddDays(dt, n);
    }

    public static String dateAddDays(Date dt, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.DAY_OF_MONTH, n);//日期加n天
        return DateFormatterHelp.formatterDate(calendar.getTime(), "yyyyMMdd");
    }

    public static int betweenDays(String startDate, String endDate) {
        LocalDate startLocalDate = DateFormatterHelp.parseLocalDate(startDate, "yyyyMMdd");
        LocalDate endLocalDate = DateFormatterHelp.parseLocalDate(endDate, "yyyyMMdd");
        return (int) (endLocalDate.getLong(ChronoField.EPOCH_DAY) - startLocalDate.getLong(ChronoField.EPOCH_DAY));
    }

    public static int daysBetweenTwo(String dayOne, String dayYwo) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date fDate;
        fDate = sdf.parse(dayOne);
        Date oDate = sdf.parse(dayYwo);
        return (int) ((oDate.getTime() - fDate.getTime()) / (1000 * 3600 * 24));
    }

    public static String timeAddHours(String sTime, int n) {
        Date dt;
        try {
            dt = DateFormatterHelp.parseDate(sTime, "yyyyMMddHHmmss");
        } catch (ParseException e) {
            return null;
        }
        return timeAddHours(dt, n);
    }

    public static String timeAddHours(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, n);//时间加n小时
        return DateFormatterHelp.formatterDate(calendar.getTime(), "yyyyMMddHHmmss");
    }

    public static String timeAddSeconds(String sTime, int n) {
        Date dt;
        try {
            dt = DateFormatterHelp.parseDate(sTime, "yyyyMMddHHmmss");
        } catch (ParseException e) {
            return null;
        }
        return timeAddSeconds(dt, n);
    }

    public static String timeAddSeconds(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, n);//时间加n小时
        return DateFormatterHelp.formatterDate(calendar.getTime(), "yyyyMMddHHmmss");
    }
}
