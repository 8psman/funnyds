package com.eightpsman.funnyds.java.client.ui;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.ImageActorData;
import com.eightpsman.funnyds.java.DeviceDrawer;
import com.eightpsman.funnyds.util.AppUtils;
import com.eightpsman.funnyds.java.JavaUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class PlayView extends GenericView implements ControllerView.ControllerCallback, DeviceDrawer.DeviceDrawerCallback, OptionView.OptionViewCallback{

    DeviceDrawer drawer;
    ControllerView controllerView;
    MiniViewPort miniViewPort;
    PetView petView;
    OptionView optionView;

    public PlayView(ClientManager manager){
       super(manager);

        /** setup container */
        JLayeredPane container = new JLayeredPane();

        /** setup pool drawer */
        drawer = new DeviceDrawer(manager);
        drawer.setBounds(0, 0, (int)device.width, (int)device.height);
        drawer.setCallback(this);

        /** setup controller */
        controllerView = new ControllerView(this);
        controllerView.setBounds(0, 0, (int)device.width, 30);

        /** setup mini view port */
        miniViewPort = new MiniViewPort(manager);
        miniViewPort.setBounds((int)device.width - 50, (int)device.height - 50, 50, 50);

        /** setup pet view */
        petView = new PetView();
        petView.setBounds((int)device.width - 50, 0, 50, (int)device.height);

        /** setup option view */
        optionView = new OptionView(this);
        optionView.setBounds(0, (int)device.height - 30, (int)device.width, 30);

        /** add component */
        container.add(drawer,           new Integer(0), 0);
        container.add(miniViewPort,     new Integer(1), 0);
        container.add(petView,          new Integer(2), 0);
        container.add(controllerView,   new Integer(3), 0);
        container.add(optionView,       new Integer(4), 0);

        add(container, BorderLayout.CENTER);

        drawer.addMouseMotionListener(mouseHandler);

        setVisible(true);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        drawer.destroy();
        miniViewPort.destroy();
    }

    @Override
    public void onShowClientView() {

    }

    @Override
    public void onShowMiniView() {
        miniViewPort.setVisible(!miniViewPort.isVisible());
    }

    @Override
    public void onFullscreen() {
        drawer.setFullScreen();
    }

    @Override
    public void onFillscreen() {
        drawer.setFillScreen();
    }

    @Override
    public void onRotate() {
        float pw = device.pw;
        float ph = device.ph;
        device.pw = ph;
        device.ph = pw;
        device.calc(manager.getDpi());
        manager.updateDevice(device);
        PlayView view = new PlayView(manager);
        int lx = getX() + getWidth()/2 - getHeight()/2;
        int ly = getY() + getHeight()/2 - getWidth()/2;
        view.setLocation(lx, ly);
        this.dispose();
    }

    @Override
    public void onMoveTo(int x, int y) {
        setLocation(x, y);
    }

    @Override
    public int getLocationX() {
        return getX();
    }

    @Override
    public int getLocationY() {
        return getY();
    }

    @Override
    public void showOrHideInfo() {
        drawer.changeShowInfo();
    }

    @Override
    public void setKeepView(boolean isKeep) {
        drawer.setKeepView(isKeep);
    }

    @Override
    public void setAutoLocation(boolean isAutoLocation) {
        this.isAutoLocation = isAutoLocation;
        if (isAutoLocation){
            Point point = getLocationOnScreen();
            onLocationChange((int)point.getX(), (int)point.getY());
        }

    }

    MouseAdapter mouseHandler = new MouseAdapter(){
        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            controllerView.setVisible(false);
            petView.setVisible(false);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);

            if (e.getX() > device.width - 50){
                if (!petView.isVisible()){
                    petView.setVisible(true);
                    controllerView.setVisible(false);
                    optionView.setVisible(false);
                }

            }else{
                if (petView.isVisible())
                    petView.setVisible(false);
            }

            if (e.getY() < 30) {
                if (!controllerView.isVisible()) {
                    controllerView.setVisible(true);
                    petView.setVisible(false);
                    optionView.setVisible(false);
                }
            }
            else{
                if (controllerView.isVisible())
                    controllerView.setVisible(false);
            }

            if (e.getY() > device.height - 30) {
                if (!optionView.isVisible()) {
                    optionView.setVisible(true);
                    petView.setVisible(false);
                    controllerView.setVisible(false);
                }
            }else{
                if (optionView.isVisible())
                    optionView.setVisible(false);
            }
        }
    };

    @Override
    public Actor createActorAt(int x, int y) {
        ImageActorData actor = null;
        BufferedImage bufferedImage = petView.getBufferedImage();
        byte[] data = JavaUtil.getImageData(bufferedImage);
        if (data != null){
            int size = optionView.getCurrentSize();
            float height = AppUtils.getActorIndependenceSize(size) * device.dpi;
            float width = height * (float)bufferedImage.getWidth() / bufferedImage.getHeight();
            float veloc = AppUtils.getActorIndependenceVelocity(optionView.getCurrentVelocity()) * manager.getDpi();
            actor = new ImageActorData(data, device.dpi, x, y, width, height, veloc, veloc);
            manager.newImageActor(actor);
        }
        return actor;
    }

    @Override
    public void addImageIcon(BufferedImage image) {
        petView.addImageIcon(image);
    }
}
