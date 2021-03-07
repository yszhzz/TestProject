package cn.sources.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class SwingComponentDemo {
    //主窗口
    JFrame jFrame = new JFrame("CS-Window");
    //菜单
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");
    JMenu editMent = new JMenu("Edit");

    JMenuItem auto = new JMenuItem("Auto Next Line");
    JMenuItem copy = new JMenuItem("Copy",new ImageIcon("FirstMoudule/src/main/resources/pic/component/copy.png"));
    JMenuItem paste = new JMenuItem("Paste",new ImageIcon("FirstMoudule/src/main/resources/pic/component/paste.png"));

    JMenu formateMenu = new JMenu("Format");
    JMenuItem comment = new JMenuItem("Comment");
    JMenuItem cancelComment = new JMenuItem("Cancel-Comment");

    //简单组件
    JTextArea jTextArea = new JTextArea(8,20);

    String[] colorLists = {"Red","Blue","Black"};
    JList<String> colorList = new JList<>(colorLists);

    JComboBox<String> colorSelect = new JComboBox<>();

    JRadioButton male = new JRadioButton("Man",true);
    JRadioButton female = new JRadioButton("Woman",false);

    ButtonGroup sexButtonGroup = new ButtonGroup();


    JCheckBox isMarried = new JCheckBox("IsMarrid?",true);

    JTextField jTextField = new JTextField(40);
    //相对路径是以Project为根目录的
    JButton ok = new JButton("确认",new ImageIcon("FirstMoudule/src/main/resources/pic/component/ok.png"));

    //右键菜单
    JPopupMenu jPopupMenu = new JPopupMenu();
    JRadioButtonMenuItem metalItem = new JRadioButtonMenuItem("Metal Style");
    JRadioButtonMenuItem nimbusItem = new JRadioButtonMenuItem("Nimbus Style");
    JRadioButtonMenuItem windowsItem = new JRadioButtonMenuItem("Windows Style",true);
    JRadioButtonMenuItem windows_classItem = new JRadioButtonMenuItem("Windows Class Style");
    JRadioButtonMenuItem motifItem = new JRadioButtonMenuItem("Motif Style");

    ButtonGroup popupButtonGroup = new ButtonGroup();

    public void init() {
        //组装 底部
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(jTextField);
        bottomPanel.add(ok);

        jFrame.add(bottomPanel);

        //组装 中下
        JPanel selectJPanel = new JPanel();
        colorSelect.addItem("Red");
        colorSelect.addItem("Green");
        colorSelect.addItem("Blue");

        selectJPanel.add(colorSelect);
        sexButtonGroup.add(female);
        sexButtonGroup.add(male);
        selectJPanel.add(female);
        selectJPanel.add(male);
        selectJPanel.add(isMarried);

        //组装 中
        Box topLeft = Box.createVerticalBox();

        topLeft.add(jTextArea);
        topLeft.add(selectJPanel);

        Box top = Box.createHorizontalBox();
        top.add(topLeft);
        top.add(colorList);

        jFrame.add(top);
        jFrame.add(bottomPanel, BorderLayout.SOUTH);

        //菜单



        formateMenu.add(comment);
        formateMenu.add(cancelComment);

        copy.setMnemonic(KeyEvent.VK_C);
        auto.setIcon(new ImageIcon("FirstMoudule/src/main/resources/pic/component/next.png"));

        editMent.add(auto);
        editMent.addSeparator();
        editMent.add(copy);
        editMent.add(paste);
        editMent.addSeparator();
        editMent.add(formateMenu);

        menuBar.add(fileMenu);
        menuBar.add(editMent);

        jFrame.setJMenuBar(menuBar);

        //右键
        popupButtonGroup.add(metalItem);
        popupButtonGroup.add(nimbusItem);
        popupButtonGroup.add(windowsItem);
        popupButtonGroup.add(windows_classItem);
        popupButtonGroup.add(motifItem);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();

                try {
                    changeFlavor(actionCommand);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }


            }
        };

        metalItem.addActionListener(actionListener);
        nimbusItem.addActionListener(actionListener);
        windowsItem.addActionListener(actionListener);
        windows_classItem.addActionListener(actionListener);
        motifItem.addActionListener(actionListener);

        jPopupMenu.add(metalItem);
        jPopupMenu.add(nimbusItem);
        jPopupMenu.add(windowsItem);
        jPopupMenu.add(windows_classItem);
        jPopupMenu.add(motifItem);

        //自动监听右键！！
        jTextArea.setComponentPopupMenu(jPopupMenu);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);

    }

    //定义一个方法，用于改变界面风格
    private void changeFlavor(String command) throws Exception{

        switch (command){
            case "Metal Style":
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                break;
            case "Nimbus Style":
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                break;
            case "Windows Style":
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                break;
            case "Windows Class Style":
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
                break;
            case "Motif Style":
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                break;
        }

        //更新f窗口内顶级容器以及所有组件的UI
        SwingUtilities.updateComponentTreeUI(jFrame.getContentPane());
        //更新mb菜单条及每部所有组件UI
        SwingUtilities.updateComponentTreeUI(menuBar);
        //更新右键菜单及内部所有菜单项的UI
        SwingUtilities.updateComponentTreeUI(jPopupMenu);
    }

    public static void main(String[] args) {

        SwingComponentDemo swingComponentDemo = new SwingComponentDemo();
        swingComponentDemo.init();
    }



}
