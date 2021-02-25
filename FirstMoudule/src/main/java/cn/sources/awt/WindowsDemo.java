package cn.sources.awt;

import java.awt.*;
public class WindowsDemo {

    public static void main(String[] args) {

        Frame frame = new Frame("ceshi");
        Panel panel = new Panel();

        panel.add(new Button("确认"));
        panel.add(new TextField("文本"));
        frame.add(panel);
        frame.setBounds(100,100,500,300);
        frame.setVisible(true);
    }

    public static void testFrame() {
        Frame frame = new Frame("ceshi");
        frame.setLocation(100,100);
        frame.setSize(500,300);
        frame.setVisible(true);
    }



}
