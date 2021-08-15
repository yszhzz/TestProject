package com.mycode.service;

import com.mycode.app.MainFram;
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
    private MainFram father;

    public SimulatedYSRSJService(MainFram father, User user) {
        this.user = user;
        this.driver = new ChromeDriver();
        this.action = new Actions(driver);
        this.father = father;
    }

/*    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(2);
            flushUserStatus(Constant.STATUS_RUN);
            TimeUnit.SECONDS.sleep(10);
            flushUserStatus(Constant.STATUS_SUCCESS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void run() {

        try {
            logger.info("账号[{}]开始学习！", user.getUsername());
            flushUserStatus(Constant.STATUS_RUN);
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

            if (driver.getCurrentUrl().startsWith("http://218.26.234.215:8088/login")) {
                logger.info("账号[{}]登录失败！结束学习！", user.getUsername());
                flushUserStatus(Constant.STATUS_ERROR);
                father.writeMessage("账号["+user.getUsername()+"]登录失败！结束学习！");
                return;
            }
            logger.info("账号[{}]登录成功！", user.getUsername());
            //获取课程
            List<WebElement> elements = driver.findElements(By.xpath("//*[@class=\"c-home-course\"]"));
            List<Course> courses = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                WebElement courseElement = elements.get(i);
                //TODO 无法使用div[@class=\"c-home-course__title\"]获取节点
                WebElement title = courseElement.findElement(By.xpath("div[2]/div[1]"));
                if (title.getText() == null || title.getText().length() == 0) continue;
                Course course = new Course(i, title.getText(), courseElement);
                courses.add(course);
            }
            logger.info("账号[{}]课程获取成功！", user.getUsername());

            //遍历课程
            for (Course cours : courses) {
                try {
                    long startTime = System.currentTimeMillis();
                    logger.info("账号[{}]开始学习课程[{}]！", user.getUsername(), cours.getCourse_title());

                    //进入课程详情页，获取课程信息
                    Course allMessage = enterAndGetCourseMessage(cours);

                    if (allMessage.isStudy_over()) {
                        logger.info("账号[{}]课程[{}]已学习完毕！跳过！", user.getUsername(), cours.getCourse_title());
                        driver.navigate().back();
                        TimeUnit.SECONDS.sleep(2);
                        continue;
                    }
                    List<WebElement> tasks = allMessage.getTasks();
                    if (tasks == null || tasks.size() == 0) {
                        logger.info("账号[{}]课程[{}]未获取到对应目录！跳过！", user.getUsername(), cours.getCourse_title());
                        driver.navigate().back();
                        TimeUnit.SECONDS.sleep(2);
                        continue;
                    }

                    //标识任务开始下标
                    int startTask = 0;

                    //获取课程完成数
                    WebElement overTaskNum = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/section/div[1]/div[1]/ul/li[1]/span"));
                    String overTask = overTaskNum.getText();
                    if (!"0".equals(overTask)) {
                        try {
                            startTask = Integer.valueOf(overTask);
                        } catch (Exception e) {

                        }
                    }

                    //从下一任务开始循环学习
                    for (int i = startTask; i < tasks.size(); i++) {
                        studyTask(i);
                    }

                    //学习完毕，回退至个人信息页
                    driver.navigate().back();
                    long endTime = System.currentTimeMillis();
                    //本课程学习完毕
                    logger.info("账号[{}]课程[{}]学习完毕！共耗时[{}]分钟。", user.getUsername(), cours.getCourse_title(), (endTime - startTime) / (1000 * 60));
                } catch (Exception e) {
                    logger.info("账号[{}]课程[{}]学习出错！跳过该课程！", user.getUsername(), cours.getCourse_title(), e);
                    if (!driver.getCurrentUrl().equals("http://218.26.234.215:8088/")) {
                        driver.navigate().back();
                    }
                }
            }

            flushUserStatus(Constant.STATUS_SUCCESS);
            logger.info("账号[{}]学习完毕！", user.getUsername());
            father.writeMessage("账号["+user.getUsername()+"]学习完毕！");
            driver.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
            flushUserStatus(Constant.STATUS_ERROR);
            father.writeMessage("账号["+user.getUsername()+"]学习发生错误！结束学习！");
        }
    }

    private Course enterAndGetCourseMessage(Course course) throws InterruptedException {

        List<WebElement> elements = driver.findElements(By.xpath("//*[@class=\"c-home-course\"]"));
        WebElement webElement = elements.get(course.getCourse_index());

        //进入课程页面
        action.click(webElement);
        action.perform();

        TimeUnit.SECONDS.sleep(2);

        WebElement percent = driver.findElement(By.xpath("//*[@id=\"orderprogress\"]/span"));
        String percentNum = percent.getText().split("\n")[1];
        if ("100%".equals(percentNum)) course.setStudy_over(true);
        course.setPercent(percentNum);

        List<WebElement> tasks = driver.findElements(By.xpath("//*[@class=\"task-item task-content mouse-control infinite-item\"]"));
        course.setTask_num(tasks.size());
        course.setTasks(tasks);

        return course;
    }

    private void studyTask(int i) {

        try {

            List<WebElement> tasks = driver.findElements(By.xpath("//*[@class=\"task-item task-content mouse-control infinite-item\"]"));
            //点击任务，自动开始播放视频
            WebElement element = tasks.get(i).findElement(By.xpath("//*[@class=\"title\"]"));
            action.click(element);
            action.perform();

            //调整视频速度，快速播放

            //监控视频，点击警告框
            boolean check = true;
            Thread jk = new Thread(() -> {
                try {
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
                } catch (Exception e) {

                }
            });

            jk.start();

            //监控视频是否完成播放
            while (true) {
                break;
            }

            jk.stop();

            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            logger.error("任务学习错误！！！");
        }
        //回退至课程详情页面
        driver.navigate().back();
    }

    private void flushUserStatus(String status) {
        user.setStatus(status);
        if (father != null) father.fulshUserStatus();
    }
}
