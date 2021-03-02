package cn.sources.awt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EventListenerDemo {

    public static void main(String[] args) {

        Frame frame = new Frame("Listener");

        TextField textField = new TextField(30);
        Choice name = new Choice();
        name.add("LY");
        name.add("SQ");
        name.add("YN");
        //全重写模式
/*        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });*/
        //设计模式 - 适配器设计模式 实现关闭
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        textField.addTextListener(new TextListener() {
            @Override
            public void textValueChanged(TextEvent e) {
                String text = textField.getText();

                System.out.println(text);
            }
        });
        name.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Object item = e.getItem();
                System.out.println(item);
            }
        });
        frame.addContainerListener(new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {
                Component child = e.getChild();
                System.out.println(child);
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                Component child = e.getChild();
                System.out.println(child);
            }
        });

        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(name);
        horizontalBox.add(textField);
        frame.add(horizontalBox);
        frame.pack();
        frame.setVisible(true);
    }

}
