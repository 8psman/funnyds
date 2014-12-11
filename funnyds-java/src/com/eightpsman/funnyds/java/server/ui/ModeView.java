package com.eightpsman.funnyds.java.server.ui;

import com.eightpsman.funnyds.java.server.ServerManager;

import javax.swing.*;
import java.awt.*;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class ModeView extends JPanel{

    DeviceView deviceView;
    public ModeView(ServerManager manager){
        super();

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(styleTab("Clients")     , null, new ClientView(manager), "All clients connected to server");
        tabbedPane.addTab(styleTab("Devices")     , null, deviceView = new DeviceView(manager), "View all devices");
        tabbedPane.addTab(styleTab("Actors")      , null, new ActorView(manager), "View all actors");
        tabbedPane.setFocusable(false);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private String styleTab(String title){
        return String.format("<html><p align='center' style='padding:0 0 0 0; width: 60px; background:cyan;'>%s</p></html>", title);
    }

    public void destroy(){
        deviceView.destroy();
    }
}
