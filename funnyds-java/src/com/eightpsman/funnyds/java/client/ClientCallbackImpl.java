package com.eightpsman.funnyds.java.client;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.core.ImageActorData;
import com.eightpsman.funnyds.rmi.ClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * FunnyDS
 * Created by 8psman on 11/20/2014.
 * Email: 8psman@gmail.com
 */
public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback{

    ClientManager manager;
    public ClientCallbackImpl(ClientManager manager) throws RemoteException {
        super();
        this.manager = manager;
    }

    @Override
    public boolean isAlive() throws RemoteException {
        return true;
    }

    @Override
    public void kickout() throws RemoteException {
        manager.kickOut();
    }

    @Override
    public boolean newDevice(Device device) throws RemoteException {
        manager.remoteUpdateDevice(device);
        return true;
    }

    @Override
    public boolean removeDevice(int id) throws RemoteException {
        manager.remoteRemoveDevice(id);
        return true;
    }

    @Override
    public boolean updateDevice(Device device) throws RemoteException {
        manager.remoteUpdateDevice(device);
        return true;
    }

    @Override
    public boolean updateActor(Actor actor) throws RemoteException {
        return manager.remoteUpdateActor(actor);
    }

    @Override
    public boolean newImageActor(ImageActorData data) throws RemoteException {
        manager.remoteNewImageActor(data);
        return true;
    }

    @Override
    public boolean removeActor(int id) throws RemoteException {
        manager.removeActor(id);
        return true;
    }

    @Override
    public boolean synActor(List<Actor> actors) throws RemoteException {
        return manager.remoteSynActor(actors);
    }

    @Override
    public void doSomeStuff(int msg) throws RemoteException {
        manager.remoteDoSomeStuff(msg);
    }

    @Override
    public void startGame() throws RemoteException {
        manager.remoteStartGame();
    }

    @Override
    public void gameOver(boolean isWin) throws RemoteException {
        manager.remoteGameOver(isWin);
    }
}
