package com.eightpsman.funnyds.javaclient.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * FunnyDS
 * Created by 8psman on 11/23/2014.
 * Email: 8psman@gmail.com
 */
public class TestView extends JFrame implements ComponentListener{

    JPanel demoPanel;
    JPanel drawPanel;
    JDesktopPane desktopPane;

    static int borderSize = 0;
    static int titleBarSize = 0;
    static JLayeredPane layeredPane;
    static JButton button;
    JInternalFrame frame;
    public TestView(boolean isUndecorated){
        super("Demo");
        setUndecorated(isUndecorated);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int w = Toolkit.getDefaultToolkit().getScreenSize().width;
        int h = Toolkit.getDefaultToolkit().getScreenSize().height;

            JPanel fixPanel = new JPanel();
            fixPanel.setPreferredSize(new Dimension(w, h));
            fixPanel.setBackground(Color.cyan);
            add(fixPanel);
            pack();
            setLocationRelativeTo(null);
            remove(fixPanel);

        setLocation(0, 0);
            if (layeredPane == null){
                layeredPane = new JLayeredPane();

                desktopPane = new JDesktopPane();
                desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

                frame = new JInternalFrame("Document", true, true, true, true);
                frame.setSize(200, 200);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                desktopPane.add(frame);

                JPanel jPanel = new JPanel();
                jPanel.setBackground(Color.RED);

                desktopPane.setOpaque(false);
                desktopPane.setBounds(0, 0, 600, 400);
                jPanel.setBounds(0, 0, 600, 400);

                button = new JButton("change");
                button.setBounds(100, 100, 200, 60);


                layeredPane.add(button, new Integer(2), 0);
                layeredPane.add(desktopPane, new Integer(1), 0);
                layeredPane.add(jPanel, new Integer(0), 0);
            }


        add(layeredPane);

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                System.out.println("Key typed: " + e.getKeyCode() + ": " + e.getKeyChar());
                if (e.getKeyChar() == KeyEvent.VK_TAB) {

                    frame.show();
                }
            }

        });

        ActionListener[] actionListeners = button.getActionListeners();
        if (actionListeners != null){
            for (ActionListener actionListener : actionListeners)
                button.removeActionListener(actionListener);
        }

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isUndecorated()) {
                    remove(layeredPane);
                    invalidate();
                    TestView view = new TestView(false);
//                    view.setLocation(getX() - borderSize, getY() - titleBarSize);
                    TestView.this.dispose();
                }else{
                    remove(layeredPane);
                    invalidate();
                    TestView newView = new TestView(true);
//                    newView.setLocation(getX() + borderSize, getY() + titleBarSize);
                    TestView.this.dispose();
                }
            }
        });

        layeredPane.setVisible(true);
        addComponentListener(this);

        setVisible(true);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        System.out.println("Resized: " + getWidth() + " : " + getHeight());
        System.out.println("Resized: " + getContentPane().getWidth() + " : " + getContentPane().getHeight());

        if (!isUndecorated()){
            borderSize = (getWidth() - getContentPane().getWidth())/2;
            titleBarSize = getHeight() - getContentPane().getHeight() - borderSize;
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
