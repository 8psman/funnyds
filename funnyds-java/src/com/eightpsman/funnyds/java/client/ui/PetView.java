package com.eightpsman.funnyds.java.client.ui;

import com.eightpsman.funnyds.java.Resources;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class PetView extends JPanel{

    JList<ImageIcon> petList;
    DefaultListModel<ImageIcon> petModel;

    List<BufferedImage> fishImage;

    public PetView(){
        super();
        setLayout(new BorderLayout());

        petList = new JList<ImageIcon>();
        petModel = new DefaultListModel<ImageIcon>();
        petList.setModel(petModel);
        petList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        fishImage = new ArrayList<BufferedImage>();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<5;i++){
                    BufferedImage bufferedImage = Resources.loadImage(getClass(), "/data/fish_" + i +".png");
                    ImageIcon icon = new ImageIcon(bufferedImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
                    fishImage.add(bufferedImage);
                    petModel.addElement(icon);
                }

            }
        });

        petList.setCellRenderer(new PetCellRenderer());

        JScrollPane jScrollPane = new JScrollPane(petList);
        jScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(3, 0));
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(jScrollPane);
        setFocusable(true);

        petList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setVisible(false);
            }
        });
    }

    public void addImageIcon(BufferedImage image){
        fishImage.add(image);
        ImageIcon icon = new ImageIcon(image.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        petModel.addElement(icon);
    }

    public BufferedImage getBufferedImage(){
        int index = petList.getSelectedIndex();
        if (index < 0 || index >= fishImage.size()){
            index = 0;
            petList.setSelectedIndex(0);
        }
        return fishImage.get(index);
    }

    class PetCellRenderer extends DefaultListCellRenderer{
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel jPanel = new JPanel(new BorderLayout());
            jPanel.setPreferredSize(new Dimension(50, 50));
            ImageIcon icon = (ImageIcon) value;


            JLabel jLabel = new JLabel(icon);
            jLabel.setOpaque(true);
            jLabel.setFocusable(true);
            jLabel.getInsets().set(5, 5 , 5, 5);
            jLabel.setPreferredSize(new Dimension(48, 48));

            if (isSelected){
                jLabel.setBackground(Color.cyan);
            }else{
                jLabel.setBackground(Color.white);
            }
            return jLabel;
        }

    }
}
