package com.eightpsman.funnyds.java.server.ui;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.java.ActorTableModel;
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
 * Created by 8psman on 11/21/2014.
 * Email: 8psman@gmail.com
 */
public class ActorView extends JPanel implements ActionListener{

    JTable table;
    ServerManager manager;
    ActorTableModel model;
    public ActorView(ServerManager manager){
        super();
        this.manager = manager;

        setLayout(new BorderLayout());

        table = new JTable();
        table.setModel(model = new ActorTableModel(manager.getActors()));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPanel.add(newButton("Remove"));
        jPanel.add(newButton("Clear"));
        add(jPanel, BorderLayout.SOUTH);

        ServerLogger.LOGGER.addHandler(clientHandler);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JButton jButton = (JButton)event.getSource();
        if (jButton.getName().equals("Clear")){
            clearActor();
        }else{
            int row = table.getSelectedRow();
            if (row >= 0){
                Actor actor = ((ActorTableModel)table.getModel()).getActorList().get(row);
                if (jButton.getName().equals("Remove")){
                    remove(actor);
                }
            }
        }
    }

    public JButton newButton(String name){
        JButton jButton = new JButton(name);
        jButton.setName(name);

        jButton.addActionListener(this);
        return jButton;
    }

    private void remove(Actor actor){
        manager.removeActor(actor.id);
    }

    private void clearActor(){
        while (manager.getActors().size() > 0){
            manager.removeActor(manager.getActor(0).id);
        }
    }


    Handler clientHandler = new Handler() {
        @Override
        public void publish(LogRecord record) {
            if (record.getMessage().startsWith("Actor added")){
                model.fireTableDataChanged();
            }else if (record.getMessage().startsWith("Actor updated")){
                model.fireTableRowsUpdated(0, model.getRowCount()-1);
            }else if (record.getMessage().startsWith("Actor removed")){
                model.fireTableDataChanged();
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
