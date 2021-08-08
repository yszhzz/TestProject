# Selenium 浏览器模拟

## 一、简介

​		**Selenium**是一个用于Web应用程序测试的工具。**Selenium**测试直接运行在浏览器中，就像真正的用户在操作一样。支持的浏览器包括IE（7, 8, 9, 10, 11），Mozilla Firefox，Safari，Google Chrome，Opera，Edge等。这个工具的主要功能包括：测试与浏览器的兼容性——测试你的应用程序看是否能够很好得工作在不同浏览器和操作系统之上。测试系统功能——创建回归测试检验软件功能和用户需求。支持自动录制动作和自动生成 .Net、Java、Perl等不同语言的测试脚本。

## 二、原理



## 三、使用及API (Java)

### 1、依赖

​		**Selenium** 的依赖可直接下载 [连接](http://docs.seleniumhq.org/download/)。Maven坐标如下：

```XML
<dependency>
	<groupId>org.seleniumhq.selenium</groupId>
	<artifactId>selenium-java</artifactId>
	<version>3.4.0</version>
</dependency>
```

### 2、驱动

​		**Selenium** 升级至3.0后的，对不同的浏览器驱动进行了规范。如果想使用selenium驱动不同的浏览器，必须单独下载并设置不同的浏览器驱动，连接如下。

> Firefox浏览器驱动：[geckodriver](https://github.com/mozilla/geckodriver/releases)
>
> Chrome浏览器驱动：[chromedriver](https://sites.google.com/a/chromium.org/chromedriver/home) [taobao备用地址](https://npm.taobao.org/mirrors/chromedriver)
>
> IE浏览器驱动：[IEDriverServer](http://selenium-release.storage.googleapis.com/index.html)
>
> Edge浏览器驱动：[MicrosoftWebDriver](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/)
>
> Opera浏览器驱动：[operadriver](https://github.com/operasoftware/operachromiumdriver/releases)
>
> PhantomJS浏览器驱动：[phantomjs](http://phantomjs.org/)

​		在使用时应导入其对应驱动：

```JAVA
// 配置系统参数 chromedriver服务地址
System.setProperty("webdriver.chrome.driver", "WebSimulated/src/main/resources/dirver/chromedriver.exe");
// 新建对应的驱动对象
WebDriver driver = new ChromeDriver(); 
```

​		对应其他驱动导入参数应使用对应`KEY-VALUE`进行配置

### 3、WebDriver 

​		`WebDriver` 为浏览器对象，该对象实现了相当多的功能，其最主要的是表示一个浏览器。

```java
public static void main(String[] args) {
    
 	WebDriver driver = new ChromeDriver();
    //访问url
	driver.get("http://www.itest.info");
    //获取该页面的标题
	String title = driver.getTitle();
	//关闭浏览器
	driver.close();
}
```

​		`WebDriver` 仍拥有很多功能：

- 获取网页参数

```JAVA
//获取当前页面url
driver.getCurrentUrl();
//获取当前页面标题
driver.getTitle();
//获取当前页面句柄
driver.getWindowHandle();
//获取当前浏览器所有句柄
driver.getWindowHandles();
```

- 控制浏览器大小，由`WebDriver`内部接口`Window`实现，使用`driver.manage().window()`获取：

```JAVA
driver.manage().window().maximize();
driver.manage().window().setSize(new Dimension(480, 800));
```

- 控制浏览器前进后退，由 `WebDriver`内部接口`Navigation`实现，使用`driver.navigate()`获取，

```JAVA
//控制页面后退
driver.navigate().back();
//控制页面前进
driver.navigate().forward();
//刷新页面
driver.navigate().refresh();
```

### 4、WebElement 

​		可通过多种方式来定位元素，通过使用`By.`使用对应定位方式：

| 定位方式          | 解释                     | 定位代码示例                              |
| ----------------- | ------------------------ | ----------------------------------------- |
| id                | 使用 id 值定位           | findElement(By.id("kw"));                 |
| name              | 使用 name 值定位         | findElement(By.name("wd"));               |
| class name        | 使用classname值定位      | findElement(By.className("s_ipt"));       |
| tag name          | 使用标签定位             | findElement(By.tagName("input"));         |
| link text         | 使用标签内容文本定位     | findElement(By.linkText("新闻"));         |
| partial link text | 使用部分标签内容文本定位 | findElement(By.partialLinkText("新"));    |
| xpath             | 使用xpath定位            | findElement(By.xpath("//*[@id='kw']"));   |
| css selector      | 使用css选择器定位        | findElement(By.cssSelector("[name=wd]")); |

​		定位时，可定位单一元素，亦可定位一组元素：

```JAVA
public static void main(String[] args) {  
 	WebDriver driver = new ChromeDriver();
	driver.get("http://www.itest.info");
    //定位单一元素
	driver.findElement(By.id("1"));
    //定位一组元素
	driver.findElements(By.id("1"));
	driver.close();
}
```

​		定位后，可获取定位的元素`WebElement` ，



### 5、WebElement











参考：

https://blog.csdn.net/qq_29817481/article/details/101052012

https://www.cnblogs.com/fairjm/p/html5_flash_video_speed_up.html

https://www.cnblogs.com/xinxin1994/p/9755552.html

https://www.cnblogs.com/TankXiao/p/5260445.html

https://blog.csdn.net/shandong_chu/article/details/7094626