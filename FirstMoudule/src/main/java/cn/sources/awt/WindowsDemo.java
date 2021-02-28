package cn.sources.awt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowsDemo {

    public static void main(String[] args) {

        testBox2();


    }

    public static void testFrame() {
        Frame frame = new Frame("ceshi");
        frame.setLocation(100,100);
        frame.setSize(500,300);
        frame.setVisible(true);
    }

    public static void testPanel() {
        Frame frame = new Frame("ceshi");
        Panel panel = new Panel();

        panel.add(new Button("确认1"));
        panel.add(new TextField("文本2"));
        frame.add(panel);
        frame.setBounds(100,100,500,300);
        frame.setVisible(true);
    }

    public static void testScrollPane() {
        Frame frame = new Frame("ceshi");
        ScrollPane panel = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);

        panel.add(new Button("确认"));
        panel.add(new TextField("文本"));
        frame.add(panel);
        frame.setBounds(100,100,500,300);
        frame.setVisible(true);
    }

    public static void testFlowLayout() {
        Frame frame = new Frame();
        frame.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        for (int i = 0; i < 100; i++) {
            Button button = new Button();
            button.setLabel("AN"+i);
            frame.add(button);
        }
        frame.pack();
        frame.setVisible(true);
    }

    public static void testBorderLayout() {
        Frame frame = new Frame();
        frame.setLayout(new BorderLayout(30,10));

        frame.add(new Button("N"),BorderLayout.NORTH);
        frame.add(new Button("S"),BorderLayout.SOUTH);
        frame.add(new Button("E"),BorderLayout.EAST);
        frame.add(new Button("W"),BorderLayout.WEST);
        frame.add(new Button("C"),BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public static void testGridLayout() {
        Frame frame = new Frame();
        //默认即为Border，无需放置
        frame.setLayout(new BorderLayout(30,10));

        Panel panel = new Panel();
        panel.setLayout(new GridLayout(4,5));
        for (int i = 0; i < 20; i++) {
             panel.add(new Button("AN"+i));
        }

        frame.add(new TextField("jsq"),BorderLayout.NORTH);
        frame.add(panel,BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    public static void testGridBagLayout() {
        Frame frame = new Frame("Test");
        //默认即为Border，无需放置
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        Button[] buttons = new Button[10];

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button("AN"+i);
        }

        gridBagConstraints.fill = GridBagConstraints.BOTH;



        frame.pack();
        frame.setVisible(true);
    }

    public static void testGardLayout() {
        Frame frame = new Frame();
        //默认即为Border，无需放置
        //frame.setLayout(new BorderLayout(30,10));

        Panel panel = new Panel();
        CardLayout cardLayout = new CardLayout(1, 2);
        panel.setLayout(cardLayout);

        String[] names = {"1","2","3","4","5"};
        for (int i = 0; i < names.length; i++) {
            panel.add(names[i],new Button(names[i]));
        }
        frame.add(panel,BorderLayout.CENTER);



        Panel pan = new Panel();
        Button button1 = new Button("上一张");
        Button button2 = new Button("下一张");
        Button button3 = new Button("第一张");
        Button button4 = new Button("最后一张");
        Button button5 = new Button("第3张");

        ActionListener actionListener = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();

                switch (actionCommand) {
                    case "上一张":
                        cardLayout.previous(panel);
                        break;
                    case "下一张":
                        cardLayout.next(panel);
                        break;
                    case "第一张":
                        cardLayout.first(panel);
                        break;
                    case "最后一张":
                        cardLayout.last(panel);
                        break;
                    case "第3张":
                        cardLayout.show(panel,"3");
                        break;

                }
            }
        };

        button1.addActionListener(actionListener);
        button2.addActionListener(actionListener);
        button3.addActionListener(actionListener);
        button4.addActionListener(actionListener);
        button5.addActionListener(actionListener);

        pan.add(button1);
        pan.add(button2);
        pan.add(button3);
        pan.add(button4);
        pan.add(button5);

        frame.add(pan,BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static void testBoxLayout() {
        Frame frame = new Frame("Test");

        frame.setLayout(new BoxLayout(frame,BoxLayout.X_AXIS));
        frame.add(new Button("AN1"));
        frame.add(new Button("AN2"));

        frame.pack();
        frame.setVisible(true);
    }

    public static void testBox() {
        Frame frame = new Frame("Test");

        Box horizontalBox = Box.createHorizontalBox();

        horizontalBox.add(new Button("h1"));
        horizontalBox.add(new Button("h2"));


        Box verticalBox = Box.createVerticalBox();

        verticalBox.add(new Button("v1"));
        verticalBox.add(new Button("v2"));


        frame.add(horizontalBox,BorderLayout.NORTH);
        frame.add(verticalBox,BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
    public static void testBox2() {
        Frame frame = new Frame("Test");

        Box horizontalBox = Box.createHorizontalBox();

        horizontalBox.add(new Button("h1"));
        horizontalBox.add(Box.createHorizontalGlue());
        horizontalBox.add(new Button("h2"));
        horizontalBox.add(Box.createHorizontalStrut(30));
        horizontalBox.add(new Button("h3"));

        Box verticalBox = Box.createVerticalBox();

        verticalBox.add(new Button("v1"));
        verticalBox.add(Box.createVerticalStrut(30));
        verticalBox.add(new Button("v2"));
        verticalBox.add(Box.createVerticalGlue());
        verticalBox.add(new Button("v3"));

        frame.add(horizontalBox,BorderLayout.NORTH);
        frame.add(verticalBox,BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }



}
