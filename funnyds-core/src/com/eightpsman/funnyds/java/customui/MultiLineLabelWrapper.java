package com.eightpsman.funnyds.java.customui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * FunnyDS
 * Created by 8psman on 12/2/2014.
 * Email: 8psman@gmail.com
 */
public class MultiLineLabelWrapper extends JScrollPane{
    public MultiLineLabelWrapper(JComponent component, int height){
        super(component);
        setPreferredSize(new Dimension(0, height));
        setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED, Color.GRAY, Color.WHITE));
    }
}
