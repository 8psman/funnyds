package com.eightpsman.funnyds.android.actor;

import android.graphics.Bitmap;
import com.eightpsman.funnyds.core.ImageActor;

/**
 * FunnyDS
 * Created by 8psman on 11/25/2014.
 * Email: 8psman@gmail.com
 */
public class AndroidImageActor extends ImageActor{

    public Bitmap bitmap;

    public AndroidImageActor(Bitmap bitmap, int dpi, float x, float y, float w, float h, float veloc_x, float veloc_y) {
        super(dpi, x, y, w, h, veloc_x, veloc_y);
        this.bitmap = bitmap;
    }

    public AndroidImageActor(Bitmap bitmap, float px, float py, float pw, float ph, float pveloc_x, float pveloc_y) {
        super(px, py, pw, ph, pveloc_x, pveloc_y);
        this.bitmap = bitmap;
    }
}
