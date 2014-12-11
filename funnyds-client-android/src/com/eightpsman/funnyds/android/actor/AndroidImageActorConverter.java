package com.eightpsman.funnyds.android.actor;

import com.eightpsman.funnyds.android.AndroidUtils;
import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.ImageActorConverter;
import com.eightpsman.funnyds.core.ImageActorData;

/**
 * FunnyDS
 * Created by 8psman on 11/25/2014.
 * Email: 8psman@gmail.com
 */
public class AndroidImageActorConverter implements ImageActorConverter{

    @Override
    public Actor getActor(ImageActorData imageData) {
        AndroidImageActor imageActor = new AndroidImageActor(
                AndroidUtils.createBitmapFromByteArray(imageData.data),
                imageData.px, imageData.py, imageData.pw, imageData.ph,
                imageData.pveloc_x, imageData.pveloc_y
        );

        imageActor.dpi     = imageData.dpi;
        imageActor.id      = imageData.id;
        imageActor.owner   = imageData.owner;

        return imageActor;
    }

    @Override
    public ImageActorData getImageData(Actor imageActor) {
        AndroidImageActor androidImageActor = (AndroidImageActor) imageActor;
        ImageActorData imageData = new ImageActorData(
                AndroidUtils.convertBitmapToByte(androidImageActor.bitmap),
                androidImageActor.px, androidImageActor.py,
                androidImageActor.pw, androidImageActor.ph,
                androidImageActor.pveloc_x, androidImageActor.pveloc_y
        );
        imageData.dpi     = androidImageActor.dpi;
        imageData.id      = androidImageActor.id;
        imageData.owner   = androidImageActor.owner;

        return imageData;
    }
}
