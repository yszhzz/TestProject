package cn.sources.awt;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventDemo {
    Frame frame = new Frame("Event");
    TextField tx = new TextField(30);
    Button ok = new Button("enter");


    public  void init() {

        ok.addActionListener(new MyListener());

        frame.add(tx,BorderLayout.CENTER);
        frame.add(ok,BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

    }

    private class MyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            tx.setText("Enter OJBK");
        }
    }


    public static void main(String[] args) {
        new EventDemo().init();
    }
}
