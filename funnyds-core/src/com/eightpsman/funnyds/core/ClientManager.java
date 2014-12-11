package com.eightpsman.funnyds.core;

import com.eightpsman.funnyds.rmi.ServerRemote;
import com.eightpsman.funnyds.util.AppUtils;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FunnyDS
 * Created by 8psman on 11/20/2014.
 * Email: 8psman@gmail.com
 */
public class ClientManager extends BasicManager{

    private Device localDevice;

    private int mode;

    private ServerRemote serverRemote;

    private static ClientManager INSTANCE;

    GameEventCallback gameCallback;

    private static ClientManager getInstance(){
        if (INSTANCE == null) INSTANCE = new ClientManager();
        return INSTANCE;
    }

    public static ClientManager getUniqueInstance(){
        return getInstance();
    }

    public static ClientManager createInstance(){
        return new ClientManager();
    }

    ImageActorConverter imageActorConverter;

    private ClientManager(){

    }

    public void setGameCallback(GameEventCallback callback){
        this.gameCallback = callback;
    }

    public void setImageActorConverter(ImageActorConverter converter){
        this.imageActorConverter = converter;
    }

    @Override
    public void init(){
        super.init();

    }

    public void kickOut(){
        mLogger.log(Level.INFO, MSG_KICKEDOUT);
    }

    public void shutdown(){
        super.shutdown();
        try {
            serverRemote.disconnect(localDevice.id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        serverRemote = null;
    }

    public void setMode(int mode){
        this.mode = mode;
    }

    public int getMode(){
        return mode;
    }

    public void setServerRemote(ServerRemote serverRemote){
        this.serverRemote = serverRemote;
    }

    public void setLocalDevice(Device device){
        this.localDevice = device;
        setDpi(localDevice.dpi);
    }

    public Device getLocalDevice(){
        return localDevice;
    }

    public void setOriginDevices(List<Device> devices){
        addDevice(devices);
    }

    public void setOriginActors(List<Actor> actors){
        for (Actor actor : actors)
            if (actor instanceof ImageActorData){
                ImageActorData actorData = (ImageActorData) actor;
                actor.calc(getDpi());
                if (imageActorConverter != null){
                    Actor imageActor = imageActorConverter.getActor(actorData);
                    addActor(imageActor);
                }else{
                    addActor(actorData.getDummyActor());
                }
            }else{
                addActor(actors);
            }
    }

    /** local update -------------------------------------------------------------------------------------------------*/
    @Override
    public void updateDevice(Device device){
        super.updateDevice(device);

        System.out.println(String.format("Client update device: %d: %f, %f", device.id, device.x, device.y));

        try {
            serverRemote.updateDevice(localDevice.id, device);
        } catch (RemoteException e) {
            e.printStackTrace();
            onRemoteException();
        }

        mLogger.log(Level.INFO, MSG_DEVICE_UPDATED, device.id);
        if (device.id == localDevice.id){
            mLogger.log(Level.INFO, MSG_LOCAL_DEVICE_UPDATED);
        }

    }

    @Override
    public void updateActor(Actor actor){
        try {
            serverRemote.updateActor(localDevice.id, actor);
        } catch (RemoteException e) {
            e.printStackTrace();
            onRemoteException();
        }
    }

    public void newImageActor(ImageActorData data){
        try {
            serverRemote.newImageActor(localDevice.id, data);
        } catch (RemoteException e) {
            e.printStackTrace();
            onRemoteException();
        }
    }

    public void localPlayerReady(){
        try {
            serverRemote.readyForGame(localDevice.id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /** end local update ---------------------------------------------------------------------------------------------*/

    /** remote update ------------------------------------------------------------------------------------------------*/

    public void remoteUpdateDevice(Device device){
        super.updateDevice(device);

        mLogger.log(Level.INFO, MSG_DEVICE_UPDATED, device.id);
        if (device.id == localDevice.id){
            mLogger.log(Level.INFO, MSG_LOCAL_DEVICE_UPDATED);
        }
    }

    public void remoteRemoveDevice(int id){
        super.removeDevice(id);
    }

    public boolean remoteUpdateActor(Actor actor){
        super.updateActor(actor);
        return true;
    }

    public boolean remoteSynActor(List<Actor> actors){
        System.out.println("Client sync actors");
        for (Actor actor : actors)
            remoteUpdateActor(actor);
        return true;
    }

    public void remoteNewImageActor(ImageActorData imageData){
        if (imageActorConverter != null){
            Actor imageActor = imageActorConverter.getActor(imageData);
            remoteUpdateActor(imageActor);
        }else{
            remoteUpdateActor(imageData.getDummyActor());
        }
    }

    public void remoteDoSomeStuff(int msg){
        System.out.println("Do some stuff asked by server!");
        switch (msg){
            case Constants.HACK_MSG_SET_LOCATION_ON_SCREEN:
                mLogger.log(Level.INFO, MSG_SET_LOCATION);
                break;
        }

    }

    public void remoteStartGame(){
        if (getMode() != Constants.MODE_GAME)
            return;
        gameCallback.startGame();
    }

    public void remoteGameOver(boolean isWin){
        gameCallback.gameOver(isWin);
    }

    /** end remote update --------------------------------------------------------------------------------------------*/


    /** handle exception event**/
    public void onRemoteException(){
        mLogger.log(Level.INFO, MSG_DISCONNECTED);
    }

    static AppUtils.IDUtil IDUtil = AppUtils.newIDUtil();

    /** ClientManager Logger */
    public Logger mLogger = Logger.getLogger("ClientManager" + IDUtil.nextID());


    static{

    }
    public static String MSG_KICKEDOUT      = "KICKEDOUT";
    public static String MSG_DISCONNECTED   = "DISCONNECTED";
    public static String MSG_SET_LOCATION   = "SET_LOCATION";
    public static String MSG_DEVICE_UPDATED = "DEVICE_UPDATED";
    public static String MSG_LOCAL_DEVICE_UPDATED = "LOCAL_DEVICE_UPDATED";


}
