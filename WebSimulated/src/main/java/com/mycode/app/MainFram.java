package com.mycode.app;

import com.mycode.constant.Constant;
import com.mycode.entity.User;
import com.mycode.service.SimulatedYSRSJService;
import com.mycode.util.FileBaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainFram {

    private Logger logger = LoggerFactory.getLogger(MainFram.class);


    private String version = "V 1.0";
    private JFrame totalJFram = new JFrame("自动刷课软件 " + version);

    private MainFram mine = null;

    private JList<User> userJList = new JList<>();
    private JTextArea messageLog = new JTextArea();

    private JPanel userDelita = new JPanel();

    private JButton importUser = new JButton("批量导入用户");
    private JButton addUser = new JButton("添加用户");
    private JButton deleteUser = new JButton("删除用户");
    private JButton flushUser = new JButton("刷新用户信息");

    private JLabel username = new JLabel("用户名：" + Constant.DEFAULT_STRING_VALUES);
    private JLabel password = new JLabel("密码：" + Constant.DEFAULT_STRING_VALUES);
    private JLabel name = new JLabel("姓名：" + Constant.DEFAULT_STRING_VALUES);
    private JLabel status = new JLabel("状态：" + Constant.DEFAULT_STRING_VALUES);

    private JButton start = new JButton("开始刷课！");

    private JButton startAll = new JButton("开始所有用户！");
    private JButton threadSetting = new JButton("设置队列配置");

    private JButton threadMessage = new JButton("查看排队信息！");

    private ThreadPoolExecutor threadPool = null;
    private int MAX_THREADS = 10;
    private int ThreadPollQueueSize = 500;
    private List<User> users = new ArrayList<>();
    private FileBaseUtil base = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public JFrame init() {

        System.setProperty("webdriver.chrome.driver", "WebSimulated/src/main/resources/dirver/chromedriver.exe");// chromedriver服务地址

        base = new FileBaseUtil(Constant.DEFAULT_BASE_FILE_PATH);
        users = base.readFromBase();
        if (users != null) userJList.setListData(users.toArray(new User[users.size()]));
        mine = this;
        threadSetting.setEnabled(false);

        messageLog.setText(Constant.HELLO_WORLD);

        if (users == null) {
            logger.error("无法读取存储数据！");
        }

        threadPool = new ThreadPoolExecutor(MAX_THREADS,
                MAX_THREADS,
                Long.MAX_VALUE,
                TimeUnit.DAYS,
                new LinkedBlockingQueue<>(ThreadPollQueueSize),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                        try {
                            threadPoolExecutor.getQueue().put(runnable);
                            logger.info("等待队列已满，阻塞！并行数为[{}]，现队列等待数量[{}]",MAX_THREADS,threadPoolExecutor.getQueue().size());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

        bindingOfTheEvent();
        assemblyOfTheView();

        return totalJFram;
    }

    private void assemblyOfTheView() {

        JScrollPane userListScrollPane = new JScrollPane(userJList);

        Box delitaMessageBox = Box.createVerticalBox();
        delitaMessageBox.add(username);
        delitaMessageBox.add(password);
        delitaMessageBox.add(name);
        delitaMessageBox.add(status);

        Box delitaButtonBox = Box.createVerticalBox();
        delitaButtonBox.add(flushUser);
        delitaButtonBox.add(importUser);
        delitaButtonBox.add(addUser);
        delitaButtonBox.add(deleteUser);
        delitaButtonBox.add(new JPanel());
        delitaButtonBox.add(threadSetting);
        delitaButtonBox.add(threadMessage);
        delitaButtonBox.add(startAll);
        delitaButtonBox.add(new JPanel());
        delitaButtonBox.add(start);

        userDelita.add(delitaMessageBox);

        userJList.setVisibleRowCount(10);
        userListScrollPane.setPreferredSize(new Dimension(200,500));
        userDelita.setPreferredSize(new Dimension(500,500));
        messageLog.setPreferredSize(new Dimension(700,200));

        totalJFram.add(userListScrollPane, BorderLayout.WEST);
        totalJFram.add(new JScrollPane(messageLog),BorderLayout.SOUTH);
        totalJFram.add(userDelita,BorderLayout.CENTER);
        totalJFram.add(delitaButtonBox,BorderLayout.EAST);
        totalJFram.setBounds(10,10,700,700);
        totalJFram.setResizable(true);
        totalJFram.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        totalJFram.setVisible(true);
    }

    private void bindingOfTheEvent() {
        userJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                User selectedValue = userJList.getSelectedValue();
                if (selectedValue != null) {
                    username.setText(selectedValue.getUsername());
                    password.setText(selectedValue.getPassword());
                    name.setText(selectedValue.getName());
                    status.setText(selectedValue.getStatus());
                }
            }
        });

        importUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = null;
                BufferedReader reader = null;
                int allCount = 0;
                int importCount = 0;

                try {
                    file = new File(Constant.DEFAULT_IMPORT_FILE_PATH);

                    if (!file.exists()) {
                        logger.error("文件[{}]不存在！",file.getAbsolutePath());
                        writeMessage(messageLog,"未发现导入文件，请将文件命名为[import.txt]，并将该文件放置于程序同目录下！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                        return;
                    }

                    reader = new BufferedReader(new FileReader(file));
                    String tempString = null;
                    // 一次读入一行，直到读入null为文件结束
                    while ((tempString = reader.readLine()) != null) {
                        allCount ++;
                        String[] split = tempString.split("\\|");
                        if (split.length < 3) {
                            logger.error("数据读取错误！[{}]",tempString);
                            writeMessage(messageLog,"数据[" + tempString + "]缺少信息！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                            continue;
                        }
                        System.out.println(tempString);
                        User user = new User();
                        user.setUsername(split[0]);
                        user.setPassword(split[1]);
                        user.setName(split[2]);
                        if (split.length >= 4) {
                            user.setStatus(split[3]);
                        }
                        boolean importUser = true;
                        for (User user1 : users) {
                            if (user1.getUsername().equals(user.getUsername())) importUser = false;
                        }
                        if (importUser) {
                            importCount ++;
                            user.setIndex(String.valueOf(users.size() + 1));
                            users.add(user);
                        }
                    }
                    reader.close();
                    fulshUserList();
                    writeMessage(messageLog,"用户导入完毕！共读取[" + allCount + "]条，成功导入[" + importCount + "]条！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);

                } catch (IOException es) {
                    logger.error("数据读取发生IO错误！",es);
                } catch (Exception es) {
                    logger.error("数据读取发生未知错误！",es);
                }
            }
        });

        addUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog jDialog = new JDialog(totalJFram, "添加用户！", true);

                Box smBox = Box.createVerticalBox();


                Box usernameBox = Box.createHorizontalBox();
                JLabel usernameLabel = new JLabel("用户名:");
                JTextField username = new JTextField();
                usernameBox.add(usernameLabel);
                usernameBox.add(username);

                Box passwordBox = Box.createHorizontalBox();
                JLabel passwordLabel = new JLabel("密码:");
                JTextField password = new JTextField();
                passwordBox.add(passwordLabel);
                passwordBox.add(password);

                Box nameBox = Box.createHorizontalBox();
                JLabel nameLabel = new JLabel("姓名:");
                JTextField name = new JTextField();
                nameBox.add(nameLabel);
                nameBox.add(name);

                JButton add = new JButton("添加");

                add.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        try {
                            if (!isBlank(username.getText()) && !isBlank(password.getText()) && !isBlank(name.getText())) {
                                User user = new User(username.getText(), password.getText(), name.getText());
                                boolean importUser = true;
                                for (User user1 : users) {
                                    if (user1.getUsername().equals(user.getUsername())) importUser = false;
                                }
                                if (importUser) {
                                    user.setIndex(String.valueOf(users.size() + 1));
                                    users.add(user);
                                    fulshUserList();
                                    logger.info("用户[{}]导入成功",user.getAllMessage());
                                    writeMessage(messageLog,"用户导入完毕！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                                }
                            } else {
                                logger.error("输入存在空值，拒绝输入");
                                writeMessage(messageLog,"用户导入错误！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                            }
                        } catch (Exception es) {
                            logger.error("导入过程出现错误！",e);
                            writeMessage(messageLog,"用户导入错误！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                        }
                    }
                });

                smBox.add(usernameBox);
                smBox.add(passwordBox);
                smBox.add(nameBox);
                smBox.add(add);
                jDialog.add(smBox, BorderLayout.CENTER);
                jDialog.setLocationRelativeTo(totalJFram);
                jDialog.pack();
                jDialog.setVisible(true);
            }
        });

        deleteUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = userJList.getSelectedIndex();
                users.remove(selectedIndex);
                for (int i = 0; i < users.size(); i++) {
                    users.get(i).setIndex(String.valueOf(i + 1));
                }
                fulshUserList();
            }
        });

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = userJList.getSelectedIndex();
                User user = users.get(selectedIndex);
                String nowStatus = user.getStatus();
                if (Constant.STATUS_READY.equals(nowStatus)) {
                    logger.info("用户[{}]已就绪，更新为WAIT，添加至线程池。",user.getAllMessage());
                    user.setStatus(Constant.STATUS_WAIT);
                    threadPool.execute(new SimulatedYSRSJService(mine,user));
                    writeMessage(messageLog,"用户[" + user.getName() + "]已加入至执行队列！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                } else if (Constant.STATUS_RUN.equals(nowStatus)) {
                    logger.info("用户[{}]已运行中，不执行任何操作",user.getAllMessage());
                    writeMessage(messageLog,"用户[" + user.getName() + "]已在运行中！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                } else if (Constant.STATUS_WAIT.equals(nowStatus)) {
                    logger.info("用户[{}]已等待中，不执行任何操作",user.getAllMessage());
                    writeMessage(messageLog,"用户[" + user.getName() + "]已在队列中！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                } else if (Constant.STATUS_SUCCESS.equals(nowStatus)) {
                    logger.info("用户[{}]已完成，不执行任何操作",user.getAllMessage());
                    writeMessage(messageLog,"用户[" + user.getName() + "]已在队列中！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                } else if (Constant.STATUS_ERROR.equals(nowStatus)) {
                    logger.info("用户[{}]标记为失败，更新为WAIT，重新添加至线程池。",user.getAllMessage());
                    user.setStatus(Constant.STATUS_WAIT);
                    writeMessage(messageLog,"用户[" + user.getName() + "]已加入至执行队列！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                    threadPool.execute(new SimulatedYSRSJService(mine,user));
                }
                fulshUserList();
            }
        });

        startAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int runCount = 0;
                for (User user : users) {
                    String nowStatus = user.getStatus();
                    if (Constant.STATUS_READY.equals(nowStatus)) {
                        logger.info("用户[{}]已就绪，更新为WAIT，添加至线程池。",user.getAllMessage());
                        user.setStatus(Constant.STATUS_WAIT);
                        threadPool.execute(new SimulatedYSRSJService(mine,user));
                        runCount ++;
                    } else if (Constant.STATUS_RUN.equals(nowStatus)) {
                        logger.info("用户[{}]已运行中，不执行任何操作",user.getAllMessage());
                    } else if (Constant.STATUS_WAIT.equals(nowStatus)) {
                        logger.info("用户[{}]已等待中，不执行任何操作",user.getAllMessage());
                    } else if (Constant.STATUS_SUCCESS.equals(nowStatus)) {
                        logger.info("用户[{}]已完成，不执行任何操作",user.getAllMessage());
                    } else if (Constant.STATUS_ERROR.equals(nowStatus)) {
                        logger.info("用户[{}]标记为失败，更新为WAIT，重新添加至线程池。",user.getAllMessage());
                        user.setStatus(Constant.STATUS_WAIT);
                        threadPool.execute(new SimulatedYSRSJService(mine,user));
                        runCount ++;
                    }
                }
                fulshUserList();
                writeMessage(messageLog,"添加["+runCount+"]个用户至执行队列！",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
            }
        });

        threadMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int runCount = threadPool.getActiveCount();
                int waitCount = threadPool.getQueue().size();
                String s = (runCount == 0)?"闲置":"执行";
                writeMessage(messageLog,"任务["+s+"]中！执行中["+runCount+"]个，排队中：["+waitCount+"]个。",Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
            }
        });

    }

    private void writeMessage(JTextArea textArea, String message, String model) {
        String time_text = sdf.format(System.currentTimeMillis());
        if (Constant.TEXT_APPEND_MODEL_NO_TIME_AND_NO_NEXT.equals(model)) {
            textArea.append(message);
        } else if (Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT.equals(model)) {
            textArea.append("[" + time_text + "]: ");
            textArea.append(message);
            textArea.append("\n");
        }
        textArea.paintImmediately(textArea.getBounds());
    }
    public void writeMessage(String message) {
        writeMessage(messageLog, message, Constant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
    }

    private void fulshUserList() {
        base.writeBase(users);
        userJList.setListData(users.toArray(new User[users.size()]));
    }

    private static boolean isBlank(String s) {
        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    public void fulshUserStatus() {
        base.writeBase(users);
        userJList.setListData(users.toArray(new User[users.size()]));
    }

}
