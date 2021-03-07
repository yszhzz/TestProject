package cn.sources.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DialogDemo {

    JFrame jFrame = new JFrame("Dialog");
    JTextArea jTextArea = new JTextArea(6,50);

    JButton messageButton = new JButton(new AbstractAction("Message",new ImageIcon("FirstMoudule/src/main/resources/pic/component/copy.png")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = jTextArea.getText() + "|" + e.getActionCommand();
            //ICON 可以不传，有默认，传了会替换
            JOptionPane.showMessageDialog(jFrame,text,"MessageDialog",JOptionPane.WARNING_MESSAGE);
        }
    });

    JButton confirmButton = new JButton(new AbstractAction("Confirm",new ImageIcon("FirstMoudule/src/main/resources/pic/component/copy.png")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = jTextArea.getText() + "|" + e.getActionCommand();
            jTextArea.append("\n");
            //ICON 可以不传，有默认，传了会替换
            int messageDialog = JOptionPane.showConfirmDialog(jFrame, text, "ConfirmDialog", JOptionPane.YES_NO_CANCEL_OPTION);
            if (messageDialog == JOptionPane.YES_OPTION) {
                jTextArea.append("enter YES"+"\n");
            } else if (messageDialog == JOptionPane.NO_OPTION) {
                jTextArea.append("enter NO"+"\n");
            } else if (messageDialog == JOptionPane.OK_OPTION) {
                jTextArea.append("enter OK"+"\n");
            } else if (messageDialog == JOptionPane.CANCEL_OPTION) {
                jTextArea.append("enter CANCEL"+"\n");
            };
        }
    });

    JButton inputButton = new JButton(new AbstractAction("Input",new ImageIcon("FirstMoudule/src/main/resources/pic/component/copy.png")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = jTextArea.getText() + "|" + e.getActionCommand();
            String s = JOptionPane.showInputDialog(jFrame, "Enter Your UserName", "InputDialog", JOptionPane.INFORMATION_MESSAGE);
            jTextArea.append(s+"\n");

        }
    });

    JButton input2Button = new JButton(new AbstractAction("Input2",new ImageIcon("FirstMoudule/src/main/resources/pic/component/copy.png")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] strings = {"First Type", "Second Type", "Third Type", "Fourth Type"};
            int i = JOptionPane.showOptionDialog(jFrame, "Select Your Type", "Input2Dialog", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    strings, "First Type");
            jTextArea.append("You Have Select "+strings[i]+"\n");

        }
    });


    public void init() {

        jFrame.add(jTextArea);
        Box verticalBox = Box.createHorizontalBox();
        verticalBox.add(messageButton);
        verticalBox.add(confirmButton);
        verticalBox.add(inputButton);
        verticalBox.add(input2Button);

        jFrame.add(verticalBox, BorderLayout.SOUTH);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);

    }

    public static void main(String[] args) {
        new DialogDemo().init();
    }
}
