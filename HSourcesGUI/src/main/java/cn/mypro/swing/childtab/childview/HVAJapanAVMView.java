package cn.mypro.swing.childtab.childview;

import cn.mypro.swing.childtab.JChildTabView;
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
import cn.mypro.swing.util.file.MyFileUtils;
import cn.mypro.swing.util.webmagic.WebMagicOfSourcesUtil;
import cn.mypro.utils.DataBaseUtils;
import cn.mypro.utils.DbName;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class HVAJapanAVMView implements JChildTabView {


    /*查询页面组件*/
    //查询组件
    private JTextField selectField = new JTextField(40);
    private JButton select = new JButton("查询");
    private JList<HVAJapanAVM> selectResult = new JList<>();
    //展示图片组件
    private JLabel cover = new JLabel();
    private JLabel cut1 = new JLabel();
    private JLabel cut2 = new JLabel();
    private JLabel cut3 = new JLabel();
    //展示信息组件
    private JLabel filePathLabel = new JLabel("文件源路径");
    private JTextField filePath = new JTextField(LabelConstant.DEFAULT_FILE_PATH);
    private JButton workAuto = new JButton("自动注入");
    private String realPath = LabelConstant.DEFAULT_FILE_PATH;
    private JLabel ifCodeLabel = new JLabel("番号");
    private JTextField ifCode = new JTextField(20);
    private JButton findMessage = new JButton("网络导入");
    private JLabel oNameLabel = new JLabel("原名");
    private JTextField oName = new JTextField(20);
    private JLabel cNameLabel = new JLabel("译名");
    private JTextField cName = new JTextField(20);
    private JButton translate = new JButton("翻译");
    private JLabel languageLable = new JLabel("语言");
    private JComboBox<String> language = new JComboBox<String>(LabelConstant.AV_LANGUAGE);
    private JLabel productionLabel = new JLabel("制作商");
    private JTextField production = new JTextField(20);
    private JLabel publishLabel = new JLabel("发行商");
    private JTextField publish = new JTextField(20);
    private JLabel publishTimeLabel = new JLabel("发行时间");
    private JTextField publishTime = new JTextField(20);
    private JLabel seriesLabel = new JLabel("系列");
    private JTextField series = new JTextField(20);
    private JLabel mosaicLable = new JLabel("马赛克(HM-有码,HN-去码,NM-无码)");
    private JComboBox<String> mosaic = new JComboBox<String>(LabelConstant.MOSICA);
    private JLabel durationLable = new JLabel("时长");
    private JComboBox<Integer> durationHour = new JComboBox<Integer>(LabelConstant.TIME_HOUR);
    private JComboBox<Integer> durationMinute = new JComboBox<Integer>(LabelConstant.TIME_MINUTE);
    private JComboBox<Integer> durationSecond = new JComboBox<Integer>(LabelConstant.TIME_SECOND);
    private JLabel describeLable = new JLabel("描述");
    private JTextArea describe = new JTextArea(5, 20);
    private JLabel AVScoreLabel = new JLabel("分数&推荐:");
    private JComboBox<Integer> AVScore = new JComboBox<>(LabelConstant.Person_Score);
    private JRadioButton recommend = new JRadioButton("推荐", false);

    private JLabel avLabelLabel = new JLabel("标签");
    private JList<HVAJapanAVLabelM> avLabelList = new JList<HVAJapanAVLabelM>();
    private JButton avLabelButton = new JButton("变更");
    private JLabel avPersonLabel = new JLabel("参演人员");
    private JList<HVAJapanAVPersonM> avPersonList = new JList<HVAJapanAVPersonM>();
    private JButton avPersonButton = new JButton("变更");

    //展示操作组件
    private JButton confirmMessage = new JButton("确认信息");
    private JButton insertMessage = new JButton("插入信息");
    private JButton updateMessage = new JButton("修改信息");
    private JButton showSources = new JButton("查看资源");
    private JButton uploadCoverAndCut = new JButton("上传封面&截图");
    private JButton clearMessage = new JButton("清空");

    //信息进度框
    private JTextArea messageRun = null;


    private Connection serviceConn = null;
    private JFrame father = null;

    private HVAJapanAVM avCase = new HVAJapanAVM();
    private HVAJapanAVS avsCase = new HVAJapanAVS();
    private List<HVAJapanAVLabelM> allLabels;
    private List<HVAJapanAVLabelM> noSelectedLabels;
    private List<HVAJapanAVPersonM> allPersons;
    private List<HVAJapanAVPersonM> noSelectedPersons;

    public HVAJapanAVMView(Connection serviceConn, JFrame father,JTextArea messageRun) {
        this.serviceConn = serviceConn;
        this.father = father;
        this.messageRun = messageRun;
    }

    public HVAJapanAVMView(JFrame father) {
        this.serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);
        this.father = father;
    }

    //构建
    @Override
    public JPanel initTab() {
        //列表信息填充
        flushAVMList();
        //事件绑定
        bindingOfTheEvent();

        return assemblyOfTheView();
    }

    //绑定键位
    @Override
    public void bindingOfTheEvent() {
        //绑定Button
        select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<HVAJapanAVM> avms = null;
                try {
                    avms = HVAJapanAVMDao.selectMessageByAll(serviceConn, selectField.getText());

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
                //HVAJapanAVM selectedValue = selectResult.getSelectedValue();
                HVAJapanAVM selectedValue = null;
                if (selectResult.getSelectedValue() == null) return;
                try {
                    selectedValue = HVAJapanAVMDao.selectMessageByUUID(serviceConn, selectResult.getSelectedValue().getUuid());

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
                        mosaic.setSelectedIndex(selectedValue.getMosaic().equals("HM") ?
                                0 : selectedValue.getMosaic().equals("HN") ?
                                1 : 2);
                        List<HVAJapanAVLabelM> labels = selectedValue.getLabels();
                        if (labels != null && labels.size() != 0) {
                            avLabelList.setListData(labels.toArray(new HVAJapanAVLabelM[labels.size()]));
                        } else {
                            avLabelList.setListData(new HVAJapanAVLabelM[0]);
                        }
                        List<HVAJapanAVPersonM> persons = selectedValue.getPersons();
                        if (persons != null && persons.size() != 0) {
                            avPersonList.setListData(persons.toArray(new HVAJapanAVPersonM[persons.size()]));
                        } else {
                            avPersonList.setListData(new HVAJapanAVPersonM[0]);
                        }
                        durationHour.setSelectedIndex((int) (selectedValue.getDuration() / 3600));
                        durationMinute.setSelectedIndex((int) (selectedValue.getDuration() % 3600) / 60);
                        durationSecond.setSelectedIndex((int) (selectedValue.getDuration() % 60));
                        describe.setText(selectedValue.getDescribe());

                        AVScore.setSelectedIndex((int) selectedValue.getScore() - 1);
                        recommend.setSelected("1".equals(selectedValue.getRecommend()));

                        if (selectedValue.getCover() != null) {
                            ImageIcon iconCover = new ImageIcon(selectedValue.getCover());
                            iconCover = new ImageIcon(iconCover.getImage().getScaledInstance(cover.getWidth(), cover.getHeight(), Image.SCALE_DEFAULT));
                            cover.setIcon(iconCover);
                        }
                        if (selectedValue.getCut1() != null) {
                            ImageIcon iconCut1 = new ImageIcon(selectedValue.getCut1());
                            iconCut1 = new ImageIcon(iconCut1.getImage().getScaledInstance(cut1.getWidth(), cut1.getHeight(), Image.SCALE_DEFAULT));
                            cut1.setIcon(iconCut1);
                        }

                        if (selectedValue.getCut2() != null) {
                            ImageIcon iconCut2 = new ImageIcon(selectedValue.getCut2());
                            iconCut2 = new ImageIcon(iconCut2.getImage().getScaledInstance(cut2.getWidth(), cut2.getHeight(), Image.SCALE_DEFAULT));
                            cut2.setIcon(iconCut2);
                        }

                        if (selectedValue.getCut3() != null) {
                            ImageIcon iconCut3 = new ImageIcon(selectedValue.getCut3());
                            iconCut3 = new ImageIcon(iconCut3.getImage().getScaledInstance(cut3.getWidth(), cut3.getHeight(), Image.SCALE_DEFAULT));
                            cut3.setIcon(iconCut3);
                        }

                        avCase = selectedValue;
                        avCase.setHave(true);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        confirmMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                avCase.setIf_Code(ifCode.getText().replace(" ","").toUpperCase());
                avCase.setOName(oName.getText());
                avCase.setCName(cName.getText());
                avCase.setLanguages(LabelConstant.AV_LANGUAGE[language.getSelectedIndex()]);
                avCase.setProduction_company(production.getText());
                avCase.setPublish_company(publish.getText());
                avCase.setPublish_time(publishTime.getText().replace("-", "").replace(":", "").replace(" ", ""));
                avCase.setSeries(series.getText());
                avCase.setMosaic(LabelConstant.MOSICA[mosaic.getSelectedIndex()]);

                avCase.setDuration(durationHour.getSelectedIndex() * 3600L + durationMinute.getSelectedIndex() * 60L + durationSecond.getSelectedIndex());
                avCase.setDescribe(describe.getText());
                avCase.setScore(LabelConstant.Person_Score[AVScore.getSelectedIndex()]);
                avCase.setRecommend(recommend.isSelected() ? "1" : "0");

                boolean exist = false;
                try {
                    exist = HVAJapanAVMDao.qryExist(serviceConn, avCase.getIf_Code());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                if (avCase.isHave()) {
                    updateMessage.setEnabled(true);

                } else {
                    insertMessage.setEnabled(true);

                    if (exist) {
                        JButton force = new JButton("强制");
                        JButton ignore = new JButton("更正");

                        force.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                //insertMessage.setEnabled(true);
                                Window win = SwingUtilities.getWindowAncestor(force);  //找到该组件所在窗口
                                win.dispose();
                            }
                        });
                        ignore.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                updateMessage.setEnabled(true);
                                insertMessage.setEnabled(false);
                                avCase.setHave(true);
                                Window win = SwingUtilities.getWindowAncestor(ignore);  //找到该组件所在窗口
                                win.dispose();
                            }
                        });
                        JButton[] buttons = {force,ignore};
                        JOptionPane.showOptionDialog(father, "该番号已存在！！是否为新数据？", "警告！", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE,new ImageIcon("HSourcesGUI/src/main/resources/pic/w2.png"),buttons,buttons[0]);
                    }

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
                uploadPhotos();

            }
        });
        clearMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearSourcesMessage();
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
                //jDialog.setLocationRelativeTo(father);
                jDialog.setLocation(500,500);
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
                //jDialog.setLocationRelativeTo(father);
                jDialog.setLocation(500,500);
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
                //jDialog.setLocationRelativeTo(father);
                jDialog.setLocation(500,500);
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
                //jDialog.setLocationRelativeTo(father);
                jDialog.setLocation(500,500);
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
        findMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WebMagicOfSourcesUtil webMagicUtil = new WebMagicOfSourcesUtil();
                String code = ifCode.getText().replace(" ","");

                JDialog jDialog = new JDialog(father, "资源添加", false);

                int MIN_PROGRESS = 0;
                int MAX_PROGRESS = 100;
                int currentProgress = MIN_PROGRESS;
                JProgressBar progressBar = new JProgressBar();
                progressBar.setMinimum(MIN_PROGRESS);
                progressBar.setMaximum(MAX_PROGRESS);
                progressBar.setValue(currentProgress);
                progressBar.setStringPainted(true);
                progressBar.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {

                    }
                });

                new Thread(() -> {
                    if(code != null) {

                        HVAJapanAVM hSources = webMagicUtil.getHSources(code, filePath.getText() + "\\" + code,progressBar,messageRun);

                        clearSourcesMessage();
                        fitAVMessage(hSources);
                        uploadPhotosQuite();
                        progressBar.setValue(90);
                        String translateString = BaiduTranslateUtil.translateAsString(oName.getText());
                        cName.setText(translateString);
                        progressBar.setValue(MAX_PROGRESS);
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        messageRun.append(code + " 导入完成！\n");
                    }
                    jDialog.setVisible(false);
                }).start();

                jDialog.add(progressBar);
                jDialog.setLocationRelativeTo(father);
                jDialog.pack();
                jDialog.setVisible(true);

            }


        });

        workAuto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                File rootFile = new File(filePath.getText());
                File[] files = rootFile.listFiles();
                List<File> files1 = new ArrayList<>(Arrays.asList(files));
                files1.removeIf(new Predicate<File>() {
                    @Override
                    public boolean test(File file) {
                        return file.isFile();
                    }
                });
                files1.sort(new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                if (files1 == null || files1.size() == 0) return;
                if (ifCode.getText() == null) {
                    ifCode.setText(files1.get(0).getName());
                    return;
                }
                String lastCode = ifCode.getText();
                clearSourcesMessage();
                ifCode.setText(lastCode);
                boolean setOk = false;
                for (File file : files1) {
                    if (setOk) {
                        ifCode.setText(file.getName());
                        break;
                    }
                    if (lastCode.equals(file.getName())) setOk = true;
                }
                if (!setOk) ifCode.setText(files1.get(0).getName());
                if (lastCode.equals(ifCode.getText())) ifCode.setText(files1.get(1).getName());

            }
        });
    }
    //视图组装
    @Override
    public JPanel assemblyOfTheView() {
        JPanel tabPanel = new JPanel();

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
        opraBoxChile1.add(clearMessage);
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
        filePathBox.add(workAuto);
        messageBox.add(filePathBox);
        //番号
        Box codeBox = Box.createHorizontalBox();
        codeBox.add(ifCodeLabel);
        codeBox.add(ifCode);
        codeBox.add(findMessage);
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
        avLabelList.setVisibleRowCount(10);
        personBox.add(new JScrollPane(avLabelList));
        personBox.add(avLabelButton);
        plBox.add(personBox);
        Box labelBox = Box.createVerticalBox();
        labelBox.add(avPersonLabel);
        avPersonList.setVisibleRowCount(10);
        labelBox.add(new JScrollPane(avPersonList));
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
        describe.setLineWrap(true);
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
        messageBox.add(new JLabel("95+:特别好 90-94：很好 80-89：挺好 70-79：一般好 69-：差"));
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
        //selectResult.setPreferredSize(new Dimension(150, 0));
        JScrollPane selectResultScrollPane = new JScrollPane(selectResult);
        selectResultScrollPane.setPreferredSize(new Dimension(150, 500));
        //messageRun.setPreferredSize(new Dimension());
        JSplitPane topRightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(cover), opraBox); //上右 竖向

        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(messageBox), topRightSplit); //上 横向

        JSplitPane allSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, new JScrollPane(cutPanel)); //总 竖向

        JSplitPane allNewSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectResultScrollPane, allSplit); //总 竖向

        //JSplitPane allNew2Split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, allNewSplit, new JScrollPane(messageRun)); //添加 总+信息框 竖向

        showPanel.add(allNewSplit);
        //展示截图

        tabBox.add(jPanel);
        tabBox.add(showPanel);

        tabPanel.add(tabBox, BorderLayout.CENTER);

        return tabPanel;
    }

    @Override
    public void flushMessageList() {

    }
    //刷新填充信息框
    private void flushAVMList() {
        List<HVAJapanAVM> allMessage = null;
        try {
            //allMessage = HVAJapanAVMDao.selectAllMessage(serviceConn);
            allMessage = HVAJapanAVMDao.selectAllNameMessage(serviceConn);
            if (allMessage != null) {
                selectResult.setListData(allMessage.toArray(new HVAJapanAVM[allMessage.size()]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //清除资源信息框
    private void clearSourcesMessage() {

        ifCode.setText("");
        oName.setText("");
        cName.setText("");
        language.setSelectedIndex(0);
        production.setText("");
        publish.setText("");
        publishTime.setText("");
        series.setText("");
        mosaic.setSelectedIndex(0);

        avLabelList.setListData(new HVAJapanAVLabelM[0]);
        avPersonList.setListData(new HVAJapanAVPersonM[0]);

        durationHour.setSelectedIndex(0);
        durationMinute.setSelectedIndex(0);
        durationSecond.setSelectedIndex(0);
        describe.setText("");

        AVScore.setSelectedIndex(0);
        recommend.setSelected(false);

        cover.setIcon(null);
        cut1.setIcon(null);
        cut2.setIcon(null);
        cut3.setIcon(null);

        selectResult.clearSelection();

        avCase = new HVAJapanAVM();
        insertMessage.setEnabled(false);
        updateMessage.setEnabled(false);

        avCase.setHave(false);
    }

    public void showSelectLabelDialog() {
        JDialog jDialog = new JDialog(father, "标签选择器", true);

        JList<HVAJapanAVLabelM> noSelected = new JList<>();
        JList<HVAJapanAVLabelM> yesSelected = new JList<>();
        //noSelected.setPreferredSize(new Dimension(150, 300));
        noSelected.setVisibleRowCount(30);
        //noSelected.setFixedCellWidth(150);
        //yesSelected.setPreferredSize(new Dimension(150, 300));
        yesSelected.setVisibleRowCount(30);
        //yesSelected.setFixedCellWidth(150);

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
                        if (!o1.getType_1().equals(o2.getType_1())) return o1.getType_1().compareTo(o2.getType_1());
                        if (!o1.getType_2().equals(o2.getType_2())) return o1.getType_2().compareTo(o2.getType_2());
                        return o1.getLabel_show().compareTo(o2.getLabel_show());
                    }
                });
                noSelectedLabels.sort(new Comparator<HVAJapanAVLabelM>() {
                    @Override
                    public int compare(HVAJapanAVLabelM o1, HVAJapanAVLabelM o2) {
                        //return o1.getLabel_code().compareTo(o2.getLabel_code());
                        if (!o1.getType_1().equals(o2.getType_1())) return o1.getType_1().compareTo(o2.getType_1());
                        if (!o1.getType_2().equals(o2.getType_2())) return o1.getType_2().compareTo(o2.getType_2());
                        return o1.getLabel_show().compareTo(o2.getLabel_show());
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
                        //return o1.getLabel_code().compareTo(o2.getLabel_code());
                        if (!o1.getType_1().equals(o2.getType_1())) return o1.getType_1().compareTo(o2.getType_1());
                        if (!o1.getType_2().equals(o2.getType_2())) return o1.getType_2().compareTo(o2.getType_2());
                        return o1.getLabel_show().compareTo(o2.getLabel_show());
                    }
                });
                noSelectedLabels.sort(new Comparator<HVAJapanAVLabelM>() {
                    @Override
                    public int compare(HVAJapanAVLabelM o1, HVAJapanAVLabelM o2) {
                        //return o1.getLabel_code().compareTo(o2.getLabel_code());
                        if (!o1.getType_1().equals(o2.getType_1())) return o1.getType_1().compareTo(o2.getType_1());
                        if (!o1.getType_2().equals(o2.getType_2())) return o1.getType_2().compareTo(o2.getType_2());
                        return o1.getLabel_show().compareTo(o2.getLabel_show());
                    }
                });
                yesSelected.setListData(avCase.getLabels().toArray(new HVAJapanAVLabelM[avCase.getLabels().size()]));
                noSelected.setListData(noSelectedLabels.toArray(new HVAJapanAVLabelM[noSelectedLabels.size()]));
                avLabelList.setListData(avCase.getLabels().toArray(new HVAJapanAVLabelM[avCase.getLabels().size()]));
                remove.setEnabled(false);
            }
        });

        JScrollPane noScrollPane = new JScrollPane(noSelected);
        noScrollPane.setPreferredSize(new Dimension(250,500));
        JScrollPane yesScrollPane = new JScrollPane(yesSelected);
        yesScrollPane.setPreferredSize(new Dimension(250,500));
        jDialog.add(noScrollPane, BorderLayout.WEST);
        jDialog.add(yesScrollPane, BorderLayout.EAST);
        Box buttonBox = Box.createVerticalBox();
        buttonBox.add(add);
        buttonBox.add(remove);
        jDialog.add(buttonBox);
        //jDialog.setLocationRelativeTo(father);
        jDialog.setLocation(500,500);
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

    public void showSelectPersonDialog() {
        JDialog jDialog = new JDialog(father, "任务选择器", true);

        JList<HVAJapanAVPersonM> noSelected = new JList<>();
        JList<HVAJapanAVPersonM> yesSelected = new JList<>();
        //noSelected.setPreferredSize(new Dimension(150, 300));
        noSelected.setFixedCellWidth(300);
        noSelected.setVisibleRowCount(30);
        //yesSelected.setPreferredSize(new Dimension(150, 300));
        yesSelected.setFixedCellWidth(300);
        yesSelected.setVisibleRowCount(30);

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
                        return o1.getNames().compareTo(o2.getNames());
                    }
                });
                noSelectedPersons.sort(new Comparator<HVAJapanAVPersonM>() {
                    @Override
                    public int compare(HVAJapanAVPersonM o1, HVAJapanAVPersonM o2) {
                        int i = o1.getGender().compareTo(o2.getGender());
                        if (i != 0) return i;
                        return o1.getNames().compareTo(o2.getNames());
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
                        return o1.getNames().compareTo(o2.getNames());
                    }
                });
                noSelectedPersons.sort(new Comparator<HVAJapanAVPersonM>() {
                    @Override
                    public int compare(HVAJapanAVPersonM o1, HVAJapanAVPersonM o2) {
                        if (o1 == null || o2 == null) return 0;
                        int i = o1.getGender().compareTo(o2.getGender());
                        if (i != 0) return i;
                        return o1.getNames().compareTo(o2.getNames());
                    }
                });
                yesSelected.setListData(avCase.getPersons().toArray(new HVAJapanAVPersonM[avCase.getPersons().size()]));
                noSelected.setListData(noSelectedPersons.toArray(new HVAJapanAVPersonM[noSelectedPersons.size()]));
                avPersonList.setListData(avCase.getPersons().toArray(new HVAJapanAVPersonM[avCase.getPersons().size()]));
                remove.setEnabled(false);
            }
        });

        jDialog.add(new JScrollPane(noSelected), BorderLayout.WEST);
        jDialog.add(new JScrollPane(yesSelected), BorderLayout.EAST);
        Box buttonBox = Box.createVerticalBox();
        buttonBox.add(add);
        buttonBox.add(remove);
        jDialog.add(buttonBox);
        //jDialog.setLocationRelativeTo(father);
        jDialog.setLocation(500,500);
        jDialog.pack();
        jDialog.setVisible(true);
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

    public void showSourcesDialog() {
        if (avCase == null || avCase.getUuid() == null) {
            JOptionPane.showMessageDialog(father, "无对应资源！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                avCase = HVAJapanAVMDao.selectMessageByUUID(serviceConn, avCase.getUuid());
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<HVAJapanAVS> hvaJapanAVS = avCase.getSources();
            ;

            JDialog jDialog = new JDialog(father, "资源列表", true);

            JButton add = new JButton("添加资源");
            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showSourcesAddDialog();
                }
            });


            JTable table = null;

            if (hvaJapanAVS == null || hvaJapanAVS.size() == 0) {
                JOptionPane.showMessageDialog(father, "无资源！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
            } else {
                int size = hvaJapanAVS.size();
                Object[][] tableData = new Object[size+1][LabelConstant.SOURCE_TABLE_COLUMN_TITLE.length];
                tableData[0] = new Object[]{"URL","质量","操作"};
                for (int i = 1; i <= size; i++) {
                    HVAJapanAVS hvaJapanAVS1 = hvaJapanAVS.get(i-1);
                    tableData[i] = new Object[]{hvaJapanAVS1.getSource_uri(), hvaJapanAVS1.getQuality(), null};
                }
                //table = new JTable(tableData, LabelConstant.SOURCE_TABLE_COLUMN_TITLE);

                DefaultTableModel model = new DefaultTableModel(tableData, LabelConstant.SOURCE_TABLE_COLUMN_TITLE) {
                };
                table = new JTable(model);
                class ActionPanelEditorRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
                    JButton delete = new JButton("删除");
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
                                if (ro == 0) return;
                                HVAJapanAVS hvaJapanAVS1 = hvaJapanAVS.get(ro-1);
                                showSourcesDetailDialog(hvaJapanAVS1);
                            }
                        });

                        delete.addActionListener(new ActionListener() {
                            @SneakyThrows
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (ro == 0) return;
                                HVAJapanAVS hvaJapanAVS1 = hvaJapanAVS.get(ro-1);
                                HVAJapanAVSDao.deleteSources(serviceConn, hvaJapanAVS1);
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
                FitTableColumns(table);
                table.setPreferredSize(new Dimension(450,100));
                jDialog.add(table, BorderLayout.CENTER);
            }

            jDialog.add(add, BorderLayout.SOUTH);
            //jDialog.setLocationRelativeTo(father);
            jDialog.pack();
            jDialog.setLocation(500,500);
            //jDialog.setPreferredSize(new Dimension(800,400));
            jDialog.setVisible(true);

        }


    }

    public JTable showTableOfSources(List<HVAJapanAVS> hvaJapanAVS, JTable table, JDialog jDialog) {
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
                            HVAJapanAVSDao.deleteSources(serviceConn, hvaJapanAVS1);
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
        JLabel uploadLabel = new JLabel("是否上传:");
        JCheckBox upload = new JCheckBox("是");
        watermarkBox.add(new JPanel());
        watermarkBox.add(uploadLabel);
        watermarkBox.add(upload);

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

/*        Box uploadBox = Box.createHorizontalBox();
        JLabel uploadLabel = new JLabel("是否上传:");
        JCheckBox upload = new JCheckBox("是");
        uploadBox.add(uploadLabel);
        uploadBox.add(upload);*/

        Box uriBox = Box.createHorizontalBox();
        JLabel uriLabel = new JLabel("URI:");
        JTextField uri = new JTextField(20);
        uri.setText(LabelConstant.SOURC_PATH_1);
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
        password.setText(LabelConstant.password);
        passwordBox.add(passwordLabel);
        passwordBox.add(password);

        translate.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (translate.getSelectedIndex() == 1) {
                    uri.setText(LabelConstant.SOURC_PATH_1 + ifCode.getText() + "-CH" + ".7z");
                } else {
                    uri.setText(LabelConstant.SOURC_PATH_1 + ifCode.getText() + ".7z");
                }
            }
        });

        smBox.add(sourceBox);
        smBox.add(sourceNameBox);
        smBox.add(translateBox);
        smBox.add(subtitleBox);
        smBox.add(qualityBox);
        smBox.add(new JLabel("视频等级：1[最低级画质] 2-3[360P杂/纯] 4-5[720P 杂/纯] 6-7[1080 杂/纯] 8-9[蓝光 杂/纯] 10[顶级画质]"));
        smBox.add(watermarkBox);
        smBox.add(sizeBox);
        smBox.add(formatBox);
        smBox.add(resolutionBox);
        smBox.add(resolution);
        smBox.add(bitBox);
        //smBox.add(uploadBox);
        smBox.add(uriBox);
        smBox.add(compressFormatBox);
        smBox.add(passwordBox);

        Box oprBox = Box.createVerticalBox();
        JButton confirmButton = new JButton("自动填充");

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String text = sourceName.getText();
                    if (text == null || "".equals(text)) text = avCase.getIf_Code() + ".mp4";
                    String filePath = source.getText() + "\\" + avCase.getIf_Code() + "\\" + text;
                    sourceName.setText(text);

                    IContainer container = IContainer.make();
                    int result = container.open(filePath, IContainer.Type.READ, null);
                    if (result < 0) {
                        JOptionPane.showMessageDialog(father, "文件打开/读取失败！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    //int numStreams = container.getNumStreams();
                    IStream stream = container.getStream(0);
                    IStreamCoder coder = stream.getStreamCoder();
                    if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                        resolution.setText(coder.getHeight() + "*" + coder.getWidth());
                    }

                    avCase.setDuration(container.getDuration() / 1000);
                    size.setText(String.valueOf(container.getFileSize() / (1024 * 1024)));
                    bit.setText(String.valueOf(container.getBitRate()));
                    //System.gc();
                    container.close();
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

                avsCase.setUuid(UUID.randomUUID().toString().replace("-", ""));
                avsCase.setM_uuid(avCase.getUuid());
                avsCase.setTranslate(LabelConstant.SOURCE_TRANSLATE[translate.getSelectedIndex()]);
                avsCase.setSubtitle(LabelConstant.SOURCE_SUBTITLE[subtitle.getSelectedIndex()]);
                avsCase.setQuality(LabelConstant.SOURCE_QUALITY[quality.getSelectedIndex()]);
                avsCase.setWatermark(watermark.isSelected() ? "1" : "0");
                avsCase.setSizes(Long.parseLong(size.getText()));
                avsCase.setFormat(LabelConstant.SOURCE_VIDEO_FORMAT[format.getSelectedIndex()]);
                avsCase.setUpload_flag(upload.isSelected() ? "1" : "0");
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
                    HVAJapanAVSDao.insertSources(serviceConn, avsCase);
                    serviceConn.commit();
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

    public void FitTableColumns(JTable myTable) {
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();

        Enumeration columns = myTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int) myTable.getTableHeader().getDefaultRenderer()
                    .getTableCellRendererComponent(myTable, column.getIdentifier(), false, false,
                            -1, col).getPreferredSize().getWidth();
            for (int row = 0; row < rowCount; row++) {
                int preferedWidth = (int) myTable.getCellRenderer(row, col)
                        .getTableCellRendererComponent(myTable, myTable.getValueAt(row, col),
                                false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column); // 此行很重要
            column.setWidth(width + myTable.getIntercellSpacing().width);
        }
    }

    private void uploadPhotos() {
        try {
            String fileRootPath = filePath.getText();
            String childPath = ifCode.getText().replace(" ","");
            File coverFile = new File(fileRootPath + "\\" + childPath + "\\" + "cover.jpg");
            File cutFile1 = new File(fileRootPath + "\\" + childPath + "\\" + "cut1.jpg");
            File cutFile2 = new File(fileRootPath + "\\" + childPath + "\\" + "cut2.jpg");
            File cutFile3 = new File(fileRootPath + "\\" + childPath + "\\" + "cut3.jpg");

            if (coverFile.exists() && coverFile.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(coverFile);

                ImageIcon iconCover = new ImageIcon(bytesFromFile);
                //等比缩放
                iconCover = new ImageIcon(iconCover.getImage().getScaledInstance(cover.getWidth(), cover.getHeight(), Image.SCALE_DEFAULT));
                //强制缩放
                //iconCover=new ImageIcon(iconCover.getImage().getScaledInstance(200, 300-25, Image.SCALE_DEFAULT));
                cover.setIcon(iconCover);
                avCase.setCover(bytesFromFile);
            }
            if (cutFile1.exists() && cutFile1.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile1);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut1.getWidth(), cut1.getHeight(), Image.SCALE_DEFAULT));
                cut1.setIcon(icon);
                avCase.setCut1(bytesFromFile);
            }
            if (cutFile2.exists() && cutFile2.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile2);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut2.getWidth(), cut2.getHeight(), Image.SCALE_DEFAULT));
                cut2.setIcon(icon);
                avCase.setCut2(bytesFromFile);
            }
            if (cutFile3.exists() && cutFile3.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile3);
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
    private void uploadPhotosQuite() {
        try {
            String fileRootPath = filePath.getText();
            String childPath = ifCode.getText().replace(" ","");
            File coverFile = new File(fileRootPath + "\\" + childPath + "\\" + "cover.jpg");
            File cutFile1 = new File(fileRootPath + "\\" + childPath + "\\" + "cut1.jpg");
            File cutFile2 = new File(fileRootPath + "\\" + childPath + "\\" + "cut2.jpg");
            File cutFile3 = new File(fileRootPath + "\\" + childPath + "\\" + "cut3.jpg");

            if (coverFile.exists() && coverFile.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(coverFile);

                ImageIcon iconCover = new ImageIcon(bytesFromFile);
                //等比缩放
                iconCover = new ImageIcon(iconCover.getImage().getScaledInstance(cover.getWidth(), cover.getHeight(), Image.SCALE_DEFAULT));
                //强制缩放
                //iconCover=new ImageIcon(iconCover.getImage().getScaledInstance(200, 300-25, Image.SCALE_DEFAULT));
                cover.setIcon(iconCover);
                avCase.setCover(bytesFromFile);
            }
            if (cutFile1.exists() && cutFile1.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile1);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut1.getWidth(), cut1.getHeight(), Image.SCALE_DEFAULT));
                cut1.setIcon(icon);
                avCase.setCut1(bytesFromFile);
            }
            if (cutFile2.exists() && cutFile2.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile2);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut2.getWidth(), cut2.getHeight(), Image.SCALE_DEFAULT));
                cut2.setIcon(icon);
                avCase.setCut2(bytesFromFile);
            }
            if (cutFile3.exists() && cutFile3.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile3);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut3.getWidth(), cut3.getHeight(), Image.SCALE_DEFAULT));
                cut3.setIcon(icon);
                avCase.setCut3(bytesFromFile);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }


    }

    private void fitAVMessage(HVAJapanAVM selectedValue) {
        try {

            if (selectedValue != null) {
                ifCode.setText(selectedValue.getIf_Code());
                oName.setText(selectedValue.getOName());
                cName.setText(selectedValue.getCName());
                if (selectedValue.getLanguages() != null) {
                    language.setSelectedIndex(selectedValue.getLanguages().equals("JAP") ?
                            0 : selectedValue.getLanguages().equals("CHN") ?
                            1 : selectedValue.getLanguages().equals("EUR") ?
                            2 : 3);
                }

                production.setText(selectedValue.getProduction_company());
                publish.setText(selectedValue.getPublish_company());
                publishTime.setText(selectedValue.getPublish_time());
                series.setText(selectedValue.getSeries());

                if (selectedValue.getMosaic() != null) {
                    mosaic.setSelectedIndex(selectedValue.getMosaic().equals("HM") ?
                            0 : selectedValue.getMosaic().equals("HN") ?
                            1 : 2);
                }

                List<HVAJapanAVPersonM> persons = selectedValue.getPersons();
                if (persons != null && persons.size() != 0) {
                    avPersonList.setListData(persons.toArray(new HVAJapanAVPersonM[persons.size()]));
                } else {
                    avPersonList.setListData(new HVAJapanAVPersonM[0]);
                }

                if (selectedValue.getDuration() != 0) {
                    durationHour.setSelectedIndex((int) (selectedValue.getDuration() / 3600));
                    durationMinute.setSelectedIndex((int) (selectedValue.getDuration() % 3600) / 60);
                    durationSecond.setSelectedIndex((int) (selectedValue.getDuration() % 60));
                }

                describe.setText(selectedValue.getDescribe());

                if (selectedValue.getScore() != 0) {
                    AVScore.setSelectedIndex((int) selectedValue.getScore() - 1);
                }
                recommend.setSelected("1".equals(selectedValue.getRecommend()));

                if (selectedValue.getCover() != null) {
                    ImageIcon iconCover = new ImageIcon(selectedValue.getCover());
                    iconCover = new ImageIcon(iconCover.getImage().getScaledInstance(cover.getWidth(), cover.getHeight(), Image.SCALE_DEFAULT));
                    cover.setIcon(iconCover);
                }
                if (selectedValue.getCut1() != null) {
                    ImageIcon iconCut1 = new ImageIcon(selectedValue.getCut1());
                    iconCut1 = new ImageIcon(iconCut1.getImage().getScaledInstance(cut1.getWidth(), cut1.getHeight(), Image.SCALE_DEFAULT));
                    cut1.setIcon(iconCut1);
                }

                if (selectedValue.getCut2() != null) {
                    ImageIcon iconCut2 = new ImageIcon(selectedValue.getCut2());
                    iconCut2 = new ImageIcon(iconCut2.getImage().getScaledInstance(cut2.getWidth(), cut2.getHeight(), Image.SCALE_DEFAULT));
                    cut2.setIcon(iconCut2);
                }

                if (selectedValue.getCut3() != null) {
                    ImageIcon iconCut3 = new ImageIcon(selectedValue.getCut3());
                    iconCut3 = new ImageIcon(iconCut3.getImage().getScaledInstance(cut3.getWidth(), cut3.getHeight(), Image.SCALE_DEFAULT));
                    cut3.setIcon(iconCut3);
                }

                avCase = selectedValue;
                avCase.setHave(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
