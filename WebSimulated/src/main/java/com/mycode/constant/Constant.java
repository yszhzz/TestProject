package com.mycode.constant;

public class Constant {

    public static final String URL_YSRSJ = "http://218.26.234.215:8088/";

    public static final String DEFAULT_STRING_VALUES = "None";

    public static final String STATUS_READY = "READY";
    public static final String STATUS_WAIT = "WAIT";
    public static final String STATUS_RUN = "RUN";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";

    public static final String DEFAULT_BASE_FILE_PATH = "./base.txt";
    public static final String DEFAULT_IMPORT_FILE_PATH = "./import.txt";


    public static final String TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT = "0";
    public static final String TEXT_APPEND_MODEL_TIME_AND_NEXT = "1";

    public static final String HELLO_WORLD =
            "欢迎使用刷课软件！\n" +
            "批量导入用户请在程序目录下新建[import.txt]文件，并以[用户名|密码|姓名]格式写入，\n" +
            "程序目录下[base.txt]作为数据库使用，请勿手动更改，\n" +
            "详细日志在程序目录下[normal.log]，若出现问题请查看该文件。\n";
}
