package com.mycode.test;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SimulatedTest {

    private static String username = "jz18635080488";
    private static String password = "hdg123456";

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "WebSimulated/src/main/resources/dirver/chromedriver.exe");// chromedriver服务地址
        WebDriver driver = new ChromeDriver();
        //访问网页
        driver.get("http://218.26.234.215:8088/");

        WebDriver.Options manage = driver.manage();

        //设置大小
        driver.manage().window().maximize();
        //driver.manage().window().setSize(new Dimension(480, 800));
        //浏览器前进 后退 刷新
        driver.navigate().back();
        driver.navigate().forward();
        driver.navigate().refresh();
        String title = driver.getTitle();
        String url = driver.getCurrentUrl();
        System.out.println(title + "(" + url + ")");

        TimeUnit.SECONDS.sleep(5);
        WebElement usernameInput = driver.findElement(By.xpath("//*[@id=\"login_username\"]"));
        usernameInput.sendKeys(username);

        WebElement passwordInput = driver.findElement(By.xpath("//*[@id=\"login_password\"]"));
        passwordInput.sendKeys(password);

        WebElement submit = driver.findElement(By.xpath("//*[@id=\"sub\"]"));
        Actions action = new Actions(driver);
        Actions click = action.click(submit);
        click.perform();

        List<WebElement> elements = driver.findElements(By.xpath("//*[@class=\"c-home-course\"]"));
        for (WebElement element : elements) {
            action.click(element);
            click.perform();
            break;
        }

        List<WebElement> tasks = driver.findElements(By.xpath("//*[@class=\"task-item task-content mouse-control infinite-item\"]"));

        WebElement task1 = tasks.get(0);

        WebElement element = task1.findElement(By.xpath("//*[@class=\"title\"]"));
        action.click(element);
        click.perform();

        WebDriverWait wait = new WebDriverWait(driver,60*20,20000);
/*        wait.until(new ExpectedCondition<WebElement>(){
            @Override
            public WebElement apply(WebDriver text) {
                return text.findElement(By.id("kw"));
            }
        }).sendKeys("selenium");*/
        Alert ok = wait.until(new ExpectedCondition<Alert>() {
            @Override
            public Alert apply(WebDriver webDriver) {
                return driver.switchTo().alert();
            }
        });
        System.out.println(ok.getText());
        ok.accept();
/*        WebDriverWait wait = new WebDriverWait(driver,60*20,1);

        wait.until(new ExpectedCondition<WebElement>(){
            @Override
            public WebElement apply(WebDriver text) {
                return text.findElement(By.id("kw"));
            }
        }).sendKeys("selenium");

        driver.findElement(By.id("su")).click();*/
/*        String over = null;
        while (true) {
            Set<String> windowHandles = driver.getWindowHandles();
            String windowHandle = driver.getWindowHandle();
            System.out.println("1 - " + windowHandles.toString());
            System.out.println("2 - " + windowHandle);
            TimeUnit.SECONDS.sleep(10);
            over = windowHandle;
            break;
        }
        driver.switchTo().window(over);
        String pageSource = driver.getPageSource();
        System.out.println(pageSource);*/

        Alert alert = driver.switchTo().alert();
        String text = alert.getText();
        System.out.println(text);


        /*
        WebElement pause = task1.findElement(By.xpath("//*[@id=\"lesson-player\"]/div[6]/div[1]/div/span"));
        for (int i = 0; i < 5; i++) {
            System.out.println("Click " + i);
            action.click(pause);
            click.perform();
            TimeUnit.SECONDS.sleep(5);
        }
        */


        //driver.close();

    }

    private static void test1() {

    }

}
