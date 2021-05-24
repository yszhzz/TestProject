package cn.mypro.swing.childtab.childview;

import cn.mypro.swing.constant.LabelConstant;
import cn.mypro.swing.dao.HVAJapanAVPersonDao;
import cn.mypro.swing.entity.HVAJapanAVPersonM;
import cn.mypro.swing.util.file.FileUtils;
import cn.mypro.utils.DataBaseUtils;
import cn.mypro.utils.DbName;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import java.util.List;

public class HVAJapanPersonView {


    private Connection serviceConn = null;
    private JFrame father = null;

    /*Person页面组件*/
    //查询组件
    private JTextField selectPersonField = new JTextField(40);
    private JButton selectPerson = new JButton("查询");
    private JList<HVAJapanAVPersonM> selectPersonResult = new JList<>();
    //操作组件
    private JButton confirmPerson = new JButton("确认信息");
    private JButton insertPerson = new JButton("新增人物");
    private JButton updatePerson = new JButton("修改人物信息");
    private JButton clearPerson = new JButton("清除信息");
    private JButton uploadPersonPhoto1 = new JButton("上传照片1");
    private JButton uploadPersonPhoto2 = new JButton("上传照片2");
    private JButton uploadPersonPhotoAuto = new JButton("自动上传");
    //信息组件
    private JLabel personNameLabel = new JLabel("姓名:");
    private JTextField personName = new JTextField(15);
    private JLabel personCNameLabel = new JLabel("中文姓名:");
    private JTextField personCName = new JTextField(15);
    private JLabel personONameLabel = new JLabel("其他姓名:");
    private JTextField personOName = new JTextField(15);
    private JLabel personGenderLabel = new JLabel("性别:");
    private JRadioButton male = new JRadioButton("男", false);
    private JRadioButton female = new JRadioButton("女", true);
    private ButtonGroup sexButtonGroup = new ButtonGroup();
    private JLabel personStartTimeLabel = new JLabel("出道时间:");
    private JTextField personStartTime = new JTextField(15);
    private JLabel personDataInfoLabel = new JLabel("人物信息:");
    private JTextArea personDataInfo = new JTextArea(5, 15);
    private JLabel personOtherInfoLabel = new JLabel("其他信息:");
    private JTextArea personOtherInfo = new JTextArea(5, 15);
    private JLabel personScoreLabel = new JLabel("评价:");
    private JComboBox<Integer> personScore = new JComboBox<>(LabelConstant.Person_Score);
    private JComboBox<String> personLevel = new JComboBox<>(LabelConstant.Person_Level);
    //人物照片组件
    private JLabel personPhoto1 = new JLabel();
    private JLabel personPhoto2 = new JLabel();
    private JFileChooser chooser = new JFileChooser("G:\\A-MyFree\\Picture\\360");

    /*缓存资源*/
    private HVAJapanAVPersonM personCase = new HVAJapanAVPersonM();

    public HVAJapanPersonView(JFrame father) {
        this.serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);
        this.father = father;
    }
    public HVAJapanPersonView(Connection serviceConn, JFrame father) {
        this.serviceConn = serviceConn;
        this.father = father;
    }



    public JPanel initAddNewPersonJFrame() {

        JPanel tabPersonPanel = new JPanel();

        flushPersonList();
        insertPerson.setEnabled(false);
        updatePerson.setEnabled(false);

        //绑定事件
        selectPerson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<HVAJapanAVPersonM> hvaJapanAVPersonMS = null;
                try {
                    hvaJapanAVPersonMS = HVAJapanAVPersonDao.qryPersonByName(serviceConn, selectPersonField.getText());
                    selectPersonResult.setListData(hvaJapanAVPersonMS.toArray(new HVAJapanAVPersonM[hvaJapanAVPersonMS.size()]));
                } catch (Exception error) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(father, "查询出错！！" + error.getMessage(), "PersonDialog", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        selectPersonResult.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                HVAJapanAVPersonM selectedValue = selectPersonResult.getSelectedValue();
                if (selectedValue != null) {
                    personName.setText(selectedValue.getNames());
                    personCName.setText(selectedValue.getCname());
                    personOName.setText(selectedValue.getOname());
                    if ("1".equals(selectedValue.getGender())) {
                        female.setSelected(true);
                    } else {
                        male.setSelected(true);
                    }
                    personStartTime.setText(selectedValue.getStart_time());
                    personDataInfo.setText(selectedValue.getDeta_info());
                    personOtherInfo.setText(selectedValue.getOther_info());
                    //personLevel.setSelectedIndex();
                    personScore.setSelectedIndex((int) selectedValue.getScores() - 1);

                    if (selectedValue.getPhtot_1() != null && selectedValue.getPhtot_1().length != 0) {
                        ImageIcon icon1 = new ImageIcon(selectedValue.getPhtot_1());
                        icon1 = new ImageIcon(icon1.getImage().getScaledInstance(400, 400, Image.SCALE_DEFAULT));
                        personPhoto1.setIcon(icon1);
                    }
                    if (selectedValue.getPhtot_2() != null && selectedValue.getPhtot_2().length != 0) {
                        ImageIcon icon2 = new ImageIcon(selectedValue.getPhtot_2());
                        icon2 = new ImageIcon(icon2.getImage().getScaledInstance(400, 400, Image.SCALE_DEFAULT));
                        personPhoto2.setIcon(icon2);
                    }

                    if (selectedValue.getLevels() == null || "".equals(selectedValue.getLevels())) {
                        personLevel.setSelectedIndex(0);
                    } else if ("SSS".equals(selectedValue.getLevels())) {
                        personLevel.setSelectedIndex(1);
                    } else if ("SS".equals(selectedValue.getLevels())) {
                        personLevel.setSelectedIndex(2);
                    } else if ("S".equals(selectedValue.getLevels())) {
                        personLevel.setSelectedIndex(3);
                    } else if ("A".equals(selectedValue.getLevels())) {
                        personLevel.setSelectedIndex(4);
                    } else if ("B".equals(selectedValue.getLevels())) {
                        personLevel.setSelectedIndex(5);
                    } else if ("C".equals(selectedValue.getLevels())) {
                        personLevel.setSelectedIndex(6);
                    } else if ("D".equals(selectedValue.getLevels())) {
                        personLevel.setSelectedIndex(7);
                    } else if ("E".equals(selectedValue.getLevels())) {
                        personLevel.setSelectedIndex(8);
                    } else if ("F".equals(selectedValue.getLevels())) {
                        personLevel.setSelectedIndex(9);
                    } else if ("G".equals(selectedValue.getLevels())) {
                        personLevel.setSelectedIndex(10);
                    }

                    personCase = selectedValue;
                    personCase.setHave(true);
                }
            }
        });

        confirmPerson.addActionListener((e) -> {
            if (personCase.isHave()) {
                updatePerson.setEnabled(true);

                personCase.setNames(personName.getText());
                personCase.setCname(personCName.getText());
                personCase.setOname(personOName.getText());
                personCase.setGender(female.isSelected() ? "1" : "2");
                personCase.setStart_time(personStartTime.getText());
                personCase.setDeta_info(personDataInfo.getText());
                personCase.setOther_info(personOtherInfo.getText());
                personCase.setScores(LabelConstant.Person_Score[personScore.getSelectedIndex()]);
                personCase.setLevels(LabelConstant.Person_Level[personLevel.getSelectedIndex()]);
                personCase.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()));
            } else {
                insertPerson.setEnabled(true);

                personCase.setValuesAsNormal();
                personCase.setNames(personName.getText());
                personCase.setCname(personCName.getText());
                personCase.setOname(personOName.getText());
                personCase.setGender(female.isSelected() ? "1" : "2");
                personCase.setStart_time(personStartTime.getText());
                personCase.setDeta_info(personDataInfo.getText());
                personCase.setOther_info(personOtherInfo.getText());
                personCase.setScores(LabelConstant.Person_Score[personScore.getSelectedIndex()]);
                personCase.setLevels(LabelConstant.Person_Level[personLevel.getSelectedIndex()]);
            }
        });

        clearPerson.addActionListener((e) -> {
            personName.setText("");
            personCName.setText("");
            personOName.setText("");

            female.setSelected(true);

            personStartTime.setText("");
            personDataInfo.setText("");
            personOtherInfo.setText("");
            personScore.setSelectedIndex(0);
            personLevel.setSelectedIndex(0);
            personPhoto1.setIcon(null);
            personPhoto2.setIcon(null);
            personCase = new HVAJapanAVPersonM();
            personCase.setHave(false);
        });

        insertPerson.addActionListener((e) -> {
            try {

                HVAJapanAVPersonDao.insertPerson(serviceConn, personCase);
                serviceConn.commit();
                insertPerson.setEnabled(false);
                JOptionPane.showMessageDialog(father, "插入成功！！", "PersonInsertDialog", JOptionPane.INFORMATION_MESSAGE);
                flushPersonList();
            } catch (SQLException error) {
                error.printStackTrace();
                JOptionPane.showMessageDialog(father, "插入出错！！" + error.getMessage(), "PersonInsertDialog", JOptionPane.ERROR_MESSAGE);
            }
        });

        updatePerson.addActionListener((e) -> {
            try {

                HVAJapanAVPersonDao.updatePerson(serviceConn, personCase);
                serviceConn.commit();
                updatePerson.setEnabled(false);
                JOptionPane.showMessageDialog(father, "修改成功！！", "PersonInsertDialog", JOptionPane.INFORMATION_MESSAGE);
                flushPersonList();
            } catch (SQLException error) {
                error.printStackTrace();
                JOptionPane.showMessageDialog(father, "修改出错！！" + error.getMessage(), "PersonUpdateDialog", JOptionPane.ERROR_MESSAGE);
            }
        });

        uploadPersonPhoto1.addActionListener((e) -> {

            try {
                chooser.showOpenDialog(father);
                File imageFile = chooser.getSelectedFile();
                byte[] bytesFromFile = FileUtils.getBytesFromFile(imageFile);

                personCase.setPhtot_1(bytesFromFile);

                ImageIcon icon1 = new ImageIcon(bytesFromFile);
                icon1 = new ImageIcon(icon1.getImage().getScaledInstance(400, 400, Image.SCALE_DEFAULT));
                personPhoto1.setIcon(icon1);
                //personPhoto1.setIcon(new ImageIcon(bytesFromFile));

                JOptionPane.showMessageDialog(father, "上传成功！！", "PersonPhotoDialog", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException error) {
                error.printStackTrace();
                JOptionPane.showMessageDialog(father, "上传出错！！" + error.getMessage(), "PersonUploadDialog", JOptionPane.ERROR_MESSAGE);
            }


        });
        uploadPersonPhoto2.addActionListener((e) -> {
            try {
                chooser.showOpenDialog(father);
                File imageFile = chooser.getSelectedFile();
                byte[] bytesFromFile = FileUtils.getBytesFromFile(imageFile);

                personCase.setPhtot_2(bytesFromFile);

                ImageIcon icon2 = new ImageIcon(bytesFromFile);
                icon2 = new ImageIcon(icon2.getImage().getScaledInstance(400, 400, Image.SCALE_DEFAULT));
                personPhoto2.setIcon(icon2);

                //personPhoto2.setIcon(new ImageIcon(bytesFromFile));

                JOptionPane.showMessageDialog(father, "上传成功！！", "PersonPhotoDialog", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException error) {
                error.printStackTrace();
                JOptionPane.showMessageDialog(father, "上传出错！！" + error.getMessage(), "PersonUploadDialog", JOptionPane.ERROR_MESSAGE);
            }
        });

        uploadPersonPhotoAuto.addActionListener((e) -> {
            try {
                File imageFile1 = new File("G:\\A-MyFree\\Picture\\360\\(0).jpg");
                File imageFile2 = new File("G:\\A-MyFree\\Picture\\360\\(1).jpg");

                byte[] bytesFromFile1 = FileUtils.getBytesFromFile(imageFile1);
                byte[] bytesFromFile2 = FileUtils.getBytesFromFile(imageFile2);

                personCase.setPhtot_1(bytesFromFile1);
                personCase.setPhtot_2(bytesFromFile2);

                ImageIcon icon1 = new ImageIcon(bytesFromFile1);
                icon1 = new ImageIcon(icon1.getImage().getScaledInstance(400, 400, Image.SCALE_DEFAULT));
                personPhoto1.setIcon(icon1);

                ImageIcon icon2 = new ImageIcon(bytesFromFile2);
                icon2 = new ImageIcon(icon2.getImage().getScaledInstance(400, 400, Image.SCALE_DEFAULT));
                personPhoto2.setIcon(icon2);

                //personPhoto1.setIcon(new ImageIcon(bytesFromFile1));
                //personPhoto2.setIcon(new ImageIcon(bytesFromFile2));

                //JOptionPane.showMessageDialog(father, "上传成功！！", "PersonPhotoDialog", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception error) {
                error.printStackTrace();
                JOptionPane.showMessageDialog(father, "上传出错！！" + error.getMessage(), "PersonUploadDialog", JOptionPane.ERROR_MESSAGE);
            }
        });

        selectPersonField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (selectPersonField.getText() == null || "".equals(selectPersonField.getText())) flushPersonList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        personPhoto1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog jDialog = new JDialog(father, "放大展示", true);
                //jDialog.setBounds(100,100,10,10);
                jDialog.setLocation(200, 200);
                //jDialog.setLocationRelativeTo(father); //显示在父窗口中心
                jDialog.add(new JLabel(new ImageIcon(personCase.getPhtot_1())));
                jDialog.pack();
                jDialog.setVisible(true);
            }
        });

        personPhoto2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog jDialog = new JDialog(father, "放大展示", true);
                //jDialog.setBounds(100,100,10,10);
                jDialog.setLocation(200, 200);
                jDialog.add(new JLabel(new ImageIcon(personCase.getPhtot_2())));
                jDialog.pack();
                jDialog.setVisible(true);
            }
        });


        //组件组装
        Box tabBox = Box.createVerticalBox();
        //查询栏
        JPanel jPanel = new JPanel();
        jPanel.add(selectPersonField);
        jPanel.add(selectPerson);

        //展示栏

        JPanel showPanel = new JPanel();
        showPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.RED, Color.GREEN, Color.BLUE, Color.GRAY));

        Box opraPersonBox = Box.createHorizontalBox(); //按钮集
        //confirmUpdate.setEnabled(false);
        opraPersonBox.add(confirmPerson);
        opraPersonBox.add(insertPerson);
        opraPersonBox.add(updatePerson);
        opraPersonBox.add(clearPerson);
        opraPersonBox.add(uploadPersonPhoto1);
        opraPersonBox.add(uploadPersonPhoto2);
        opraPersonBox.add(uploadPersonPhotoAuto);

        Box messagePersonBox = Box.createVerticalBox(); //信息集

        Box personNameBox = Box.createHorizontalBox();
        personNameBox.add(personNameLabel);
        personNameBox.add(personName);
        messagePersonBox.add(personNameBox);
        messagePersonBox.add(new JPanel());

        Box personCNameBox = Box.createHorizontalBox();
        personCNameBox.add(personCNameLabel);
        personCNameBox.add(personCName);
        messagePersonBox.add(personCNameBox);
        messagePersonBox.add(new JPanel());

        Box personONameBox = Box.createHorizontalBox();
        personONameBox.add(personONameLabel);
        personONameBox.add(personOName);
        messagePersonBox.add(personONameBox);
        messagePersonBox.add(new JPanel());

        Box personGenderBox = Box.createHorizontalBox();
        sexButtonGroup.add(female);
        sexButtonGroup.add(male);
        personGenderBox.add(personGenderLabel);
        personGenderBox.add(male);
        personGenderBox.add(female);
        messagePersonBox.add(personGenderBox);
        messagePersonBox.add(new JPanel());

        Box personStartTimeBox = Box.createHorizontalBox();
        personStartTimeBox.add(personStartTimeLabel);
        personStartTimeBox.add(personStartTime);
        messagePersonBox.add(personStartTimeBox);
        messagePersonBox.add(new JPanel());

        Box personDataInfoBox = Box.createHorizontalBox();
        personDataInfo.setLineWrap(true);
        personDataInfoBox.add(personDataInfoLabel);
        personDataInfoBox.add(personDataInfo);
        messagePersonBox.add(personDataInfoBox);
        messagePersonBox.add(new JPanel());

        Box personOtherInfoBox = Box.createHorizontalBox();
        personOtherInfo.setLineWrap(true);
        personOtherInfoBox.add(personOtherInfoLabel);
        personOtherInfoBox.add(personOtherInfo);
        messagePersonBox.add(personOtherInfoBox);
        messagePersonBox.add(new JPanel());

        Box personScoreBox = Box.createHorizontalBox();
        personScoreBox.add(personScoreLabel);
        personScoreBox.add(personScore);
        personScoreBox.add(personLevel);
        messagePersonBox.add(personScoreBox);

        Box photoBox = Box.createHorizontalBox();
        personPhoto1.setPreferredSize(new Dimension(400, 400));
        personPhoto2.setPreferredSize(new Dimension(400, 400));
        photoBox.add(personPhoto1);
        photoBox.add(new JPanel());
        photoBox.add(personPhoto2);

        photoBox.setPreferredSize(new Dimension(810, 400));
        opraPersonBox.setPreferredSize(new Dimension(800, 50));
        messagePersonBox.setPreferredSize(new Dimension(600, 450));
        selectPersonResult.setPreferredSize(new Dimension(200, 450));

        JSplitPane topRightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(photoBox), opraPersonBox); //上右 竖向

        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(messagePersonBox), topRightSplit); //上 横向

        JSplitPane allNewSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(selectPersonResult), topSplit); //总 竖向

        showPanel.add(allNewSplit);
        //展示截图

        tabBox.add(jPanel);
        tabBox.add(showPanel);

        tabPersonPanel.add(tabBox, BorderLayout.CENTER);
        return tabPersonPanel;
    }

    private void flushPersonList() {
        java.util.List<HVAJapanAVPersonM> hvaJapanAVPersonMS = null;
        try {
            hvaJapanAVPersonMS = HVAJapanAVPersonDao.qryAll(serviceConn);
            selectPersonResult.setListData(hvaJapanAVPersonMS.toArray(new HVAJapanAVPersonM[hvaJapanAVPersonMS.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}