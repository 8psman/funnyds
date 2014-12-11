package com.eightpsman.funnyds.core;

import java.io.Serializable;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public class Device extends IndependenceDeviceActor implements Serializable{

    public int id;
    public String name;
    public String os;
    public String ip;

    transient public boolean isActive = false;

    public Device(float px, float py, float pw, float ph, int dpi){
        super(px, py, pw, ph);
        this.dpi = dpi;
    }

    public Device(int dpi, float x, float y, float w, float h){
        super(dpi, x, y, w, h);
    }

    public boolean isInDevice(float px, float py){
        if (px >= x - width/2 && px <= x + width/2 && py >= y - height/2 && py <= y + height/2)
            return true;
        return false;
    }

    public boolean isInDevice(Actor actor){
        boolean isXOk =
                (actor.pRight() >= pLeft() && actor.pRight() <= pRight() && actor.pveloc_x > 0) ||
                        (actor.pLeft() <= pRight() && actor.pLeft() >= pLeft() && actor.pveloc_x < 0);
        boolean isYOk =
                (actor.pBottom() >= pTop() && actor.pBottom() <= pBottom() && actor.pveloc_y > 0) ||
                        (actor.pTop() <= pBottom() && actor.pTop() >= pTop() && actor.pveloc_y < 0);
        return (isXOk && isYOk);
    }

//    public boolean isInDevice(Actor actor){
//        boolean isXOk =
//                (actor.right() >= left() && actor.right() <= right() && actor.veloc_x > 0) ||
//                (actor.left() <= right() && actor.left() >= left() && actor.veloc_x < 0);
//        boolean isYOk =
//                (actor.bottom() >= top() && actor.bottom() <= bottom() && actor.veloc_y > 0) ||
//                (actor.top() <= bottom() && actor.top() >= top() && actor.veloc_y < 0);
//        return (isXOk && isYOk);
//    }

//    public float left(){
//        return x - w/2;
//    }
//    public float right(){
//        return x + w/2;
//    }
//    public float top(){
//        return y - h/2;
//    }
//    public float bottom(){
//        return y + h/2;
//    }
//
//    public float left(int dpi){
//        return x - w(dpi)/2;
//    }
//    public float right(int dpi){
//        return x + w(dpi)/2;
//    }
//    public float top(int dpi){
//        return y - h(dpi)/2;
//    }
//    public float bottom(int dpi){
//        return y + h(dpi)/2;
//    }
//
//    public float realWidth(){
//        return (float)w / dpi;
//    }
//
//    public float realHeight(){
//        return (float)h / dpi;
//    }
//
//    public int w(int dpi){
//        return (int)(realWidth() * dpi);
//    }
//
//    public int h(int dpi){
//        return (int)(realHeight() * dpi);
//    }

    /** I don't know, but it need to create a copy of device before writing to stream */
    public Device clone(){
        Device device = new Device(px, py, pw, ph, dpi);
        device.id = id;
        device.name = name;
        device.os = os;
        device.ip = ip;
        return device;
    }
}
