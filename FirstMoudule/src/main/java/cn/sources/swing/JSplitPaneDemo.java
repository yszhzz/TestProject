package cn.sources.swing;

import cn.sources.swing.entity.Book;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class JSplitPaneDemo {

    Book[] books = {
            new Book("java自学宝典", new ImageIcon("FirstMoudule/src/main/resources/pic/container/java.png"), "国内关于 Java 编程最全面的图书 \n 看得懂 ， 学得会"),
            new Book("轻量级的JAVAEE企业应用实战", new ImageIcon("FirstMoudule/src/main/resources/pic/container/ee.png"), "SSM整合开发的经典图书，值的拥有"),
            new Book("Android基础教程", new ImageIcon("FirstMoudule/src/main/resources/pic/container/android.png"), "全面介绍Android平台应用程序\n 开发的各方面知识")
    };

    JFrame jFrame = new JFrame();
    //JList 会依次调用泛型种类的toString方法，来展示其名字，因此以实体类为模型时，需要重写toString方法
    JList<Book> bookList = new JList<>(books);
    JLabel bookCover = new JLabel();
    JTextArea bookDesc = new JTextArea();

    public void init() {
        bookList.setPreferredSize(new Dimension(150,400));
        bookCover.setPreferredSize(new Dimension(220,270));
        bookDesc.setPreferredSize(new Dimension(220,130));

        bookList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Book selectedValue = bookList.getSelectedValue();
                bookCover.setIcon(selectedValue.getIcon());
                bookDesc.setText(selectedValue.getDesc());
            }
        });


        JSplitPane jLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, bookCover, new JScrollPane(bookDesc));
        jLeft.setOneTouchExpandable(true);

        JSplitPane all = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jLeft,bookList);
        all.setContinuousLayout(true);
        all.setDividerSize(10);//设置分割线大小


        jFrame.add(all);
        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new JSplitPaneDemo().init();
    }

}
