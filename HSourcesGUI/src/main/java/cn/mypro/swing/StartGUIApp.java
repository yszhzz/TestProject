package cn.mypro.swing;

import cn.mypro.swing.childtab.HVAJapanAVMTab;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class StartGUIApp {

    JFrame totalJFram = new JFrame("HSources Manage Tools");
    JTabbedPane level_1_child = new JTabbedPane(SwingConstants.LEFT,JTabbedPane.WRAP_TAB_LAYOUT);
    JTextArea runMessage = new JTextArea(20,50);
    //菜单
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("文件");
    JMenu editMent = new JMenu("编辑");
    JMenuItem newFrame = new JMenuItem("新建管理页面");
    JMenuItem auto = new JMenuItem("Auto Next Line");
    JMenuItem copy = new JMenuItem("Copy",new ImageIcon("FirstMoudule/src/main/resources/pic/component/copy.png"));
    JMenuItem paste = new JMenuItem("Paste",new ImageIcon("FirstMoudule/src/main/resources/pic/component/paste.png"));
    JMenu formateMenu = new JMenu("Format");
    JMenuItem comment = new JMenuItem("Comment");
    JMenuItem cancelComment = new JMenuItem("Cancel-Comment");
    //右键菜单
    JPopupMenu jPopupMenu = new JPopupMenu();
    JRadioButtonMenuItem metalItem = new JRadioButtonMenuItem("Metal Style");
    JRadioButtonMenuItem nimbusItem = new JRadioButtonMenuItem("Nimbus Style");
    JRadioButtonMenuItem windowsItem = new JRadioButtonMenuItem("Windows Style",true);
    JRadioButtonMenuItem windows_classItem = new JRadioButtonMenuItem("Windows Class Style");
    JRadioButtonMenuItem motifItem = new JRadioButtonMenuItem("Motif Style");
    ButtonGroup popupButtonGroup = new ButtonGroup();


    public static void main(String[] args) {
        StartGUIApp startGUIApp = new StartGUIApp();
        startGUIApp.init();

    }

    public void init(){

        /*日志框 设置*/
        runMessage.append("欢迎使用！！\n");
        runMessage.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        /*初始化部件*/
        //顶部菜单初始化
        menuInit();
        //右键菜单初始化
        rightMenuInit();

        //日本AV Tab
        HVAJapanAVMTab hvaJapanAVMTab = new HVAJapanAVMTab();
        JTabbedPane jTabbedPane = hvaJapanAVMTab.initAddNewSourceTab(totalJFram,runMessage);
        level_1_child.addTab("日本AV管理",new ImageIcon("HSourcesGUI/src/main/resources/pic/new.png"),jTabbedPane,"日本AV管理");
        //level_1_child.setSelectedIndex(0);
        JSplitPane all = new JSplitPane(JSplitPane.VERTICAL_SPLIT, level_1_child, new JScrollPane(runMessage));
        all.setDividerSize(10);
        all.setDividerLocation(800);
        all.setContinuousLayout(true);
        all.setOneTouchExpandable(true);

        /*jFram 设置*/
        //设置jf大小
        totalJFram.setBounds(10,10,1800,1000);
        //设置jf大小是否能变化
        totalJFram.setResizable(true);
        //设置jf退出模式
        totalJFram.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //添加组件
        totalJFram.add(all);
        totalJFram.setVisible(true);

    }

    public void menuInit() {
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

        totalJFram.setJMenuBar(menuBar);
    }

    public void rightMenuInit() {
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


        level_1_child.setComponentPopupMenu(jPopupMenu);

    }

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
        SwingUtilities.updateComponentTreeUI(totalJFram.getContentPane());
        //更新mb菜单条及每部所有组件UI
        SwingUtilities.updateComponentTreeUI(menuBar);
        //更新右键菜单及内部所有菜单项的UI
        SwingUtilities.updateComponentTreeUI(jPopupMenu);
    }

}
