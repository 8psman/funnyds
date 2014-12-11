package com.eightpsman.funnyds.core;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * FunnyDS
 * Created by 8psman on 11/20/2014.
 * Email: 8psman@gmail.com
 */
public class BasicManager {

    private List<Device> devices = new CopyOnWriteArrayList<Device>();
    private List<Actor> actors   = new CopyOnWriteArrayList<Actor>();
    private Bound bound = new Bound();

    private Timer updateActorTimer;

    /**
     * Screen resolution dots per inch
     * take the default value if not set
     */
    private int dpi = 96;

    protected BasicManager(){

    }

    public void setDpi(int dpi){
        this.dpi = dpi;
    }

    public int getDpi(){
        return dpi;
    }

    protected void init(){
        devices.clear();
        actors.clear();
        updateActorTimer = new Timer();
        UpdateActorTask updateActorTask = new UpdateActorTask(Constants.DELTA_TIME, dpi, devices, actors);
        updateActorTimer.scheduleAtFixedRate(updateActorTask, 0, (long)(Constants.DELTA_TIME * 1000));
    }

    public void shutdown(){
        devices.clear();
        actors.clear();
        if (updateActorTimer != null) updateActorTimer.cancel();
    }

    protected void addDevice(List<Device> devices){
        for (Device device : devices)
            device.calc(dpi);
        this.devices.addAll(devices);
        updateDeviceChange();
    }

    protected void addActor(List<Actor> actors){
        for (Actor actor : actors)
            actor.calc(dpi);
        this.actors.addAll(actors);
    }

    protected void addDevice(Device device){
        device.calc(dpi);
        this.devices.add(device);
        updateDeviceChange();
    }

    protected void addActor(Actor actor){
        actor.calc(dpi);
        this.actors.add(actor);
    }

    public List<Device> getDevices(){
        return devices;
    }

    public Device getDevice(int index){
        return devices.get(index);
    }

    public List<Actor> getActors(){
        return actors;
    }

    public Actor getActor(int index){
        return actors.get(index);
    }

    public Device getDeviceByID(int id){
        for (int i=0; i<devices.size(); i++)
            if (devices.get(i).id == id)
                return devices.get(i);
        return null;
    }

    public Actor getActorByID(int id){
        for (int i=0; i<actors.size(); i++)
            if (actors.get(i).id == id)
                return actors.get(i);
        return null;
    }

    public void removeDevice(int id){
        for (int i=0; i<devices.size(); i++)
            if (devices.get(i).id == id){
                devices.remove(i);
                break;
            }
        updateDeviceChange();
    }

    public void removeActor(int id){
        for (int i=0; i<actors.size(); i++)
            if (actors.get(i).id == id){
                actors.remove(i);
                break;
            }
    }
    
    public void updateDevice(Device device) {
        boolean isFound = false;
        for (Device dv : devices)
            if (dv.id == device.id) {
                dv.px = device.px;
                dv.py = device.py;
                dv.pw = device.pw;
                dv.ph = device.ph;
                dv.calc(dpi);
                isFound = true;
            }
        if (!isFound) addDevice(device);
        updateDeviceChange();
    }

    public void updateActor(Actor actor){
        boolean isFound = false;
        for (Actor at : getActors())
            if (at.id == actor.id){
                isFound = true;
                at.px        = actor.px;
                at.py        = actor.py;
                at.pw        = actor.pw;
                at.ph        = actor.ph;
                at.pveloc_x  = actor.pveloc_x;
                at.pveloc_y  = actor.pveloc_y;
                at.calc(dpi);
            }
        if (!isFound)
            addActor(actor);
    }

    public Bound getBound(){
        return bound;
    }

    protected void updateDeviceChange(){
        if (devices.size() == 0) return;
        bound.left 		= devices.get(0).left;
        bound.right 	= devices.get(0).right;
        bound.top 	 	= devices.get(0).top;
        bound.bottom 	= devices.get(0).bottom;
        for (Device device : devices){
            if (device.left 	< bound.left) 	bound.left 		= device.left;
            if (device.top 	    < bound.top) 	bound.top 		= device.top;
            if (device.right 	> bound.right) 	bound.right 	= device.right;
            if (device.bottom   > bound.bottom) bound.bottom 	= device.bottom;
        }
    }
}
