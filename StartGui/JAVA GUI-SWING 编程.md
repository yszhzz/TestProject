# 图形界面编程 GUI:AWT & SWING

## 一、简介

## 二、AWT

​		Java提供了一套基本的GUI类库，被称为**抽象窗口工具集（CAbstract Window ToolKit）**，它为Java提供了基本的图形组件。当使用AWT编程时，程序仅仅指定了界面组件的位置和行为，并未提供真正的实现，JVM调用操作系统本地的图形界面来创建和平台一致的对等体，使其在不同的操作系统中有着对应的界面风格，即**Write Once，Run Anywhere**。

### 1、AWT的继承体系

![](G:\A-MyStudy\技术\PIC\GUI-AWT-继承体系.png)

​		所有AWT编程相关的类均放在java.awt中

#### 1.1 Component 

​		Component 代表一个能图形化方式显示出来，并可与用户交互的对象，

#### 1.2 MenuComponent