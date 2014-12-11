package com.eightpsman.funnyds.java;

import com.eightpsman.funnyds.core.Actor;

import javax.swing.table.AbstractTableModel;
import java.util.Timer;
import java.util.TimerTask;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class ActorTableModel extends AbstractTableModel{

    String[] columnNames = new String[]{
            "ID", "Owner", "PosX", "PosY", "VelocX", "VelocY", "Width", "Height"
    };
    java.util.List<Actor> actorList;

    public ActorTableModel(java.util.List<Actor> actorList){
        this.actorList = actorList;

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ActorTableModel.this.fireTableRowsUpdated(0, getRowCount()-1);
            }
        }, 1000, 500);
    }

    public java.util.List<Actor> getActorList(){
        return actorList;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return actorList.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        switch (col){
            case 0: return actorList.get(row).id;
            case 1: return actorList.get(row).owner;
            case 2: return actorList.get(row).px;
            case 3: return actorList.get(row).py;
            case 4: return actorList.get(row).pveloc_x;
            case 5: return actorList.get(row).pveloc_y;
            case 6: return actorList.get(row).pw;
            case 7: return actorList.get(row).ph;
        }
        return "unknown";
    }

    public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void setValueAt(Object value, int row, int col) {
        // Do nothing, my table is not editable
        fireTableCellUpdated(row, col);
    }
}
