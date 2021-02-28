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
