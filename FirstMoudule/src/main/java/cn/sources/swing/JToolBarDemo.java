package cn.sources.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class JToolBarDemo {

    JFrame jf = new JFrame("测试工具条");

    JTextArea jta = new JTextArea(6,35);

    //创建工具条
    JToolBar jtb = new JToolBar("Song",SwingConstants.HORIZONTAL);

    Action pre = new AbstractAction("上一曲",new ImageIcon("FirstMoudule/src/main/resources/pic/component/pre.png")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            jta.append("上一曲"+"\n");
        }
    };

    Action pause = new AbstractAction("暂停",new ImageIcon("FirstMoudule/src/main/resources/pic/component/pause.png")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            jta.append("暂停"+"\n");
        }
    };

    Action next = new AbstractAction("下一曲",new ImageIcon("FirstMoudule/src/main/resources/pic/component/next.png")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            jta.append("下一曲"+"\n");
        }
    };


    public void init(){

        //给JTextArea添加滚动条
        jf.add(new JScrollPane(jta));

        //以Action的形式创建按钮，并将按钮添加到Panel中
        JButton preBtn = new JButton(pre);
        JButton pauseBtn = new JButton(pause);
        JButton nextBtn = new JButton(next);


        //让工具条可以拖动 默认为false
        //jtb.setFloatable(true);
        //往工具条中添加Action对象，该对象会转换成工具按钮
        jtb.add(preBtn);
        jtb.addSeparator();
        jtb.add(pauseBtn);
        jtb.addSeparator();
        jtb.add(nextBtn);

        //向窗口中添加工具条
        jf.add(jtb, BorderLayout.NORTH);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jf.pack();
        jf.setVisible(true);

/*
        jtb.add(pre);
        jtb.addSeparator();
        jtb.add(pause);
        jtb.addSeparator();
        jtb.add(next);

        //向窗口中添加工具条
        jf.add(jtb, BorderLayout.NORTH);
        jf.add(jta);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jf.pack();
        jf.setVisible(true);*/
    }

    public static void main(String[] args) {
        new JToolBarDemo().init();
    }

}
