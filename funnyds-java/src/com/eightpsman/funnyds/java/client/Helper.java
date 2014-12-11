package com.eightpsman.funnyds.java.client;

import com.eightpsman.funnyds.core.Constants;
import com.eightpsman.funnyds.java.JavaUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class Helper extends JFrame{

    JTextField tfRow;
    JTextField tfCol;
    JTextField tfWidth;
    JTextField tfHeight;

    public Helper(){
        super("FunnyDS Helper");

        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0; add(new JLabel("Col"), gbc);     add(tfCol = new JTextField(15), gbc);
        gbc.gridy = 1; add(new JLabel("Row"), gbc);     add(tfRow = new JTextField(15), gbc);
        gbc.gridy = 2; add(new JLabel("Width"), gbc);   add(tfWidth  = new JTextField(15), gbc);
        gbc.gridy = 3; add(new JLabel("Height"), gbc);  add(tfHeight = new JTextField(15), gbc);
        gbc.gridy = 4; gbc.gridwidth = 2;

        JButton btCreate = new JButton("DO IT");
        add(btCreate, gbc);

        btCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doStuff();
            }
        });

        setVisible(true);
    }

    public void doStuff(){
        try{
            int col     = Integer.parseInt(tfCol.getText());
            int row     = Integer.parseInt(tfRow.getText());
            int width   = Integer.parseInt(tfWidth.getText());
            int height  = Integer.parseInt(tfHeight.getText());

            JavaUtil.executeClass(AutoConnector.class, false, Constants.MODE_PRES + "", col + "", row + "", width + "", height + "");
            System.exit(0);
        }catch (Exception ex){
            ex.printStackTrace();
            return ;
        }
    }


    public static void main(String args[]){
        JavaUtil.changeLookAndFeel();
        new Helper();
    }


}
