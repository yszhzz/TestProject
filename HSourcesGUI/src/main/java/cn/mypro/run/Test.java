package cn.mypro.run;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class Test {
    public static void main(String[] args) {

        Test test = new Test();
        test.showSourcesDiolog();

    }

    public void showSourcesDiolog() {

        JOptionPane jop=new JOptionPane();
        jop.setLayout(new BorderLayout());
        JLabel im=new JLabel("Java Technology Dive Log", new ImageIcon("images/gwhite.gif"),JLabel.CENTER);
        jop.add(im,BorderLayout.NORTH);
        jop.setVisible(true);
    }

}

