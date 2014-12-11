package com.eightpsman.funnyds.java.server;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.Constants;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.core.ImageActorData;
import com.eightpsman.funnyds.rmi.ClientCallback;
import com.eightpsman.funnyds.rmi.ServerRemote;
import com.eightpsman.funnyds.java.JavaUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public class ServerRemoteImpl extends UnicastRemoteObject implements ServerRemote {

    /** each client connect to server with a specific mode
     * so, use a hash map to store mode for each client
     */
    Map<Integer, Integer> modeMap;

    public ServerRemoteImpl() throws RemoteException {
        super();
        modeMap = new ConcurrentHashMap<Integer, Integer>();
        ServerManager.initAllInstance();

        int dpi = JavaUtil.getScreenResolution();
        ServerManager.getExhiInstance().setDpi(dpi);
        ServerManager.getPresInstance().setDpi(dpi);
        ServerManager.getGameInstance().setDpi(dpi);
    }

    private ServerManager getManager(int clientId){
        if (modeMap.containsKey(clientId)){
            int mode =  modeMap.get(clientId);
            return getManagerByMode(mode);
        }else{
            return null;
        }
    }

    private ServerManager getManagerByMode(int mode){
        switch (mode){
            case Constants.MODE_EXHI:
                return ServerManager.getExhiInstance();
            case Constants.MODE_PRES:
                return ServerManager.getPresInstance();
            case Constants.MODE_GAME:
                return ServerManager.getGameInstance();
            default:
                return ServerManager.getExhiInstance();
        }
    }

    @Override
    public List<Device> getDevices(int clientId) throws RemoteException{
        System.out.println("Client get devices: " + clientId);
        ServerManager manager = getManager(clientId);
        if (manager != null)
            return manager.getDevices();
        return null;
    }

    @Override
    public List<Actor> getActors(int clientId) throws RemoteException{
        System.out.println("Client get actors: " + clientId);
        ServerManager manager = getManager(clientId);
        if (manager != null)
            return manager.getOriginActors();
        return null;
    }

    @Override
    public Device joinDevice(int mode, Device device, ClientCallback callback) throws RemoteException{
        System.out.println("Client join: " + device.id);
        Device newDevice = getManagerByMode(mode).joinDevice(device, callback);
        modeMap.put(newDevice.id, mode);
        return newDevice;
    }

    @Override
    public void disconnect(int clientId) throws RemoteException {
        System.out.println("Client disconnected: " + clientId);
        ServerManager manager = getManager(clientId);
        if (manager != null)
            manager.removeDevice(clientId);
    }

    @Override
    public void updateDevice(int clientId, Device device) throws RemoteException {
        System.out.println("Client updateDevice: " + clientId);
        ServerManager manager = getManager(clientId);
        if (manager != null)
            manager.updateDevice(clientId, device);
    }

    @Override
    public void updateActor(int clientId, Actor actor) throws RemoteException {
        ServerManager manager = getManager(clientId);
        if (manager != null)
            manager.updateActor(clientId, actor);
    }

    @Override
    public void newImageActor(int clientId, ImageActorData data) throws RemoteException {
        getManager(clientId).newImageActor(clientId, data);
    }

    @Override
    public void readyForGame(int clientId) throws RemoteException {
        getManager(clientId).clientReadyForGame(clientId);
    }
}
