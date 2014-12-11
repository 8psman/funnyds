package com.eightpsman.funnyds.core;

import java.util.List;
import java.util.TimerTask;

/**
 * FunnyDS
 * Created by 8psman on 11/21/2014.
 * Email: 8psman@gmail.com
 */
public class UpdateActorTask extends TimerTask{
    List<Device> devices;
    List<Actor> actors;
    float deltaTime;
    int dpi;

    public UpdateActorTask(float deltaTime, int dpi, List<Device> devices, List<Actor> actors){
        this.devices = devices;
        this.dpi = dpi;
        this.actors  = actors;
        this.deltaTime = deltaTime;
    }

    @Override
    public void run() {
        for (Actor actor : actors){
            actor.update(deltaTime);

            if (!isInPoolArea(actor)){
                continue;
            }

            actor.moveNext(deltaTime);
            if (isInPoolArea(actor)){
                actor.calc(dpi);
                continue;
            }
            actor.moveBack(deltaTime);

            actor.revertY();
            actor.moveNext(deltaTime);
            if (isInPoolArea(actor)){
                actor.calc(dpi);
                continue;
            }
            actor.moveBack(deltaTime);
            actor.revertY();

            actor.revertX();
            actor.moveNext(deltaTime);
            if (isInPoolArea(actor)){
                actor.calc(dpi);
                continue;
            }
            actor.moveBack(deltaTime);
            actor.revertX();

            actor.revertX();
            actor.revertY();
            actor.moveNext(deltaTime);

            actor.calc(dpi);
        }
    }

    private boolean isInPoolArea(Actor actor){
        for (Device device : devices)
            if (device.isInDevice(actor))
                return true;
        return false;
    }

}
