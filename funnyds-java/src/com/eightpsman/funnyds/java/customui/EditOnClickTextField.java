package com.eightpsman.funnyds.java.customui;

import javax.swing.*;
import java.awt.event.*;

/**
 * FunnyDS
 * Created by 8psman on 12/2/2014.
 * Email: 8psman@gmail.com
 */
public class EditOnClickTextField extends JTextField {

    public EditOnClickTextField(String content, int cols, ActionListener listener){
        super(content, cols);
        setEditable(false);
        setupListener(listener);
    }

    public EditOnClickTextField(int cols, final ActionListener listener){
        super(cols);
        setEditable(false);
        setupListener(listener);
    }

    private void setupListener(final ActionListener listener){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (listener !=  null){
                    setEditable(true);
                    requestFocus();
                    getCaret().setVisible(true);
                }
            }
        });

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (listener != null){
                    setEditable(false);
                    listener.actionPerformed(new ActionEvent(e.getSource(), e.getID(), ""));
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == KeyEvent.VK_ENTER){
                    transferFocus();
                }
            }
        });
    }
}
