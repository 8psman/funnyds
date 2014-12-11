package com.eightpsman.funnyds.java;

import com.eightpsman.funnyds.core.Device;

import javax.swing.table.AbstractTableModel;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class ClientTableModel extends AbstractTableModel {
    String[] columnNames = new String[]{
            "ID", "Name", "IP", "OS", "Width", "Height", "DPI", "PosX", "PosY"
    };
    java.util.List<Device> deviceList;

    public ClientTableModel(java.util.List<Device> deviceList){
        this.deviceList = deviceList;
    }
    public java.util.List<Device> getDeviceList(){
        return deviceList;
    }
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return deviceList.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        switch (col){
            case 0: return deviceList.get(row).id;
            case 1: return deviceList.get(row).name;
            case 2: return deviceList.get(row).ip;
            case 3: return deviceList.get(row).os;
            case 4: return deviceList.get(row).pw;
            case 5: return deviceList.get(row).ph;
            case 6: return deviceList.get(row).dpi;
            case 7: return deviceList.get(row).x;
            case 8: return deviceList.get(row).y;
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
