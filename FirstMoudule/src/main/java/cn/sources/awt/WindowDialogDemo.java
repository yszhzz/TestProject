package cn.sources.awt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowDialogDemo {

    public static void main(String[] args) {

        Frame frame = new Frame();

        Box box = Box.createVerticalBox();
        Button m_button = new Button("Modal");
        Button n_button = new Button("NON-Modal");

        Button save = new Button("SAVE");
        Button load = new Button("NON-LOAD");

        Dialog modal = new Dialog(frame, "Modal", true);
        modal.setBounds(20,30,300,200);
        m_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modal.setVisible(true);
            }
        });
        Box vb = Box.createVerticalBox();
        vb.add(new TextField(20));
        vb.add(new Button("x1"));
        modal.add(vb);


        Dialog nModal = new Dialog(frame, "NON-Modal", false);
        nModal.setBounds(20,30,300,200);
        n_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nModal.setVisible(true);
            }
        });

        String loadFile = null;
        String saveFile = null;

        FileDialog fileS = new FileDialog(frame, "Save",FileDialog.SAVE);
        fileS.setBounds(20,30,300,200);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileS.setVisible(true);//该方法会阻塞！！！
                System.out.println(fileS.getFile()+"文件下载完成！！"+fileS.getDirectory());
            }
        });
        //fileS.add(new Label("Save"));

        FileDialog fileL = new FileDialog(frame, "Load",FileDialog.LOAD);
        fileL.setBounds(20,30,300,200);
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileL.setVisible(true);

                System.out.println(fileL.getFile()+"文件上传完成！！"+fileL.getDirectory());

            }
        });

        box.add(m_button);
        box.add(n_button);
        box.add(save);
        box.add(load);

        frame.add(box);

        frame.pack();
        frame.setVisible(true);


    }


}
