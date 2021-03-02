# 图形界面编程 GUI:AWT & SWING

## 一、简介

## 二、AWT

​		Java提供了一套基本的GUI类库，被称为**抽象窗口工具集（CAbstract Window ToolKit）**，它为Java提供了基本的图形组件。当使用AWT编程时，程序仅仅指定了界面组件的位置和行为，并未提供真正的实现，JVM调用操作系统本地的图形界面来创建和平台一致的对等体，使其在不同的操作系统中有着对应的界面风格，即**Write Once，Run Anywhere**。

### 1、AWT的继承体系

![](..\PIC\GUI-AWT-继承体系.png)

​		所有AWT编程相关的类均放在java.awt中

> - Component :Component 代表一个能图形化方式显示出来，并可与用户交互的对象。其中Container是一种特殊的Component，它代表一种容器，可以盛装普通的Component。
> - MenuComponent:图形界面菜单组件。
> - LayoutManager:管理组件布局方式。



### 2、简单的Component实现

#### 2.1 Component 常用 API

| 方法签名                                     | 方法功能                 |
| :------------------------------------------- | :----------------------- |
| setLocation(int x, int y)                    | 设置组件的位置           |
| setSize(int width,int height)                | 设置宽高                 |
| setBounds(int x, int y,int width,int height) | 联合使用                 |
| setResizable(boolean)                        | 设置是否可以更改窗体大小 |
| setVisible(boolean)                          | 设置该组件的可见性       |
| add()                                        |                          |

#### 2.2 Component 实现

--

### 3、Container 容器

 ![](..\PIC\GUI-AWT-Container容器.png)

​		重要组件介绍：

> - Window 窗口容器：可以独立存在的顶级窗口，默认使用BorderLayout管理器。
> - Panel 内嵌容器：可以容纳其他组件，但不能独立存在，必须内嵌与其他容器中使用，默认使用FlowLayout管理器。
> - ScrollPane 含有滚动条的容器：一个带有滚动条的容器，亦不可独立存在，默认使用BorderLayout管理器。

#### 3.1 常见API

| 方法签名                               | 方法功能                                 |
| -------------------------------------- | ---------------------------------------- |
| Component add(Component comp)          | 向容器中添加其他组件，并返回被添加的组件 |
| Component getComponentAt(int x, int y) | 返回指定点的组件                         |
| int getComponentCount()                | 返回该容器内组件的数量                   |
| Component[] getComponents()            | 返回该容器内的所有组件                   |

#### 3.2 简单实现

```java
import java.awt.*;

public class WindowsDemo {
    public static void main(String[] args) {
		testFrame();
        testPanel();
        testScrollPane();
    }

    public static void testFrame() {
        //创建窗口对象
        Frame frame = new Frame("ceshi");
        //设置窗口属性
        frame.setLocation(100,100);
        frame.setSize(500,300);
        frame.setVisible(true);
    }

    public static void testPanel() {
        Frame frame = new Frame("ceshi");
        Panel panel = new Panel();
		//添加子窗口到Panel中
        panel.add(new Button("确认"));
        panel.add(new TextField("文本"));
         //设置窗口属性
        frame.setBounds(100,100,500,300);
        frame.setVisible(true);
        //将Panel添加至Frame中
        frame.add(panel);
    }

	public static void testScrollPane() {
        Frame frame = new Frame("ceshi");
        //创建含滚动条的窗口 参数为配置，要求即使内容没有超过一个窗口也要显示滚动条。
        ScrollPane panel = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
        panel.add(new Button("确认"));
        panel.add(new TextField("文本"));
        frame.add(panel);
        frame.setBounds(100,100,500,300);
        frame.setVisible(true);
    }

}

```

注意事项：

> - AWT实现是控制操作系统调取对应的窗口实现的，因此展现时候使用的是操作系统的编码格式（中文系统为GBK），因此当编码解码格式不一致时（如在IDEA执行时IDEA使用UTF-8而系统使用GBK）会出现乱码情况。可食用`-Dfile.encoding=gbk`来实现执行时编码为GBK解决乱码。
> - 

#### 3.3 Box

​		Swing 包中默认使用BoxLayout布局的容器。

| 工厂方法                                          | 方法功能                                                     |
| ------------------------------------------------- | ------------------------------------------------------------ |
| static Box createHorizontalBox()                  | 创建一个水平排列组件的Box容器                                |
| static Box createVerticalBox()                    | 创建一个垂直排列组件的Box容器                                |
| static Component createHorizontalGlue()           | 创建一条水平Glue（允许在两个方向上同时拉伸的间距）           |
| static Component createVerticalGlue()             | 创建一条垂直Glue（允许在两个方向上同时拉伸的间距）           |
| static Component createHorizontalStrut(int width) | 创建一条指定宽度（水平不可拉伸）的水平Strut（可在垂直方向上拉伸） |
| static Component createVerticalStrut(int height)  | 创建一条指定高度（垂直不可拉伸）的垂直Strut（可在水平方向上拉伸） |

> - Glue 可变分隔，即空白版
> - Strut 半固定分隔，即空白版

### 4、LayoutManager

​		若手动通过`setBounds()`来设定容器的位置与大小，会导致程序的不通用性，即在不同的操作系统中显示出明显的差异。

​		如创建Label（标签）组件，若器**最佳大小**为居中，且自身高与宽一致，则需要在不同系统设置不同的属性。为了解决通用性问题，则提供了LayoutManager（布局管理器），使其可以感觉平台来自动调整组件的大小。

![](G:\A-MyStudy\技术\PIC\GUI-AWT-LayoutManager继承体系.png)

#### 4.1 FlowLayout 流式布局

​		在FlowLayout布局管理器中，组件像流水一样向某方向流动（排列），遇到障碍（边界）就折回，重头开始排列。默认为从左到右排列，遇到边界就从下一行开始。

| 构造方法                                    | 方法功能                                                 |
| ------------------------------------------- | -------------------------------------------------------- |
| FlowLayout（）                              | 使用**默认**的对齐方式以及**默认**的垂直间距、水平间距。 |
| FlowLayout（int align）                     | 使用**指定**的对齐方式及**默认**的垂直间距、水平间距。   |
| FlowLayout（int align，int hgap，int vgap） | 使用**指定**的对齐方式及**指定**的垂直间距、水平间距。   |

注：

> - 对齐方式包括FlowLayout.LEFT(默认) 左对齐、FlowLayout.RIGHT(默认) 右对齐、FlowLayout.CENTER(默认) 居中
> - 垂直间距为 hgap，水平间距为vgap，单位为像素
> - 实例中.pack()方法为自动调节合适的大小，可自己调节。

**代码演示：**

```java
public static void testFlowLayout() {
        Frame frame = new Frame();
        frame.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        for (int i = 0; i < 100; i++) {
            Button button = new Button();
            button.setLabel("AN"+i);
            frame.add(button);
        }
        frame.pack();
        frame.setVisible(true);
}
```

#### 4.2 BorderLayout  边框布局

​		BorderLayout将容器分为 East、South、West、North、Center五个区域，普通组件可以被放置在5个区域的任意一个中。

![](G:\A-MyStudy\技术\PIC\GUI-AWT-BorderLayout.png)

注：

> - 改变BorderLayout的容器大小时，南、北、中水平调整，东、西、中垂直调整。
> - 像BorderLayout中添加组件时，需要指定区域，若不指定默认为Center。
> - 如果向同一个区域添加多个组件时，后放入的组件会覆盖先放入的，即一个区域只能有一个组件；若需要多个，则再放入Container即可。
> - 若某个区域没有添加，则其空间被其他区域挤占，东西会被中挤占，南北会被东西中挤占。

| 构造方法                           | 方法功能                           |
| ---------------------------------- | ---------------------------------- |
| BorderLayout（int align）          | 使用**默认**的垂直间距、水平间距。 |
| BorderLayout（int hgap，int vgap） | 使用**指定**的垂直间距、水平间距。 |

**代码演示：**

```java
public static void testBorderLayout() {
        Frame frame = new Frame();
        frame.setLayout(new BorderLayout(30,10));

        frame.add(new Button("N"),BorderLayout.NORTH);
        frame.add(new Button("S"),BorderLayout.SOUTH);
        frame.add(new Button("E"),BorderLayout.EAST);
        frame.add(new Button("W"),BorderLayout.WEST);
        frame.add(new Button("C"),BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
```

#### 4.3 GridLayout 网格布局

​		GridLayout是将容器分割成纵横线分隔的网格，没个网格所占得区域大小相同。当先使用GridLayout的容器中添加组件时，默认从左到右、从上到下一次添加到每个网格中。与FlowLayout不同的是，放置在GridLayout布局管理器中的各组件的大小有组件所处的区域决定（自动默认占满网格）。

| 构造方法                                              | 方法功能                                                 |
| ----------------------------------------------------- | -------------------------------------------------------- |
| GridrLayout（int rows，int cols）                     | 使用**指定**的行数、列数和**默认**的垂直间距、水平间距。 |
| GridrLayout（int rows，int cols，int hgap，int vgap） | 使用**指定**的行数、列数和**指定**的垂直间距、水平间距。 |

**代码演示：**

```java
public static void testGridLayout() {
        Frame frame = new Frame();
        //默认即为Border，无需放置
        frame.setLayout(new BorderLayout(30,10));

        Panel panel = new Panel();
        panel.setLayout(new GridLayout(4,5));
        for (int i = 0; i < 20; i++) {
             panel.add(new Button("AN"+i));
        }

        frame.add(new TextField("jsq"),BorderLayout.NORTH);
        frame.add(panel,BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }
```

#### 4.4 GridBagLayout 网格包布局

​		GridBagLayout在GridLayout 基础上，允许一个组件占据跨越相邻的多个网格，并且可以设置各网格的大小互不相同。

注：

> - CridBagLayout 提供了 GridBagConstaints类，与特定的组件绑定，完成具体大小和跨越性的设置。
> - 当窗口大小发生改变时，会控制各组件同比例放缩。

| 成员变量 | 含义 |
| -------- | ---- |
| gridx    |      |
| gridy    |      |
|          |      |

--忽略

#### 4.5 CardLayout 卡片布局

​		CardLaayout 以时间维度来管理其组件，它将容器中的所有组件叠加起来，只有最上层的组件才会被显示，就好像一叠卡片一样。

| 构造方法                              | 方法功能                                                 |
| ------------------------------------- | -------------------------------------------------------- |
| CardLayout（）                        | 使用**指定**的行数、列数和**默认**的垂直间距、水平间距。 |
| CardLayout（int hgap，int vgap）      | 使用**指定**的容器与组件的垂直上下间距、水平左右间距。   |
| frist（Container target）             | 显示target容器中的第一个组件                             |
| last（Container target）              | 显示target容器中的最后一个组件                           |
| previous（Container target）          | 显示target容器中的上一个组件                             |
| next（Container target）              | 显示target容器中的下一个组件                             |
| show（Container target，String name） | 显示target容器中指定名字的组件                           |

注：

> - 顺序由添加顺序决定。
> - 通过方法来使其显示那一层的组件。
> - 

**代码演示：**

```java
public static void testGardLayout() {
        Frame frame = new Frame();
        //默认即为Border，无需放置
        //frame.setLayout(new BorderLayout(30,10));

        Panel panel = new Panel();
        CardLayout cardLayout = new CardLayout(1, 2);
        panel.setLayout(cardLayout);

        String[] names = {"1","2","3","4","5"};
        for (int i = 0; i < names.length; i++) {
            panel.add(names[i],new Button(names[i]));
        }
        frame.add(panel,BorderLayout.CENTER);



        Panel pan = new Panel();
        Button button1 = new Button("上一张");
        Button button2 = new Button("下一张");
        Button button3 = new Button("第一张");
        Button button4 = new Button("最后一张");
        Button button5 = new Button("第3张");

        ActionListener actionListener = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();

                switch (actionCommand) {
                    case "上一张":
                        cardLayout.previous(panel);
                        break;
                    case "下一张":
                        cardLayout.next(panel);
                        break;
                    case "第一张":
                        cardLayout.first(panel);
                        break;
                    case "最后一张":
                        cardLayout.last(panel);
                        break;
                    case "第3张":
                        cardLayout.show(panel,"3");
                        break;

                }
            }
        };

        button1.addActionListener(actionListener);
        button2.addActionListener(actionListener);
        button3.addActionListener(actionListener);
        button4.addActionListener(actionListener);
        button5.addActionListener(actionListener);

        pan.add(button1);
        pan.add(button2);
        pan.add(button3);
        pan.add(button4);
        pan.add(button5);

        frame.add(pan,BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
```

#### 4.6 BoxLayout

​		为了简化开发，Swing引入了新的布局管理器。允许在垂直与水平方向上摆放GUI组件。

| 构造方法                                | 方法功能                                                     |
| --------------------------------------- | ------------------------------------------------------------ |
| BoxLayout（Container target，int axis） | 该容器中的组件按照axis方向排列，包括BoxLayout.X_AXIS 横向 和 BoxLayout.Y_AXIS 纵向 |

**代码演示：**

```java
public static void testBoxLayout() {
        Frame frame = new Frame("Test");

        frame.setLayout(new BoxLayout(frame,BoxLayout.X_AXIS));
        frame.add(new Button("AN1"));
        frame.add(new Button("AN2"));
        
        frame.pack();
        frame.setVisible(true);
    }
public static void testBox2() {
        Frame frame = new Frame("Test");

        Box horizontalBox = Box.createHorizontalBox();

        horizontalBox.add(new Button("h1"));
        horizontalBox.add(Box.createHorizontalGlue());
        horizontalBox.add(new Button("h2"));
        horizontalBox.add(Box.createHorizontalStrut(30));
        horizontalBox.add(new Button("h3"));

        Box verticalBox = Box.createVerticalBox();

        verticalBox.add(new Button("v1"));
        verticalBox.add(Box.createVerticalStrut(30));
        verticalBox.add(new Button("v2"));
        verticalBox.add(Box.createVerticalGlue());
        verticalBox.add(new Button("v3"));

        frame.add(horizontalBox,BorderLayout.NORTH);
        frame.add(verticalBox,BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }
```

### 5、常用组件

#### 5.1 基本组件

| 组件名        | 功能                                     |
| ------------- | ---------------------------------------- |
| Button        | 按钮                                     |
| Canvas        | 用于绘图的画布                           |
| CheckBox      | 复选框的组件（可用作单选框）             |
| CheckBoxGroup | 将多个CheckBox组件组合起来，实现指定功能 |
| Choice        | 下拉选择框                               |
| Frame         | 基本窗口                                 |
| Label         | 标签类，用于放置提示性文本               |
| List          | 列表框组件，允许添加多条项目             |
| Panel         | 基本容器                                 |
| ScrollBar     | 滑动条组件                               |
| ScrollPane    | 带滑动条的容器                           |
| TextArea      | 多行文本域                               |
| TextField     | 单行文本框                               |
| Dialog        | 对话框                                   |

**代码演示：**

```java
package cn.sources.awt;

import javax.swing.*;
import java.awt.*;

public class WindowBasicComponentDemo {

    private Frame frame = new Frame();
    private TextArea ta = new TextArea(5,20);
    private Choice color = new Choice();

    private CheckboxGroup cbg = new CheckboxGroup();
    //标签 复选框组 是否默认选择
    private Checkbox man = new Checkbox("Man",cbg,true);
    private Checkbox wom = new Checkbox("Woman",cbg,false);

    private Checkbox isMar = new Checkbox("已婚？");

    private TextField tf = new TextField(50);
    private Button ok = new Button("确认");

    //列表长度，是否允许多选
    private List colorList = new List(6,true);

    public void init() {
        Box hBox = Box.createHorizontalBox();
        hBox.add(tf);
        hBox.add(ok);

        frame.add(hBox,BorderLayout.SOUTH);


        color.add("RED");
        color.add("BLUE");
        color.add("YELLOW");

        Box cBox = Box.createHorizontalBox();
        cBox.add(color);
        cBox.add(man);
        cBox.add(wom);
        cBox.add(isMar);

        Box tlBox = Box.createVerticalBox();
        tlBox.add(ta);
        tlBox.add(cBox);

        colorList.add("RED");
        colorList.add("BLUE");
        colorList.add("YELLOW");

        Box tBox = Box.createHorizontalBox();
        tBox.add(tlBox);
        tBox.add(colorList);

        frame.add(tBox);

        frame.pack();
        frame.setVisible(true);



    }

    public static void main(String[] args) {
        new WindowBasicComponentDemo().init();
    }

}

```

#### 5.2 Dialog 对话框

​		`Dialog`是`Window`的子类，属于容器的一种。`Dialog`是可以独立存在的顶级窗口。

注：

> - 对话框是可以独立存在的顶级窗口，但是通常依赖于其他窗口，即一般情况下需要一个父窗口。
> - 对话框有`非模式（non-modal）`与`模式（modal）`两种选择，即选用模式时，该对话框总位于父窗口之上，且对话框被关闭前，父窗口不可获得焦点。

| 构造方法                                           | 方法功能                                               |
| -------------------------------------------------- | ------------------------------------------------------ |
| Dialog（Frame owner，String title，boolean modal） | 创建一个对话框，参数为父窗口、标题、是否为模式对话框。 |

```java
package cn.sources.awt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowDialogDemo {

    public static void main(String[] args) {

        Frame frame = new Frame();

        Box box = Box.createVerticalBox();
        Button m_button = new Button("Modal");
        Button n_button = new Button("NON-Modal");

        Dialog modal = new Dialog(frame, "Modal", true);
        modal.setBounds(20,30,300,200);
        m_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modal.setVisible(true);
            }
        });
        Box vb = Box.createVerticalBox();
        vb.add(new TextField(20));
        vb.add(new Button("x1"));
        modal.add(vb);


        Dialog nModal = new Dialog(frame, "NON-Modal", false);
        nModal.setBounds(20,30,300,200);
        n_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nModal.setVisible(true);
            }
        });

        box.add(m_button);
        box.add(n_button);

        frame.add(box);

        frame.pack();
        frame.setVisible(true);


    }


}
```

##### 5.2.1 FileDialog 文件对话框

​		`FileDialog`是`Diolog `的子类，代表一个文件对话框，用于打开和保存文件。

注：

> - FileDialog 无法指定模态或非模态，它依赖于操作系统的文件对话框，由系统的文件系统决定是否为模态。
> - 

> - 

| 构造方法                                          | 方法功能                                                     |
| ------------------------------------------------- | ------------------------------------------------------------ |
| FileDialog（Frame owner，String title，int mode） | 创建一个对话框，参数为父窗口、标题、对话框类型。对话框类型包括：FileDialog.LOAD(打开、上传文件)，FileDialog.SAVE（保存、下载文件）。 |
| String getDirectory()                             | 获取文件的绝对路径                                           |
| String getFile()                                  | 获取文件的名称                                               |

```java
package cn.sources.awt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowDialogDemo {

    public static void main(String[] args) {

        Frame frame = new Frame();

        Box box = Box.createVerticalBox();
        Button m_button = new Button("Modal");
        Button n_button = new Button("NON-Modal");

        Button save = new Button("SAVE");
        Button load = new Button("NON-LOAD");

        Dialog modal = new Dialog(frame, "Modal", true);
        modal.setBounds(20,30,300,200);
        m_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modal.setVisible(true);
            }
        });
        Box vb = Box.createVerticalBox();
        vb.add(new TextField(20));
        vb.add(new Button("x1"));
        modal.add(vb);


        Dialog nModal = new Dialog(frame, "NON-Modal", false);
        nModal.setBounds(20,30,300,200);
        n_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nModal.setVisible(true);
            }
        });

        String loadFile = null;
        String saveFile = null;

        FileDialog fileS = new FileDialog(frame, "Save",FileDialog.SAVE);
        fileS.setBounds(20,30,300,200);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileS.setVisible(true);//该方法会阻塞！！！
                System.out.println(fileS.getFile()+"文件下载完成！！"+fileS.getDirectory());
            }
        });
        //fileS.add(new Label("Save"));

        FileDialog fileL = new FileDialog(frame, "Load",FileDialog.LOAD);
        fileL.setBounds(20,30,300,200);
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileL.setVisible(true);

                System.out.println(fileL.getFile()+"文件上传完成！！"+fileL.getDirectory());

            }
        });

        box.add(m_button);
        box.add(n_button);
        box.add(save);
        box.add(load);

        frame.add(box);

        frame.pack();
        frame.setVisible(true);
    }
}
```

### 6、事件处理

​		针对界面于组件，定义其操作功能于实现，即事件处理。

#### 6.1 GUI事件处理机制

​		当在某个组件发生某些操作时，自动触发一段代码的执行。

> - 事件源（Event Source）：操作发生的场所，通常指某个组件。
> - 事件（Event）：事件源发生的操作。
> - 事件监听器（Event Listener）：对这个事件进行处理。
> - 注册监听：吧事件监听器绑定到事件源上。

![](..\PIC\GUI-AWT-事件监听机制.png)

![](..\PIC\GUI-AWT-事件监听机制2.png)

#### 6.2 事件处理使用步骤

> 1. 创建事件源对象组件。
> 2. 自定义类，实现XXXListener接口，重写方法。
> 3. 创建事件监听器对象（自定义对象）。
> 4. 调用事件源组件对象的addListener方法进行注册监听。

```java
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

```

#### 6.3 事件与事件监听器

​		AWT中事件类都是`AWTEvent`类的子类，`AWTEvent`是`EventObject`的子类。

##### 6.3.1 事件	

​		事件分为低级事件与高级事件。

- 低级事件

| 事件           | 触发机制                                                     |
| -------------- | ------------------------------------------------------------ |
| ComponentEvent | 组件事件，当组件尺寸发生变化、位置发生移动、显示\隐藏状态发生变化时触发该事件。 |
| ContainerEvent | 容器事件，当容器中发生添加组件、删除组件时触发。             |
| WindowEvent    | 窗口事件，当窗口状态发生改变，如打开、关闭、最大化、最小化时触发。 |
| FocusEvent     | 焦点事件，当组件获得焦点或失去焦点时触发。                   |
| KeyEvent       | 键盘事件，当按键被按下、松开、单击时触发该事件。             |
| MouseEvent     | 鼠标事件，当单击、按下、松开、移动等时触发。                 |
| PaintEvent     | 组件绘制事件，该事件是一个特殊的事件类型，当GUI组件调用Update/paint方法来呈现自身触发该事件，并非专用于事件处理模型。 |

- 高级事件

| 事件           | 触发机制                                                     |
| -------------- | ------------------------------------------------------------ |
| ActionEvent    | 动作事件，当按钮、菜单项被单击，在TextField中按Enter时触发。 |
| AjustmentEvent | 调节事件，在滑动条上移动滑块调节数值时触发时触发。           |
| ItemEvent      | 选项事件，当用户选中、取消选中时触发。                       |
| TextEvent      | 文本事件，当文本框、文本域里的文本发生改变时触发时触发。     |

##### 6.3.2 事件监听器

​		不同的事件需要不同的监听器监听，即使用不同的接口。

| 事件           | 描述信息             | 监听器接口          |
| -------------- | -------------------- | ------------------- |
| ActionEvent    | 激活组件             | ActionListener      |
| ItemEvent      | 选择了某些项目       | ItemListener        |
| MouseEvent     | 鼠标移动             | MouseMotionListener |
| MouseEvent     | 鼠标点击             | MouseListener       |
| KeyEvent       | 键盘输入             | KeyListener         |
| FocusEvent     | 收到或失去焦点       | FocusListener       |
| AjustmentEvent | 移动了滚动条         | AjustmentListener   |
| ComponentEvent | 对象移动缩放显示隐藏 | ComponentListener   |
| WindowEvent    | 窗口接受到窗口级事件 | WindowListener      |
| ContainerEvent | 容器中增加删除组件   | ContainerListener   |
| TextEvent      | 文本发生改变         | TextListener        |

