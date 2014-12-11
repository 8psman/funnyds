package com.eightpsman.funnyds.java.customui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * FunnyDS
 * Created by 8psman on 12/2/2014.
 * Email: 8psman@gmail.com
 */
public class FlatButton extends JLabel {
    Color normalColor = Color.WHITE;
    Color pressedColor = Color.CYAN.darker();
    Color hoverColor = Color.CYAN.brighter();

    boolean isFocusing;
    ActionListener actionListener;

    public FlatButton(String title, int width, int height){
        super(title, JLabel.CENTER);
        setOpaque(true);
        setBackground(normalColor);
        isFocusing = false;

        this.setPreferredSize(new Dimension(width, height));
        setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED, Color.GRAY, Color.WHITE));

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (isEnabled()){
                    if (actionListener != null)
                        actionListener.actionPerformed(new ActionEvent(e.getSource(), e.getID(), ""));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (isEnabled()){
                    setBackground(pressedColor);
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (isEnabled()){
                    if (isFocusing)
                        setBackground(hoverColor);
                    else setBackground(normalColor);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                if (isEnabled()){
                    setBackground(hoverColor);
                    isFocusing = true;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                if (isEnabled()){
                    setBackground(normalColor);
                    isFocusing = false;
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public FlatButton(String title){
        this(title, 100, 30);
    }

    public void setActionListener(ActionListener listener){
        this.actionListener = listener;
    }
}