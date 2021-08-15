package com.mycode.app;

import com.mycode.constant.Constant;
import com.mycode.entity.Course;
import com.mycode.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StartApp {

    private static Logger logger = LoggerFactory.getLogger(StartApp.class);

    private static String version = "V 1.0";
    private static JFrame totalJFram = new JFrame("自动刷课软件 " + version);

    private static JList<User> userJList = new JList<>();
    private static JTextArea messageLog = new JTextArea();

    private static JPanel userDelita = new JPanel();

    private static JButton importUser = new JButton("批量导入用户");
    private static JButton addUser = new JButton("添加用户");

    private static JList<Course> courseJList= new JList<>();
    private static JLabel name = new JLabel(Constant.DEFAULT_STRING_VALUES);
    private static JLabel username = new JLabel(Constant.DEFAULT_STRING_VALUES);
    private static JLabel password = new JLabel(Constant.DEFAULT_STRING_VALUES);
    private static JLabel status = new JLabel(Constant.DEFAULT_STRING_VALUES);

    private static JButton start = new JButton("开始刷课！");


    private List<User> users = new ArrayList<>();

    public static void main(String[] args) {

        MainFram mainFram = new MainFram();
        mainFram.init();
    }

}
