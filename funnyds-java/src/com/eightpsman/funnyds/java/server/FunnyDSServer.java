package com.eightpsman.funnyds.java.server;

import com.eightpsman.funnyds.java.server.ui.StartView;
import com.eightpsman.funnyds.java.JavaUtil;

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
