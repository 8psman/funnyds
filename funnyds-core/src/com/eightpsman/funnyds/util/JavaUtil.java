package com.eightpsman.funnyds.util;

import com.eightpsman.funnyds.core.Device;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.*;

/**
 * FunnyDS
 * Created by 8psman on 11/22/2014.
 * Email: 8psman@gmail.com
 */
public class JavaUtil {

    /**
     * change look and feel to system look and feel
     */
    public static void changeLookAndFeel(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static int getScreenResolution(){
        return Toolkit.getDefaultToolkit().getScreenResolution();
    }

    public static byte[] getImageData(BufferedImage image){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1000);
        try {
            ImageIO.write(image, "png", outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public static String getHostIP(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    public static Device getLocalDevice() {
        int width 	= Toolkit.getDefaultToolkit().getScreenSize().width;
        int height  = Toolkit.getDefaultToolkit().getScreenSize().height;
        int dpi 	= Toolkit.getDefaultToolkit().getScreenResolution();

        Device device = new Device(dpi, 0f, 0f, (float)width, (float)height);

        device.name = System.getProperty("user.name", "unknown");
        device.os	= System.getProperty("os.name", "unknown");
        try {
            device.ip	= InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            device.ip = "unknown";
            e.printStackTrace();
        }
        return device;
    }


    /** launch a class in other process */
    public static int executeClass(Class klass, boolean isWait, String... params) throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = klass.getCanonicalName();

        String[] commands = new String[4 + params.length];

        commands[0] = javaBin;
        commands[1] = "-cp";
        commands[2] = classpath;
        commands[3] = className;
        for (int i=0; i<params.length; i++)
            commands[i+4] = params[i];

        ProcessBuilder builder = new ProcessBuilder(
                commands);

        Process process = builder.start();
        int exitValue = 0;
        if (isWait){
            process.waitFor();
            exitValue = process.exitValue();
        }
        return exitValue;
    }
}
