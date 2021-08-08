package cn.mypro.swing.constant;

import cn.mypro.swing.childtab.JChildTabView;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicVariablePool {
    public static JFrame fatherJFrame;
    public static JTextArea fatherLogArea;
    public static final Map<String, JChildTabView> allTab = new HashMap<>();
    public static final Map<String, String> allVariableString = new HashMap<>();
    public static final Map<String, Integer> allVariableNumber = new HashMap<>();

    public static final Map<String,JComponent> allComponents = new HashMap<>();

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static void saveString(String key,String value) {
        allVariableString.put(key,value);
    }
    public static String getStringValue(String key) {
        return allVariableString.get(key);
    }

    public static void saveInteger(String key,Integer value) {
        allVariableNumber.put(key,value);
    }
    public static Integer getIntegerValue(String key) {
        return allVariableNumber.get(key);
    }

    public static void saveTab(String key,JChildTabView value) {
        allTab.put(key,value);
    }
    public static JChildTabView getTabsValue(String key) {
        return allTab.get(key);
    }

    public static void saveComponent(String key,JComponent value) {
        allComponents.put(key,value);
    }
    public static JComponent getComponentValue(String key) {
        return allComponents.get(key);
    }


    public static void saveFather(JFrame father) {
        fatherJFrame = father;
    }
    public static JFrame getFatherValue() {
        return fatherJFrame;
    }

    public static void saveFatherLogArea(JTextArea logArea) {
        fatherLogArea = logArea;
    }
    public static JTextArea getFatherLogAreaValue() {
        return fatherLogArea;
    }

    /**
     * @param
     * @param message
     * @param model    LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT & LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT
     */
    public static void runfatherLogMessagePrint(String message, String model) {
        String time_text = sdf.format(System.currentTimeMillis());
        if (LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT.equals(model)) {
            fatherLogArea.append(message);
        } else if (LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT.equals(model)) {
            fatherLogArea.append("[" + time_text + "]: ");
            fatherLogArea.append(message);
        }
        fatherLogArea.paintImmediately(fatherLogArea.getBounds());
    }


}
