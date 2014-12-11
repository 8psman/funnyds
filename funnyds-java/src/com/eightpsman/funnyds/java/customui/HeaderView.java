package com.eightpsman.funnyds.java.customui;

import javax.swing.*;
import java.awt.*;

/**
 * FunnyDS
 * Created by 8psman on 12/2/2014.
 * Email: 8psman@gmail.com
 */
public class HeaderView extends JPanel {
    public HeaderView(String title, JComponent component){
        super();
        setLayout(new BorderLayout());
        setBackground(Color.CYAN);
        setPreferredSize(new Dimension(200, 30));
        add(new JLabel(title, JLabel.CENTER), BorderLayout.CENTER);
        if (component != null){
            add(component, BorderLayout.WEST);
            component.setBackground(Color.CYAN);
        }
    }
}
