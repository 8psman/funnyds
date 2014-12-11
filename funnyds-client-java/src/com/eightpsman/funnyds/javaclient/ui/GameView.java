package com.eightpsman.funnyds.javaclient.ui;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.BarrierActor;
import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.GameEventCallback;
import com.eightpsman.funnyds.java.DeviceDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class GameView extends GenericView implements GameEventCallback, ActionListener{

    DeviceDrawer drawer;
    BarrierActor barrier;

    JButton readyButton;

    public GameView(ClientManager manager){
        super(manager);

        JLayeredPane container = new JLayeredPane();

        int width = (int)device.width;
        int height = (int)device.height;
        drawer = new DeviceDrawer(manager, false);
        drawer.setBounds(0, 0, width, height);

        readyButton = new JButton("READY");
        readyButton.setBounds(width/2 -40 , height/2 - 20, 120, 40);
        readyButton.addActionListener(this);
        readyButton.setFocusable(false);

        container.add(drawer, new Integer(0), 0);
        container.add(readyButton, new Integer(1), 0);

        add(container, BorderLayout.CENTER);

        setVisible(true);

        drawer.addMouseMotionListener(mouseHandler);
        drawer.addMouseListener(mouseHandler);

        manager.setGameCallback(this);

    }

    MouseAdapter mouseHandler = new MouseAdapter() {
        int startX;
        int startY;
        @Override
        public void mousePressed(MouseEvent event) {
            super.mousePressed(event);
            if (SwingUtilities.isRightMouseButton(event)){
                startX = event.getX();
                startY = event.getY();
            }else{
                updateBarrier(event);
            }
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            super.mouseDragged(event);
            if (SwingUtilities.isRightMouseButton(event)){
                GameView.this.setLocation(event.getXOnScreen() - startX, event.getYOnScreen() - startY);
            }else{
                updateBarrier(event);
            }

        }
    };

    public void updateBarrier(MouseEvent event){
        Point point = new Point(event.getX(), event.getY());
        drawer.inverseTransform(point);
        barrier.px = (float)point.x / manager.getDpi();
        barrier.calc(manager.getDpi());
        manager.updateActor(barrier);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        drawer.destroy();
    }

    @Override
    protected void onKickOut(){
        onDestroy();
        this.dispose();
        System.exit(0);
    }

    @Override
    protected void onDisconnected(){
        onDestroy();
        this.dispose();
        System.exit(0);
    }

    public BarrierActor getMyBarrier(){
        for (Actor actor : manager.getActors())
            if (actor instanceof BarrierActor &&  device.isInDevice(actor.x, actor.y)){
                return (BarrierActor)actor;
            }
        return null;
    }

    @Override
    public void startGame() {
        readyButton.setVisible(false);
        barrier = getMyBarrier();
    }

    @Override
    public void gameOver(boolean isWin) {
        readyButton.setVisible(true);
        readyButton.setEnabled(true);
        if (isWin){
            readyButton.setText("YOU WIN");
        }else {
            readyButton.setText("YOU LOOSE");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == readyButton){
            readyButton.setText("WAITING ...");
            readyButton.setEnabled(false);
            manager.localPlayerReady();
        }
    }
}
