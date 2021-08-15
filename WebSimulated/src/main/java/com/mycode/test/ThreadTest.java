package com.mycode.test;

import java.util.concurrent.TimeUnit;

public class ThreadTest {

    public static void main(String[] args) throws Exception {

        Thread sleep_exception = new Thread(() -> {
            while (true) {
                try {
                    System.out.println(1);
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println("sleep exception");
                    break;
                }
            }
        });
        sleep_exception.start();
        TimeUnit.SECONDS.sleep(5);
        //sleep_exception.stop();
        sleep_exception.interrupt();

    }

}
