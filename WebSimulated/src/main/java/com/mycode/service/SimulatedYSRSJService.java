package com.mycode.service;

import com.mycode.constant.Constant;
import com.mycode.entity.Course;
import com.mycode.entity.User;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SimulatedYSRSJService implements SimulatedService {

    private Logger logger = LoggerFactory.getLogger(SimulatedYSRSJService.class);

    private String url = Constant.URL_YSRSJ;

    private User user;
    private Cookie cookie;
    private boolean isRun = false;

    private WebDriver driver;

    private Actions action;

    public SimulatedYSRSJService(User user) {
        this.user = user;
        this.driver = new ChromeDriver();
        this.action = new Actions(driver);
    }

    @Override
    public void run() {

        try {
            //访问
            driver.get(url);
            TimeUnit.SECONDS.sleep(1);

            //输入账号
            WebElement usernameInput = driver.findElement(By.xpath("//*[@id=\"login_username\"]"));
            usernameInput.sendKeys(user.getUsername());

            //输入密码
            WebElement passwordInput = driver.findElement(By.xpath("//*[@id=\"login_password\"]"));
            passwordInput.sendKeys(user.getPassword());

            //提交表单
            WebElement submit = driver.findElement(By.xpath("//*[@id=\"sub\"]"));
            action.click(submit);
            action.perform();

            //获取课程
            List<WebElement> elements = driver.findElements(By.xpath("//*[@class=\"c-home-course\"]"));
            List<Course> courses = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                WebElement courseElement = elements.get(i);
                WebElement title = courseElement.findElement(By.xpath("//*[@class=\"c-home-course_title\"]"));
                Course course = new Course(i, title.getText(), courseElement);
                courses.add(course);
            }

            //遍历课程
            for (Course cours : courses) {
                Course course = enterAndGetCourseMessage(cours);

                if (course.isStudy_over()) {
                    logger.info("课程[{}]未已学习完毕！跳过！", cours.getCourse_title());
                    continue;
                }

                List<WebElement> tasks = course.getTasks();
                if (tasks == null || tasks.size() == 0) {
                    logger.info("课程[{}]未获取到对应目录！跳过！", cours.getCourse_title());
                    continue;
                }
                int startTask = 0;
                WebElement overTaskNum = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/section/div[1]/div[1]/ul/li[1]/span"));
                String overTask = overTaskNum.getText();
                if (!"0".equals(overTask)) {
                    try {
                        startTask = Integer.valueOf(overTask);
                    } catch (Exception e) {
                    }
                }

                for (int i = startTask; i < tasks.size(); i++) {
                    studyTask(tasks.get(i));
                }

            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private Course enterAndGetCourseMessage(Course course) {

        //进入课程页面
        action.click(course.getCourse());
        action.perform();

        WebElement percent = driver.findElement(By.xpath("//*[@id=\"orderprogress\"]/span/span"));
        String percentNum = percent.getText();
        if ("100%".equals(percentNum)) course.setStudy_over(true);
        course.setPercent(percentNum);

        List<WebElement> tasks = driver.findElements(By.xpath("//*[@class=\"task-item task-content mouse-control infinite-item\"]"));
        course.setTask_num(tasks.size());
        course.setTasks(tasks);

        return course;
    }

    private void studyTask(WebElement task) {

        //点击任务，自动开始播放视频
        WebElement element = task.findElement(By.xpath("//*[@class=\"title\"]"));
        action.click(element);
        action.perform();

        //调整视频速度，快速播放

        //监控视频，点击警告框

        boolean check = true;
        Thread jk = new Thread(() -> {
            while (check) {
                WebDriverWait wait = new WebDriverWait(driver, 60 * 1, 20000);
                Alert ok = wait.until(new ExpectedCondition<Alert>() {
                    @Override
                    public Alert apply(WebDriver webDriver) {
                        return driver.switchTo().alert();
                    }
                });
                ok.accept();
            }
        });

        jk.start();

        if () {
            check = false;
        }
        //回退至目录页面
        driver.navigate().back();
    }
}
