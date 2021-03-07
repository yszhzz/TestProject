package cn.mypro.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

public class Configs {

    private static final String PROPERTIES_FILE_NAME = "datafusion.properties";
    private static final Properties properties = new Properties();
    private static String CONFIG_TYPE;

    static {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            CONFIG_TYPE = "test";
        } else {
            CONFIG_TYPE = "run";
        }
        InputStream is = ClassLoader.getSystemResourceAsStream(PROPERTIES_FILE_NAME);
        try {
            properties.load(is);
        } catch (IOException e) {
            System.out.println("配置文件加载错误");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Properties prop = getProperties("dest");
        Properties prop2 = getProperties();
        System.out.println(prop.getProperty("db.url"));
        System.out.println(prop2.getProperty("dest.db.url"));
    }

    public static Properties getProperties() {
        return getProperties(null);
    }

    public static Properties getProperties(String db) {
        Properties prop = new Properties();
        Iterator iterator = properties.keySet().iterator();
        String type;
        if (db == null || "".equals(db)) {
            type = CONFIG_TYPE + ".";
        } else {
            type = CONFIG_TYPE + "." + db + ".";
        }
        int index = type.length();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (key.startsWith(type)) {
                prop.put(key.substring(index), properties.getProperty(key));
            }
        }
        return prop;
    }

    public static void setConfigType(String configType) {
        CONFIG_TYPE = configType;
    }

    public static String getRootDir() {
        return properties.getProperty(CONFIG_TYPE + ".root.dir");
    }

    public static boolean inRunEnvironment() {
        if ("run".equals(CONFIG_TYPE)) {
            return true;
        }
        return false;
    }
}
