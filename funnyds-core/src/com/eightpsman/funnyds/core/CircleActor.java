package com.eightpsman.funnyds.core;

/**
 * FunnyDS
 * Created by 8psman on 11/21/2014.
 * Email: 8psman@gmail.com
 */
public class CircleActor extends Actor{

    public CircleActor(int dpi, float x, float y, float radius, float veloc_x, float veloc_y){
        super(dpi, x, y, 2 * radius, 2 * radius, veloc_x, veloc_y);
    }
}
