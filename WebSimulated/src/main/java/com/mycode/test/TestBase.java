package com.mycode.test;

import com.mycode.constant.Constant;
import com.mycode.entity.User;
import com.mycode.util.FileBaseUtil;

import java.util.ArrayList;
import java.util.List;

public class TestBase {

    public static void main(String[] args) {


        FileBaseUtil fileBaseUtil = new FileBaseUtil("./base.txt");
/*        List<User> users = new ArrayList<>();
        User user1 = new User("1","jzr1","123","zsy1", Constant.STATUS_READY);
        User user2 = new User("2","jzr2","123","zsy2", Constant.STATUS_READY);
        User user3 = new User("3","jzr3","123","zsy3", Constant.STATUS_READY);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        fileBaseUtil.writeBase(users);*/

        System.out.println(fileBaseUtil.readFromBase());

    }

}
