package com.eightpsman.funnyds.java;

import com.eightpsman.funnyds.core.*;
import com.eightpsman.funnyds.util.JavaUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FunnyDS
 * Created by 8psman on 11/25/2014.
 * Email: 8psman@gmail.com
 */
public class JavaImageActorConverter implements ImageActorConverter{

    @Override
    public Actor getActor(ImageActorData imageData) {
        BufferedImage image = null;
        InputStream in = new ByteArrayInputStream(imageData.data);
        try {
            image = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image == null) return imageData.getDummyActor();

        JavaImageActor imageActor =
                new JavaImageActor(image,
                        imageData.px, imageData.py,
                        imageData.pw, imageData.ph,
                        imageData.pveloc_x, imageData.pveloc_y);
        imageActor.dpi     = imageData.dpi;
        imageActor.id      = imageData.id;
        imageActor.owner   = imageData.owner;
        return imageActor;
    }

    @Override
    public ImageActorData getImageData(Actor imageActor) {
        JavaImageActor javaImageActor = (JavaImageActor) imageActor;
        ImageActorData imageData = new ImageActorData(
                JavaUtil.getImageData(javaImageActor.image),
                javaImageActor.px, javaImageActor.py,
                javaImageActor.pw, javaImageActor.ph,
                javaImageActor.pveloc_x, javaImageActor.pveloc_y
        );
        imageData.dpi     = javaImageActor.dpi;
        imageData.id      = javaImageActor.id;
        imageData.owner   = javaImageActor.owner;
        return imageData;
    }
}
