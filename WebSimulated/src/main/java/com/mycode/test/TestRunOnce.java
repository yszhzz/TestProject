package com.mycode.test;

import com.mycode.entity.User;
import com.mycode.service.SimulatedYSRSJService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRunOnce {
    private static Logger logger = LoggerFactory.getLogger(TestRunOnce.class);

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "WebSimulated/src/main/resources/dirver/chromedriver.exe");// chromedriver服务地址

        SimulatedYSRSJService simulatedYSRSJService = new SimulatedYSRSJService(null,new User("jz18635080488", "hdg123456","张三"));
        simulatedYSRSJService.run();

    }


}
