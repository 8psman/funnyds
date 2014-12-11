package com.eightpsman.funnyds.javaclient;

import com.eightpsman.funnyds.javaclient.ui.StartView;
import com.eightpsman.funnyds.javaclient.ui.Welcome;

import javax.swing.*;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public class FunnyDSClient {

    public static void main(String[] args) {
        for (int i=0; i<args.length; i++)
            System.out.println(i + " : " + args[i]);
        System.out.println("Hello from FunnyDS Java Client!");

        // change java look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

//        new SetupView();

        new StartView();
//        new Welcome();
//        new Helper();
    }

}
