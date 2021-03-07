package cn.mypro.utils;

import cn.mypro.utils.exception.DateStringParseException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IDUtils {
    private static final int[] xs = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] crc = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    private static final Set<String> provinceSet = new HashSet<>(Arrays.asList("11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "81", "82", "83"));
    //private static final Set<String> provinceSet = new HashSet<String>(Arrays.asList(provinces));
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT);

//    static {
//        Collections.addAll(provinceSet, provinces);
//    }

    public static String convertID15to18(String idNum) {
        char[] result = new char[18];
        char[] input = idNum.toCharArray();
        for (int i = 0; i < input.length; i++) {
            if (i <= 5) {
                result[i] = input[i];
            } else {
                result[i + 2] = input[i];
            }
        }
        //1999年7月1日开始身份证号码统一改为18位
        result[6] = '1';
        result[7] = '9';
        //计算最后一位
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (result[i] - 0x30) * xs[i];
        }
        //对11求余，的余数 0 - 10
        //所得余数映射到对应数字即可
        result[17] = crc[sum % 11];
        return String.copyValueOf(result);
    }

    public static boolean isIDNo18(String idNo) {
        return checkIDNo18(idNo) == 0;
    }

    public static String getGenderByIdNo(String idNo) {
        int n = Integer.valueOf(idNo.substring(16, 17)) % 2;
        return n == 0 ? "2" : "1";
    }

    public static String getBirthDate(String idNo) {
        return idNo.substring(6, 14);
    }

    public static int checkIDNo18(String idNo) {
        if (idNo == null || idNo.length() != 18) {
            return -1;
        }
        if (!provinceSet.contains(idNo.substring(0, 2))) {
            return -2;
        }
        try {
            LocalDate birthDate = LocalDate.parse(idNo.substring(6, 14), dateFormatter);
            if (birthDate.isAfter(LocalDate.now())) {
                return -4;
            }
        } catch (DateTimeParseException dte) {
            return -3;
        }
        char[] chars = idNo.toCharArray();
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            if (chars[i] > 0x39 || chars[i] < 0x30) {
                return -5;
            }
            sum += (chars[i] - 0x30) * xs[i];
        }
        char crcChar = crc[sum % 11];
        if (crcChar == 'X' && chars[17] == 'x') {
            return 1;
        }
        if (crcChar != chars[17]) {
            return -6;
        }
        return 0;
    }

    public static int checkName(String name) {
        if (StringUtils.isBlank(name)) {
            return -1;
        }
        for (char c : name.toCharArray()) {
            if (StringUtils.getChineseNameCharacterQuality(c) < 60) {
                return -2;
            }
        }
        return 0;
    }

    public static boolean nameNeedUpdate(String oldName, String newName) {
        if (StringUtils.isEmpty(newName)) {
            return false;
        }
        if (StringUtils.isEmpty(oldName)) {
            return true;
        }
        if (checkName(oldName) != 0 && checkName(newName) == 0) {
            return true;
        }
        if (oldName.length() != newName.length()) {
            return false;
        }
        Boolean needUpdate = null;
        for (int i = 0; i < oldName.length(); i++) {
            if (oldName.charAt(i) != newName.charAt(i)) {
                int better = StringUtils.getChineseNameCharacterQuality(newName.charAt(i)) - StringUtils.getChineseNameCharacterQuality(oldName.charAt(i));
                if (better > 0) {
                    if (needUpdate == null) {
                        needUpdate = Boolean.TRUE;
                    } else if (Boolean.FALSE.equals(needUpdate)) {
                        return false;
                    }
                } else if (better < 0) {
                    if (needUpdate == null) {
                        needUpdate = Boolean.FALSE;
                    } else if (Boolean.TRUE.equals(needUpdate)) {
                        return false;
                    }
                }
            }
        }
        if (needUpdate == null) {
            return false;
        } else {
            return needUpdate;
        }
    }

    public static int checkSignDate(String signDateString, String birthDateString) {
        if (StringUtils.isBlank(signDateString) || StringUtils.isBlank(birthDateString)) {
            return -1;
        }
        try {
            LocalDate signDate = LocalDate.parse(signDateString, dateFormatter);
            LocalDate birthDate = LocalDate.parse(birthDateString, dateFormatter);
            if (signDate.isBefore(birthDate)) {
                return -3;
            }
            if (signDate.isAfter(LocalDate.now())) {
                return -4;
            }
            return 0;
        } catch (DateTimeParseException dte) {
            return -2;
        }
    }

    public static int checkExpireDate(String expireDateString, String birthDateString, String signDateString) {
        LocalDate signDate = LocalDate.parse(signDateString, dateFormatter);
        LocalDate birthDate = LocalDate.parse(birthDateString, dateFormatter);
        int years = signDate.getYear() - birthDate.getYear();
        LocalDate fullYearDate = birthDate.plusYears(years);
        int age = signDate.isBefore(fullYearDate) ? years - 1 : years;
        if ("99991231".equals(expireDateString) && age > 45) {
            return 1;
        }
        if (expireDateString == null || expireDateString.length() < 6) {
            return 2;
        }
        boolean patchDate = false;
        if (expireDateString.length() < 8) {
            expireDateString = expireDateString + signDateString.substring(expireDateString.length());
            patchDate = true;
        }
        LocalDate expireDate;
        try {
            if ("00000000".equals(expireDateString)) {
                expireDate = LocalDate.MAX;
            } else {
                expireDate = LocalDate.parse(expireDateString, dateFormatter);
            }
        } catch (DateTimeParseException dte) {
            if (expireDateString.endsWith("0229")) {
                return -1;
            } else {
                return 4;
            }
        }
        LocalDate caculateExpireDate = calculateExpireDate(signDate, birthDate);
        if (caculateExpireDate.isEqual(expireDate)) {
            return patchDate ? 3 : 0;
        }
        long daysBetween = caculateExpireDate.toEpochDay() - expireDate.toEpochDay();
        if (daysBetween >= -7 && daysBetween <= 7) {
            return 0;
        }
        int validPeriod = getValidPeriod(signDate, expireDate);
        if (validPeriod < 0) {
            return validPeriod - 1;
        }
        int caculateValidPeriod = caculateValidPeriod(birthDate, signDate);
        int validPeriodBetweens = caculateValidPeriod - validPeriod;
        return (validPeriodBetweens == -1 || validPeriodBetweens == 1) ? -4 : -5;
    }

    public static String calculateExpireDate(String signDateString, String birthDateString) {
        LocalDate signDate = LocalDate.parse(signDateString, dateFormatter);
        LocalDate birthDate = LocalDate.parse(birthDateString, dateFormatter);
        LocalDate caculateExpireDate = calculateExpireDate(signDate, birthDate);
        if (caculateExpireDate.equals(LocalDate.MAX)) {
            return "00000000";
        }
        return caculateExpireDate.format(dateFormatter);
    }

    public static boolean isDateRevesed(String birthDateString, String signDateString, String expireDateString) throws DateStringParseException {
        LocalDate birthDate = getLocalDateByString(birthDateString);
        LocalDate signDate = getLocalDateByString(signDateString);
        LocalDate expireDate = getLocalDateByString(expireDateString);
        if (signDate.isAfter(birthDate) && expireDate.isAfter(signDate)) {
            return false;
        } else {
            LocalDate tempDate = signDate;
            signDate = expireDate;
            expireDate = tempDate;
            return signDate.isAfter(birthDate) && signDate.isBefore(LocalDate.now()) && isExpireDateValid(expireDate, birthDate, signDate);
        }
    }

    private static boolean isExpireDateValid(LocalDate expireDate, LocalDate birthDate, LocalDate signDate) {
        LocalDate caculateExpireDate = calculateExpireDate(signDate, birthDate);
        if (caculateExpireDate.isEqual(expireDate)) {
            return true;
        }
        if (caculateExpireDate.isEqual(LocalDate.MAX) || expireDate.isEqual(LocalDate.MAX)) {
            return false;
        }
        long daysBetween = caculateExpireDate.toEpochDay() - expireDate.toEpochDay();
        return daysBetween >= -7 && daysBetween <= 7;
    }


    public static LocalDate getLocalDateByString(String dateString) throws DateStringParseException {
        if ("00000000".equals(dateString)) {
            return LocalDate.MAX;
        } else {
            try {
                return LocalDate.parse(dateString, dateFormatter);
            } catch (DateTimeParseException | NullPointerException dte) {
                throw new DateStringParseException(String.format("[%s]非要求的时间格式", dateString), dte);
            }
        }
    }

    public static LocalDate calculateExpireDate(LocalDate signDate, LocalDate birthDate) {
        int years = signDate.getYear() - birthDate.getYear();
        LocalDate fullYearDate = birthDate.plusYears(years);
        int age = signDate.isBefore(fullYearDate) ? years - 1 : years;
        LocalDate caculateExpireDate;
        if (age < 16) {
            caculateExpireDate = signDate.plusYears(5L);
        } else if (age <= 25) {
            caculateExpireDate = signDate.plusYears(10L);
        } else if (age <= 45) {
            caculateExpireDate = signDate.plusYears(20L);
        } else {
            caculateExpireDate = LocalDate.MAX;
        }
        return caculateExpireDate;
    }

    private static int getValidPeriod(LocalDate signDate, LocalDate expireDate) {
        if (expireDate.equals(LocalDate.MAX)) {
            return 4;
        }
        int days = signDate.getDayOfYear() - expireDate.getDayOfYear();
        if (days > 7 || days < -7) {
            return -1;
        }
        int years = expireDate.getYear() - signDate.getYear();
        if (years == 5) {
            return 1;
        }
        if (years == 10) {
            return 2;
        }
        if (years == 20) {
            return 3;
        }
        return -2;
    }

    private static int caculateValidPeriod(LocalDate birthDate, LocalDate signDate) {
        int years = signDate.getYear() - birthDate.getYear();
        LocalDate fullYearDate = birthDate.plusYears(years);
        int age = signDate.isBefore(fullYearDate) ? years - 1 : years;
        if (age < 16) {
            return 1;
        } else if (age <= 25) {
            return 2;
        } else if (age <= 45) {
            return 3;
        } else {
            return 4;
        }
    }

    public static String cleaningDate(String dateString) {
        if ("长期".equals(dateString)) {
            return "00000000";
        }
        //null/空/只有空格
        if (StringUtils.isBlank(dateString)) {
            return "";
        }
        //去掉非数字 取前8位
        String result = dateString.replaceAll("[^0-9]", "");
        if (result.length() > 8) {
            result = result.substring(0, 8);
        }
        return result;
    }
}
