package cn.mypro.swing.childtab.childview;

import cn.mypro.swing.childtab.HVAJapanAVMTab;
import cn.mypro.swing.childtab.JChildTabView;
import cn.mypro.swing.constant.LabelConstant;
import cn.mypro.swing.dao.HVAJapanAVLabelDao;
import cn.mypro.swing.entity.HVAJapanAVLabelM;
import cn.mypro.utils.DataBaseUtils;
import cn.mypro.utils.DbName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class HVAJapanLabelView implements JChildTabView {

    private static Logger logger = LoggerFactory.getLogger(HVAJapanLabelView.class);

    /*Label页面组件*/
    //展示所有Lab
    private  JList<HVAJapanAVLabelM> labelList = new JList<HVAJapanAVLabelM>();

    //添加Label-select
    private  JComboBox<String> level_1_Select = new JComboBox<>(LabelConstant.LABEL_LEVEL_1_SHOW);
    private  JComboBox<String> level_2_Select = new JComboBox<>();
    private  JTextField level_3_add = new JTextField();

    //Label-area
    private  JTextField label_code = new JTextField(20);
    private  JTextField label_show = new JTextField(20);
    private  JTextArea label_comment = new JTextArea(3, 20);
    private  JButton label_ok = new JButton("OK");
    private  JButton label_commit = new JButton("Commit");

    private JTextArea messageRun = null;

    private HVAJapanAVLabelM labelCase = new HVAJapanAVLabelM();

    private Connection serviceConn = null;
    private JFrame father = null;


    public HVAJapanLabelView(Connection serviceConn,JFrame father,JTextArea messageRun) {
        this.serviceConn = serviceConn;
        this.father = father;
        this.messageRun = messageRun;

    }

    public HVAJapanLabelView(JFrame father) {
        this.serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);
        this.father = father;
    }

    public JPanel initTab() {

        logger.info("开始初始化[标签操作界面]");
        logger.info("[标签操作界面]-[列表信息填充]");
        //填充展示列表
        flushLabelList();
        logger.info("[标签操作界面]-[绑定事件]");
        bindingOfTheEvent();
        logger.info("[标签操作界面]-[组装视图]");
        return assemblyOfTheView();
    }

    @Override
    public void bindingOfTheEvent() {
        level_1_Select.setMaximumRowCount(10);
        //绑定事件
        level_1_Select.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                level_2_Select.removeAllItems();
                String actionCommand = (String) level_1_Select.getSelectedItem();
                if (actionCommand.equals("J-剧情")) {
                    for (String s : LabelConstant.LABEL_LEVEL_2_J_SHOW) {
                        level_2_Select.addItem(s);
                    }
                } else if (actionCommand.equals("C-人物")) {
                    for (String s : LabelConstant.LABEL_LEVEL_2_C_SHOW) {
                        level_2_Select.addItem(s);
                    }
                } else if (actionCommand.equals("R-关系")) {
                    for (String s : LabelConstant.LABEL_LEVEL_2_R_SHOW) {
                        level_2_Select.addItem(s);
                    }
                } else if (actionCommand.equals("S-环境")) {
                    for (String s : LabelConstant.LABEL_LEVEL_2_S_SHOW) {
                        level_2_Select.addItem(s);
                    }
                } else if (actionCommand.equals("X-性爱")) {
                    for (String s : LabelConstant.LABEL_LEVEL_2_X_SHOW) {
                        level_2_Select.addItem(s);
                    }
                } else if (actionCommand.equals("O-其他")) {
                    for (String s : LabelConstant.LABEL_LEVEL_2_O_SHOW) {
                        level_2_Select.addItem(s);
                    }
                } else {
                }
            }
        });

        level_2_Select.addItemListener((e) -> {
            if (level_2_Select.getSelectedItem() == null || "请选择".equals((String) level_2_Select.getSelectedItem())) {

            } else {
                level_3_add.setText("");
                label_code.setText("");
                label_show.setText("");

                String type1 = (String) level_1_Select.getSelectedItem();
                String[] split1 = type1.split("-");
                String type2 = (String) level_2_Select.getSelectedItem();
                String[] split2 = type2.split("-");

                try {
                    level_3_add.setText(addZero(HVAJapanAVLabelDao.getNextType3ByType12(serviceConn, split1[0], split2[0])));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                String text = level_3_add.getText();

                label_code.setText(split1[0] + split2[0] + text);
                label_show.setText(split1[1] + "-" + split2[1] + "-");
            }

        });

        label_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labelCase.setValuesAsNormal();
                String type1 = (String) level_1_Select.getSelectedItem();
                String type2 = (String) level_2_Select.getSelectedItem();
                labelCase.setType_1(type1.split("-")[0]);
                labelCase.setType_2(type2.split("-")[0]);
                labelCase.setType_3(level_3_add.getText());

                labelCase.setLabel_code(label_code.getText());
                labelCase.setLabel_show(label_show.getText());

                if (label_comment.getText() == null || "".equals(label_comment.getText())) {
                    labelCase.setCommentAsNormal();
                } else {
                    labelCase.setLabel_comment(label_comment.getText());
                }

            }
        });

        label_commit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    HVAJapanAVLabelDao.insertLabel(serviceConn, labelCase);
                    serviceConn.commit();
                    JOptionPane.showMessageDialog(father, "提交成功", "LabelDialog", JOptionPane.INFORMATION_MESSAGE);
                    flushLabelList();

                    level_1_Select.setSelectedIndex(0);
                    level_1_Select.setSelectedIndex(0);
                    label_code.setText("");
                    label_show.setText("");
                    label_comment.setText("");

                } catch (SQLException error) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(father, "提交失败！" + error.getMessage(), "LabelDialog", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }

    @Override
    public JPanel assemblyOfTheView() {
        JPanel tabPanel = new JPanel();
        tabPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.RED, Color.GREEN, Color.BLUE, Color.GRAY));

        //组件组装
        Box selectBox = Box.createHorizontalBox();
        selectBox.add(level_1_Select);
        selectBox.add(level_2_Select);
        selectBox.add(level_3_add);

        Box buttenBox = Box.createHorizontalBox();
        buttenBox.add(label_ok);
        buttenBox.add(new JPanel());
        buttenBox.add(label_commit);

        Box textBox = Box.createVerticalBox();
        label_comment.setLineWrap(true);//自动换行
        textBox.add(label_code);
        textBox.add(new JPanel());
        textBox.add(label_show);
        textBox.add(new JPanel());
        textBox.add(label_comment);
        textBox.add(buttenBox);

        //labelList.setPreferredSize(new Dimension(150, 1000));
        selectBox.setPreferredSize(new Dimension(300, 25));
        textBox.setPreferredSize(new Dimension(300, 200));

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(selectBox), textBox); //上右 竖向
        //labelList.setVisibleRowCount(30);
        JSplitPane allSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(labelList), rightSplit); //上右 竖向




        tabPanel.add(allSplit, BorderLayout.CENTER);
        return tabPanel;
    }

    @Override
    public void flushMessageList() {

    }

    private void flushLabelList() {
        List<HVAJapanAVLabelM> hvaJapanAVLabelMS = null;
        try {
            hvaJapanAVLabelMS = HVAJapanAVLabelDao.qryAll(serviceConn);
            labelList.setListData(hvaJapanAVLabelMS.toArray(new HVAJapanAVLabelM[hvaJapanAVLabelMS.size()]));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String addZero(String s) {
        int length = s.length();
        if (length >= 3) return s;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 3 - length; i++) {
            stringBuilder.append("0");
        }
        stringBuilder.append(s);
        return stringBuilder.toString();
    }
}
