package com.mycode.util;

import com.mycode.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBaseUtil {

    private Logger logger = LoggerFactory.getLogger(FileBaseUtil.class);

    private String baseFileName = "";
    private String baseFilePath = "";

    public FileBaseUtil() {
    }

    public FileBaseUtil(String baseFilePath) {
        this.baseFilePath = baseFilePath;
    }

    public FileBaseUtil(String baseFileName, String baseFilePath) {
        this.baseFileName = baseFileName;
        this.baseFilePath = baseFilePath;
    }

    public List<User> readFromBase() {
        File file = null;
        BufferedReader reader = null;
        List<User> users= null;

        try {
            file = new File(baseFilePath);

            if (!file.exists()) {
                file.createNewFile();
                return users;
            }

            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            users = new ArrayList<>();
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                String[] split = tempString.split("\\|");
                if (split.length < 4) {
                    logger.error("数据读取错误！[{}]",tempString);
                    continue;
                }
                System.out.println(tempString);
                User user = new User();
                user.setIndex(split[0]);
                user.setUsername(split[1]);
                user.setPassword(split[2]);
                user.setName(split[3]);
                if (split.length >= 5) {
                    user.setStatus(split[4]);
                }
                users.add(user);
            }
            reader.close();
        } catch (IOException e) {
            logger.error("数据读取发生IO错误！",e);
        } catch (Exception e) {
            logger.error("数据读取发生未知错误！",e);
        }
        return users;
    }

    public boolean writeBase(List<User> users) {

        File file = null;
        boolean success = true;
        try {
            file = new File(baseFilePath);

            if (!file.exists()) {
                file.createNewFile();
            }
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(file, false);
            for (User user : users) {
                StringBuilder userString = new StringBuilder();
                userString.append(user.getIndex()).append("|")
                        .append(user.getUsername()).append("|")
                        .append(user.getPassword()).append("|")
                        .append(user.getName()).append("|")
                        .append(user.getStatus()).append("\n");

                writer.write(userString.toString());
            }
            writer.close();
        } catch (IOException e) {
            logger.error("数据写入发生未知错误！",e);
            success = false;
        }
        return true;
    }


}
