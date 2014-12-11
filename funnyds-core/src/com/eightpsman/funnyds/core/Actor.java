package com.eightpsman.funnyds.core;

import java.io.Serializable;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public class Actor extends IndependenceDeviceActor implements Serializable{

    public int id;
    public int owner;
    public float pveloc_x;
    public float pveloc_y;

    transient public float veloc_x;
    transient public float veloc_y;

    public Actor(float px, float py, float pw, float ph, float pveloc_x, float pveloc_y){
        super(px, py, pw, ph);
        this.pveloc_x = pveloc_x;
        this.pveloc_y = pveloc_y;
    }

    public Actor(int dpi, float x, float y, float width, float height, float veloc_x, float veloc_y){
        super(dpi, x, y, width, height);
        this.id = -1;
        this.veloc_x = veloc_y;
        this.veloc_y = veloc_x;

        this.pveloc_x = veloc_x / dpi;
        this.pveloc_y = veloc_y / dpi;
    }

    @Override
    public void calc(int dpi) {
        super.calc(dpi);
        veloc_x = pveloc_x * dpi;
        veloc_y = pveloc_y * dpi;

        left    = x - width/2;
        top     = y - height/2;
        right   = x + width/2;
        bottom  = y + height/2;
    }

    @Override
    public void calcPhysicalSize() {
        super.calcPhysicalSize();
        pveloc_x = veloc_x / dpi;
        pveloc_y = veloc_y / dpi;
    }

    public void update(float delta){

    }

    /** move next one step with current velocity */
    public void moveNext(float deltaTime){
        px += pveloc_x * deltaTime;
        py += pveloc_y * deltaTime;
    }

    /** move back one step width current velocity */
    public void moveBack(float deltaTime){
        px -= pveloc_x * deltaTime;
        py -= pveloc_y * deltaTime;
    }

    public void revertX(){
        pveloc_x = - pveloc_x;
    }

    public void revertY(){
        pveloc_y = - pveloc_y;
    }

    /** Each actor has one direction of x
     * and it need to flip when it change direction
     * @return true if need to flip
     */
    public boolean isFlip(){
        return false;
    }

    /** actor need to be sent over network
     * but some of its extend has some variable that not be serializable
     * so it need to generate an actor for that purpose
     * @return
     */
    public Actor getActor(){
        return this;
    }

}
