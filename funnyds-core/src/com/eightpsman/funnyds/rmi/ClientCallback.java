package com.eightpsman.funnyds.rmi;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.core.ImageActorData;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * FunnyDS
 * Created by 8psman on 11/20/2014.
 * Email: 8psman@gmail.com
 */
public interface ClientCallback extends Remote{

    /**
     * Check client still living or not
     * @return client callback has to return true to tell server that it is still alive
     * @throws RemoteException
     */
    public boolean isAlive() throws RemoteException;

    /**
     * When server want to kick out client
     */
    public void kickout() throws RemoteException;

    /**
     * When new client connected to server
     */
    public boolean newDevice(Device device) throws RemoteException;

    /**
     * When a client disconnected
     * @param id
     * @return
     * @throws RemoteException
     */
    public boolean removeDevice(int id) throws RemoteException;

    /**
     * When device state change
     */
    public boolean updateDevice(Device device) throws RemoteException;

    /**
     *
     * @param actor
     * @return
     * @throws RemoteException
     */
    public boolean updateActor(Actor actor) throws RemoteException;

    /**
     * When create new image actor, send image data
     * @param data
     * @return
     * @throws RemoteException
     */
    public boolean newImageActor(ImageActorData data) throws RemoteException;
    /**
     * When server remove an actor
     * @param id
     * @return
     * @throws RemoteException
     */
    public boolean removeActor(int id) throws RemoteException;
    /**
     *
     * @param actors
     * @return
     * @throws RemoteException
     */
    public boolean synActor(List<Actor> actors) throws RemoteException;

    /**
     * When server want client to do something
     * @param msg
     * @throws RemoteException
     */
    public void doSomeStuff(int msg) throws RemoteException;

    public void startGame() throws RemoteException;

    public void gameOver(boolean isWin) throws RemoteException;

}
