package cn.mypro.swing.constant;

public class LabelConstant {

    public static final String[] LABEL_LEVEL_1_SHOW = new String[] {"请选择","J-剧情","C-人物","R-关系","S-环境","X-性爱","O-其他"};

    public static final String[] LABEL_LEVEL_2_J_SHOW = new String[] {"请选择","F-氛围","M-模式","H-情节"};
    public static final String[] LABEL_LEVEL_2_C_SHOW = new String[] {"请选择","S-社会角色","A-年龄角色","I-身份角色","F-特征角色"};
    public static final String[] LABEL_LEVEL_2_R_SHOW = new String[] {"请选择","S-社会关系","C-人际关系","I-身份关系"};
    public static final String[] LABEL_LEVEL_2_S_SHOW = new String[] {"请选择","P-场所","S-服装","T-道具"};
    public static final String[] LABEL_LEVEL_2_X_SHOW = new String[] {"请选择","Z-姿势","T-特征","A-行为"};
    public static final String[] LABEL_LEVEL_2_O_SHOW = new String[] {"请选择","F-其他"};

    public static final Integer[] Person_Score = new Integer[100];
    public static final String[] Person_Level = new String[]{"","SSS","SS","S","A","B","C","D","E","F","G"};

    public static final String[] AV_LANGUAGE = new String[]{"JAP","CHN","EUR","USA"};
    public static final String[] MOSICA = new String[]{"HM","HN","NM"};
    public static final Integer[] TIME_HOUR = new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24};
    public static final Integer[] TIME_MINUTE = new Integer[61];
    public static final Integer[] TIME_SECOND = new Integer[61];

    public static final String[] SOURCE_TABLE_COLUMN_TITLE = {"URL","质量","操作"};
    public static final String DEFAULT_FILE_PATH = "E:\\H Sources\\DealSourcePath\\HAVJ";

    public static final String[] SOURCE_TRANSLATE = {"NUL","CHN","JAP","USA","EUR"};
    public static final String[] SOURCE_SUBTITLE = {"NUL","CHN","JAP","USA","EUR"};
    public static final String[] SOURCE_QUALITY = {"NUL","1","2","3","4","5","6","7","8","9","10"};
    public static final String[] SOURCE_VIDEO_FORMAT = {"NUL","MPEG-4","MOV","MKV","AVI","WMV","RMVB","DVD","BLU-RAY DISK","FIV","ASF","NAVI","TS"};
    public static final String[] SOURCE_COMPRESS_FORMAT = {"NUL","7Z","ZIP","RAR","TAR","GZ"};

    public static final String password = "123@pass";

    public static final String personFirstText = "身高 - T:\n " +
            "出生日期 - \n" +
            "三围 - B:(C) W: H:\n " +
            "出道公司 - \n" +
            "经纪公司 - Bambi\n" +
            "AV数量 - 部\n";

    static {
        for (int i = 1; i <= Person_Score.length; i++) {
            Person_Score[i-1] = i;
        }

        for (int k = 0; k <= 60; k++) {
            TIME_MINUTE[k] = k;
        }
        for (int l = 0; l <= 60; l++) {
            TIME_SECOND[l] = l;
        }
    }

    public static void main(String[] args) {

        System.out.println(TIME_MINUTE);
    }
}
