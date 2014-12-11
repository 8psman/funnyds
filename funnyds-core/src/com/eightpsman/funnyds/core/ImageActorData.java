package com.eightpsman.funnyds.core;

import java.awt.image.BufferedImage;

/**
 * FunnyDS
 * Created by 8psman on 11/25/2014.
 * Email: 8psman@gmail.com
 */
public class ImageActorData extends ImageActor{

    public byte[] data;

    public ImageActorData(byte[] data, float px, float py, float pw, float ph, float pveloc_x, float pveloc_y){
        super(px, py, pw, ph, pveloc_x, pveloc_y);
        this.data = data;
    }

    public ImageActorData(byte[] data, int dpi, float x, float y, float w, float h, float veloc_x, float veloc_y) {
        super(dpi, x, y, w, h, veloc_x, veloc_y);
        this.data = data;
    }

    public Actor getDummyActor(){
        Actor actor = new Actor(dpi, x, y, width, height, veloc_x, veloc_y);
        actor.id = id;
        actor.owner = owner;
        return actor;
    }
}
