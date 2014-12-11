package com.eightpsman.funnyds.javaclient.ui;

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
public class OptionView extends JScrollPane implements ActionListener{

    Integer[] sizes = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    JComboBox<Integer> sizeChooser;
    JComboBox<Integer> velocChooser;
    JButton pickImage;
    OptionViewCallback callback;

    public OptionView(OptionViewCallback callback){
        super();
        this.callback = callback;

        JPanel container = new JPanel();
        container.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));

        sizeChooser = new JComboBox<Integer>(sizes);
        sizeChooser.setSelectedItem(5);

        velocChooser = new JComboBox<Integer>(sizes);
        velocChooser.setSelectedItem(5);

        container.add(new JLabel("Size"));
        container.add(sizeChooser);

        container.add(new JLabel("Velocity"));
        container.add(velocChooser);

        container.add(pickImage = new JButton("Pick my own"));
        setViewportView(container);

        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        getHorizontalScrollBar().setPreferredSize(new Dimension(0, 3));

        pickImage.addActionListener(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setVisible(false);
            }
        });
    }

    public int getCurrentSize(){
        return (Integer)(sizeChooser.getSelectedItem());
    }

    public int getCurrentVelocity(){
        return (Integer) velocChooser.getSelectedItem();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pickImage){
            pickImage();
        }
    }

    public void pickImage(){
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
                callback.addImageIcon(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OptionViewCallback{
        void addImageIcon(BufferedImage image);
    }
}
