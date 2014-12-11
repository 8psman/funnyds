package com.eightpsman.funnyds.javaclient;

import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.Constants;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.javaclient.ui.GenericView;
import com.eightpsman.funnyds.javaclient.ui.PlayView;
import com.eightpsman.funnyds.javaclient.ui.PresentationView;
import com.eightpsman.funnyds.javaclient.worker.ServerConnector;
import com.eightpsman.funnyds.util.JavaUtil;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class AutoConnector {

    public static void main(String args[]){
        /** get params */
        final int mode   = Integer.parseInt(args[0]);
        final int col    = Integer.parseInt(args[1]);
        final int row    = Integer.parseInt(args[2]);
        final int width  = Integer.parseInt(args[3]);
        final int height = Integer.parseInt(args[4]);

        int space = 2;
        int deviceW = (int)((width - (col - 1) * space) / (float)col);
        int deviceH = (int)((height - (row - 1) * space) / (float)row);

        int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;

        int start_x = screenW/2 - width/2;
        int start_y = screenH/2 - height/2;

        for (int i=0; i<col; i++)
            for (int j=0; j<row; j++){
                int x = start_x + i*deviceW + space * i;
                int y = start_y + j*deviceH + space * j;
                launchClient(mode, x, y, deviceW, deviceH);
            }

        JavaUtil.changeLookAndFeel();

        JFrame frame = new JFrame("FunnyDS AutoConnector");
        frame.setSize(100, 100);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setState(JFrame.ICONIFIED);
        frame.setVisible(true);

    }

    public static void launchClient(final int mode, final int x, final int y, int w, int h){
        Device device = JavaUtil.getLocalDevice();
        final int screenW = (int)device.width;
        final int screenH = (int)device.height;

        device.width = w;
        device.height = h;
        device.calcPhysicalSize();

        final ClientManager manager = ClientManager.createInstance();
        manager.setLocalDevice(device);
        manager.setMode(mode);

        new ServerConnector(Constants.ConnectingMethod.RMI, null, manager, new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getMessage().startsWith("Success")){
                    Device localDevice = manager.getLocalDevice();
                    localDevice.x = x - screenW/2;
                    localDevice.y = y - screenH/2;
                    manager.updateDevice(localDevice);
                    GenericView view = null;
                    switch (mode){
                        case Constants.MODE_EXHI:
                            view = new PlayView(manager);
                            break;
                        case Constants.MODE_PRES:
                            view = new PresentationView(manager);
                            break;
                        case Constants.MODE_GAME:
                            view = new PlayView(manager);
                            break;
                    }
                    view.setLocation(x, y);

                }else{

                }
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        }).execute();
    }
}
