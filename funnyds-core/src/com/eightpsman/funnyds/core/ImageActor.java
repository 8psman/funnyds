package com.eightpsman.funnyds.core;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class ImageActor extends Actor{

    public ImageActor(float px, float py, float pw, float ph, float pveloc_x, float pveloc_y){
        super(px, py, pw, ph, pveloc_x, pveloc_y);
    }

    public ImageActor(int dpi, float x, float y, float pw, float ph, float veloc_x, float veloc_y){
        super(dpi, x, y, pw, ph, veloc_y, veloc_x);
    }

    @Override
    public void update(float delta){
        super.update(delta);

    }

    @Override
    public boolean isFlip(){
        return pveloc_x > 0;
    }

}
