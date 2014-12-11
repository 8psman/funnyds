package com.eightpsman.funnyds.core;

/**
 * FunnyDS
 * Created by 8psman on 11/27/2014.
 * Email: 8psman@gmail.com
 */
public class BarrierActor extends Actor{

    public BarrierActor(int dpi, float x, float y, float width, float height, float veloc_x, float veloc_y) {
        super(dpi, x, y, width, height, veloc_x, veloc_y);
    }

    public BarrierActor(float px, float py, float pw, float ph, float pveloc_x, float pveloc_y) {
        super(px, py, pw, ph, pveloc_x, pveloc_y);
    }
}
