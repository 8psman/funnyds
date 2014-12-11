package com.eightpsman.funnyds.server;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.core.ImageActorData;
import com.eightpsman.funnyds.rmi.ClientCallback;
import com.eightpsman.funnyds.util.SocketMessage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * FunnyDS
 * Created by 8psman on 11/20/2014.
 * Email: 8psman@gmail.com
 */
public class SocketClientCallback implements ClientCallback{

    public SocketClientHandler clientHandler;

    public SocketClientCallback(SocketClientHandler handler){
        this.clientHandler = handler;
    }

    @Override
    public boolean isAlive(){
        return clientHandler.isAlive();
    }

    @Override
    public void kickout(){
        clientHandler.send(new SocketMessage(SocketMessage.MSG_KICKOUT));
    }

    @Override
    public boolean newDevice(Device device){
        clientHandler.send(new SocketMessage(SocketMessage.MSG_UPDATE_DEVICE, device));
        return true;
    }

    @Override
    public boolean removeDevice(int id){
        clientHandler.send(new SocketMessage(SocketMessage.MSG_REMOVE_DEVICE, id));
        return true;
    }

    @Override
    public boolean updateDevice(Device device){
        clientHandler.send(new SocketMessage(SocketMessage.MSG_UPDATE_DEVICE, device.clone()));
        return true;
    }

    @Override
    public boolean updateActor(Actor actor) throws RemoteException {
        clientHandler.send(new SocketMessage(SocketMessage.MSG_UPDATE_ACTOR, actor.getActor()));
        return true;
    }

    @Override
    public boolean removeActor(int id) throws RemoteException {
        clientHandler.send(new SocketMessage(SocketMessage.MSG_REMOVE_ACTOR, id));
        return true;
    }

    @Override
    public boolean synActor(List<Actor> actors) throws RemoteException {
        List<Actor> serActors = new ArrayList<Actor>();
        for (Actor actor : actors)
            serActors.add(actor.getActor());
        clientHandler.send(new SocketMessage(SocketMessage.MSG_SYNC_ACTOR, serActors));
        return true;
    }

    @Override
    public boolean newImageActor(ImageActorData data) throws RemoteException {
        clientHandler.send(new SocketMessage(SocketMessage.MSG_NEW_ACTOR, data));
        return true;
    }

    @Override
    public void doSomeStuff(int msg) throws RemoteException {
        clientHandler.send(new SocketMessage(SocketMessage.MSG_DO_SOME_STUFF, msg));
    }

    @Override
    public void startGame() throws RemoteException {

    }

    @Override
    public void gameOver(boolean isWin) throws RemoteException {

    }
}
