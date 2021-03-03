package cn.sources.awt;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphicsDemo {

    private final String RECT_SHAPE = "RECT";
    private final String OVAL_SHAPE = "OVAL";

    private Frame frame = new Frame("Graphics");

    Button bDRect = new Button("Rect");
    Button bDOval = new Button("Oval");

    private String shape = null;
    private MyCanvas myCanvas = new MyCanvas();


    private class MyCanvas extends Canvas {
        @Override
        public void paint(Graphics g) {
            //绘制！！
            if ("RECT".equals(shape)) {
                g.setColor(Color.BLACK);
                g.drawRect(100,100,200,200);
            } else if ("OVAL".equals(shape)){
                g.setColor(Color.RED);
                g.drawOval(300,300,100,100);
            }
        }
    }


    public void init() {

        bDRect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shape = RECT_SHAPE;
                myCanvas.repaint();
            }
        });

        bDOval.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shape = OVAL_SHAPE;
                myCanvas.repaint();
            }
        });

        myCanvas.setPreferredSize(new Dimension(500,500));

        Panel panel = new Panel();
        panel.add(bDRect);
        panel.add(bDOval);

        frame.add(panel,BorderLayout.SOUTH);
        frame.add(myCanvas);


        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new GraphicsDemo().init();
    }

}
