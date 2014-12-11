package com.eightpsman.funnyds.java;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.ImageActor;
import com.eightpsman.funnyds.core.ImageActorData;

import java.awt.image.BufferedImage;

/**
 * FunnyDS
 * Created by 8psman on 11/25/2014.
 * Email: 8psman@gmail.com
 */
public class JavaImageActor extends ImageActor {

    transient public BufferedImage image;

    public JavaImageActor(BufferedImage image, float px, float py, float pw, float ph, float pveloc_x, float pveloc_y){
        super(px, py, pw, ph, pveloc_x, pveloc_y);
        this.image = image;
    }

    public JavaImageActor(BufferedImage image, int dpi, float x, float y, float w, float h, float veloc_x, float veloc_y) {
        super(dpi, x, y, w, h, veloc_x, veloc_y);
        this.image = image;
    }

    public ImageActorData getImageActorData(){
        ImageActorData imageData = new ImageActorData(JavaUtil.getImageData(image), dpi, x, y, width, height, veloc_x, veloc_y);
        imageData.id      = id;
        imageData.owner   = owner;
        return imageData;
    }

    @Override
    public Actor getActor() {
        Actor actor = new Actor(px, py, pw, ph, pveloc_x, pveloc_y);
        actor.dpi   = dpi;
        actor.id    = id;
        actor.owner = owner;
        return actor;
    }
}


