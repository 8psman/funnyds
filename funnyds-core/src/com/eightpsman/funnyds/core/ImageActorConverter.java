package com.eightpsman.funnyds.core;

/**
 * FunnyDS
 * Created by 8psman on 11/25/2014.
 * Email: 8psman@gmail.com
 */
public interface ImageActorConverter {

    Actor getActor(ImageActorData actorData);
    ImageActorData getImageData(Actor imageActor);
}
