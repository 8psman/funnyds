package com.eightpsman.funnyds.java.client.ui;

import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.Device;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class GenericView extends JFrame implements ComponentListener{

    Device device;
    ClientManager manager;
    /** change location along with device */
    boolean isAutoLocation;

    public GenericView(ClientManager manager){
        super("FunnyDS Client");
        this.manager = manager;
        this.device =  manager.getLocalDevice();

        /** setup frame */
        setUndecorated(true);

        /** auto set size for frame */
        JPanel tmpPanel = new JPanel();
        tmpPanel.setPreferredSize(new Dimension((int)device.width, (int)device.height));
        add(tmpPanel, BorderLayout.CENTER);
        pack();
        remove(tmpPanel);

        /** setup frame */
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                onDestroy();
            }
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
            }
        });

        addComponentListener(this);
        isAutoLocation = false;
        manager.mLogger.addHandler(mHandler);
    }

    protected void onDestroy(){
        manager.mLogger.removeHandler(mHandler);
        manager.shutdown();
    }

    protected void onKickOut(){
        onDestroy();
        this.dispose();
        StartView startView = new StartView();
//        JOptionPane.showMessageDialog(welcome, "Ooops! You're kicked out!", "Message", JOptionPane.ERROR_MESSAGE);
    }

    protected void onDisconnected(){
        onDestroy();
        this.dispose();
        StartView startView = new StartView();
        JOptionPane.showMessageDialog(startView, "Ooops! Disconnected from server!", "Message", JOptionPane.ERROR_MESSAGE);
    }

    Handler mHandler = new Handler() {
        @Override
        public void publish(LogRecord record) {
            if (record.getMessage().equals(ClientManager.MSG_KICKEDOUT)){
                onKickOut();
            }else if(record.getMessage().equals(ClientManager.MSG_DISCONNECTED)){
                onDisconnected();
            }else if(record.getMessage().equals(ClientManager.MSG_SET_LOCATION)){
                setLocationDependOnDevice();
            }else if (record.getMessage().equals(ClientManager.MSG_LOCAL_DEVICE_UPDATED)){
                if (isAutoLocation){
                    setLocationDependOnDevice();
                }
            }
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    };

    /**
     * set frame location depend on the device's position, ask by server
     */
    public void setLocationDependOnDevice(){
        int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
        int newX = (int)device.x + screenW/2 - (int)device.width/2;
        int newY = (int)device.y + screenH/2 - (int)device.height/2;
        setLocation(newX, newY);
    }

    public void onLocationChange(int x, int y){
        int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
        device.x = x + device.width/2 - screenW/2;
        device.y = y + device.height/2 - screenH/2;
        manager.updateDevice(device);
    }

    public void onExit() {
        onDestroy();
        this.dispose();
        Welcome welcome = new Welcome();
    }

    @Override
    public void componentResized(ComponentEvent e) {

    }

    @Override
    public void componentMoved(ComponentEvent e) {
        if (isAutoLocation){
            Component com = e.getComponent();
            onLocationChange(com.getX(), com.getY());
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
