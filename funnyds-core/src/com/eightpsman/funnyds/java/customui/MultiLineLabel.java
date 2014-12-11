package com.eightpsman.funnyds.java.customui;

import javax.swing.*;
import java.awt.*;

/**
 * FunnyDS
 * Created by 8psman on 12/2/2014.
 * Email: 8psman@gmail.com
 */
public class MultiLineLabel extends JTextArea {
    public MultiLineLabel(String content){
        super(content);

        setLineWrap(true);
        setEditable(false);
        setForeground(Color.BLACK);
        setWrapStyleWord(true);
    }
}