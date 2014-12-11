package com.eightpsman.funnyds.java.server.ui;

import com.eightpsman.funnyds.core.Constants;
import com.eightpsman.funnyds.java.customui.*;
import com.eightpsman.funnyds.rmi.RMIConstants;
import com.eightpsman.funnyds.java.server.worker.StartServerWorker;
import com.eightpsman.funnyds.java.JavaUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * FunnyDS
 * Created by 8psman on 12/1/2014.
 * Email: 8psman@gmail.com
 */
public class StartView extends JFrame implements ActionListener{

    JCheckBox rmiEnable;
    JTextField rmiName;
    JTextField rmiPort;
    JCheckBox socketEnable;
    JTextField socketPort;
    FlatButton start;
    MultiLineLabel alert;

    public StartView(){
        super("FunnyDS Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBackground(Color.GRAY.darker());
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.set(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridy = 0;
        add(makeInfoView(), gbc);
        gbc.gridy = 1;
        add(makeRMIView(), gbc);
        gbc.gridy = 2;
        add(makeSocketView(), gbc);
        gbc.gridy = 3;
        add(new MultiLineLabelWrapper(alert = new MultiLineLabel("Hit start button and have fun!"), 60), gbc);
        gbc.gridy = 4;
        add(start = CustomUI.newFlatButton("START"), gbc);

        rmiName.setText(RMIConstants.RMI_NAME);
        rmiPort.setText(RMIConstants.RMI_PORT+"");
        socketPort.setText(Constants.SERVER_SOCKET_PORT+"");

        rmiEnable.setSelected(true);
        socketEnable.setSelected(true);

        alert.setAlignmentX(1f);

        start.setActionListener(this);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel makeInfoView(){
        SubView infoPanel = new SubView();
        GridBagConstraints gbc = infoPanel.gbc;

        gbc.insets.set(0, 0, 0, 0);
        gbc.gridy = 0; gbc.gridwidth = 2;
        infoPanel.add(CustomUI.newHeaderView("INFO", null), gbc);

        gbc.insets.set(5, 5, 5, 5);
        gbc.gridy = 1; gbc.gridwidth = 1;
        infoPanel.add(new JLabel("IP"), gbc);
        JTextField tfHost;
        infoPanel.add(tfHost = new JTextField(JavaUtil.getHostIP(), 15), gbc);
        tfHost.setEditable(false);

        return infoPanel;
    }

    private JPanel makeRMIView(){
        SubView rmiPanel = new SubView();
        GridBagConstraints gbc = rmiPanel.gbc;

        gbc.insets.set(0, 0, 0, 0); gbc.gridwidth = 2;
        rmiPanel.add(CustomUI.newHeaderView("RMI", rmiEnable = new JCheckBox()), gbc);

        gbc.insets.set(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 1; gbc.gridwidth = 1;

        rmiPanel.add(new JLabel("Name"), gbc);
        rmiPanel.add(rmiName = new JTextField(10), gbc);

        gbc.gridy = 2;
        rmiPanel.add(new JLabel("Port"), gbc);
        rmiPanel.add(rmiPort = new JTextField(5), gbc);

        rmiName.setEditable(false);
        rmiPort.setEditable(false);
        return rmiPanel;
    }

    private JPanel makeSocketView(){
        SubView socketPanel = new SubView();
        GridBagConstraints gbc = socketPanel.gbc;

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.set(0, 0, 0, 0); gbc.gridwidth = 2;
        socketPanel.add(CustomUI.newHeaderView("SOCKET", socketEnable = new JCheckBox()), gbc);

        gbc.insets.set(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 1; gbc.gridwidth = 1;
        socketPanel.add(new JLabel("Port"), gbc);
        socketPanel.add(socketPort  = new JTextField(5), gbc);

        socketPort.setEditable(false);
        return socketPanel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == start){
            startServer();
        }
    }

    private void startServer(){
        start.setEnabled(false);
        alert.setForeground(Color.BLACK);
        alert.setText("Starting server ...");
        new StartServerWorker(handler, rmiEnable.isSelected(), socketEnable.isSelected())
                .execute();
    }

    private void onServerStarted(){
        dispose();
        new ServerView();
    }

    Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {
            if (record.getMessage().startsWith("Error")){
                alert.setForeground(Color.RED);
                alert.setText(record.getMessage());
                start.setEnabled(true);
                start.setText("RETRY");
            }else if (record.getMessage().startsWith("Success")){
                alert.setText(record.getMessage());
                onServerStarted();
            }
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException { }
    };
}
