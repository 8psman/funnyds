package com.eightpsman.funnyds.java.server.ui;

import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.java.ClientTableModel;
import com.eightpsman.funnyds.java.server.ServerLogger;
import com.eightpsman.funnyds.java.server.ServerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class ClientView extends JPanel implements ActionListener{

    JTable table;
    ServerManager manager;
    public ClientView(ServerManager manager){
        super();
        this.manager = manager;

        setLayout(new BorderLayout());

        table = new JTable();
        table.setModel(new ClientTableModel(manager.getDevices()));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPanel.add(newButton("Kick out"));
        jPanel.add(newButton("Kick all"));
        jPanel.add(newButton("Send message"));

        add(jPanel, BorderLayout.SOUTH);

        ServerLogger.LOGGER.addHandler(clientHandler);

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JButton jButton = (JButton)event.getSource();
        if (jButton.getName().equals("Kick all")){
            kickAll();
            return;
        }
        int row = table.getSelectedRow();
        if (row >= 0){
            Device device = ((ClientTableModel)table.getModel()).getDeviceList().get(row);
            if (jButton.getName().equals("Kick out")){
                kickOut(device);
            }else if (jButton.getName().equals("Send message")){
                sendMessage(device);
            }
        }
    }

    public JButton newButton(String name){
        JButton jButton = new JButton(name);
        jButton.setName(name);
        jButton.addActionListener(this);
        return jButton;
    }

    private void kickAll(){
        manager.kickOutAll();
    }
    private void kickOut(Device device){
        manager.kickOut(device.id);
    }

    private void sendMessage(Device device){
        System.out.println("Wanna send message to: " + device.name);
    }

    private void updateTable(){
        ClientTableModel model = (ClientTableModel) table.getModel();
        model.fireTableDataChanged();
    }

    Handler clientHandler = new Handler() {
        @Override
        public void publish(LogRecord record) {
            if (record.getMessage().startsWith("Client connected")){
                updateTable();
            }else if (record.getMessage().startsWith("Client disconnected")){
                updateTable();
            }else if (record.getMessage().startsWith("Update device")){
                updateTable();
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
