package cn.mypro.swing.childtab.childview;

import cn.mypro.swing.childtab.JChildTabView;
import cn.mypro.swing.childtab.childview.menu.PersonPopupMenuFactory;
import cn.mypro.swing.childtab.childview.menu.TextPopupMenuFactory;
import cn.mypro.swing.constant.LabelConstant;
import cn.mypro.swing.dao.HVAJapanAVLabelDao;
import cn.mypro.swing.dao.HVAJapanAVMDao;
import cn.mypro.swing.dao.HVAJapanAVPersonDao;
import cn.mypro.swing.dao.HVAJapanAVSDao;
import cn.mypro.swing.entity.HVAJapanAVLabelM;
import cn.mypro.swing.entity.HVAJapanAVM;
import cn.mypro.swing.entity.HVAJapanAVPersonM;
import cn.mypro.swing.entity.HVAJapanAVS;
import cn.mypro.swing.util.compress.CompressionUtil;
import cn.mypro.swing.util.file.WindowClipboardUtil;
import cn.mypro.swing.util.video.VideoAnalyticalUtil;
import cn.mypro.swing.util.translateAPI.BaiduTranslateUtil;
import cn.mypro.swing.util.file.MyFileUtils;
import cn.mypro.swing.util.webmagic.WebMagicOfPersonsUtil;
import cn.mypro.swing.util.webmagic.WebMagicOfSourcesUtil;
import cn.mypro.utils.DataBaseUtils;
import cn.mypro.utils.DbName;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
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

    private Logger logger = LoggerFactory.getLogger(HVAJapanAVMView.class);

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
    private JLabel cut4 = new JLabel();
    private JLabel cut5 = new JLabel();
    private JLabel cut6 = new JLabel();
    private JLabel cut7 = new JLabel();
    private JLabel cut8 = new JLabel();
    private JLabel cut9 = new JLabel();


    //展示信息组件
    private JLabel filePathLabel = new JLabel("文件源路径");
    private JTextField filePath = new JTextField(LabelConstant.DEFAULT_FILE_PATH);
    private JButton workAuto = new JButton("自动注入");
    private String realPath = LabelConstant.DEFAULT_FILE_PATH;
    private JLabel ifCodeLabel = new JLabel("番号");
    private JTextField ifCode = new JTextField(20);
    private JButton addMessageByNet = new JButton("网络导入");
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
    private JRadioButton isRobot = new JRadioButton("自动注入", false);
    private JRadioButton isRobot2 = new JRadioButton("自动注入-初步处理", false);
    private JLabel robotStatusLable = new JLabel("导入状态:");
    private JComboBox<String> robotStatus = new JComboBox<String>(LabelConstant.ROBOT_STATUS);
    private ButtonGroup statusGroup = new ButtonGroup();
    private JButton scoreRule = new JButton("评分细则");

    private JLabel avLabelLabel = new JLabel("元素标签：");
    private JList<HVAJapanAVLabelM> avLabelList = new JList<HVAJapanAVLabelM>();
    private JButton avLabelButton = new JButton("变更");
    private JLabel avPersonLabel = new JLabel("参演人员：");
    private JList<HVAJapanAVPersonM> avPersonList = new JList<HVAJapanAVPersonM>();
    private JButton avPersonButton = new JButton("变更");

    //展示单数据操作组件
    private JButton confirmMessage = new JButton("确认信息");
    private JButton insertMessage = new JButton("插入信息");
    private JButton updateMessage = new JButton("修改信息");
    private JButton showSources = new JButton("查看资源");
    private JButton uploadCoverAndCut = new JButton("上传封面&截图");
    private JButton clearMessage = new JButton("    清空    ");
    //展示列表数据批量操作组件

    private JButton increaseCompressPollNumber = new JButton("增加压缩池并行数          ");
    private JButton reduceCompressPollNumber = new JButton("减少压缩池并行数          ");

    private JButton fulshRobotList = new JButton("获取数据-Web Import");
    private JButton fulshRobot2List = new JButton("获取数据-Deal First");
    private JButton updateRobotToTwo = new JButton("修正数据(Web-Deal)");
    private JButton updateRobotToZero = new JButton("修正数据(Deal-Artificial)");

    //信息进度框
    private JTextArea messageRun = null;

    private JButton autoImport = new JButton("全自动导入");
    private JLabel listCount = new JLabel();
    private JButton fulshList = new JButton("刷新列表");

    //自动注入框
    private JDialog showBar = null;
    private JProgressBar showProgressBar = new JProgressBar();
    private JTextArea sources = new JTextArea();
    private boolean isClose = false;


    private CompressionUtil compressionUtil = null;

    private Connection serviceConn = null;
    private JFrame father = null;

    private HVAJapanAVM avCase = new HVAJapanAVM();
    private HVAJapanAVS avsCase = new HVAJapanAVS();
    private List<HVAJapanAVLabelM> allLabels;
    private List<HVAJapanAVLabelM> noSelectedLabels;
    private List<HVAJapanAVPersonM> allPersons;
    private List<HVAJapanAVPersonM> noSelectedPersons;

    private int openShowDialogCut = -1;
    private JDialog showCutDialog = new JDialog(father, "放大展示", true);
    private JLabel showCutLabel = new JLabel();

    private String sourcesListMoudel = LabelConstant.SOURCES_FLUSH_MODLE_ALL;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public HVAJapanAVMView(Connection serviceConn, JFrame father, JTextArea messageRun) {
        this.serviceConn = serviceConn;
        this.father = father;
        this.messageRun = messageRun;

        showBar = new JDialog(father, "资源自动注入进度", false);
        compressionUtil = new CompressionUtil(messageRun, true);
    }

    public HVAJapanAVMView(JFrame father) {
        this.serviceConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);
        this.father = father;

        showBar = new JDialog(father, "资源自动注入进度", false);
        compressionUtil = new CompressionUtil();
    }

    //构建
    @Override
    public JPanel initTab() {
        logger.info("开始初始化[资源操作界面]");
        logger.info("[资源操作界面]-[列表信息填充]");
        //列表信息填充
        //flushAVMList();
        flushResourcesList(LabelConstant.SOURCES_FLUSH_MODLE_ALL, null);
        logger.info("[资源操作界面]-[绑定事件]");
        //事件绑定
        bindingOfTheEvent();
        logger.info("[资源操作界面]-[组装视图]");

        return assemblyOfTheView();
    }

    //初始化缓存
    private void initCacheData() {
        try {
            allPersons = HVAJapanAVPersonDao.qryAllSimplePersonMessage(serviceConn);

        } catch (Exception e) {

        }
    }

    //绑定键位
    @Override
    public void bindingOfTheEvent() {

        filePath.setEnabled(false);
        ifCode.setComponentPopupMenu(TextPopupMenuFactory.createPopupMenu(LabelConstant.TEXT_POPUPMENU_TYPE_FIELD, ifCode));
        describe.setComponentPopupMenu(TextPopupMenuFactory.createPopupMenu(LabelConstant.TEXT_POPUPMENU_TYPE_AREA, describe));
        oName.setComponentPopupMenu(TextPopupMenuFactory.createPopupMenu(LabelConstant.TEXT_POPUPMENU_TYPE_FIELD, oName));
        cName.setComponentPopupMenu(TextPopupMenuFactory.createPopupMenu(LabelConstant.TEXT_POPUPMENU_TYPE_FIELD, cName));
        production.setComponentPopupMenu(TextPopupMenuFactory.createPopupMenu(LabelConstant.TEXT_POPUPMENU_TYPE_FIELD, production));
        publish.setComponentPopupMenu(TextPopupMenuFactory.createPopupMenu(LabelConstant.TEXT_POPUPMENU_TYPE_FIELD, publish));
        publishTime.setComponentPopupMenu(TextPopupMenuFactory.createPopupMenu(LabelConstant.TEXT_POPUPMENU_TYPE_FIELD, publishTime));
        series.setComponentPopupMenu(TextPopupMenuFactory.createPopupMenu(LabelConstant.TEXT_POPUPMENU_TYPE_FIELD, series));

        avPersonList.setComponentPopupMenu(PersonPopupMenuFactory.createPopupMenu(avPersonList));

        //绑定Button
        select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<HVAJapanAVM> avms = null;
                try {
                    avms = HVAJapanAVMDao.selectMessageByAll(serviceConn, selectField.getText());
                    listCount.setText("共计" + avms.size() + "个");
                    sourcesListMoudel = LabelConstant.SOURCES_FLUSH_MODLE_SELECT;
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
                        if (selectedValue.getLanguages() != null)
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

/*                        isRobot.setSelected("1".equals(selectedValue.getRobot()));
                        isRobot2.setSelected("2".equals(selectedValue.getRobot()));*/
                        robotStatus.setSelectedIndex(Integer.valueOf(selectedValue.getRobot()));

                        if (selectedValue.getCover() != null) {
                            ImageIcon iconCover = new ImageIcon(selectedValue.getCover());
                            iconCover = new ImageIcon(iconCover.getImage().getScaledInstance(cover.getWidth(), cover.getHeight(), Image.SCALE_DEFAULT));
                            cover.setIcon(iconCover);
                        } else {
                            cover.setIcon(null);
                        }

                        if (selectedValue.getCut1() != null) {
                            ImageIcon iconCut1 = new ImageIcon(selectedValue.getCut1());
                            iconCut1 = new ImageIcon(iconCut1.getImage().getScaledInstance(cut1.getWidth(), cut1.getHeight(), Image.SCALE_DEFAULT));
                            cut1.setIcon(iconCut1);
                        } else {
                            cut1.setIcon(null);
                        }

                        if (selectedValue.getCut2() != null) {
                            ImageIcon iconCut2 = new ImageIcon(selectedValue.getCut2());
                            iconCut2 = new ImageIcon(iconCut2.getImage().getScaledInstance(cut2.getWidth(), cut2.getHeight(), Image.SCALE_DEFAULT));
                            cut2.setIcon(iconCut2);
                        } else {
                            cut2.setIcon(null);
                        }

                        if (selectedValue.getCut3() != null) {
                            ImageIcon iconCut3 = new ImageIcon(selectedValue.getCut3());
                            iconCut3 = new ImageIcon(iconCut3.getImage().getScaledInstance(cut3.getWidth(), cut3.getHeight(), Image.SCALE_DEFAULT));
                            cut3.setIcon(iconCut3);
                        } else {
                            cut3.setIcon(null);
                        }

                        if (selectedValue.getCut4() != null) {
                            ImageIcon iconCut4 = new ImageIcon(selectedValue.getCut4());
                            iconCut4 = new ImageIcon(iconCut4.getImage().getScaledInstance(cut4.getWidth(), cut4.getHeight(), Image.SCALE_DEFAULT));
                            cut4.setIcon(iconCut4);
                        } else {
                            cut4.setIcon(null);
                        }

                        if (selectedValue.getCut5() != null) {
                            ImageIcon iconCut5 = new ImageIcon(selectedValue.getCut5());
                            iconCut5 = new ImageIcon(iconCut5.getImage().getScaledInstance(cut5.getWidth(), cut5.getHeight(), Image.SCALE_DEFAULT));
                            cut5.setIcon(iconCut5);
                        } else {
                            cut5.setIcon(null);
                        }

                        if (selectedValue.getCut6() != null) {
                            ImageIcon iconCut6 = new ImageIcon(selectedValue.getCut6());
                            iconCut6 = new ImageIcon(iconCut6.getImage().getScaledInstance(cut6.getWidth(), cut6.getHeight(), Image.SCALE_DEFAULT));
                            cut6.setIcon(iconCut6);
                        } else {
                            cut6.setIcon(null);
                        }

                        if (selectedValue.getCut7() != null) {
                            ImageIcon iconCut7 = new ImageIcon(selectedValue.getCut7());
                            iconCut7 = new ImageIcon(iconCut7.getImage().getScaledInstance(cut7.getWidth(), cut7.getHeight(), Image.SCALE_DEFAULT));
                            cut7.setIcon(iconCut7);
                        } else {
                            cut7.setIcon(null);
                        }

                        if (selectedValue.getCut8() != null) {
                            ImageIcon iconCut8 = new ImageIcon(selectedValue.getCut8());
                            iconCut8 = new ImageIcon(iconCut8.getImage().getScaledInstance(cut8.getWidth(), cut8.getHeight(), Image.SCALE_DEFAULT));
                            cut8.setIcon(iconCut8);
                        } else {
                            cut8.setIcon(null);
                        }

                        if (selectedValue.getCut9() != null) {
                            ImageIcon iconCut9 = new ImageIcon(selectedValue.getCut9());
                            iconCut9 = new ImageIcon(iconCut9.getImage().getScaledInstance(cut9.getWidth(), cut9.getHeight(), Image.SCALE_DEFAULT));
                            cut9.setIcon(iconCut9);
                        } else {
                            cut9.setIcon(null);
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

                avCase.setIf_Code(ifCode.getText().replace(" ", "").toUpperCase());
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
                avCase.setRobot("0");
/*                if (isRobot.isSelected()) avCase.setRobot("1");
                if (isRobot2.isSelected()) avCase.setRobot("2");*/
                avCase.setRobot(String.valueOf(robotStatus.getSelectedIndex()));
//                avCase.setRobot(isRobot.isSelected() ? "1" :"0");
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
                        JButton[] buttons = {force, ignore};
                        JOptionPane.showOptionDialog(father, "该番号已存在！！是否为新数据？", "警告！", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon("HSourcesGUI/src/main/resources/pic/w2.png"), buttons, buttons[0]);
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
                    //flushAVMList();
                    flushResourcesList(sourcesListMoudel, null);
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
                    //flushAVMList();
                    flushResourcesList(sourcesListMoudel, null);
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
                if (selectField.getText() == null || "".equals(selectField.getText()))
                    flushResourcesList(LabelConstant.SOURCES_FLUSH_MODLE_ALL, null);
                ;
                sourcesListMoudel = LabelConstant.SOURCES_FLUSH_MODLE_ALL;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        showCutDialog.setLocation(400, 100);
        showCutDialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                byte[] nextCut = getNextCut(openShowDialogCut);
                //showCutDialog.setVisible(false);
                showCutLabel.setIcon(new ImageIcon(nextCut));
                showCutDialog.add(showCutLabel);
                showCutDialog.setLocation(400, 100);
                showCutDialog.pack();
                showCutDialog.setVisible(true);
            }
        });
        showCutDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        cover.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog jDialog = new JDialog(father, "放大展示", true);
                //jDialog.setBounds(100,100,10,10);
                jDialog.add(new JLabel(new ImageIcon(avCase.getCover())));
                //jDialog.setLocationRelativeTo(father);
                jDialog.setLocation(400, 100);
                jDialog.pack();
                jDialog.setVisible(true);
            }
        });
        cut1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (avCase.getCut1() != null) {
                    openShowDialogCut = 1;
                    showCutLabel.setIcon(new ImageIcon(avCase.getCut1()));
                    showCutDialog.add(showCutLabel);
                    showCutDialog.pack();
                    showCutDialog.setVisible(true);
                }
            }
        });
        cut2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (avCase.getCut2() != null) {
                    openShowDialogCut = 2;
                    showCutLabel.setIcon(new ImageIcon(avCase.getCut2()));
                    showCutDialog.add(showCutLabel);
                    showCutDialog.pack();
                    showCutDialog.setVisible(true);
                }
            }
        });
        cut3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (avCase.getCut3() != null) {
                    openShowDialogCut = 3;
                    showCutLabel.setIcon(new ImageIcon(avCase.getCut3()));
                    showCutDialog.add(showCutLabel);
                    showCutDialog.pack();
                    showCutDialog.setVisible(true);
                }
            }
        });
        cut4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (avCase.getCut4() != null) {
                    openShowDialogCut = 4;
                    showCutLabel.setIcon(new ImageIcon(avCase.getCut4()));
                    showCutDialog.add(showCutLabel);
                    showCutDialog.pack();
                    showCutDialog.setVisible(true);
                }
            }
        });
        cut5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (avCase.getCut5() != null) {
                    openShowDialogCut = 5;
                    showCutLabel.setIcon(new ImageIcon(avCase.getCut5()));
                    showCutDialog.add(showCutLabel);
                    showCutDialog.pack();
                    showCutDialog.setVisible(true);
                }
            }
        });
        cut6.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (avCase.getCut6() != null) {
                    openShowDialogCut = 6;
                    showCutLabel.setIcon(new ImageIcon(avCase.getCut6()));
                    showCutDialog.add(showCutLabel);
                    showCutDialog.pack();
                    showCutDialog.setVisible(true);
                }
            }
        });
        cut7.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (avCase.getCut7() != null) {
                    openShowDialogCut = 7;
                    showCutLabel.setIcon(new ImageIcon(avCase.getCut7()));
                    showCutDialog.add(showCutLabel);
                    showCutDialog.pack();
                    showCutDialog.setVisible(true);
                }
            }
        });
        cut8.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (avCase.getCut8() != null) {
                    openShowDialogCut = 8;
                    showCutLabel.setIcon(new ImageIcon(avCase.getCut8()));
                    showCutDialog.add(showCutLabel);
                    showCutDialog.pack();
                    showCutDialog.setVisible(true);
                }
            }
        });
        cut9.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (avCase.getCut9() != null) {
                    openShowDialogCut = 9;
                    showCutLabel.setIcon(new ImageIcon(avCase.getCut9()));
                    showCutDialog.add(showCutLabel);
                    showCutDialog.pack();
                    showCutDialog.setVisible(true);
                }
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
        addMessageByNet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WebMagicOfSourcesUtil webMagicUtil = new WebMagicOfSourcesUtil();
                WebMagicOfPersonsUtil webMagicPersonUtil = new WebMagicOfPersonsUtil();

                String code = ifCode.getText().replace(" ", "");

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
                    if (code != null) {

                        try {
                            if (HVAJapanAVMDao.qryExist(serviceConn, code)) {
                                runMessagePrint(messageRun, "已存在，跳过！\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                                return;
                            } else {
                                progressBar.setValue(MIN_PROGRESS);
                                long start = System.currentTimeMillis();
                                HVAJapanAVM hSources = webMagicUtil.getHSources(code, filePath.getText() + "\\" + code, progressBar, messageRun);
                                progressBar.setValue(70);
                                if (hSources != null) {
                                    if (hSources.getNotExistsPerson() != null && hSources.getNotExistsPerson().size() != 0) {
                                        for (String notExistsPerson : hSources.getNotExistsPerson()) {
                                            try {
                                                HVAJapanAVPersonM hPersonSimple = webMagicPersonUtil.getHPersonSimple(notExistsPerson);
                                                if (hPersonSimple != null) {
                                                    hSources.getPersons().add(hPersonSimple);
                                                    hPersonSimple.setRobot("1");
                                                    HVAJapanAVPersonDao.insertPerson(serviceConn, hPersonSimple);
                                                    runMessagePrint(messageRun, "添加人物[" + notExistsPerson + "]完成！\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                                                } else {
                                                    runMessagePrint(messageRun, "添加人物[" + notExistsPerson + "]失败！原因[不存在该人物]\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                                                }
                                            } catch (Exception exx) {
                                                exx.printStackTrace();
                                                runMessagePrint(messageRun, "添加人物[" + notExistsPerson + "]失败！原因[导入错误]\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                                            }
                                        }
                                    }

                                    progressBar.setValue(80);

                                    String translateString = null;
                                    try {
                                        translateString = BaiduTranslateUtil.translateAsString(hSources.getOName());
                                    } catch (Exception exxx) {
                                        exxx.printStackTrace();
                                    }
                                    hSources.setRobot("0");

                                    hSources.setCName(translateString);
                                    hSources.setLanguages("JAP");
                                    hSources.setMosaic("HM");
                                    if (hSources.getScore() == 0) hSources.setScore(60);
                                    hSources.setRecommend("0");

                                    progressBar.setValue(90);
                                    clearSourcesMessage();
                                    fitAVMessage(hSources);
                                    //uploadPhotosQuite();

                                    if (hSources.getCover() == null) {
                                        runMessagePrint(messageRun, "[" + code + "] 封面获取失败]！\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                                    }
                                    long end = System.currentTimeMillis();
                                    runMessagePrint(messageRun, "[" + code + "]数据填充完成！耗时[" + (end - start) / 1000 + "]\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                                } else {
                                    long end = System.currentTimeMillis();
                                    runMessagePrint(messageRun, "[" + code + "] 未获取有效数据！耗时[" + (end - start) / 1000 + "]\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                                }
                                showProgressBar.setValue(MAX_PROGRESS);
                            }
                        } catch (Exception exception) {

                        }
                        jDialog.setVisible(false);

                    }
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

        autoImport.addActionListener((e) -> {

            int MIN_PROGRESS = 0;
            int MAX_PROGRESS = 100;

            Box verticalBox = Box.createVerticalBox();

            showProgressBar.setMinimum(MIN_PROGRESS);
            showProgressBar.setMaximum(MAX_PROGRESS);
            showProgressBar.setValue(MIN_PROGRESS);
            showProgressBar.setPreferredSize(new Dimension(700, 20));
            showProgressBar.setStringPainted(true);
            //sources.setPreferredSize(new Dimension(300, 400));
            JScrollPane jScrollPane = new JScrollPane(sources);
            jScrollPane.setPreferredSize(new Dimension(700, 600));
            verticalBox.add(showProgressBar);
            verticalBox.add(jScrollPane);

            showBar.add(verticalBox);
            showBar.setLocationRelativeTo(father);
            showBar.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {

                }

                @Override
                public void windowClosed(WindowEvent e) {
                    isClose = true;
                    showBar.setVisible(false);
                }

                @Override
                public void windowIconified(WindowEvent e) {

                }

                @Override
                public void windowDeiconified(WindowEvent e) {

                }

                @Override
                public void windowActivated(WindowEvent e) {

                }

                @Override
                public void windowDeactivated(WindowEvent e) {

                }
            });

            new Thread(() -> {

                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (Exception se) {
                    se.printStackTrace();
                }

                Connection importConn = DataBaseUtils.ensureDataBaseConnection(DbName.LOCAL);

                runMessagePrint(messageRun, "自动注入开始！\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);

                WebMagicOfSourcesUtil webMagicUtil = new WebMagicOfSourcesUtil();
                WebMagicOfPersonsUtil webMagicPersonUtil = new WebMagicOfPersonsUtil();
                //获取需自动注入的所有 code
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

                runMessagePrint(messageRun, "本次需处理[" + files1.size() + "]个番号\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);

                if (isClose) {
                    runMessagePrint(messageRun, "关闭窗口自动注入停止！\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                    return;
                }

                int successCount = 0;
                int errorCount = 0;

                for (File file : files1) {
                    try {
                        runMessagePrint(messageRun, "开始处理[" + file.getName() + "]", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);

                        showProgressBar.setValue(MIN_PROGRESS);
                        if (HVAJapanAVMDao.qryExist(importConn, file.getName())) {
                            //runMessagePrint(sources," 已存在，跳过！\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                            runMessagePrint(messageRun, " 已存在，跳过！\n", LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT);
                            continue;
                        }
                        ;

                        long start = System.currentTimeMillis();
                        String code = file.getName().replace(" ", "");

                        if (isClose) {
                            runMessagePrint(messageRun, "\n关闭窗口自动注入停止！\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                            return;
                        }
                        runMessagePrint(sources, "******开始处理[" + file.getName() + "]******\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                        runMessagePrint(messageRun, " .", LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT);

                        HVAJapanAVM hSources = webMagicUtil.getHSourceBatch(code, filePath.getText() + "\\" + code, showProgressBar, sources);

                        runMessagePrint(messageRun, " .", LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT);
                        showProgressBar.setValue(80);

                        if (isClose) {
                            runMessagePrint(messageRun, "\n关闭窗口自动注入停止！\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                            return;
                        }

                        if (hSources != null) {

                            if (hSources.getNotExistsPerson() != null && hSources.getNotExistsPerson().size() != 0) {

                                for (String notExistsPerson : hSources.getNotExistsPerson()) {
                                    try {

                                        HVAJapanAVPersonM hPersonSimple = webMagicPersonUtil.getHPersonSimple(notExistsPerson);

                                        if (hPersonSimple != null) {
                                            hSources.getPersons().add(hPersonSimple);
                                            hPersonSimple.setRobot("1");
                                            HVAJapanAVPersonDao.insertPerson(importConn, hPersonSimple);
                                            runMessagePrint(sources, "添加人物[" + notExistsPerson + "]完成！\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);

                                        } else {
                                            runMessagePrint(sources, "添加人物[" + notExistsPerson + "]失败！原因[不存在该人物]\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);

                                        }
                                    } catch (Exception exx) {
                                        exx.printStackTrace();
                                        runMessagePrint(sources, "添加人物[" + notExistsPerson + "]失败！原因[导入错误]\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                                    }
                                }
                            }
                            runMessagePrint(messageRun, " .", LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT);
                            runMessagePrint(sources, "[" + file.getName() + "]完成人物加载\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);

                            showProgressBar.setValue(85);

                            hSources.setRobot("1");
                            String translateString = null;
                            try {
                                translateString = BaiduTranslateUtil.translateAsString(hSources.getOName());
                            } catch (Exception exxx) {
                                exxx.printStackTrace();
                            }
                            hSources.setCName(translateString);
                            hSources.setLanguages("JAP");
                            hSources.setMosaic("HM");
                            if (hSources.getScore() == 0) hSources.setScore(60);
                            hSources.setRecommend("0");

                            runMessagePrint(messageRun, " .", LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT);
                            runMessagePrint(sources, "[" + file.getName() + "]数据填充完成\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);


                            if (hSources.getCover() == null) {
                                runMessagePrint(messageRun, "数据处理发生错误！\n", LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT);
                                runMessagePrint(sources, "[" + file.getName() + "]因[封面获取失败],跳过本次注入！\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                                continue;
                            }

                            showProgressBar.setValue(90);
                            HVAJapanAVMDao.insertMessage(importConn, hSources);
                            long end = System.currentTimeMillis();
                            runMessagePrint(sources, "[" + file.getName() + "]完成注入！耗时[" + (end - start) / 1000 + "]\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                            showProgressBar.setValue(MAX_PROGRESS);

                            importConn.commit();
                            successCount++;
                            runMessagePrint(messageRun, "处理成功！\n", LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT);
                        } else {
                            long end = System.currentTimeMillis();
                            runMessagePrint(sources, "[" + file.getName() + "]因[未获取有效数据]注入失败！耗时[" + (end - start) / 1000 + "]\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                            runMessagePrint(messageRun, "未获取有效数据，跳过！\n", LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT);

                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();
                        errorCount++;
                        runMessagePrint(messageRun, "处理发生异常，跳过！\n", LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT);
                    }
                }
                //showBar.setVisible(false);
                runMessagePrint(sources, "本次注入完成！成功处理[" + successCount + "]个，失败[" + errorCount + "]个", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                runMessagePrint(messageRun, "本次注入完成！成功处理[" + successCount + "]个，失败[" + errorCount + "]个", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
            }).start();

            showBar.pack();
            showBar.setVisible(true);

        });

        fulshList.addActionListener((e) -> {
            flushResourcesList(LabelConstant.SOURCES_FLUSH_MODLE_ALL, null);
            sourcesListMoudel = LabelConstant.SOURCES_FLUSH_MODLE_ALL;
        });

        fulshRobotList.addActionListener((e) -> {
            flushResourcesList(LabelConstant.SOURCES_FLUSH_MODLE_AUTO_IMPORT, null);
            sourcesListMoudel = LabelConstant.SOURCES_FLUSH_MODLE_AUTO_IMPORT;

        });
        fulshRobot2List.addActionListener((e) -> {
            flushResourcesList(LabelConstant.SOURCES_FLUSH_MODLE_DEAL_FIRST, null);
            sourcesListMoudel = LabelConstant.SOURCES_FLUSH_MODLE_DEAL_FIRST;
        });
        updateRobotToTwo.addActionListener(e -> {

            JDialog jDialog = new JDialog(father, "资源详情", true);
            JLabel okLable = new JLabel("是否将自动注入的数据标记为初步处理？");
            JButton ok = new JButton("是");
            JButton no = new JButton("否");
            Box bBox = Box.createHorizontalBox();
            bBox.add(ok);
            bBox.add(new JPanel());
            bBox.add(no);

            ok.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        HVAJapanAVMDao.updateAllRoBotByRobotMessage(serviceConn, LabelConstant.ROBOT_TEXT_IMPORT_AUTO, LabelConstant.ROBOT_TEXT_DEAL_FIRST);
                        serviceConn.commit();
                        flushResourcesList(LabelConstant.SOURCES_FLUSH_MODLE_AUTO_IMPORT, null);
                        jDialog.setVisible(false);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
            no.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jDialog.setVisible(false);
                }
            });


            jDialog.add(okLable, BorderLayout.CENTER);
            jDialog.add(bBox, BorderLayout.SOUTH);
            jDialog.setLocationRelativeTo(father);
            jDialog.pack();
            jDialog.setVisible(true);

        });

        updateRobotToZero.addActionListener(e -> {

            JDialog jDialog = new JDialog(father, "资源详情", true);
            JLabel okLable = new JLabel("是否将初步处理的数据标记为正常状态？");
            JButton ok = new JButton("是");
            JButton no = new JButton("否");
            Box bBox = Box.createHorizontalBox();
            bBox.add(ok);
            bBox.add(new JPanel());
            bBox.add(no);

            ok.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        HVAJapanAVMDao.updateAllRoBotByRobotMessage(serviceConn, LabelConstant.ROBOT_TEXT_DEAL_FIRST, LabelConstant.ROBOT_TEXT_IMPORT_BY_PERSON);
                        serviceConn.commit();
                        flushResourcesList(LabelConstant.SOURCES_FLUSH_MODLE_DEAL_FIRST, null);
                        jDialog.setVisible(false);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
            no.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jDialog.setVisible(false);
                }
            });


            jDialog.add(okLable, BorderLayout.CENTER);
            jDialog.add(bBox, BorderLayout.SOUTH);
            jDialog.setLocationRelativeTo(father);
            jDialog.pack();
            jDialog.setVisible(true);

        });

        scoreRule.addActionListener(e -> {

            JDialog jDialog = new JDialog(father, "评分细则",  false);
            Box smBox = Box.createVerticalBox();
            smBox.add(new JLabel("95+  :极品，无法形容，一般为剧情画面以及其他都属于极品的作品"));
            smBox.add(new JLabel("90-94：完美，感觉特别很好看，一般为喜欢的各种作品"));
            smBox.add(new JLabel("80-89：很不错，但是没有那么喜欢，属于较为优秀的作品"));
            smBox.add(new JLabel("70-79：没有什么亮点，但也还可以，属于一般般的作品"));
            smBox.add(new JLabel("60-69：有明显的缺点，属于无所谓的作品"));
            smBox.add(new JLabel("60-  ：引起不适的作品"));
            jDialog.add(smBox, BorderLayout.CENTER);
            jDialog.setLocationRelativeTo(father);
            jDialog.pack();
            jDialog.setVisible(true);

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
        //opraBoxChile2.add(autoImport);

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
        codeBox.add(addMessageByNet);
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

        Box avLabelPanelBox = Box.createHorizontalBox();
        Box avPersonPanelBox = Box.createHorizontalBox();

        avLabelPanelBox.add(avLabelLabel);
        avLabelPanelBox.add(new JPanel());
        avLabelPanelBox.add(avLabelButton);
        avPersonPanelBox.add(avPersonLabel);
        avPersonPanelBox.add(new JPanel());
        avPersonPanelBox.add(avPersonButton);

        avLabelList.setVisibleRowCount(10);
        avPersonList.setVisibleRowCount(10);

        Box personBox = Box.createVerticalBox();
        personBox.add(avLabelPanelBox);
        personBox.add(new JScrollPane(avLabelList));
        plBox.add(personBox);

        Box labelBox = Box.createVerticalBox();
        labelBox.add(avPersonPanelBox);
        labelBox.add(new JScrollPane(avPersonList));
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
        describeBox.add(new JScrollPane(describe));
        messageBox.add(describeBox);
        //分数
/*        statusGroup.add(isRobot);
        statusGroup.add(isRobot2);*/
        Box scoreBox = Box.createHorizontalBox();
        scoreBox.add(AVScoreLabel);
        scoreBox.add(AVScore);
        scoreBox.add(scoreRule);
        scoreBox.add(new JPanel());
        scoreBox.add(recommend);
/*        scoreBox.add(isRobot);
        scoreBox.add(isRobot2);*/
        scoreBox.add(new JPanel());
        scoreBox.add(robotStatusLable);
        scoreBox.add(robotStatus);
        messageBox.add(scoreBox);

        JPanel cutPanel = new JPanel(); //截图集
        cut1.setPreferredSize(new Dimension(100, 90));
        cut2.setPreferredSize(new Dimension(100, 90));
        cut3.setPreferredSize(new Dimension(100, 90));
        cut4.setPreferredSize(new Dimension(100, 90));
        cut5.setPreferredSize(new Dimension(100, 90));
        cut6.setPreferredSize(new Dimension(100, 90));
        cut7.setPreferredSize(new Dimension(100, 90));
        cut8.setPreferredSize(new Dimension(100, 90));
        cut9.setPreferredSize(new Dimension(100, 90));
        cutPanel.add(cut1);
        cutPanel.add(cut2);
        cutPanel.add(cut3);
        cutPanel.add(cut4);
        cutPanel.add(cut5);
        cutPanel.add(cut6);
        cutPanel.add(cut7);
        cutPanel.add(cut8);
        cutPanel.add(cut9);
        cover.setPreferredSize(new Dimension(200, 300));
        opraBox.setPreferredSize(new Dimension(200, 200));
        messageBox.setPreferredSize(new Dimension(800, 500));
        cutPanel.setPreferredSize(new Dimension(450, 100));
        //selectResult.setPreferredSize(new Dimension(100, 0));
        Box listBox = Box.createVerticalBox();

        Box listButtonBox = Box.createHorizontalBox();
        listButtonBox.add(autoImport);
        listButtonBox.add(fulshList);


        /*Box compressBox = Box.createHorizontalBox();
        increaseCompressPollNumber.setPreferredSize(new Dimension(70,40));
        reduceCompressPollNumber.setPreferredSize(new Dimension(70,40));
        compressBox.add(increaseCompressPollNumber);
        compressBox.add(reduceCompressPollNumber);*/

        Box listButtonBox2 = Box.createVerticalBox();

/*        increaseCompressPollNumber.setPreferredSize(new Dimension(150,20));
        reduceCompressPollNumber.setPreferredSize(new Dimension(150,20));*/

        fulshRobotList.setPreferredSize(new Dimension(150, 20));
        fulshRobot2List.setPreferredSize(new Dimension(150, 20));
        updateRobotToTwo.setPreferredSize(new Dimension(150, 20));
        updateRobotToZero.setPreferredSize(new Dimension(150, 20));

        //compressBox.setPreferredSize(new Dimension(150,40));
/*        listButtonBox2.add(increaseCompressPollNumber);
        listButtonBox2.add(reduceCompressPollNumber);
        listButtonBox2.add(new JPanel());*/
        listButtonBox2.add(fulshRobotList);
        listButtonBox2.add(fulshRobot2List);
        listButtonBox2.add(updateRobotToTwo);
        listButtonBox2.add(updateRobotToZero);

        JScrollPane selectResultScrollPane = new JScrollPane(selectResult);
        listBox.add(listButtonBox);
        listBox.add(listCount);
        listBox.add(selectResultScrollPane);

        selectResultScrollPane.setPreferredSize(new Dimension(150, 450));
        //listButtonBox2.setPreferredSize(new Dimension(150, 50));
        //messageRun.setPreferredSize(new Dimension());
        JSplitPane topRightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(cover), opraBox); //上右 竖向

        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(messageBox), topRightSplit); //上 横向

        JSplitPane allSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, new JScrollPane(cutPanel)); //总 竖向

        JSplitPane allNewSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listBox, allSplit); //总 竖向
        //showPanel.add(allNewSplit);
        JScrollPane listButtonBoxPanl = new JScrollPane(listButtonBox2);
        JSplitPane allNew2Split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listButtonBoxPanl, allNewSplit); //添加 总+信息框 竖向
        allNew2Split.setContinuousLayout(true);
        allNew2Split.setOneTouchExpandable(true);

        showPanel.add(allNew2Split);

        //展示截图

        tabBox.add(jPanel);
        tabBox.add(showPanel);

        tabPanel.add(tabBox, BorderLayout.CENTER);

        return tabPanel;
    }

    //刷新填充信息框
    private void flushResourcesList(String moudle, List<HVAJapanAVM> allMessage) {
        try {
            if (LabelConstant.SOURCES_FLUSH_MODLE_ALL.equals(moudle)) {
                allMessage = HVAJapanAVMDao.selectAllNameMessage(serviceConn);
                flushResourcesList(allMessage);
            } else if (LabelConstant.SOURCES_FLUSH_MODLE_AUTO_IMPORT.equals(moudle)) {
                allMessage = HVAJapanAVMDao.selectAllNameRobotMessage(serviceConn, LabelConstant.ROBOT_TEXT_IMPORT_AUTO);
                flushResourcesList(allMessage);
            } else if (LabelConstant.SOURCES_FLUSH_MODLE_DEAL_FIRST.equals(moudle)) {
                allMessage = HVAJapanAVMDao.selectAllNameRobotMessage(serviceConn, LabelConstant.ROBOT_TEXT_DEAL_FIRST);
                flushResourcesList(allMessage);
            } else if (LabelConstant.SOURCES_FLUSH_MODLE_SELECT.equals(moudle)) {
                allMessage = HVAJapanAVMDao.selectMessageByAll(serviceConn, selectField.getText());
                flushResourcesList(allMessage);
            } else if (LabelConstant.SOURCES_FLUSH_MODLE_BY_CUSTOM.equals(moudle)) {
                flushResourcesList(allMessage);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void flushResourcesList(List<HVAJapanAVM> allMessage) {
        if (allMessage != null) {
            listCount.setText("共计" + allMessage.size() + "个");
            selectResult.setListData(allMessage.toArray(new HVAJapanAVM[allMessage.size()]));
        } else {
            listCount.setText("共计0个");
            selectResult.setListData(new HVAJapanAVM[0]);
        }
    }

    private void flushRobotAVMList(String robot) {
        List<HVAJapanAVM> allMessage = null;
        try {
            //allMessage = HVAJapanAVMDao.selectAllMessage(serviceConn);
            allMessage = HVAJapanAVMDao.selectAllNameRobotMessage(serviceConn, robot);
            if (allMessage != null) {
                listCount.setText("共计" + allMessage.size() + "个");
                selectResult.setListData(allMessage.toArray(new HVAJapanAVM[allMessage.size()]));
            } else {
                listCount.setText("共计0个");
                selectResult.setListData(new HVAJapanAVM[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void flushAVMList() {
        List<HVAJapanAVM> allMessage = null;
        try {
            //allMessage = HVAJapanAVMDao.selectAllMessage(serviceConn);
            allMessage = HVAJapanAVMDao.selectAllNameMessage(serviceConn);
            if (allMessage != null) {
                listCount.setText("共计" + allMessage.size() + "个");
                selectResult.setListData(allMessage.toArray(new HVAJapanAVM[allMessage.size()]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flushMessageList() {

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
/*        isRobot.setSelected(false);
        isRobot2.setSelected(false);*/
        robotStatus.setSelectedIndex(0);

        cover.setIcon(null);
        cut1.setIcon(null);
        cut2.setIcon(null);
        cut3.setIcon(null);
        cut4.setIcon(null);
        cut5.setIcon(null);
        cut6.setIcon(null);
        cut7.setIcon(null);
        cut8.setIcon(null);
        cut9.setIcon(null);
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
        JTextField find = new JTextField();

        add.setEnabled(false);
        remove.setEnabled(false);

        find.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
/*                if (find.getText() == null) {
                    noSelected.setListData(noSelectedLabels.toArray(new HVAJapanAVLabelM[noSelectedLabels.size()]));
                } else {
                    noSelected.setListData(HVAJapanAVLabelDao.qryLabelByShow());
                }*/

            }
        });

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
        noScrollPane.setPreferredSize(new Dimension(250, 500));
        JScrollPane yesScrollPane = new JScrollPane(yesSelected);
        yesScrollPane.setPreferredSize(new Dimension(250, 500));
        jDialog.add(noScrollPane, BorderLayout.WEST);
        jDialog.add(yesScrollPane, BorderLayout.EAST);
        Box buttonBox = Box.createVerticalBox();
        buttonBox.add(add);
        buttonBox.add(remove);
        jDialog.add(buttonBox);
        jDialog.add(find, BorderLayout.NORTH);
        //jDialog.setLocationRelativeTo(father);
        jDialog.setLocation(500, 500);
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
        JDialog jDialog = new JDialog(father, "人物选择器", true);

        JList<HVAJapanAVPersonM> noSelected = new JList<>();
        JList<HVAJapanAVPersonM> yesSelected = new JList<>();
        noSelected.setFixedCellWidth(300);
        noSelected.setVisibleRowCount(30);
        yesSelected.setFixedCellWidth(300);
        yesSelected.setVisibleRowCount(30);

        try {
            allPersons = HVAJapanAVPersonDao.qryAllSimplePersonMessage(serviceConn);
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
        JTextField find = new JTextField();
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
        find.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (find.getText() == null || find.getText().length() == 0) {
                    noSelectedPersons = removeRepPersons(allPersons, avCase.getPersons());
                    noSelected.setListData(noSelectedPersons.toArray(new HVAJapanAVPersonM[noSelectedPersons.size()]));
                } else {

                    try {
                        List<HVAJapanAVPersonM> findList = HVAJapanAVPersonDao.qryPersonByName(serviceConn, find.getText());
                        if (findList != null && findList.size() != 0) {
                            noSelectedPersons = removeRepPersons(findList, avCase.getPersons());
                            noSelected.setListData(noSelectedPersons.toArray(new HVAJapanAVPersonM[findList.size()]));
                        } else {
                            noSelectedPersons = findList;
                            noSelected.setListData(new HVAJapanAVPersonM[0]);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (find.getText() == null) {
                    noSelectedPersons = removeRepPersons(allPersons, avCase.getPersons());
                    noSelected.setListData(noSelectedPersons.toArray(new HVAJapanAVPersonM[noSelectedPersons.size()]));
                } else {

                    try {
                        List<HVAJapanAVPersonM> findList = HVAJapanAVPersonDao.qryPersonByName(serviceConn, find.getText());
                        if (findList != null && findList.size() != 0) {
                            noSelectedPersons = removeRepPersons(findList, avCase.getPersons());
                            noSelected.setListData(findList.toArray(new HVAJapanAVPersonM[findList.size()]));
                        } else {
                            noSelectedPersons = findList;
                            noSelected.setListData(new HVAJapanAVPersonM[0]);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println(3);

            }
        });


        jDialog.add(new JScrollPane(noSelected), BorderLayout.WEST);
        jDialog.add(new JScrollPane(yesSelected), BorderLayout.EAST);
        jDialog.add(find, BorderLayout.NORTH);
        Box buttonBox = Box.createVerticalBox();
        buttonBox.add(add);
        buttonBox.add(remove);
        jDialog.add(buttonBox);
        //jDialog.setLocationRelativeTo(father);
        jDialog.setLocation(500, 500);
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
                if (avCase == null) return;
                avCase.setHave(true);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<HVAJapanAVS> hvaJapanAVS = avCase.getSources();

            JDialog jDialog = new JDialog(father, "资源列表", true);

            JButton add = new JButton("添加资源");
            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showSourcesAddDialog(jDialog);
                }
            });


            JTable table = null;

            if (hvaJapanAVS == null || hvaJapanAVS.size() == 0) {
                JOptionPane.showMessageDialog(father, "无资源！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
            } else {
                int size = hvaJapanAVS.size();
                Object[][] tableData = new Object[size + 1][LabelConstant.SOURCE_TABLE_COLUMN_TITLE.length];
                tableData[0] = new Object[]{"URL", "质量", "操作"};
                for (int i = 1; i <= size; i++) {
                    HVAJapanAVS hvaJapanAVS1 = hvaJapanAVS.get(i - 1);
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
                                HVAJapanAVS hvaJapanAVS1 = hvaJapanAVS.get(ro - 1);
                                showSourcesDetailDialog(hvaJapanAVS1);
                            }
                        });

                        delete.addActionListener(new ActionListener() {
                            @SneakyThrows
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (ro == 0) return;
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
                FitTableColumns(table);
                table.setPreferredSize(new Dimension(450, 100));
                jDialog.add(table, BorderLayout.CENTER);
            }

            jDialog.add(add, BorderLayout.SOUTH);
            //jDialog.setLocationRelativeTo(father);
            jDialog.pack();
            jDialog.setLocation(500, 500);
            //jDialog.setPreferredSize(new Dimension(800,400));
            jDialog.setVisible(true);

        }


    }

    public void showSourcesDetailDialog(HVAJapanAVS hvaJapanAVS) {
        JDialog jDialog = new JDialog(father, "资源详情", true);
        JLabel uuid = new JLabel("UUID:" + hvaJapanAVS.getUuid());
        JLabel SOURCE_URI = new JLabel("URI:" + hvaJapanAVS.getSource_uri());
        JLabel SOURCE_NAME = new JLabel("资源名称:" + hvaJapanAVS.getSource_name());
        JLabel translate = new JLabel("翻译:" + hvaJapanAVS.getTranslate());
        JLabel SUBTITLE = new JLabel("字幕:" + hvaJapanAVS.getSubtitle());

        JLabel QUALITY = new JLabel("资源质量:" + LabelConstant.SOURCE_QUALITY_TEXT[Integer.valueOf(hvaJapanAVS.getQuality()) - 1]);
        JLabel WATERMARK = new JLabel("水印广告:" + (hvaJapanAVS.getWatermark().equals("1") ? "有" : "无"));
        JLabel SIZES = new JLabel("资源大小:" + hvaJapanAVS.getSizes() + " MB");
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

    public void showSourcesAddDialog(JDialog fatherDialog) {
        JDialog jDialog = new JDialog(father, "资源添加", true);

        Box smBox = Box.createVerticalBox();

        Box sourceBox = Box.createHorizontalBox();
        JLabel sourceLabel = new JLabel("资源路径:");
        JTextField source = new JTextField(LabelConstant.DEFAULT_FILE_PATH);
        source.setEnabled(false);
        sourceBox.add(sourceLabel);
        sourceBox.add(source);

        Box sourceNameBox = Box.createHorizontalBox();
        JLabel sourceNameLabel = new JLabel("资源名称:");
        JTextField sourceName = new JTextField(20);
        JButton importButton = new JButton("自动填充");
        sourceNameBox.add(sourceNameLabel);
        sourceNameBox.add(sourceName);
        sourceNameBox.add(importButton);

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
        sizeBox.add(new JLabel("单位：MB"));

        Box formatBox = Box.createHorizontalBox();
        JLabel formatLabel = new JLabel("视频格式:");
        JComboBox<String> format = new JComboBox<String>(LabelConstant.SOURCE_VIDEO_FORMAT);
        formatBox.add(formatLabel);
        formatBox.add(format);

        Box resolutionBox = Box.createHorizontalBox();
        JLabel resolutionLabel = new JLabel("视频分辨率:");
        JComboBox<String> resolutionCombBox = new JComboBox<String>(LabelConstant.SOURCE_RESOLUTION);
        JTextField resolution = new JTextField(20);
        JLabel qualityLabel = new JLabel("视频质量（1-3级）:");
        JComboBox<String> quality = new JComboBox<String>(LabelConstant.SOURCE_QUALITY);
        resolutionBox.add(resolutionLabel);
        resolutionBox.add(resolutionCombBox);
        resolutionBox.add(resolution);
        resolutionBox.add(qualityLabel);
        resolutionBox.add(quality);
        /*
        Box qualityBox = Box.createHorizontalBox();
        qualityBox.add(qualityLabel);
        qualityBox.add(quality);
        */

        Box bitBox = Box.createHorizontalBox();
        JLabel bitLabel = new JLabel("视频码率:");
        JTextField bit = new JTextField(20);
        bitBox.add(bitLabel);
        bitBox.add(bit);
        sizeBox.add(new JLabel("单位：kbps(千位每秒)"));

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


        smBox.add(sourceBox);
        smBox.add(sourceNameBox);
        smBox.add(translateBox);
        smBox.add(subtitleBox);
        smBox.add(watermarkBox);
        smBox.add(sizeBox);
        smBox.add(formatBox);
        smBox.add(resolutionBox);
        smBox.add(new JLabel("视频等级：1[低画质] 2[中画质] 3[顶级画质]"));
        smBox.add(bitBox);
        smBox.add(uriBox);
        smBox.add(new JLabel("后缀说明：-CH 中文 -F 相对完全版"));
        smBox.add(compressFormatBox);
        smBox.add(passwordBox);

        Box oprBox = Box.createHorizontalBox();
        JButton insertButton = new JButton("添加");
        JButton addCompressButton = new JButton("手动添加至压缩队列");
        JCheckBox addOnlyOnceVedio = new JCheckBox("仅压缩该视频");
        oprBox.add(addOnlyOnceVedio);
        oprBox.add(addCompressButton);
        oprBox.add(insertButton);

        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    System.gc();

                    String text = sourceName.getText();
                    if (text == null || "".equals(text)) text = avCase.getIf_Code() + ".mp4";
                    String filePath = source.getText() + "\\" + avCase.getIf_Code() + "\\" + text;
                    sourceName.setText(text);

                    Map<String, Object> returnMap = VideoAnalyticalUtil.analyticalWithFFmpeg(filePath);

                    String resolutionText = (String) returnMap.get("resolution");
                    resolution.setText(resolutionText);
                    resolutionCombBox.setSelectedIndex(0);
                    if (resolutionText != null && resolutionText.length() != 0) {
                        for (int i = 1; i < LabelConstant.SOURCE_RESOLUTION.length; i++) {
                            if (resolutionText.equals(LabelConstant.SOURCE_RESOLUTION[i])) {
                                resolutionCombBox.setSelectedIndex(i);
                            }
                        }
                    }

                    avCase.setDuration(returnMap.get("duration") == null ? 1 : (long) returnMap.get("duration"));
                    size.setText((String) returnMap.get("size"));
                    bit.setText((String) returnMap.get("bit"));

                    String[] split = text.trim().split("\\.");
                    String suffix = split[split.length - 1].toLowerCase();

                    //"NUL","MPEG-4","MOV","MKV","AVI","WMV","RMVB","DVD","BLU-RAY DISK","FIV","ASF","NAVI","TS"
                    switch (suffix) {
                        case "mp4":
                            format.setSelectedIndex(1);
                            break;
                        case "mov":
                            format.setSelectedIndex(2);
                            break;
                        case "mkv":
                            format.setSelectedIndex(3);
                            break;
                        case "avi":
                            format.setSelectedIndex(4);
                            break;
                        case "wmv":
                            format.setSelectedIndex(5);
                            break;
                        case "rmvb":
                            format.setSelectedIndex(6);
                            break;
                        case "dvd":
                            format.setSelectedIndex(7);
                            break;
                        case "flv":
                            format.setSelectedIndex(9);
                            break;
                        case "ts":
                            format.setSelectedIndex(12);
                            break;
                        default:
                            format.setSelectedIndex(0);
                            break;
                    }

                    compressFormat.setSelectedIndex(1);
                    uri.setText(LabelConstant.SOURC_PATH_1 + ifCode.getText() + ".7z");
                    upload.setSelected(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(father, "文件打开/读取失败！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
                }

            }
        });
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
                if (bit.getText() == null) {
                    avsCase.setBit_rate(0);
                } else {
                    avsCase.setBit_rate(Long.parseLong(bit.getText()));
                }
                avsCase.setCompress_format(LabelConstant.SOURCE_COMPRESS_FORMAT[compressFormat.getSelectedIndex()]);
                avsCase.setSource_name(sourceName.getText());
                try {
                    HVAJapanAVSDao.insertSources(serviceConn, avsCase);
                    serviceConn.commit();
                    JOptionPane.showMessageDialog(father, "上传成功！！", "SourcesDialog", JOptionPane.INFORMATION_MESSAGE);
                    jDialog.setVisible(false);
                    fatherDialog.setVisible(false);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    JOptionPane.showMessageDialog(father, "上传失败！！", "SourcesDialog", JOptionPane.WARNING_MESSAGE);
                    jDialog.setVisible(false);
                    fatherDialog.setVisible(false);
                }
            }
        });
        addCompressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String text = sourceName.getText();
                if (text == null || "".equals(text)) {
                    //输出语句 +
                    return;
                }
                String thisfilePath = source.getText() + "\\" + avCase.getIf_Code() + "\\" + text;
                String filePath = source.getText() + "\\" + avCase.getIf_Code();
                String uriString = uri.getText();
                String compressFileName = null;
                String compressFilePath = null;
                if (uriString != null) {
                    String[] split = uriString.split("/");
                    if (split.length > 0) compressFileName = split[split.length - 1];
                    if (compressFileName != null)
                        compressFilePath = LabelConstant.DEFAULT_COMPRESS_FILE_PATH + "\\" + compressFileName;
                }

                if (addOnlyOnceVedio.isSelected()) {
                    compressionUtil.addCompressPool(thisfilePath, compressFilePath, "N", true, password.getText());
                    runMessagePrint(messageRun, "[" + filePath + "]已加入压缩队列，压缩结果文件应为[" + compressFilePath + "]\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);

                } else {
                    compressionUtil.addCompressPool(filePath, compressFilePath, "N", false, password.getText());
                    runMessagePrint(messageRun, "[" + filePath + "]已加入压缩队列，压缩结果文件应为[" + compressFilePath + "]\n", LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                }
            }
        });
        translate.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (translate.getSelectedIndex() == 1) {
                    uri.setText(LabelConstant.SOURC_PATH_1 + ifCode.getText() + "-CH" + ".7z");
                } else {
                    uri.setText(LabelConstant.SOURC_PATH_1 + ifCode.getText() + ".7z");
                }

                subtitle.setSelectedIndex(translate.getSelectedIndex());

            }
        });
        quality.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
/*                int selectedIndex = quality.getSelectedIndex();

                if (selectedIndex == 1 || selectedIndex == 2 || selectedIndex == 4 || selectedIndex == 6 || selectedIndex == 8)
                    watermark.setSelected(true);
                if (selectedIndex == 3 || selectedIndex == 5 || selectedIndex == 7 || selectedIndex == 9 || selectedIndex == 10)
                    watermark.setSelected(false);*/
            }
        });
        resolutionCombBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (resolutionCombBox.getSelectedIndex() != 0) {
                    resolution.setText(LabelConstant.SOURCE_RESOLUTION[resolutionCombBox.getSelectedIndex()]);
                } else {
                    resolution.setText("");
                }
            }
        });

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
            String childPath = ifCode.getText().replace(" ", "");
            File coverFile = new File(fileRootPath + "\\" + childPath + "\\" + "cover.jpg");
            File cutFile1 = new File(fileRootPath + "\\" + childPath + "\\" + "cut1.jpg");
            File cutFile2 = new File(fileRootPath + "\\" + childPath + "\\" + "cut2.jpg");
            File cutFile3 = new File(fileRootPath + "\\" + childPath + "\\" + "cut3.jpg");
            File cutFile4 = new File(fileRootPath + "\\" + childPath + "\\" + "cut4.jpg");
            File cutFile5 = new File(fileRootPath + "\\" + childPath + "\\" + "cut5.jpg");
            File cutFile6 = new File(fileRootPath + "\\" + childPath + "\\" + "cut6.jpg");
            File cutFile7 = new File(fileRootPath + "\\" + childPath + "\\" + "cut7.jpg");
            File cutFile8 = new File(fileRootPath + "\\" + childPath + "\\" + "cut8.jpg");
            File cutFile9 = new File(fileRootPath + "\\" + childPath + "\\" + "cut9.jpg");

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
            if (cutFile4.exists() && cutFile4.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile4);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut4.getWidth(), cut4.getHeight(), Image.SCALE_DEFAULT));
                cut4.setIcon(icon);
                avCase.setCut4(bytesFromFile);
            }
            if (cutFile5.exists() && cutFile5.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile5);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut5.getWidth(), cut5.getHeight(), Image.SCALE_DEFAULT));
                cut5.setIcon(icon);
                avCase.setCut5(bytesFromFile);
            }
            if (cutFile6.exists() && cutFile6.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile6);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut6.getWidth(), cut6.getHeight(), Image.SCALE_DEFAULT));
                cut6.setIcon(icon);
                avCase.setCut6(bytesFromFile);
            }
            if (cutFile7.exists() && cutFile7.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile7);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut7.getWidth(), cut7.getHeight(), Image.SCALE_DEFAULT));
                cut7.setIcon(icon);
                avCase.setCut7(bytesFromFile);
            }
            if (cutFile8.exists() && cutFile8.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile8);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut8.getWidth(), cut8.getHeight(), Image.SCALE_DEFAULT));
                cut8.setIcon(icon);
                avCase.setCut8(bytesFromFile);
            }
            if (cutFile9.exists() && cutFile9.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile9);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut9.getWidth(), cut9.getHeight(), Image.SCALE_DEFAULT));
                cut9.setIcon(icon);
                avCase.setCut9(bytesFromFile);
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
            String childPath = ifCode.getText().replace(" ", "");
            File coverFile = new File(fileRootPath + "\\" + childPath + "\\" + "cover.jpg");
            File cutFile1 = new File(fileRootPath + "\\" + childPath + "\\" + "cut1.jpg");
            File cutFile2 = new File(fileRootPath + "\\" + childPath + "\\" + "cut2.jpg");
            File cutFile3 = new File(fileRootPath + "\\" + childPath + "\\" + "cut3.jpg");
            File cutFile4 = new File(fileRootPath + "\\" + childPath + "\\" + "cut4.jpg");
            File cutFile5 = new File(fileRootPath + "\\" + childPath + "\\" + "cut5.jpg");
            File cutFile6 = new File(fileRootPath + "\\" + childPath + "\\" + "cut6.jpg");
            File cutFile7 = new File(fileRootPath + "\\" + childPath + "\\" + "cut7.jpg");
            File cutFile8 = new File(fileRootPath + "\\" + childPath + "\\" + "cut8.jpg");
            File cutFile9 = new File(fileRootPath + "\\" + childPath + "\\" + "cut9.jpg");

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
            if (cutFile4.exists() && cutFile4.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile4);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut4.getWidth(), cut4.getHeight(), Image.SCALE_DEFAULT));
                cut4.setIcon(icon);
                avCase.setCut4(bytesFromFile);
            }
            if (cutFile5.exists() && cutFile5.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile5);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut5.getWidth(), cut5.getHeight(), Image.SCALE_DEFAULT));
                cut5.setIcon(icon);
                avCase.setCut5(bytesFromFile);
            }
            if (cutFile6.exists() && cutFile6.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile6);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut6.getWidth(), cut6.getHeight(), Image.SCALE_DEFAULT));
                cut6.setIcon(icon);
                avCase.setCut6(bytesFromFile);
            }
            if (cutFile7.exists() && cutFile7.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile7);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut7.getWidth(), cut7.getHeight(), Image.SCALE_DEFAULT));
                cut7.setIcon(icon);
                avCase.setCut7(bytesFromFile);
            }
            if (cutFile8.exists() && cutFile8.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile8);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut8.getWidth(), cut8.getHeight(), Image.SCALE_DEFAULT));
                cut8.setIcon(icon);
                avCase.setCut8(bytesFromFile);
            }
            if (cutFile9.exists() && cutFile9.isFile()) {
                byte[] bytesFromFile = MyFileUtils.getBytesFromFile(cutFile9);
                ImageIcon icon = new ImageIcon(bytesFromFile);
                icon = new ImageIcon(icon.getImage().getScaledInstance(cut9.getWidth(), cut9.getHeight(), Image.SCALE_DEFAULT));
                cut9.setIcon(icon);
                avCase.setCut9(bytesFromFile);
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
                if (!(selectedValue.getDescribe() == null && describe.getText() != null)) {
                    describe.setText(selectedValue.getDescribe());
                }


                if (selectedValue.getScore() != 0) {
                    AVScore.setSelectedIndex((int) selectedValue.getScore() - 1);
                }
                recommend.setSelected("1".equals(selectedValue.getRecommend()));

/*                isRobot.setSelected("1".equals(selectedValue.getRobot()));
                isRobot2.setSelected("2".equals(selectedValue.getRobot()));
                //isRobot.setSelected("1".equals(selectedValue.getRobot()));*/
                robotStatus.setSelectedIndex(Integer.valueOf(selectedValue.getRobot()));
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
                if (selectedValue.getCut4() != null) {
                    ImageIcon iconCut4 = new ImageIcon(selectedValue.getCut4());
                    iconCut4 = new ImageIcon(iconCut4.getImage().getScaledInstance(cut4.getWidth(), cut4.getHeight(), Image.SCALE_DEFAULT));
                    cut4.setIcon(iconCut4);
                }
                if (selectedValue.getCut5() != null) {
                    ImageIcon iconCut5 = new ImageIcon(selectedValue.getCut5());
                    iconCut5 = new ImageIcon(iconCut5.getImage().getScaledInstance(cut5.getWidth(), cut5.getHeight(), Image.SCALE_DEFAULT));
                    cut5.setIcon(iconCut5);
                }
                if (selectedValue.getCut6() != null) {
                    ImageIcon iconCut6 = new ImageIcon(selectedValue.getCut6());
                    iconCut6 = new ImageIcon(iconCut6.getImage().getScaledInstance(cut6.getWidth(), cut6.getHeight(), Image.SCALE_DEFAULT));
                    cut6.setIcon(iconCut6);
                }
                if (selectedValue.getCut7() != null) {
                    ImageIcon iconCut7 = new ImageIcon(selectedValue.getCut7());
                    iconCut7 = new ImageIcon(iconCut7.getImage().getScaledInstance(cut7.getWidth(), cut7.getHeight(), Image.SCALE_DEFAULT));
                    cut7.setIcon(iconCut7);
                }
                if (selectedValue.getCut8() != null) {
                    ImageIcon iconCut8 = new ImageIcon(selectedValue.getCut8());
                    iconCut8 = new ImageIcon(iconCut8.getImage().getScaledInstance(cut8.getWidth(), cut8.getHeight(), Image.SCALE_DEFAULT));
                    cut8.setIcon(iconCut8);
                }
                if (selectedValue.getCut9() != null) {
                    ImageIcon iconCut9 = new ImageIcon(selectedValue.getCut9());
                    iconCut9 = new ImageIcon(iconCut9.getImage().getScaledInstance(cut9.getWidth(), cut9.getHeight(), Image.SCALE_DEFAULT));
                    cut9.setIcon(iconCut9);
                }
                avCase = selectedValue;
                avCase.setHave(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param textArea
     * @param message
     * @param model    LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT & LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT
     */
    private void runMessagePrint(JTextArea textArea, String message, String model) {
        String time_text = sdf.format(System.currentTimeMillis());
        if (LabelConstant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT.equals(model)) {
            textArea.append(message);
        } else if (LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT.equals(model)) {
            textArea.append("[" + time_text + "]: ");
            textArea.append(message);
        }
        textArea.paintImmediately(textArea.getBounds());
    }

    private byte[] getNextCut(int openShowDialogCut) {
        int nextCut = openShowDialogCut == 9 ? 1 : openShowDialogCut + 1;
        byte[] nextPhoto = null;
        switch (nextCut) {
            case 1:
                nextPhoto = avCase.getCut1();
                break;
            case 2:
                nextPhoto = avCase.getCut2();
                break;
            case 3:
                nextPhoto = avCase.getCut3();
                break;
            case 4:
                nextPhoto = avCase.getCut4();
                break;
            case 5:
                nextPhoto = avCase.getCut5();
                break;
            case 6:
                nextPhoto = avCase.getCut6();
                break;
            case 7:
                nextPhoto = avCase.getCut7();
                break;
            case 8:
                nextPhoto = avCase.getCut8();
                break;
            case 9:
                nextPhoto = avCase.getCut9();
                break;
            default:
                nextPhoto = avCase.getCut1();
                break;
        }
        if (nextPhoto != null) {
            this.openShowDialogCut = nextCut;
            return nextPhoto;
        } else {
            return getNextCut(nextCut);
        }
    }

}
