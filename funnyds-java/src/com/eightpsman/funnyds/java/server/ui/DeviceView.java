package com.eightpsman.funnyds.java.server.ui;

import com.eightpsman.funnyds.core.*;
import com.eightpsman.funnyds.java.DeviceDrawer;
import com.eightpsman.funnyds.java.server.ServerManager;
import com.eightpsman.funnyds.util.ArrangeDeviceUtil;
import com.eightpsman.funnyds.java.JavaUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class DeviceView extends JPanel implements ActionListener{

    String[] arrangerName = new String[]{"Horizontal", "Vertical", "Horizontal Box", "Vertical Box"};
    ArrangeDeviceUtil.DeviceArranger[] arranger = new ArrangeDeviceUtil.DeviceArranger[]{
            new ArrangeDeviceUtil.HorizontalArranger(),
            new ArrangeDeviceUtil.VerticalArranger(),
            new ArrangeDeviceUtil.HorizontalBoxArrranger(),
            new ArrangeDeviceUtil.HorizontalBoxArrranger.VerticalBoxArrranger()
    };
    JComboBox<String> arrangerChooser;

    ServerManager manager;

    DeviceDrawer drawer;
    public DeviceView(ServerManager manager){
        super();
        this.manager = manager;

        setLayout(new BorderLayout());

        add(drawer = new DeviceDrawer(manager), BorderLayout.CENTER);

        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPanel.add(arrangerChooser = new JComboBox<String>(arrangerName));
        JButton btArrange;
        jPanel.add(btArrange = newButton("Arrange"));

        if (manager.getMode() == Constants.MODE_PRES)
            jPanel.add(newButton("Load image"));
        add(jPanel, BorderLayout.SOUTH);

        /** HACK to change client view's location */
        btArrange.addMouseListener(new MouseAdapter() {
            Timer timer = new Timer(3000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    arrangeAndAskClientChangeLocation();
                }
            });
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                timer.start();
                timer.setRepeats(false);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                timer.stop();
            }
        });
    }

    public void destroy(){
        drawer.destroy();
    }

    public JButton newButton(String name){
        JButton jButton = new JButton(name);
        jButton.setName(name);
        jButton.addActionListener(this);
        return jButton;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JButton jButton = (JButton) event.getSource();
        if (jButton.getName().equals("Load image")){
            loadImage();
        }else if (jButton.getName().equals("Arrange")){
            arrange();
        }
    }

    private void arrange(){
        int index = arrangerChooser.getSelectedIndex();
        if (index >= 0 && index < arranger.length){
            arranger[index].arrange(manager.getDevices());
            for (int i=0; i<manager.getDevices().size(); i++)
                manager.updateDevice(manager.getDevice(i));
        }
    }


    private void arrangeAndAskClientChangeLocation(){
        arrange();
        manager.sendMessageToClients(Constants.HACK_MSG_SET_LOCATION_ON_SCREEN);
        System.out.println("Invoked arrange and ask client change location");
    }

    public void loadImage(){
        final JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                if (name.endsWith(".jpg") || name.endsWith(".png"))
                    return true;
                return false;
            }
            @Override
            public String getDescription() {
                return "PNG or JPG image";
            }
        });
        int result = fc.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(file);
                byte[] data = JavaUtil.getImageData(image);
                if (data != null){
                    Bound bound = manager.getBound();
                    ImageActorData actor = new ImageActorData(data, manager.getDpi(),
                            bound.centerX(), bound.centerY(),
                            bound.width(), bound.height(), 0f, 0f);
                    actor.id = -1;
                    manager.newImageActor(0, actor);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

}
