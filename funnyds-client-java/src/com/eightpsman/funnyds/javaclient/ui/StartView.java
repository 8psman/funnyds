package com.eightpsman.funnyds.javaclient.ui;

import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.Constants;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.java.customui.*;
import com.eightpsman.funnyds.javaclient.worker.ServerConnector;
import com.eightpsman.funnyds.util.AppUtils;
import com.eightpsman.funnyds.util.JavaUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * FunnyDS
 * Created by 8psman on 12/3/2014.
 * Email: 8psman@gmail.com
 */
public class StartView extends JFrame{

    FlatButton start;
    MultiLineLabel alert;

    Device device = JavaUtil.getLocalDevice();

    public StartView(){
        super("FunnyDS Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBackground(Color.GRAY.darker());
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.set(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridy = 0; gbc.gridwidth = 1;
        add(makeInfoView(), gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        add(makeConnectionView(), gbc);

        gbc.gridy = 2;
        add(makeServerView(), gbc);

        gbc.gridy = 3; gbc.gridwidth = 1;
        add(makeModeView(), gbc);

        gbc.gridy = 4;
        add(new MultiLineLabelWrapper(alert = new MultiLineLabel("Hit start button and have fun!"), 60), gbc);
        gbc.gridy = 5;
        add(start = CustomUI.newFlatButton("START"), gbc);

        start.setActionListener(startListener);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    int width;
    ActionListener widthListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField jTextField = (JTextField) e.getSource();
            String content = jTextField.getText();
            int w;
            try {
                w = Integer.parseInt(content);
                if (w >= 10 && w <= device.width){
                    width = w;
                    jTextField.setText(w+"");
                }else{
                    jTextField.setText(width+"");
                }
            } catch (Exception ex) {
                jTextField.setText(width+"");
            }
        }
    };

    int height;
    ActionListener heightListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField jTextField = (JTextField) e.getSource();
            String content = jTextField.getText();
            int h;
            try {
                h = Integer.parseInt(content);
                if (h >= 10 && h <= device.height){
                    height = h;
                    jTextField.setText(h+"");
                }else{
                    jTextField.setText(height+"");
                }
            } catch (Exception ex) {
                jTextField.setText(height+"");
            }
        }
    };

    ActionListener maxButtonListener =  new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            width = (int)device.width;
            tfWidth.setText(width+"");
            height = (int) device.height;
            tfHeight.setText(height+"");
        }
    };
    JTextField tfWidth;
    JTextField tfHeight;

    String name;
    ActionListener nameListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField jTextField = (JTextField) e.getSource();
            name = jTextField.getText();
        }
    };

    private JPanel makeInfoView(){
        SubView infoPanel = new SubView();
        GridBagConstraints gbc = infoPanel.gbc;

        gbc.insets.set(0, 0, 0, 0);
        gbc.gridy = 0; gbc.gridwidth = 2;
        infoPanel.add(CustomUI.newHeaderView("YOUR DEVICE", null), gbc);

        gbc.insets.set(5, 5, 5, 5);

        gbc.gridy = 1; gbc.gridwidth = 1;
        infoPanel.add(new JLabel("Name"), gbc);
        infoPanel.add(CustomUI.newTextField(name = device.name, 10, nameListener), gbc);

        gbc.gridy = 2;
        infoPanel.add(new JLabel("IP"), gbc);
        infoPanel.add(CustomUI.newTextField(device.ip, 10, null), gbc);

        FlatButton maxButton;
        gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizePanel.setBackground(Color.WHITE);
        sizePanel.add(new JLabel("Size"));
        sizePanel.add(tfWidth = CustomUI.newTextField((width = (int)device.width/4) + "", 4, widthListener));
        sizePanel.add(tfHeight = CustomUI.newTextField((height = (int)device.height/4) + "", 4, heightListener));
        sizePanel.add(maxButton = new FlatButton("[ ]", 20, 20)); maxButton.setActionListener(maxButtonListener);
        sizePanel.add(new JLabel("DPI"));
        sizePanel.add(CustomUI.newTextField(device.dpi+"", 2, null));
        infoPanel.add(sizePanel, gbc);

        return infoPanel;
    }

    JRadioButton rmiMethod;
    JRadioButton socketMethod;

    private JPanel makeConnectionView(){
        SubView conPanel = new SubView();
        GridBagConstraints gbc = conPanel.gbc;

        gbc.insets.set(0, 0, 0, 0);
        gbc.gridy = 0; gbc.gridwidth = 2;
        conPanel.add(CustomUI.newHeaderView("CONNECTION", null), gbc);

        gbc.insets.set(5, 5, 5, 5);
        gbc.gridy = 1; gbc.gridwidth = 1;
        conPanel.add(rmiMethod = new JRadioButton("RMI"), gbc);
        conPanel.add(socketMethod = new JRadioButton("Socket"), gbc);
        ButtonGroup group = new ButtonGroup();
        group.add(rmiMethod); rmiMethod.setBackground(Color.WHITE);
        group.add(socketMethod); socketMethod.setBackground(Color.WHITE);

        rmiMethod.setSelected(true);

        return conPanel;
    }

    JRadioButton exhiMode;
    JRadioButton presMode;

    private JPanel makeModeView(){
        SubView modePanel = new SubView();
        GridBagConstraints gbc = modePanel.gbc;

        gbc.insets.set(0, 0, 0, 0);
        gbc.gridy = 0; gbc.gridwidth = 2;
        modePanel.add(CustomUI.newHeaderView("MODE", null), gbc);

        gbc.insets.set(5, 5, 5, 5);
        gbc.gridy = 1; gbc.gridwidth = 1;
        modePanel.add(exhiMode = new JRadioButton("Exhibition"), gbc);
        modePanel.add(presMode = new JRadioButton("Presentation"), gbc);
        ButtonGroup group = new ButtonGroup();
        group.add(exhiMode); exhiMode.setBackground(Color.WHITE);
        group.add(presMode); presMode.setBackground(Color.WHITE);

        exhiMode.setSelected(true);

        return modePanel;
    }

    JRadioButton auto;
    JRadioButton config;

    ChangeListener changeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (config.isSelected()){
                tfHost.setVisible(true);
            }else{
                tfHost.setVisible(false);
            }
        }
    };
    JTextField tfHost;
    String host;
    ActionListener hostListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField jTextField = (JTextField) e.getSource();
            String addr = jTextField.getText();
            try {
                InetAddress.getByName(addr);
                host = addr;
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                jTextField.setText(host);
            }
        }
    };
    private JPanel makeServerView(){
        SubView serverPanel = new SubView();
        GridBagConstraints gbc = serverPanel.gbc;

        gbc.insets.set(0, 0, 0, 0);
        gbc.gridy = 0; gbc.gridwidth = 2;
        serverPanel.add(CustomUI.newHeaderView("SERVER", null), gbc);

        gbc.insets.set(5, 5, 5, 5);
        gbc.gridy = 1; gbc.gridwidth = 1;
        serverPanel.add(auto = new JRadioButton("Auto find"), gbc);
        serverPanel.add(config = new JRadioButton("Set"), gbc);
        ButtonGroup group = new ButtonGroup();
        group.add(auto); auto.setBackground(Color.WHITE);
        group.add(config); config.setBackground(Color.WHITE);

        gbc.gridy = 2; gbc.gridwidth = 2;
        serverPanel.add(tfHost = CustomUI.newTextField(host = "127.0.0.1", 10, hostListener), gbc);

        config.setSelected(true);
        config.addChangeListener(changeListener);

        return serverPanel;
    }

    ActionListener startListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            doConnect();
        }
    };

    ClientManager manager;

    private void doConnect(){
        if (manager == null)
            manager = ClientManager.createInstance();
        Device localDevice = JavaUtil.getLocalDevice();
        localDevice.width  = width;
        localDevice.height = height;
        localDevice.name   = name;
        localDevice.calcPhysicalSize();
        manager.setLocalDevice(localDevice);
        manager.setMode(exhiMode.isSelected()? Constants.MODE_EXHI : Constants.MODE_PRES);

        Constants.ConnectingMethod method = rmiMethod.isSelected() ? Constants.ConnectingMethod.RMI : Constants.ConnectingMethod.Socket;
        String hostAddr = config.isSelected() ? host : null;

        start.setEnabled(false);
        alert.setForeground(Color.BLACK);
        alert.setText("Connecting ...");

        new ServerConnector(method, hostAddr, manager, handler).execute();
    }

    Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {
            if (record.getMessage().startsWith("Error")){
                alert.setForeground(Color.RED);
                alert.setText(record.getMessage());
                start.setEnabled(true);
            }else if (record.getMessage().startsWith("Success")){
                alert.setForeground(Color.BLACK);
                alert.setText(record.getMessage());
                start.setEnabled(true);
                onConnected();
            }
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    };

    private void onConnected(){
        this.dispose();
        switch (manager.getMode()){
            case Constants.MODE_EXHI:
                new PlayView(manager);
                break;
            case Constants.MODE_PRES:
                new PresentationView(manager);
                break;
            case Constants.MODE_GAME:
                new GameView(manager);
                break;
        }
    }
}
