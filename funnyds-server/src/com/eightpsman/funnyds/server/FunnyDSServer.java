package com.eightpsman.funnyds.server;

import com.eightpsman.funnyds.server.ui.ServerView;
import com.eightpsman.funnyds.server.ui.StartView;
import com.eightpsman.funnyds.util.JavaUtil;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public class FunnyDSServer {

    public static void main(String[] args) {
        System.out.println("Hello from FunnyDS Server!");

        JavaUtil.changeLookAndFeel();

//        new ServerView();

        new StartView();
    }
}
