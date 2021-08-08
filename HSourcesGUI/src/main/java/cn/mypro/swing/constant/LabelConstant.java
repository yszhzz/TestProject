package cn.mypro.swing.constant;

import org.apache.bcel.generic.PUTFIELD;

import java.util.HashMap;
import java.util.Map;

public class LabelConstant {

    public static final String[] LABEL_LEVEL_1_SHOW = new String[]{"请选择", "J-剧情", "C-人物", "R-关系", "S-环境", "X-性爱", "O-其他"};

    public static final String[] LABEL_LEVEL_2_J_SHOW = new String[]{"请选择", "F-氛围", "M-模式", "H-情节"};
    public static final String[] LABEL_LEVEL_2_C_SHOW = new String[]{"请选择", "S-社会角色", "A-年龄角色", "I-身份角色", "F-特征角色"};
    public static final String[] LABEL_LEVEL_2_R_SHOW = new String[]{"请选择", "S-社会关系", "C-人际关系", "I-身份关系"};
    public static final String[] LABEL_LEVEL_2_S_SHOW = new String[]{"请选择", "P-场所", "S-服装", "T-道具"};
    public static final String[] LABEL_LEVEL_2_X_SHOW = new String[]{"请选择", "Z-姿势", "T-特征", "A-行为"};
    public static final String[] LABEL_LEVEL_2_O_SHOW = new String[]{"请选择", "F-其他"};

    public static final Integer[] Person_Score = new Integer[100];
    public static final String[] Person_Level = new String[]{"", "SSS", "SS", "S", "A", "B", "C", "D", "E", "F", "G"};

    public static final String[] AV_LANGUAGE = new String[]{"JAP", "CHN", "EUR", "USA"};
    public static final String[] MOSICA = new String[]{"HM", "HN", "NM"};
    public static final Integer[] TIME_HOUR = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
    public static final Integer[] TIME_MINUTE = new Integer[61];
    public static final Integer[] TIME_SECOND = new Integer[61];

    public static final String[] SOURCE_TABLE_COLUMN_TITLE = {"URL", "质量", "操作"};
    public static final String DEFAULT_FILE_PATH = "E:\\H Sources\\DealSourcePath\\HAVJ";
    public static final String DEFAULT_COMPRESS_FILE_PATH = "E:\\H Sources\\DealSourcePath\\Compress";

    public static final String SOURCES_FLUSH_MODLE_ALL = "0";
    public static final String SOURCES_FLUSH_MODLE_AUTO_IMPORT = "1";
    public static final String SOURCES_FLUSH_MODLE_DEAL_FIRST = "2";
    public static final String SOURCES_FLUSH_MODLE_SELECT = "3";

    public static final String SOURCES_FLUSH_MODLE_BY_CUSTOM = "9";

    public static final String SOURC_PATH_1 = "Baidu@ZH1://HSources/AM/JAP/AV/";

    public static final String[] SOURCE_TRANSLATE = {"NUL", "CHN", "JAP", "USA", "EUR"};
    public static final String[] SOURCE_SUBTITLE = {"NUL", "CHN", "JAP", "USA", "EUR"};
    public static final String[] SOURCE_QUALITY = {"1", "2", "3"};
    public static final String[] SOURCE_QUALITY_TEXT = {"低画质", "中画质", "高画质"};
    public static final String[] SOURCE_VIDEO_FORMAT = {"NUL", "MPEG-4", "MOV", "MKV", "AVI", "WMV", "RMVB", "DVD", "BLU-RAY DISK", "FIV", "ASF", "NAVI", "TS", "OTHER"};
    public static final String[] SOURCE_COMPRESS_FORMAT = {"NUL", "7Z", "ZIP", "RAR", "TAR", "GZ"};
    public static final String[] SOURCE_RESOLUTION = {"Other", "2560x1440", "1920x1080", "1600×1200", "1280x720", "1024×768", "800×600", "640×480", "480*360"};


    public static final String password = "123@pass";

    public static final String TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT = "0";
    public static final String TEXT_APPEND_MODEL_TIME_AND_NEXT = "1";

    public static final String ROBOT_TEXT_IMPORT_BY_PERSON = "0";
    public static final String ROBOT_TEXT_IMPORT_AUTO = "1";
    public static final String ROBOT_TEXT_DEAL_FIRST = "2";

    public static final String IS_FILE = "";
    public static final String IS_NOT_FILE = "";

    public static final String RETURN_ANSWER_SUCCESS = "SUCCESS";
    public static final String RETURN_ANSWER_OBJECT = "RETURN";

    public static final String IF_COD_ERROR_PREFIX_1PONDO = "1Pondo";
    public static final String IF_COD_ERROR_PREFIX_10MUSUME = "10MUSUME";
    public static final String IF_COD_ERROR_PREFIX_CARIBBEAN = "Caribbean";
    public static final String IF_COD_ERROR_PREFIX_FC2 = "FC2";
    public static final String IF_COD_ERROR_PREFIX_HEYZO = "Heyzo";
    public static final String personFirstText = "身高 - T:\n " +
            "出生日期 - \n" +
            "三围 - B:(C) W: H:\n " +
            "星座 - \n" +
            "血型 - \n" +
            "国籍 - \n" +
            "出道公司 - \n" +
            "经纪公司 - Bambi\n" +
            "AV数量 - 部\n";

    public static final String personRegexFirstText = "身高 - T:(T-Content)\n" +
            "出生日期 - (Birthday-Content)\n" +
            "三围 - B:(B-Content)((C-Content)) W:(W-Content) H:(H-Content)\n" +
            "星座 - (Star-Content)\n" +
            "血型 - (Blood-Content)\n" +
            "国籍 - (County-Content)\n" +
            "出道公司 - (Company1-Content)\n" +
            "经纪公司 - (Company2-Content)\n" +
            "AV数量 - (AV Count-Content)部\n";

    static {
        for (int i = 1; i <= Person_Score.length; i++) {
            Person_Score[i - 1] = i;
        }

        for (int k = 0; k <= 60; k++) {
            TIME_MINUTE[k] = k;
        }
        for (int l = 0; l <= 60; l++) {
            TIME_SECOND[l] = l;
        }
    }

    public static final String TEXT_POPUPMENU_TYPE_FIELD = "Field";
    public static final String TEXT_POPUPMENU_TYPE_AREA = "Area";

    public static final String[] ROBOT_STATUS = {"Artificial Import", "Web Import", "Deal First"};
    public static final Map<String, String> ROBOT_STATUS_STRING = new HashMap<String, String>() {
        {
            put("ArtificialImport","0");
            put("WebImport","1");
            put("DealFirst","2");
        }
    };

    ;

    public static void main(String[] args) {

        System.out.println(TIME_MINUTE);
    }
}
