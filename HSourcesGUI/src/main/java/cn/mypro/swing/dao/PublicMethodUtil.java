package cn.mypro.swing.dao;

import cn.mypro.swing.constant.LabelConstant;

import javax.swing.*;
import java.text.SimpleDateFormat;

public class PublicMethodUtil {


    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     *
     * @param textArea
     * @param message
     * @param model 是否添加日志时间 LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT & LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT
     */
    public static void runMessagePrint(JTextArea textArea, String message, String model) {
        String time_text = sdf.format(System.currentTimeMillis());
        if (LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT.equals(model)) {
            textArea.append(message);
        } else if (LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT.equals(model)){
            textArea.append("["+time_text+"]: ");
            textArea.append(message);
        }
        textArea.paintImmediately(textArea.getBounds());
    }

}
