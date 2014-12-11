package com.eightpsman.funnyds.java.client.ui;

import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.Constants;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.java.client.worker.ServerConnector;
import com.eightpsman.funnyds.util.AppUtils;
import com.eightpsman.funnyds.java.JavaUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * FunnyDS
 * Created by 8psman on 11/23/2014.
 * Email: 8psman@gmail.com
 */
public class Welcome extends JFrame{

    int mode;
    JButton exhMode;
    JButton presMode;
    JButton gamMode;
    JLabel errorMsg;
    JButton retry;

    public Welcome(){
        super("FunnyDS Client");

        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupView();

        setVisible(true);
    }

    private void setupView(){
        /** use Grid Bag Layout : use grid bag layout to align component center
         * and not become stretched in border layout*/
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.set(10, 10, 10, 10); // margin for all component
        /** button to see client detail */
        add(createInfoView(), BorderLayout.NORTH);

        /** Button to choose mode */
        JPanel wrapper = new JPanel(new GridBagLayout());
        exhMode = new JButton("EXHIBITION");
        presMode = new JButton("PRESENTATION");
        gamMode = new JButton("GAME");

        errorMsg = new JLabel("LET CHOOSE MODE AND HAVE FUN");
        retry = new JButton("RETRY");

        wrapper.add(exhMode, gbc);
        wrapper.add(presMode, gbc);
        wrapper.add(gamMode, gbc);
        gbc.gridy = 1; gbc.gridwidth = 3;
        wrapper.add(errorMsg, gbc);
        gbc.gridy = 2; gbc.gridwidth = 3;
        wrapper.add(retry, gbc);
        retry.setVisible(false);

        add(wrapper, BorderLayout.CENTER);

        /** and now set listener for button */
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(exhMode)){
                    mode = Constants.MODE_EXHI;
                    doConnect();
                }else if (e.getSource().equals(presMode)){
                    mode = Constants.MODE_PRES;
                    doConnect();
                }else if (e.getSource().equals(gamMode)){
                    mode = Constants.MODE_GAME;
                    doConnect();
                }else if (e.getSource().equals(retry)){
                    retryConnect();
                }
            }
        };
        exhMode.addActionListener(listener);
        presMode.addActionListener(listener);
        gamMode.addActionListener(listener);
        retry.addActionListener(listener);

    }

    private JTextField tfWidth;
    private JTextField tfHeight;

    private String lastW;
    private String lastH;

    private int maxW;
    private int maxH;
    JButton btEdit;

    private Device device;

    private JPanel createInfoView(){
        /** get local device */
        device = JavaUtil.getLocalDevice();
        maxW = (int)device.width;
        maxH = (int)device.height;
        device.width = maxW / 4;
        device.height = maxH / 4;

        /** setup view */
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.set(10, 10, 3, 3);
        gbc.anchor = GridBagConstraints.LINE_START;

        JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setBorder(BorderFactory.createTitledBorder("MY DEVICE"));
        gbc.gridy = 0;

        JTextField tfName;

        JTextField tfIp;

        jPanel.add(new JLabel("Name:"), gbc);
        jPanel.add(tfName = new JTextField(15), gbc);
        jPanel.add(new JLabel("IP:"), gbc);
        jPanel.add(tfIp = new JTextField(15), gbc);

        gbc.gridy = 1;
        jPanel.add(new JLabel("Width:"), gbc);
        jPanel.add(tfWidth = new JTextField(15), gbc);
        jPanel.add(new JLabel("Height:"), gbc);
        jPanel.add(tfHeight = new JTextField(15), gbc);

        JButton btFullscreen;
        gbc.anchor = GridBagConstraints.LINE_END;
        jPanel.add(btFullscreen = new JButton("Max"), gbc);

        jPanel.add(btEdit = new JButton("Edit"), gbc);

        tfName.setText(device.name); tfName.setEditable(false);
        tfIp.setText(device.ip); tfIp.setEditable(false);
        tfWidth.setText(device.width + ""); tfWidth.setEditable(false);
        tfHeight.setText(device.height + ""); tfHeight.setEditable(false);

        btFullscreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfWidth.setText(maxW + "");
                tfHeight.setText(maxH + "");
                device.width = maxW;
                device.height = maxH;
            }
        });
        btEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton button = (JButton)e.getSource();
                if (button.getText().equals("Edit")){
                    tfWidth.setEditable(true);
                    tfHeight.setEditable(true);
                    lastW = tfWidth.getText();
                    lastH = tfHeight.getText();
                    button.setText("Save");
                }else if (button.getText().equals("Save")){
                    saveInfo();
                }
            }
        });
        return jPanel;
    }

    /** save device info */
    private void saveInfo(){
        try{
            int w = Integer.parseInt(tfWidth.getText());
            int h = Integer.parseInt(tfHeight.getText());
            if (w > maxW) w = maxW;
            if (h > maxH) h = maxH;
            if (w < 100) w = 100;
            if (h < 100) h = 100;
            device.width  = w;
            device.height = h;
            tfWidth.setText(w + "");
            tfHeight.setText(h + "");
        }catch (Exception ex){
            tfWidth.setText(lastW);
            tfHeight.setText(lastH);
        }
        tfWidth.setEditable(false);
        tfHeight.setEditable(false);
        btEdit.setText("Edit");
    }

    private void retryConnect(){
        retry.setVisible(false);
        doConnect();
    }

    ClientManager manager;

    private void doConnect(){
        /** if user has not saved device info */
        if (btEdit.getText().equals("Save")){
            saveInfo();
        }

        if (manager == null){
             manager = ClientManager.createInstance();
        }

        device.calcPhysicalSize();
        manager.setLocalDevice(device);
        manager.setMode(mode);

        exhMode.setEnabled(false);
        presMode.setEnabled(false);
        gamMode.setEnabled(false);
        retry.setVisible(false);
        errorMsg.setText(getModeName() + " CONNECTING ...");
        new ServerConnector(Constants.ConnectingMethod.RMI, null, manager, handler).execute();
    }

    private String getModeName(){
        switch (mode){
            case Constants.MODE_EXHI:
                return exhMode.getText();
            case Constants.MODE_GAME:
                return gamMode.getText();
            case Constants.MODE_PRES:
                return presMode.getText();
            default:
                return "";
        }
    }

    private void onSuccess(){
        AppUtils.LOGGER.log(Level.INFO, "Connected to server!");
        switch (mode){
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
        this.dispose();
    }

    private void onError(String msg){
        exhMode.setEnabled(true);
        presMode.setEnabled(true);
        gamMode.setEnabled(true);
        errorMsg.setText("ERROR: " + msg);
        retry.setVisible(true);
    }

    Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {
            if (record.getMessage().startsWith("Success")){
                onSuccess();

            }else{
                onError(record.getMessage());
            }
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    };
}
