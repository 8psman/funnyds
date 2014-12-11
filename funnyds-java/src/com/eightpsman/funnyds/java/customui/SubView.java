package com.eightpsman.funnyds.java.customui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * FunnyDS
 * Created by 8psman on 12/2/2014.
 * Email: 8psman@gmail.com
 */
public class SubView extends JPanel {
    public GridBagConstraints gbc;
    public SubView(){
        super();
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED, Color.GRAY, Color.WHITE));
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
    }
}