package com.eightpsman.funnyds.socket;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.core.ImageActorData;
import com.eightpsman.funnyds.rmi.ClientCallback;
import com.eightpsman.funnyds.rmi.ServerRemote;
import com.eightpsman.funnyds.util.SocketMessage;

import java.rmi.RemoteException;
import java.util.List;

/**
 * FunnyDS
 * Created by 8psman on 11/20/2014.
 * Email: 8psman@gmail.com
 */
public class SocketServerRemote implements ServerRemote{

    SocketClientHandler clientHandler;

    public SocketServerRemote(SocketClientHandler clientHandler){
        this.clientHandler = clientHandler;
    }

    /** don't user */
    @Override
    public List<Device> getDevices(int clientId){
        return null;
    }

    /** don't user */
    @Override
    public List<Actor> getActors(int clientId){
        return null;
    }

    /** don't user */
    @Override
    public Device joinDevice(int mode, Device device, ClientCallback callback){
        return null;
    }

    @Override
    public void disconnect(int id){
        clientHandler.send(new SocketMessage(SocketMessage.MSG_DISCONNECT, id));
    }

    @Override
    public void updateDevice(int client, Device device){
        clientHandler.send(new SocketMessage(SocketMessage.MSG_UPDATE_DEVICE, client, device.clone()));
    }

    @Override
    public void updateActor(int clientId, Actor actor) throws RemoteException {
        clientHandler.send(new SocketMessage(SocketMessage.MSG_UPDATE_ACTOR, clientId, actor));
    }

    @Override
    public void newImageActor(int clientId, ImageActorData data) throws RemoteException {
        clientHandler.send(new SocketMessage(SocketMessage.MSG_NEW_ACTOR, clientId, data));
    }

    @Override
    public void readyForGame(int clientId) throws RemoteException {

    }
}
