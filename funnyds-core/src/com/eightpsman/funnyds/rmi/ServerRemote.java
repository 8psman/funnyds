package com.eightpsman.funnyds.rmi;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.core.ImageActorData;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public interface ServerRemote extends Remote{

    /**
     * Get all devices connected to server
     * @return
     */
    public List<Device> getDevices(int clientId) throws RemoteException;

    /**
     * Get all actors currently acting in server
     */
    public List<Actor> getActors(int clientId) throws RemoteException;

    /**
     * Client want to join to server
     * @param device: the device that want to join
     * @return it's device with new id if join successful, false otherwise
     */
    public Device joinDevice(int mode, Device device, ClientCallback callback) throws RemoteException;

    /**
     * Client want to disconnect
     * @param clientId
     * @throws RemoteException
     */
    public void disconnect(int clientId) throws RemoteException;

    /**
     * When client update device
     * @param device
     * @throws RemoteException
     */
    public void updateDevice(int clientId, Device device) throws RemoteException;

    /**
     * Client add new actor
     * @param actor
     * @throws RemoteException
     */
    public void updateActor(int clientId, Actor actor) throws RemoteException;

    /**
     * Client add new ImageActor
     * @param data
     * @throws RemoteException
     */
    public void newImageActor(int clientId, ImageActorData data) throws RemoteException;

    public void readyForGame(int clientId) throws RemoteException;
}
