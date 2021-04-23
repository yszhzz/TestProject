package cn.mypro.swing.childtab;

import cn.mypro.swing.constant.LabelConstant;
import cn.mypro.swing.dao.HVAJapanAVLabelDao;
import cn.mypro.swing.dao.HVAJapanAVMDao;
import cn.mypro.swing.dao.HVAJapanAVPersonDao;
import cn.mypro.swing.dao.HVAJapanAVSDao;
import cn.mypro.swing.entity.HVAJapanAVLabelM;
import cn.mypro.swing.entity.HVAJapanAVM;
import cn.mypro.swing.entity.HVAJapanAVPersonM;
import cn.mypro.swing.entity.HVAJapanAVS;
import cn.mypro.swing.util.BaiduTranslateUtil;
import cn.mypro.utils.DataBaseUtils;
import cn.mypro.utils.DbName;
import com.xuggle.xuggler.IContainer;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import javax.lang.model.element.VariableElement;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


public class HVAJapanAVMTab {
    JFrame father = null;
    //数据信息

    /*查询页面组件*/
    //查询组件
    JTextField selectField = new JTextField(40);
    JButton select = new JButton("查询");
    JList<HVAJapanAVM> selectResult = new JList<>();
    //展示图片组件
    JLabel cover = new JLabel();
    JLabel cut1 = new JLabel();
    JLabel cut2 = new JLabel();
    JLabel cut3 = new JLabel();
    //展示信息组件
    JLabel filePathLabel = new JLabel("文件源路径");
    JTextField filePath = new JTextField(LabelConstant.DEFAULT_FILE_PATH);
    String realPath = LabelConstant.DEFAULT_FILE_PATH;
    JLabel ifCodeLabel = new JLabel("番号");
    JTextField ifCode = new JTextField(20);
    JLabel oNameLabel = new JLabel("原名");
    JTextField oName = new JTextField(20);
    JLabel cNameLabel = new JLabel("译名");
    JTextField cName = new JTextField(20);
    JButton translate = new JButton("翻译");
    JLabel languageLable = new JLabel("语言");
    JComboBox<String> language = new JComboBox<String>(LabelConstant.AV_LANGUAGE);
    JLabel productionLabel = new JLabel("制作商");
    JTextField production = new JTextField(20);
    JLabel publishLabel = new JLabel("发行商");
    JTextField publish = new JTextField(20);
    JLabel publishTimeLabel = new JLabel("发行时间");
    JTextField publishTime = new JTextField(20);
    JLabel seriesLabel = new JLabel("系列");
    JTextField series = new JTextField(20);
    JLabel mosaicLable = new JLabel("马赛克(HM-有码,HN-去码,NM-无码)");
    JComboBox<String> mosaic = new JComboBox<String>(LabelConstant.MOSICA);
    JLabel durationLable = new JLabel("时长");
    JComboBox<Integer> durationHour = new JComboBox<Integer>(LabelConstant.TIME_HOUR);
    JComboBox<Integer> durationMinute = new JComboBox<Integer>(LabelConstant.TIME_MINUTE);
    JComboBox<Integer> durationSecond = new JComboBox<Integer>(LabelConstant.TIME_SECOND);
    JLabel describeLable = new JLabel("描述");
    JTextArea describe = new JTextArea(5, 20);
    JLabel AVScoreLabel = new JLabel("分数&推荐:");
    JComboBox<Integer> AVScore = new JComboBox<>(LabelConstant.Person_Score);
    JRadioButton recommend = new JRadioButton("推荐", false);

    JLabel avLabelLabel = new JLabel("标签");
    JList<HVAJapanAVLabelM> avLabelList = new JList<HVAJapanAVLabelM>();
    JButton avLabelButton = new JButton("变更");
    JLabel avPersonLabel = new JLabel("参演人员");
    JList<HVAJapanAVPersonM> avPersonList = new JList<HVAJapanAVPersonM>();
    JButton avPersonButton = new JButton("变更");

    //展示操作组件
    JButton confirmMessage = new JButton("确认信息");
    JButton insertMessage = new JButton("插入信息");
    JButton updateMessage = new JButton("修改信息");
    JButton showSources = new JButton("查看资源");
    JButton uploadCoverAndCut = new JButton("上传封面&截图");


    /*Person页面组件*/
    //查询组件
    JTextField selectPersonField = new JTextField(40);
    JButton selectPerson = new JButton("查询");
    JList<HVAJapanAVPersonM> selectPersonResult = new JList<>();
    //操作组件
    JButton confirmPerson = new JButton("确认信息");
    JButton insertPerson = new JButton("新增人物");
    JButton updatePerson = new JButton("修改人物信息");
    JButton clearPerson = new JButton("清除信息");
    JButton uploadPersonPhoto1 = new JButton("上传照片1");
    JButton uploadPersonPhoto2 = new JButton("上传照片2");
    JButton uploadPersonPhotoAuto = new JButton("自动上传");


    //信息组件
    JLabel personNameLabel = new JLabel("姓名:");
    JTextField personName = new JTextField(15);
    JLabel personCNameLabel = new JLabel("中文姓名:");
    JTextField personCName = new JTextField(15);
    JLabel personONameLabel = new JLabel("其他姓名:");
    JTextField personOName = new JTextField(15);
    JLabel personGenderLabel = new JLabel("性别:");
    JRadioButton male = new JRadioButton("男", false);
    JRadioButton female = new JRadioButton("女", true);
    ButtonGroup sexButtonGroup = new ButtonGroup();
    JLabel personStartTimeLabel = new JLabel("出道时间:");
    JTextField personStartTime = new JTextField(15);
    JLabel personDataInfoLabel = new JLabel("人物信息:");
    JTextArea personDataInfo = new JTextArea(5, 15);
    JLabel personOtherInfoLabel = new JLabel("其他信息:");
    JTextArea personOtherInfo = new JTextArea(5, 15);
    JLabel personScoreLabel = new JLabel("评价:");
    JComboBox<Integer> personScore = new JComboBox<>(LabelConstant.Person_Score);
    JComboBox<String> personLevel = new JComboBox<>(LabelConstant.Person_Level);

    //人物照片组件
    JLabel personPhoto1 = new JLabel();
    JLabel personPhoto2 = new JLabel();
    JFileChooser chooser = new JFileChooser("G:\\A-MyFree\\Picture\\360");

    /*Label页面组件*/
    //展示所有Lab
    JList<HVAJapanAVLabelM> labelList = new JList<HVAJapanAVLabelM>();
    //添加Label-select
    JComboBox<String> level_1_Select = new JComboBox<>(LabelConstant.LABEL_LEVEL_1_SHOW);
    JComboBox<String> level_2_Select = new JComboBox<>();
    JTextField level_3_add = new JTextField();
    //Label-area
    JTextField label_code = new JTextField(20);
    JTextField label_show = new JTextField(20);
    JTextArea label_comment = new JTextArea(3, 20);

    JButton label_ok = new JButton("OK");
    JButton label_commit = new JButton("Commit");

    /*Sources Dialog组件*/
    JTable sources = new JTable();

    JButton addSources = new JButton();
    JButton deleteAllSources = new JButton();
    JButton saveSources = new JButton();
    JButton exit = new JButton();

    /*数据*/
    Connection serviceConn = null;
    HVAJapanAVLabelM labelCase = new HVAJapanAVLabelM();
    HVAJapanAVPersonM personCase = new HVAJapanAVPersonM();
    HVAJapanAVM avCase = new HVAJapanAVM();
    HVAJapanAVS avsCase = new HVAJapanAVS();

    List<HVAJapanAVLabelM> allLabels;
    List<HVAJapanAVLabelM> noSelectedLabels;

    List<HVAJapanAVPersonM> allPersons;
    List<HVAJapanAVPersonM> noSelectedPersons;

    public JTabbedPane initAddNewSourceTab(JFrame jFrame) {
        serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);
        father = jFrame;
        JTabbedPane child = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        child.addTab("查询资源", initSelectSourcesJFrame());
        child.addTab("添加新资源", new JList<String>(new String[]{"订单一", "订单二", "订单三"}));
        child.addTab("添加新男/女优", initAddNewPersonJFrame());
        child.addTab("添加新标签", initAddNewLabelJFrame());

        return child;
    }

    public JPanel initSelectSourcesJFrame() {

        JPanel tabPanel = new JPanel();

        flushAVMList();
        //绑定Button
        select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<HVAJapanAVM> avms = null;
                try {
                    avms = HVAJapanAVMDao.selectMessageByAll(serviceConn, selectPersonField.getText());
                    selectResult.setListData(avms.toArray(new HVAJapanAVM[avms.size()]));
                } catch (Exception error) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(father, "查询出错！！" + error.getMessage(), "MessageDialog", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        insertMessage.setEnabled(false);
        updateMessage.setEnabled(false);
        selectResult.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                HVAJapanAVM selectedValue = selectResult.getSelectedValue();
                if (selectedValue != null) {
                    ifCode.setText(selectedValue.getIf_Code());
                    oName.setText(selectedValue.getOName());
                    cName.setText(selectedValue.getCName());
                    language.setSelectedIndex(selectedValue.getLanguages().equals("JAP") ?
                            0 : selectedValue.getLanguages().equals("CHN") ?
                            1 : selectedValue.getLanguages().equals("EUR") ?
                            2 : 3);
                    production.setText(selectedValue.getProduction_company());
                    publish.setText(selectedValue.getPublish_company());
                    publishTime.setText(selectedValue.getPublish_time());
                    series.setText(selectedValue.getSeries());
                    mosaic.setSelectedIndex(selectedValue.getLanguages().equals("HM") ?
                            0 : selectedValue.getLanguages().equals("HN") ?
                            1 : 2);
                    List<HVAJapanAVLabelM> labels = selectedValue.getLabels();
                    if (labels != null && labels.size() != 0) {
                        avLabelList.setListData(labels.toArray(new HVAJapanAVLabelM[labels.size()]));
                    }
                    List<HVAJapanAVPersonM> persons = selectedValue.getPersons();
                    if (persons != null && persons.size() != 0) {
                        avPersonList.setListData(persons.toArray(new HVAJapanAVPersonM[persons.size()]));
                    }
                    durationHour.setSelectedIndex((int) (selectedValue.getDuration() / 3600));
                    durationMinute.setSelectedIndex((int) (selectedValue.getDuration() % 3600) / 60);
                    durationSecond.setSelectedIndex((int) (selectedValue.getDuration() % 60));
                    describe.setText(selectedValue.getDescribe());

                    AVScore.setSelectedIndex((int) selectedValue.getScore() - 1);
                    recommend.setSelected("1".equals(selectedValue.getRecommend()));

                    ImageIcon iconCover = new ImageIcon(selectedValue.getCover());
                    iconCover = new ImageIcon(iconCover.getImage().getScaledInstance(cover.getWidth(), cover.getHeight(), Image.SCALE_DEFAULT));
                    cover.setIcon(iconCover);
                    ImageIcon iconCut1 = new ImageIcon(selectedValue.getCut1());
                    iconCut1 = new ImageIcon(iconCut1.getImage().getScaledInstance(cut1.getWidth(), cut1.getHeight(), Image.SCALE_DEFAULT));
                    cut1.setIcon(iconCut1);
                    ImageIcon iconCut2 = new ImageIcon(selectedValue.getCut2());
                    iconCut2 = new ImageIcon(iconCut2.getImage().getScaledInstance(cut2.getWidth(), cut2.getHeight(), Image.SCALE_DEFAULT));
                    cut2.setIcon(iconCut2);
                    ImageIcon iconCut3 = new ImageIcon(selectedValue.getCut3());
                    iconCut3 = new ImageIcon(iconCut3.getImage().getScaledInstance(cut3.getWidth(), cut3.getHeight(), Image.SCALE_DEFAULT));
                    cut3.setIcon(iconCut3);

                    avCase = selectedValue;
                    avCase.setHave(true);
                }
            }
        });

        confirmMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (avCase.isHave()) {
                    updateMessage.setEnabled(true);

                    avCase.setIf_Code(ifCode.getText());
                    avCase.setOName(oName.getText());
                    avCase.setCName(cName.getText());
                    avCase.setLanguages(LabelConstant.AV_LANGUAGE[language.getSelectedIndex()]);
                    avCase.setProduction_company(production.getText());
                    avCase.setPublish_company(publish.getText());
                    avCase.setPublish_time(publishTime.getText());
                    avCase.setSeries(series.getText());
                    avCase.setMosaic(LabelConstant.MOSICA[mosaic.getSelectedIndex()]);

                    avCase.setDuration(durationHour.getSelectedIndex() * 3600L + durationMinute.getSelectedIndex() * 60L + durationSecond.getSelectedIndex());
                    avCase.setDescribe(describe.getText());
                    avCase.setScore(LabelConstant.Person_Score[AVScore.getSelectedIndex()]);
                    avCase.setRecommend(recommend.isSelected() ? "1" : "0");

                } else {
                    insertMessage.setEnabled(true);

                    avCase.setIf_Code(ifCode.getText());
                    avCase.setOName(oName.getText());
                    avCase.setCName(cName.getText());
                    avCase.setLanguages(LabelConstant.AV_LANGUAGE[language.getSelectedIndex()]);
                    avCase.setProduction_company(production.getText());
                    avCase.setPublish_company(publish.getText());
                    avCase.setPublish_time(publishTime.getText());
                    avCase.setSeries(series.getText());
                    avCase.setMosaic(LabelConstant.MOSICA[mosaic.getSelectedIndex()]);

                    avCase.setDuration(durationHour.getSelectedIndex() * 3600L + durationMinute.getSelectedIndex() * 60L + durationSecond.getSelectedIndex());
                    avCase.setDescribe(describe.getText());
                    avCase.setScore(LabelConstant.Person_Score[AVScore.getSelectedIndex()]);
                    avCase.setRecommend(recommend.isSelected() ? "1" : "0");
                }
            }
        });
        insertMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    HVAJapanAVMDao.insertMessage(serviceConn, avCase);
                    serviceConn.commit();
                    insertMessage.setEnabled(false);
                    JOptionPane.showMessageDialog(father, "插入成功！！", "MessageInsertDialog", JOptionPane.INFORMATION_MESSAGE);
                    flushAVMList();
                } catch (SQLException error) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(father, "插入出错！！" + error.getMessage(), "MessageInsertDialog", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        updateMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    HVAJapanAVMDao.updateMessage(serviceConn, avCase);
                    serviceConn.commit();
                    updateMessage.setEnabled(false);
                    JOptionPane.showMessageDialog(father, "修改成功！！", "MessageUpdateDialog", JOptionPane.INFORMATION_MESSAGE);
                    flushAVMList();
                } catch (SQLException error) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(father, "修改出错！！" + error.getMessage(), "MessageUpdateDialog", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        uploadCoverAndCut.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String fileRootPath = filePath.getText();
                    String childPath = ifCode.getText();
                    File coverFile = new File(fileRootPath + "\\" + childPath + "\\" + "cover.jpg");
                    File cutFile1 = new File(fileRootPath + "\\" + childPath + "\\" + "c1.jpg");
                    File cutFile2 = new File(fileRootPath + "\\" + childPath + "\\" + "c2.jpg");
                    File cutFile3 = new File(fileRootPath + "\\" + childPath + "\\" + "c3.jpg");

                    if (coverFile.exists() && coverFile.isFile()) {
                        byte[] bytesFromFile = getBytesFromFile(coverFile);

                        ImageIcon iconCover = new ImageIcon(bytesFromFile);
                        //等比缩放
                        iconCover = new ImageIcon(iconCover.getImage().getScaledInstance(cover.getWidth(), cover.getHeight(), Image.SCALE_DEFAULT));
                        //强制缩放
                        //iconCover=new ImageIcon(iconCover.getImage().getScaledInstance(200, 300-25, Image.SCALE_DEFAULT));
                        cover.setIcon(iconCover);
                        avCase.setCover(bytesFromFile);
                    }
                    if (cutFile1.exists() && cutFile1.isFile()) {
                        byte[] bytesFromFile = getBytesFromFile(cutFile1);
                        ImageIcon icon = new ImageIcon(bytesFromFile);
                        icon = new ImageIcon(icon.getImage().getScaledInstance(cut1.getWidth(), cut1.getHeight(), Image.SCALE_DEFAULT));
                        cut1.setIcon(icon);
                        avCase.setCut1(bytesFromFile);
                    }
                    if (cutFile2.exists() && cutFile2.isFile()) {
                        byte[] bytesFromFile = getBytesFromFile(cutFile2);
                        ImageIcon icon = new ImageIcon(bytesFromFile);
                        icon = new ImageIcon(icon.getImage().getScaledInstance(cut2.getWidth(), cut2.getHeight(), Image.SCALE_DEFAULT));
                        cut2.setIcon(icon);
                        avCase.setCut2(bytesFromFile);
                    }
                    if (cutFile3.exists() && cutFile3.isFile()) {
                        byte[] bytesFromFile = getBytesFromFile(cutFile3);
                        ImageIcon icon = new ImageIcon(bytesFromFile);
                        icon = new ImageIcon(icon.getImage().getScaledInstance(cut3.getWidth(), cut3.getHeight(), Image.SCALE_DEFAULT));
                        cut3.setIcon(icon);
                        avCase.setCut3(bytesFromFile);
                    }
                    JOptionPane.showMessageDialog(father, "上传成功！！", "MessagePhotoDialog", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception error) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(father, "上传出错！！" + error.getMessage(), "MessagePhotoDialog", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        selectField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (selectField.getText() == null || "".equals(selectField.getText())) flushAVMList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        cover.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog jDialog = new JDialog(father, "放大展示", true);
                //jDialog.setBounds(100,100,10,10);
                jDialog.add(new JLabel(new ImageIcon(avCase.getCover())));
                jDialog.setLocationRelativeTo(father);
                jDialog.pack();
                jDialog.setVisible(true);
            }
        });
        cut1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog jDialog = new JDialog(father, "放大展示", true);
                //jDialog.setBounds(100,100,10,10);
                jDialog.add(new JLabel(new ImageIcon(avCase.getCut1())));
                jDialog.setLocationRelativeTo(father);
                jDialog.pack();
                jDialog.setVisible(true);
            }
        });
        cut2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog jDialog = new JDialog(father, "放大展示", true);
                //jDialog.setBounds(100,100,10,10);
                jDialog.add(new JLabel(new ImageIcon(avCase.getCut2())));
                jDialog.setLocationRelativeTo(father);
                jDialog.pack();
                jDialog.setVisible(true);
            }
        });
        cut3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog jDialog = new JDialog(father, "放大展示", true);
                //jDialog.setBounds(100,100,10,10);
                jDialog.add(new JLabel(new ImageIcon(avCase.getCut3())));
                jDialog.setLocationRelativeTo(father);
                jDialog.pack();
                jDialog.setVisible(true);
            }
        });

        avLabelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSelectLabelDialog();
            }
        });
        avPersonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSelectPersonDialog();
            }
        });
        showSources.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSourcesDialog();
            }
        });

        translate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String translateString = BaiduTranslateUtil.translateAsString(oName.getText());
                cName.setText(translateString);
            }
        });

        Box tabBox = Box.createVerticalBox();
        //查询栏
        JPanel jPanel = new JPanel();
        jPanel.add(selectField);
        jPanel.add(select);

        //展示栏

        JPanel showPanel = new JPanel();
        showPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.RED, Color.GREEN, Color.BLUE, Color.GRAY));

        Box opraBox = Box.createHorizontalBox(); //按钮集

        Box opraBoxChile1 = Box.createVerticalBox(); //按钮集
        opraBoxChile1.add(showSources);
        opraBoxChile1.add(confirmMessage);
        Box opraBoxChile2 = Box.createVerticalBox(); //按钮集
        opraBoxChile2.add(insertMessage);
        opraBoxChile2.add(updateMessage);
        opraBoxChile2.add(uploadCoverAndCut);

        opraBox.add(opraBoxChile1);
        opraBox.add(opraBoxChile2);

        Box messageBox = Box.createVerticalBox(); //信息集
        //文件源路径
        Box filePathBox = Box.createHorizontalBox();
        filePathBox.add(filePathLabel);
        filePathBox.add(filePath);
        messageBox.add(filePathBox);
        //番号
        Box codeBox = Box.createHorizontalBox();
        codeBox.add(ifCodeLabel);
        codeBox.add(ifCode);
        messageBox.add(codeBox);
        //原名称
        Box oNameBox = Box.createHorizontalBox();
/*        oNameLabel.setPreferredSize(new Dimension(30,15));
        oName.setPreferredSize(new Dimension(120,15));*/
        oNameBox.add(oNameLabel);
        oNameBox.add(oName);
        messageBox.add(oNameBox);
        //中文名称
        Box cNameBox = Box.createHorizontalBox();
        cNameBox.add(cNameLabel);
        cNameBox.add(cName);
        cNameBox.add(translate);
        messageBox.add(cNameBox);
        //语言
        Box languageBox = Box.createHorizontalBox();
        languageBox.add(languageLable);
        languageBox.add(language);
        messageBox.add(languageBox);
        //制作商
        Box productionBox = Box.createHorizontalBox();
        productionBox.add(productionLabel);
        productionBox.add(production);
        messageBox.add(productionBox);
        //发行商
        Box publishBox = Box.createHorizontalBox();
        publishBox.add(publishLabel);
        publishBox.add(publish);
        messageBox.add(publishBox);
        //发行时间
        Box publishTimeBox = Box.createHorizontalBox();
        publishTimeBox.add(publishTimeLabel);
        publishTimeBox.add(publishTime);
        messageBox.add(publishTimeBox);
        //系列名称
        Box seriesBox = Box.createHorizontalBox();
        seriesBox.add(seriesLabel);
        seriesBox.add(series);
        messageBox.add(seriesBox);
        //马赛克
        Box mosaicBox = Box.createHorizontalBox();
        mosaicBox.add(mosaicLable);
        mosaicBox.add(mosaic);
        messageBox.add(mosaicBox);
        //Person and label
        Box plBox = Box.createHorizontalBox();

        Box personBox = Box.createVerticalBox();
        personBox.add(avLabelLabel);
        personBox.add(avLabelList);
        personBox.add(avLabelButton);
        plBox.add(personBox);
        Box labelBox = Box.createVerticalBox();
        labelBox.add(avPersonLabel);
        labelBox.add(avPersonList);
        labelBox.add(avPersonButton);
        plBox.add(new JPanel());
        plBox.add(labelBox);
        messageBox.add(plBox);
        //视频长度
        Box durationBox = Box.createHorizontalBox();
        durationBox.add(durationLable);
        durationBox.add(durationHour);
        durationBox.add(durationMinute);
        durationBox.add(durationSecond);
        messageBox.add(durationBox);
        //描述
        Box describeBox = Box.createHorizontalBox();
        describeBox.add(describeLable);
        describeBox.add(describe);
        messageBox.add(describeBox);
        //分数
        Box scoreBox = Box.createHorizontalBox();
        scoreBox.add(AVScoreLabel);
        scoreBox.add(AVScore);
        scoreBox.add(new JPanel());
        scoreBox.add(recommend);
        messageBox.add(scoreBox);

        JPanel cutPanel = new JPanel(); //截图集
        cut1.setPreferredSize(new Dimension(100, 90));
        cut2.setPreferredSize(new Dimension(100, 90));
        cut3.setPreferredSize(new Dimension(100, 90));
        cutPanel.add(cut1);
        cutPanel.add(cut2);
        cutPanel.add(cut3);

        cover.setPreferredSize(new Dimension(200, 300));
        opraBox.setPreferredSize(new Dimension(200, 100));
        messageBox.setPreferredSize(new Dimension(600, 400));
        cutPanel.setPreferredSize(new Dimension(450, 100));
        selectResult.setPreferredSize(new Dimension(50, 500));

        JSplitPane topRightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(cover), opraBox); //上右 竖向

        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(messageBox), topRightSplit); //上 横向

        JSplitPane allSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, new JScrollPane(cutPanel)); //总 竖向

        JSplitPane allNewSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(selectResult), allSplit); //总 竖向

        showPanel.add(allNewSplit);
        //展示截图

        tabBox.add(jPanel);
        tabBox.add(showPanel);

        tabPanel.add(tabBox, BorderLayout.CENTER);

        return tabPanel;
    }

    public JPanel initAddNewSourcesJFrame() {

        JPanel tabPanel = new JPanel();


        return tabPanel;
    }

    public JPanel initAddNewPersonJFrame() {

        JPanel tabPersonPanel = new JPanel();

        flushPersonList();
        insertPerson.setEnabled(false);
        updatePerson.setEnabled(false);

        selectPerson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<HVAJapanAVPersonM> hvaJapanAVPersonMS = null;
                try {
                    hvaJapanAVPersonMS = HVAJapanAVPersonDao.qryPersonByName(serviceConn, selectPersonField.getText());
                    System.out.println(hvaJapanAVPersonMS);
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

                    personPhoto1.setIcon(new ImageIcon(selectedValue.getPhtot_1()));
                    personPhoto2.setIcon(new ImageIcon(selectedValue.getPhtot_2()));

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
                byte[] bytesFromFile = getBytesFromFile(imageFile);

                personCase.setPhtot_1(bytesFromFile);
                personPhoto1.setIcon(new ImageIcon(bytesFromFile));

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
                byte[] bytesFromFile = getBytesFromFile(imageFile);

                personCase.setPhtot_2(bytesFromFile);
                personPhoto2.setIcon(new ImageIcon(bytesFromFile));

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

                byte[] bytesFromFile1 = getBytesFromFile(imageFile1);
                byte[] bytesFromFile2 = getBytesFromFile(imageFile2);

                personCase.setPhtot_1(bytesFromFile1);
                personCase.setPhtot_2(bytesFromFile2);

                personPhoto1.setIcon(new ImageIcon(bytesFromFile1));
                personPhoto2.setIcon(new ImageIcon(bytesFromFile2));

                JOptionPane.showMessageDialog(father, "上传成功！！", "PersonPhotoDialog", JOptionPane.INFORMATION_MESSAGE);
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
                jDialog.add(new JLabel(new ImageIcon(personCase.getPhtot_1())));
                jDialog.setLocationRelativeTo(father);
                jDialog.pack();
                jDialog.setVisible(true);
            }
        });

        personPhoto2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog jDialog = new JDialog(father, "放大展示", true);
                //jDialog.setBounds(100,100,10,10);
                jDialog.add(new JLabel(new ImageIcon(personCase.getPhtot_2())));
                jDialog.setLocationRelativeTo(father);
                jDialog.pack();
                jDialog.setVisible(true);
            }
        });

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
        personDataInfoBox.add(personDataInfoLabel);
        personDataInfoBox.add(personDataInfo);
        messagePersonBox.add(personDataInfoBox);
        messagePersonBox.add(new JPanel());

        Box personOtherInfoBox = Box.createHorizontalBox();
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
        photoBox.add(personPhoto1);
        photoBox.add(new JPanel());
        photoBox.add(personPhoto2);

        photoBox.setPreferredSize(new Dimension(800, 400));
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

    public JPanel initAddNewLabelJFrame() {

        JPanel tabPanel = new JPanel();
        tabPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.RED, Color.GREEN, Color.BLUE, Color.GRAY));

        flushLabelList();

        level_1_Select.setMaximumRowCount(5);


        Box selectBox = Box.createHorizontalBox();
        selectBox.add(level_1_Select);
        selectBox.add(level_2_Select);
        selectBox.add(level_3_add);

        Box buttenBox = Box.createHorizontalBox();
        buttenBox.add(label_ok);
        buttenBox.add(new JPanel());
        buttenBox.add(label_commit);


        Box textBox = Box.createVerticalBox();
        textBox.add(label_code);
        textBox.add(new JPanel());
        textBox.add(label_show);
        textBox.add(new JPanel());
        textBox.add(label_comment);
        textBox.add(buttenBox);

        labelList.setPreferredSize(new Dimension(150, 500));
        selectBox.setPreferredSize(new Dimension(300, 25));
        textBox.setPreferredSize(new Dimension(300, 200));

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(selectBox), textBox); //上右 竖向

        JSplitPane allSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(labelList), rightSplit); //上右 竖向

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
                    level_3_add.setText(HVAJapanAVLabelDao.getNextType3ByType12(serviceConn, split1[0], split2[0]));
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

        tabPanel.add(allSplit, BorderLayout.CENTER);
        return tabPanel;
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

    private void flushPersonList() {
        List<HVAJapanAVPersonM> hvaJapanAVPersonMS = null;
        try {
            hvaJapanAVPersonMS = HVAJapanAVPersonDao.qryAll(serviceConn);
            selectPersonResult.setListData(hvaJapanAVPersonMS.toArray(new HVAJapanAVPersonM[hvaJapanAVPersonMS.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void flushAVMList() {
        List<HVAJapanAVM> allMessage = null;
        try {
            allMessage = HVAJapanAVMDao.selectAllMessage(serviceConn);
            if (allMessage != null) {
                selectResult.setListData(allMessage.toArray(new HVAJapanAVM[allMessage.size()]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytesFromFile(File cntIdImg) throws IOException {
        FileInputStream is = new FileInputStream(cntIdImg);
        long length = cntIdImg.length();
        byte[] idImgBytes = new byte[(int) length];
        is.read(idImgBytes);
        is.close();
        return idImgBytes;

    }

    public void showSelectLabelDialog() {
        JDialog jDialog = new JDialog(father, "标签选择器", true);

/*        List<HVAJapanAVLabelM> allLabels;
        List<HVAJapanAVLabelM> noSelectedLabels;*/

        JList<HVAJapanAVLabelM> noSelected = new JList<>();
        JList<HVAJapanAVLabelM> yesSelected = new JList<>();
        noSelected.setPreferredSize(new Dimension(150, 300));
        yesSelected.setPreferredSize(new Dimension(150, 300));

        try {
            allLabels = HVAJapanAVLabelDao.qryAll(serviceConn);
            noSelectedLabels = allLabels;
            if (avCase != null && avCase.getLabels() != null && avCase.getLabels().size() != 0) {
                yesSelected.setListData(avCase.getLabels().toArray(new HVAJapanAVLabelM[avCase.getLabels().size()]));
                noSelectedLabels = removeRepLabels(allLabels, avCase.getLabels());
            }
            noSelected.setListData(noSelectedLabels.toArray(new HVAJapanAVLabelM[noSelectedLabels.size()]));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton add = new JButton("添加标签");
        JButton remove = new JButton("删除标签");
        add.setEnabled(false);
        remove.setEnabled(false);

        yesSelected.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                remove.setEnabled(true);
            }
        });

        noSelected.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                add.setEnabled(true);
            }
        });

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HVAJapanAVLabelM selectedValue = noSelected.getSelectedValue();
                int selectedIndex = noSelected.getSelectedIndex();
                if (avCase == null) avCase = new HVAJapanAVM();
                if (avCase.getLabels() == null) avCase.setLabels(new ArrayList<HVAJapanAVLabelM>());
                avCase.getLabels().add(selectedValue);
                noSelectedLabels.remove(selectedIndex);
                avCase.getLabels().sort(new Comparator<HVAJapanAVLabelM>() {
                    @Override
                    public int compare(HVAJapanAVLabelM o1, HVAJapanAVLabelM o2) {
                        return o1.getLabel_code().compareTo(o2.getLabel_code());
                    }
                });
                noSelectedLabels.sort(new Comparator<HVAJapanAVLabelM>() {
                    @Override
                    public int compare(HVAJapanAVLabelM o1, HVAJapanAVLabelM o2) {
                        return o1.getLabel_code().compareTo(o2.getLabel_code());
                    }
                });
                yesSelected.setListData(avCase.getLabels().toArray(new HVAJapanAVLabelM[avCase.getLabels().size()]));
                noSelected.setListData(noSelectedLabels.toArray(new HVAJapanAVLabelM[noSelectedLabels.size()]));
                avLabelList.setListData(avCase.getLabels().toArray(new HVAJapanAVLabelM[avCase.getLabels().size()]));
                add.setEnabled(false);
            }
        });
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HVAJapanAVLabelM selectedValue = yesSelected.getSelectedValue();
                int selectedIndex = yesSelected.getSelectedIndex();
                if (noSelectedLabels == null) noSelectedLabels = new ArrayList<HVAJapanAVLabelM>();
                noSelectedLabels.add(selectedValue);
                avCase.getLabels().remove(selectedIndex);
                avCase.getLabels().sort(new Comparator<HVAJapanAVLabelM>() {
                    @Override
                    public int compare(HVAJapanAVLabelM o1, HVAJapanAVLabelM o2) {
                        return o1.getLabel_code().compareTo(o2.getLabel_code());
                    }
                });
                noSelectedLabels.sort(new Comparator<HVAJapanAVLabelM>() {
                    @Override
                    public int compare(HVAJapanAVLabelM o1, HVAJapanAVLabelM o2) {
                        return o1.getLabel_code().compareTo(o2.getLabel_code());
                    }
                });
                yesSelected.setListData(avCase.getLabels().toArray(new HVAJapanAVLabelM[avCase.getLabels().size()]));
                noSelected.setListData(noSelectedLabels.toArray(new HVAJapanAVLabelM[noSelectedLabels.size()]));
                avLabelList.setListData(avCase.getLabels().toArray(new HVAJapanAVLabelM[avCase.getLabels().size()]));
                remove.setEnabled(false);
            }
        });


        jDialog.add(noSelected, BorderLayout.WEST);
        jDialog.add(yesSelected, BorderLayout.EAST);
        Box buttonBox = Box.createVerticalBox();
        buttonBox.add(add);
        buttonBox.add(remove);
        jDialog.add(buttonBox);
        jDialog.setLocationRelativeTo(father);
        jDialog.pack();
        jDialog.setVisible(true);
    }

    public List<HVAJapanAVLabelM> removeRepLabels(List<HVAJapanAVLabelM> list1, List<HVAJapanAVLabelM> list2) {
        cyc1:
        for (HVAJapanAVLabelM label2 : list2) {
            cyc2:
            for (HVAJapanAVLabelM label1 : list1) {
                if (label1.getLabel_code().equals(label2.getLabel_code())) {
                    list1.remove(label1);
                    continue cyc1;
                }
            }
        }
        return list1;
    }

    public List<HVAJapanAVPersonM> removeRepPersons(List<HVAJapanAVPersonM> list1, List<HVAJapanAVPersonM> list2) {
        cyc1:
        for (HVAJapanAVPersonM person2 : list2) {
            cyc2:
            for (HVAJapanAVPersonM person1 : list1) {
                if (person1.getUuid().equals(person2.getUuid())) {
                    list1.remove(person1);
                    continue cyc1;
                }
            }
        }
        return list1;
    }

    public void showSelectPersonDialog() {
        JDialog jDialog = new JDialog(father, "任务选择器", true);

        JList<HVAJapanAVPersonM> noSelected = new JList<>();
        JList<HVAJapanAVPersonM> yesSelected = new JList<>();
        noSelected.setPreferredSize(new Dimension(150, 300));
        yesSelected.setPreferredSize(new Dimension(150, 300));

        try {
            allPersons = HVAJapanAVPersonDao.qryAll(serviceConn);
            noSelectedPersons = allPersons;
            if (avCase != null && avCase.getPersons() != null && avCase.getPersons().size() != 0) {
                yesSelected.setListData(avCase.getPersons().toArray(new HVAJapanAVPersonM[avCase.getPersons().size()]));
                noSelectedPersons = removeRepPersons(allPersons, avCase.getPersons());
            }
            noSelected.setListData(noSelectedPersons.toArray(new HVAJapanAVPersonM[noSelectedPersons.size()]));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        JButton add = new JButton("添加人物");
        JButton remove = new JButton("删除人物");
        add.setEnabled(false);
        remove.setEnabled(false);

        yesSelected.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                remove.setEnabled(true);
            }
        });

        noSelected.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                add.setEnabled(true);
            }
        });

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HVAJapanAVPersonM selectedValue = noSelected.getSelectedValue();
                int selectedIndex = noSelected.getSelectedIndex();
                if (avCase == null) avCase = new HVAJapanAVM();
                if (avCase.getPersons() == null) avCase.setPersons(new ArrayList<HVAJapanAVPersonM>());
                avCase.getPersons().add(selectedValue);
                noSelectedPersons.remove(selectedIndex);
                avCase.getPersons().sort(new Comparator<HVAJapanAVPersonM>() {
                    @Override
                    public int compare(HVAJapanAVPersonM o1, HVAJapanAVPersonM o2) {
                        int i = o1.getGender().compareTo(o2.getGender());
                        if (i != 0) return i;
                        return o1.getOname().compareTo(o2.getOname());
                    }
                });
                noSelectedPersons.sort(new Comparator<HVAJapanAVPersonM>() {
                    @Override
                    public int compare(HVAJapanAVPersonM o1, HVAJapanAVPersonM o2) {
                        int i = o1.getGender().compareTo(o2.getGender());
                        if (i != 0) return i;
                        return o1.getOname().compareTo(o2.getOname());
                    }
                });
                yesSelected.setListData(avCase.getPersons().toArray(new HVAJapanAVPersonM[avCase.getPersons().size()]));
                noSelected.setListData(noSelectedPersons.toArray(new HVAJapanAVPersonM[noSelectedPersons.size()]));
                avPersonList.setListData(avCase.getPersons().toArray(new HVAJapanAVPersonM[avCase.getPersons().size()]));
                add.setEnabled(false);
            }
        });
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HVAJapanAVPersonM selectedValue = yesSelected.getSelectedValue();
                int selectedIndex = yesSelected.getSelectedIndex();
                if (noSelectedPersons == null) noSelectedPersons = new ArrayList<HVAJapanAVPersonM>();
                noSelectedPersons.add(selectedValue);
                avCase.getPersons().remove(selectedIndex);
                avCase.getPersons().sort(new Comparator<HVAJapanAVPersonM>() {
                    @Override
                    public int compare(HVAJapanAVPersonM o1, HVAJapanAVPersonM o2) {
                        if (o1 == null || o2 == null) return 0;
                        int i = o1.getGender().compareTo(o2.getGender());
                        if (i != 0) return i;
                        return o1.getOname().compareTo(o2.getOname());
                    }
                });
                noSelectedPersons.sort(new Comparator<HVAJapanAVPersonM>() {
                    @Override
                    public int compare(HVAJapanAVPersonM o1, HVAJapanAVPersonM o2) {
                        if (o1 == null || o2 == null) return 0;
                        int i = o1.getGender().compareTo(o2.getGender());
                        if (i != 0) return i;
                        return o1.getOname().compareTo(o2.getOname());
                    }
                });
                yesSelected.setListData(avCase.getPersons().toArray(new HVAJapanAVPersonM[avCase.getPersons().size()]));
                noSelected.setListData(noSelectedPersons.toArray(new HVAJapanAVPersonM[noSelectedPersons.size()]));
                avPersonList.setListData(avCase.getPersons().toArray(new HVAJapanAVPersonM[avCase.getPersons().size()]));
                remove.setEnabled(false);
            }
        });

        jDialog.add(noSelected, BorderLayout.WEST);
        jDialog.add(yesSelected, BorderLayout.EAST);
        Box buttonBox = Box.createVerticalBox();
        buttonBox.add(add);
        buttonBox.add(remove);
        jDialog.add(buttonBox);
        jDialog.setLocationRelativeTo(father);
        jDialog.pack();
        jDialog.setVisible(true);
    }

    public void showSourcesDialog() {
        if (avCase == null || avCase.getUuid() == null) {
            JOptionPane.showMessageDialog(father, "无对应资源！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
        } else {
            List<HVAJapanAVS> hvaJapanAVS = avCase.getSources();;

/*            hvaJapanAVS = new ArrayList<>();
            HVAJapanAVS hvaJapanAVS_1 = new HVAJapanAVS();
            hvaJapanAVS_1.setSource_uri("11");
            hvaJapanAVS_1.setQuality("5");
            hvaJapanAVS.add(hvaJapanAVS_1);
            HVAJapanAVS hvaJapanAVS_2 = new HVAJapanAVS();
            hvaJapanAVS_2.setSource_uri("22  ");
            hvaJapanAVS_2.setQuality("5");
            hvaJapanAVS.add(hvaJapanAVS_2);*/
            //hvaJapanAVS = HVAJapanAVSDao.selectSourcesByMUUID(serviceConn, avCase.getUuid());

            JDialog jDialog = new JDialog(father, "资源列表", true);

            JButton add = new JButton("添加资源");
            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });


            JTable table = null;

            if (hvaJapanAVS == null || hvaJapanAVS.size() == 0) {
                JOptionPane.showMessageDialog(father, "无资源！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
            } else {
                int size = hvaJapanAVS.size();
                Object[][] tableData = new Object[size][LabelConstant.SOURCE_TABLE_COLUMN_TITLE.length];
                for (int i = 0; i < size; i++) {
                    HVAJapanAVS hvaJapanAVS1 = hvaJapanAVS.get(i);
                    tableData[i] = new Object[]{hvaJapanAVS1.getSource_uri(), hvaJapanAVS1.getQuality(), null};
                }
                //table = new JTable(tableData, LabelConstant.SOURCE_TABLE_COLUMN_TITLE);

                DefaultTableModel model = new DefaultTableModel(tableData, LabelConstant.SOURCE_TABLE_COLUMN_TITLE) {
                };
                table = new JTable(model);
                class ActionPanelEditorRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
                    JButton delete = new JButton("查看");
                    JButton details = new JButton("详情");
                    Box buttonBox = Box.createHorizontalBox();
                    List<HVAJapanAVS> hvaJapanAVS = null;

                    int ro;
                    int col;

                    public ActionPanelEditorRenderer(List<HVAJapanAVS> hvaJapanAVS) {
                        buttonBox.add(details);
                        buttonBox.add(delete);
                        this.hvaJapanAVS = hvaJapanAVS;
                        details.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                HVAJapanAVS hvaJapanAVS1 = hvaJapanAVS.get(ro - 1);
                                showSourcesDetailDialog(hvaJapanAVS1);
                            }
                        });

                        delete.addActionListener(new ActionListener() {
                            @SneakyThrows
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                HVAJapanAVS hvaJapanAVS1 = hvaJapanAVS.get(ro - 1);
                                HVAJapanAVSDao.deleteSources(serviceConn,hvaJapanAVS1);
                                avCase.setSources(HVAJapanAVSDao.selectSourcesByMUUID(serviceConn, avCase.getUuid()));
                                JOptionPane.showMessageDialog(father, "请重新打开资源表格！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
                            }
                        });

                    }

                    @Override
                    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                        buttonBox.setBackground(table.getSelectionBackground());
                        if (isSelected) {
                            ro = row;
                            col = column;
                        }
                        return buttonBox;
                    }

                    @Override
                    public Object getCellEditorValue() {
                        return null;
                    }

                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
/*                        if (isSelected) {
                            ro = row;
                            col = column;
                        }*/
                        buttonBox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                        buttonBox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                        return buttonBox;
                    }
                }
                ActionPanelEditorRenderer er = new ActionPanelEditorRenderer(hvaJapanAVS);
                TableColumn column = table.getColumnModel().getColumn(2);
                column.setCellRenderer(er);
                column.setCellEditor(er);
            }

            jDialog.add(table, BorderLayout.CENTER);
            jDialog.add(add, BorderLayout.SOUTH);
            jDialog.setLocationRelativeTo(father);
            jDialog.pack();
            jDialog.setVisible(true);

        }


    }

    public JTable showTableOfSources(List<HVAJapanAVS> hvaJapanAVS,JTable table,JDialog jDialog) {
        if (hvaJapanAVS == null || hvaJapanAVS.size() == 0) {
            JOptionPane.showMessageDialog(father, "无资源！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
        } else {
            int size = hvaJapanAVS.size();
            Object[][] tableData = new Object[size][LabelConstant.SOURCE_TABLE_COLUMN_TITLE.length];
            for (int i = 0; i < size; i++) {
                HVAJapanAVS hvaJapanAVS1 = hvaJapanAVS.get(i);
                tableData[i] = new Object[]{hvaJapanAVS1.getSource_uri(), hvaJapanAVS1.getQuality(), null};
            }
            //table = new JTable(tableData, LabelConstant.SOURCE_TABLE_COLUMN_TITLE);

            DefaultTableModel model = new DefaultTableModel(tableData, LabelConstant.SOURCE_TABLE_COLUMN_TITLE) {
            };
            table = new JTable(model);
            class ActionPanelEditorRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
                JButton delete = new JButton("查看");
                JButton details = new JButton("详情");
                Box buttonBox = Box.createHorizontalBox();
                List<HVAJapanAVS> hvaJapanAVS = null;

                int ro;
                int col;

                public ActionPanelEditorRenderer(List<HVAJapanAVS> hvaJapanAVS) {
                    buttonBox.add(details);
                    buttonBox.add(delete);
                    this.hvaJapanAVS = hvaJapanAVS;
                    details.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            HVAJapanAVS hvaJapanAVS1 = hvaJapanAVS.get(ro - 1);
                            showSourcesDetailDialog(hvaJapanAVS1);
                        }
                    });

                    delete.addActionListener(new ActionListener() {
                        @SneakyThrows
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            HVAJapanAVS hvaJapanAVS1 = hvaJapanAVS.get(ro - 1);
                            HVAJapanAVSDao.deleteSources(serviceConn,hvaJapanAVS1);
                            avCase.setSources(HVAJapanAVSDao.selectSourcesByMUUID(serviceConn, avCase.getUuid()));
                            JOptionPane.showMessageDialog(father, "请重新打开资源表格！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
                        }
                    });

                }

                @Override
                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                    buttonBox.setBackground(table.getSelectionBackground());
                    if (isSelected) {
                        ro = row;
                        col = column;
                    }
                    return buttonBox;
                }

                @Override
                public Object getCellEditorValue() {
                    return null;
                }

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
/*                        if (isSelected) {
                            ro = row;
                            col = column;
                        }*/
                    buttonBox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    buttonBox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    return buttonBox;
                }
            }
            ActionPanelEditorRenderer er = new ActionPanelEditorRenderer(hvaJapanAVS);
            TableColumn column = table.getColumnModel().getColumn(2);
            column.setCellRenderer(er);
            column.setCellEditor(er);
        }
        return table;
    }

    public void showSourcesDetailDialog(HVAJapanAVS hvaJapanAVS) {
        JDialog jDialog = new JDialog(father, "资源详情", true);
        JLabel uuid = new JLabel("UUID:" + hvaJapanAVS.getUuid());
        JLabel SOURCE_URI = new JLabel("URI:" + hvaJapanAVS.getSource_uri());
        JLabel SOURCE_NAME = new JLabel("资源名称:" + hvaJapanAVS.getSource_name());
        JLabel translate = new JLabel("翻译:" + hvaJapanAVS.getTranslate());
        JLabel SUBTITLE = new JLabel("字幕:" + hvaJapanAVS.getSubtitle());
        JLabel QUALITY = new JLabel("资源质量:" + hvaJapanAVS.getQuality());
        JLabel WATERMARK = new JLabel("水印广告:" + hvaJapanAVS.getWatermark());
        JLabel SIZES = new JLabel("资源大小:" + hvaJapanAVS.getSizes() + "MB");
        JLabel FORMAT = new JLabel("资源格式:" + hvaJapanAVS.getFormat());
        JLabel resolution = new JLabel("视频分辨率:" + hvaJapanAVS.getResolution());
        JLabel BIT_RATE = new JLabel("视频码率（kbps）:" + hvaJapanAVS.getBit_rate());
        JLabel UPLOAD_FLAG = new JLabel("是否已上传？:" + ("1".equals(hvaJapanAVS.getUpload_flag()) ? "是" : "否"));
        JLabel UPLOAD_TIME = new JLabel("上传时间:" + hvaJapanAVS.getUpload_time());
        JLabel COMPRESS_FORMAT = new JLabel("压缩格式:" + hvaJapanAVS.getCompress_format());
        JLabel PASSWORD = new JLabel("解压密码:" + hvaJapanAVS.getPassword());

        Box smBox = Box.createVerticalBox();
        smBox.add(uuid);
        smBox.add(SOURCE_URI);
        smBox.add(SOURCE_NAME);
        smBox.add(translate);
        smBox.add(SUBTITLE);
        smBox.add(QUALITY);
        smBox.add(WATERMARK);
        smBox.add(SIZES);
        smBox.add(FORMAT);
        smBox.add(resolution);
        smBox.add(BIT_RATE);
        smBox.add(UPLOAD_FLAG);
        smBox.add(UPLOAD_TIME);
        smBox.add(COMPRESS_FORMAT);
        smBox.add(PASSWORD);

        jDialog.add(smBox, BorderLayout.CENTER);
        jDialog.setLocationRelativeTo(father);
        jDialog.pack();
        jDialog.setVisible(true);
    }

    public void showSourcesAddDialog() {
        JDialog jDialog = new JDialog(father, "资源添加", true);

        Box smBox = Box.createVerticalBox();

        Box sourceBox = Box.createHorizontalBox();
        JLabel sourceLabel = new JLabel("资源路径:");
        JTextField source = new JTextField(LabelConstant.DEFAULT_FILE_PATH);
        sourceBox.add(sourceLabel);
        sourceBox.add(source);

        Box sourceNameBox = Box.createHorizontalBox();
        JLabel sourceNameLabel = new JLabel("资源名称:");
        JTextField sourceName = new JTextField(20);
        sourceNameBox.add(sourceNameLabel);
        sourceNameBox.add(sourceName);

        Box translateBox = Box.createHorizontalBox();
        JLabel translateLabel = new JLabel("翻译:");
        JComboBox<String> translate = new JComboBox<String>(LabelConstant.SOURCE_TRANSLATE);
        translateBox.add(translateLabel);
        translateBox.add(translate);

        Box subtitleBox = Box.createHorizontalBox();
        JLabel subtitleLabel = new JLabel("字幕:");
        JComboBox<String> subtitle = new JComboBox<String>(LabelConstant.SOURCE_SUBTITLE);
        subtitleBox.add(subtitleLabel);
        subtitleBox.add(subtitle);

        Box qualityBox = Box.createHorizontalBox();
        JLabel qualityLabel = new JLabel("视频质量（1-10级）:");
        JComboBox<String> quality = new JComboBox<String>(LabelConstant.SOURCE_QUALITY);
        qualityBox.add(qualityLabel);
        qualityBox.add(quality);

        Box watermarkBox = Box.createHorizontalBox();
        JLabel watermarkLabel = new JLabel("水印广告:");
        JCheckBox watermark = new JCheckBox("有");
        watermarkBox.add(watermarkLabel);
        watermarkBox.add(watermark);

        Box sizeBox = Box.createHorizontalBox();
        JLabel sizeLabel = new JLabel("视频大小:");
        JTextField size = new JTextField(20);
        sizeBox.add(sizeLabel);
        sizeBox.add(size);

        Box formatBox = Box.createHorizontalBox();
        JLabel formatLabel = new JLabel("视频格式:");
        JComboBox<String> format = new JComboBox<String>(LabelConstant.SOURCE_VIDEO_FORMAT);
        formatBox.add(formatLabel);
        formatBox.add(format);

        Box resolutionBox = Box.createHorizontalBox();
        JLabel resolutionLabel = new JLabel("视频分辨率:");
        JTextField resolution = new JTextField(20);
        resolutionBox.add(resolutionLabel);
        resolutionBox.add(resolution);

        Box bitBox = Box.createHorizontalBox();
        JLabel bitLabel = new JLabel("视频码率:");
        JTextField bit = new JTextField(20);
        bitBox.add(bitLabel);
        bitBox.add(bit);

        Box uploadBox = Box.createHorizontalBox();
        JLabel uploadLabel = new JLabel("是否上传:");
        JCheckBox upload = new JCheckBox("是");
        uploadBox.add(uploadLabel);
        uploadBox.add(upload);

        Box uriBox = Box.createHorizontalBox();
        JLabel uriLabel = new JLabel("URI:");
        JTextField uri = new JTextField(20);
        uriBox.add(uriLabel);
        uriBox.add(uri);

        Box compressFormatBox = Box.createHorizontalBox();
        JLabel compressFormatLabel = new JLabel("压缩格式:");
        JComboBox<String> compressFormat = new JComboBox<String>(LabelConstant.SOURCE_COMPRESS_FORMAT);
        compressFormatBox.add(compressFormatLabel);
        compressFormatBox.add(compressFormat);

        Box passwordBox = Box.createHorizontalBox();
        JLabel passwordLabel = new JLabel("解压密码:");
        JTextField password = new JTextField(20);
        passwordBox.add(passwordLabel);
        passwordBox.add(password);





        smBox.add(sourceBox);
        smBox.add(sourceNameBox);
        smBox.add(translateBox);
        smBox.add(subtitleBox);
        smBox.add(qualityBox);
        smBox.add(watermarkBox);
        smBox.add(sizeBox);
        smBox.add(formatBox);
        smBox.add(resolutionBox);
        smBox.add(resolution);
        smBox.add(bitBox);
        smBox.add(uploadBox);
        smBox.add(uriBox);
        smBox.add(compressFormatBox);
        smBox.add(passwordBox);

        Box oprBox = Box.createVerticalBox();
        JButton confirmButton = new JButton("自动填充");

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String filePath = source.getText()+"\\"+sourceName.getText();
                    IContainer container = IContainer.make();
                    int result = container.open(filePath, IContainer.Type.READ, null);
                    if (result < 0) {
                        JOptionPane.showMessageDialog(father, "文件打开/读取失败！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    avCase.setDuration(container.getDuration()/1000);
                    size.setText(String.valueOf(container.getFileSize()/(1024*1024)));
                    bit.setText(String.valueOf(container.getBitRate()));

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(father, "文件打开/读取失败！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
                }


            }
        });

        JButton insertButton = new JButton("添加");

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String thisTime = sdf.format(System.currentTimeMillis());

                avsCase.setUuid(UUID.randomUUID().toString().replace("-",""));
                avsCase.setM_uuid(avCase.getUuid());
                avsCase.setTranslate(LabelConstant.SOURCE_TRANSLATE[translate.getSelectedIndex()]);
                avsCase.setSubtitle(LabelConstant.SOURCE_SUBTITLE[subtitle.getSelectedIndex()]);
                avsCase.setQuality(LabelConstant.SOURCE_QUALITY[quality.getSelectedIndex()]);
                avsCase.setWatermark(watermark.isSelected()?"1":"0");
                avsCase.setSizes(Long.parseLong(size.getText()));
                avsCase.setFormat(LabelConstant.SOURCE_VIDEO_FORMAT[format.getSelectedIndex()]);
                avsCase.setUpload_flag(upload.isSelected()?"1":"0");
                if ("1".equals(avsCase.getUpload_flag())) avsCase.setUpload_time(thisTime);
                avsCase.setSource_uri(uri.getText());
                avsCase.setPassword(password.getText());
                avsCase.setCreate_time(thisTime);
                avsCase.setUpdate_time(thisTime);
                avsCase.setResolution(resolution.getText());
                avsCase.setBit_rate(Long.parseLong(bit.getText()));
                avsCase.setCompress_format(LabelConstant.SOURCE_COMPRESS_FORMAT[compressFormat.getSelectedIndex()]);
                avsCase.setSource_name(sourceName.getText());
                try {
                    HVAJapanAVSDao.insertSources(serviceConn,avsCase);
                    JOptionPane.showMessageDialog(father, "上传成功！！", "SourcesDialog", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    JOptionPane.showMessageDialog(father, "上传失败！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        oprBox.add(confirmButton);
        oprBox.add(insertButton);

        jDialog.add(smBox, BorderLayout.CENTER);
        jDialog.add(oprBox, BorderLayout.SOUTH);
        jDialog.setLocationRelativeTo(father);
        jDialog.pack();
        jDialog.setVisible(true);
    }
}
