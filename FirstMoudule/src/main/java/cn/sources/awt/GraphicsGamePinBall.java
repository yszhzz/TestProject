package cn.sources.awt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GraphicsGamePinBall {

    private Frame frame = new Frame("PinBall");

    private final int TABLE_HEIGHT = 400;
    private final int TABLE_WIDTH = 300;

    private final int BACKET_WIDTH = 60;
    private final int BACKET_HEIGHT = 20;

    private final int Ball_Size = 16;

    private int ball_x = 150;
    private int ball_y = 20;

    private int speedY = 10;
    private int speedX = 5;

    private int racketX = 150;
    private final int racketY = 340;

    private boolean isOver = false;

    private Timer timer;

    private class MyCanvas extends Canvas {
        @Override
        public void paint(Graphics g) {
            if (isOver) {
                g.setColor(Color.BLUE);
                g.setFont(new Font("Timer",Font.BOLD,30));
                g.drawString("Game Over!",50,200);
            } else {
                g.setColor(Color.RED);
                g.fillOval(ball_x,ball_y,Ball_Size,Ball_Size);

                g.setColor(Color.PINK);
                g.fillRect(racketX,racketY,BACKET_WIDTH,BACKET_HEIGHT);
            }
        }
    }

    private MyCanvas drawArea = new MyCanvas();

    public void init() {

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_LEFT) {
                    if (racketX > 0) racketX = racketX - 10;
                    if (racketX < 0) racketX = 0;
                }

                if (keyCode == KeyEvent.VK_RIGHT) {
                    if (racketX < (TABLE_WIDTH - BACKET_WIDTH)) racketX = racketX + 10;
                    if (racketX > (TABLE_WIDTH - BACKET_WIDTH)) racketX = TABLE_WIDTH - BACKET_WIDTH;
                }


            }
        };

        frame.addKeyListener(keyAdapter);
        drawArea.addKeyListener(keyAdapter);


        ActionListener task = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (ball_x <= 0 || ball_x >= (TABLE_WIDTH - Ball_Size)) speedX = -speedX;
                if (ball_y <= 0 || (ball_y > racketX - Ball_Size && ball_x > racketX && ball_x < racketX + BACKET_WIDTH)) speedY = -speedY;
                if (ball_y > racketY - Ball_Size && (ball_x < racketX || ball_x > racketX + BACKET_WIDTH)) {
                    isOver = true;
                    timer.stop();
                    drawArea.repaint();
                }
                ball_x += speedX;
                ball_y += speedY;

                drawArea.repaint();
            }
        };
        timer = new Timer(100,task);
        timer.start();

        drawArea.setPreferredSize(new Dimension(TABLE_WIDTH,TABLE_HEIGHT));
        frame.add(drawArea);

        frame.pack();
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        new GraphicsGamePinBall().init();
    }







}
