package com.eightpsman.funnyds.javaclient.ui;

import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.java.DeviceDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class PresentationView extends GenericView{

    DeviceDrawer drawer;

    public PresentationView(ClientManager manager){
        super(manager);

        drawer = new DeviceDrawer(manager, false);

        add(drawer, BorderLayout.CENTER);
        setVisible(true);

        drawer.addMouseMotionListener(mouseHandler);
        drawer.addMouseListener(mouseHandler);

    }

    MouseAdapter mouseHandler = new MouseAdapter() {
        int startX;
        int startY;
        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            startX = e.getX();
            startY = e.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            PresentationView.this.setLocation(e.getXOnScreen() - startX, e.getYOnScreen() - startY);

        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        drawer.destroy();
    }

    @Override
    protected void onKickOut(){
        onDestroy();
        this.dispose();
        System.exit(0);
    }

    @Override
    protected void onDisconnected(){
        onDestroy();
        this.dispose();
        System.exit(0);
    }
}
