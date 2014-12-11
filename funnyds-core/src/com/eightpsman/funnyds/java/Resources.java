package com.eightpsman.funnyds.java;

import com.eightpsman.funnyds.core.ClientManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Resources {

	public static URL resource(String url){
		return ClientManager.class.getResource(url);
	}

	public static BufferedImage loadImage(Class cls, String path){
		try {
			return ImageIO.read(cls.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static HashMap<String, ImageIcon> iconMap = new HashMap<String, ImageIcon>();

	public static ImageIcon loadImageIcon(Class cls, String path){
		if (iconMap.containsKey(path))
			return iconMap.get(path);
		ImageIcon icon = new ImageIcon(cls.getResource(path));
		if (icon != null){
			iconMap.put(path, icon);
		}
		return icon;


	}
}
