package com.eightpsman.funnyds.java.server;

import com.eightpsman.funnyds.core.*;
import com.eightpsman.funnyds.java.JavaImageActor;
import com.eightpsman.funnyds.java.JavaImageActorConverter;
import com.eightpsman.funnyds.rmi.ClientCallback;
import com.eightpsman.funnyds.util.ArrangeDeviceUtil;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public class ServerManager extends BasicManager{

    private ConcurrentMap<Integer, ClientCallback> clients = new ConcurrentHashMap<Integer, ClientCallback>();

    Timer timer;
    Timer synActorTimer;

    static IDUtil deviceIDUtil  = new IDUtil();
    static IDUtil actorIDUtil   = new IDUtil();

    private static ServerManager EXHI_INSTANCE;
    private static ServerManager PRES_INSTANCE;
    private static ServerManager GAME_INSTANCE;

    /**
     * server static instance
     * there are three mode for server, so we have three instance for three mode
     */
    public static ServerManager getExhiInstance(){
        if (EXHI_INSTANCE == null) EXHI_INSTANCE = new ServerManager(Constants.MODE_EXHI);
        return EXHI_INSTANCE;
    }

    public static ServerManager getPresInstance(){
        if (PRES_INSTANCE == null) PRES_INSTANCE = new ServerManager(Constants.MODE_PRES);
        return PRES_INSTANCE;
    }

    public static ServerManager getGameInstance(){
        if (GAME_INSTANCE == null) GAME_INSTANCE = new ServerManager(Constants.MODE_GAME);
        return GAME_INSTANCE;
    }

    public static ServerManager getInstance(int mode){
        switch (mode){
            case Constants.MODE_EXHI:
                return ServerManager.getExhiInstance();
            case Constants.MODE_PRES:
                return ServerManager.getPresInstance();
            case Constants.MODE_GAME:
                return ServerManager.getGameInstance();
        }
        return null;
    }

    int mode;

    private ServerManager(int mode){
        this.mode = mode;
    }

    public int getMode(){
        return mode;
    }

    public static void initAllInstance(){
        getExhiInstance().init();
        getPresInstance().init();
        getGameInstance().init();
    }

    public static void shutdownAllInstance(){
        getExhiInstance().shutdown();
        getPresInstance().shutdown();
        getGameInstance().shutdown();

        EXHI_INSTANCE = null;
        PRES_INSTANCE = null;
        GAME_INSTANCE = null;

//        try {
//            LocateRegistry.getRegistry(RMIConstants.RMI_PORT).unbind(RMIConstants.RMI_NAME);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } catch (NotBoundException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void init(){
        super.init();
        deviceIDUtil.reset();
        actorIDUtil.reset();

        timer = new Timer();
        timer.scheduleAtFixedRate(clientObserver, 1000, 3000);

        synActorTimer = new Timer();
        synActorTimer.scheduleAtFixedRate(new SynActorTask(), Constants.SYN_ACTOR_PERIOD, Constants.SYN_ACTOR_PERIOD);
    }

    @Override
    public void shutdown(){
        super.shutdown();

        /** kick out all clients */
        kickOutAll();

        /** stop timer */
        if (timer !=  null) timer.cancel();
        if (synActorTimer != null) synActorTimer.cancel();
    }

    /** init game */
    private void initGame(){
        new ArrangeDeviceUtil.VerticalArranger().arrange(getDevices());
        for (int i=0; i<getDevices().size(); i++)
            updateDevice(getDevice(i));
        ball = new CircleActor(getDpi(), 0f, 0f, 10f, 80f, 80f);
        ball.id = -1;

        Device aboveDevice = getDevice(0);
        Device belowDevice = getDevice(1);

        float barrierH = 0.1f;
        float aboveW = aboveDevice.pw / 4;
        aboveBarrier = new BarrierActor(
                0f, aboveDevice.py - aboveDevice.ph/2 + barrierH/2 + 2*barrierH,
                aboveW, barrierH, 0f, 0f
        );
        float belowW = belowDevice.pw / 4;
        belowBarrier = new BarrierActor(
                0f, belowDevice.py + belowDevice.ph/2 - barrierH/2 - 2*barrierH,
                belowW, barrierH, 0f, 0f
        );
        aboveBarrier.dpi = getDpi();
        belowBarrier.dpi = getDpi();
        aboveBarrier.calc(getDpi());
        belowDevice.calc(getDpi());
        aboveBarrier.id = -1;
        belowBarrier.id = -1;

        this.updateActor(ball);
        this.updateActor(aboveBarrier);
        this.updateActor(belowBarrier);

        /** notify all player */
        Iterator<Map.Entry<Integer, ClientCallback>> entryIterator = clients.entrySet().iterator();
        while (entryIterator.hasNext()){
            Map.Entry<Integer, ClientCallback> entry = entryIterator.next();
            try{
                entry.getValue().startGame();
            }catch (RemoteException ex){
                ex.printStackTrace();
            }
        }

        isOver = false;
        gameObserverTask = new Timer();
        gameObserverTask.scheduleAtFixedRate(new GameObserverTask(),  0, (long)(Constants.DELTA_TIME * 1000));
    }

    public void clientReadyForGame(int clientId){
        Device device = getDeviceByID(clientId);
        device.isActive = true;
        /** not enough 2 player */
        if (getDevices().size() < 2) return;
        /** check all player ready */
        boolean gameReady = true;
        for (Device dv : getDevices()){
            if (!dv.isActive){
                gameReady = false;
                break;
            }
        }
        if (gameReady){
            for (Device dv : getDevices())
                dv.isActive = false;
            initGame();
        }

    }

    public Device joinDevice(Device device, ClientCallback callback){
        /** if mode is game, accept only two client */
        if (getMode() == Constants.MODE_GAME){
            if (getDevices().size() > 1){
                device.id = -1;
                return device;
            }
        }
        device.id = deviceIDUtil.nextID();
        putDevice(device);
        super.updateDevice(device);
        clients.put(device.id, callback);
        ServerLogger.LOGGER.log(Level.INFO, String.format("Client connected: ID: %d, Name: %s, IP: %s", device.id, device.name, device.id));
        // notify all client for new device
        Iterator<Map.Entry<Integer, ClientCallback>> entryIterator = clients.entrySet().iterator();
        while (entryIterator.hasNext()){
            Map.Entry<Integer, ClientCallback> entry = entryIterator.next();
            if (entry.getKey() != device.id){
                try{
                    entry.getValue().newDevice(device);
                }catch (RemoteException ex){
                    ex.printStackTrace();
                }
            }
        }

        return device;
    }

    private Device putDevice(Device device){
        device.px = 0;
        device.py = 0;
        return device;
    }

    public List<Actor> getOriginActors(){
        List<Actor> dataList = new ArrayList<Actor>();
        for (Actor actor : getActors())
            if (actor instanceof JavaImageActor){
                JavaImageActor javaImageActor = (JavaImageActor)actor;
                ImageActorData imageActorData = javaImageActor.getImageActorData();
                dataList.add(imageActorData);
            }else{
                dataList.add(actor);
            }
        return dataList;
    }

    @Override
    public void updateDevice(Device device){
        this.updateDevice(0, device);
    }

    public void updateDevice(int client, Device device){
        System.out.println(String.format("ServerManager Client update device: %d: %f, %f", device.id, device.x, device.y));

        super.updateDevice(device);

        ServerLogger.LOGGER.log(Level.INFO, String.format("Update device: Client: by %s, DeviceID: %d, Mode: %d, %f, %f",
                client > 0 ? "client " + client : "Server ", device.id, mode, device.x, device.y));

        Iterator<Map.Entry<Integer, ClientCallback>> entryIterator = clients.entrySet().iterator();
        while (entryIterator.hasNext()){
            Map.Entry<Integer, ClientCallback> entry = entryIterator.next();
            if (entry.getKey() != client){
                try{
                    entry.getValue().updateDevice(device);
                }catch (RemoteException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * New ImageActor
     */
    public void newImageActor(final int clientId, final ImageActorData imageData){
        doBackgroundWork(new Runnable() {
            @Override
            public void run() {
                JavaImageActor actor = (JavaImageActor) new JavaImageActorConverter().getActor(imageData);
                actor.id = -1;

                updateActor(clientId, actor);

                imageData.id = actor.id;
                imageData.owner = actor.owner;

                Iterator<Map.Entry<Integer, ClientCallback>> entryIterator = clients.entrySet().iterator();
                while (entryIterator.hasNext()){
                    Map.Entry<Integer, ClientCallback> entry = entryIterator.next();
                    try{
                        entry.getValue().newImageActor(imageData);
                    }catch (RemoteException ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    /**
     * Update actor by server
     * @param actor
     */
    @Override
    public void updateActor(Actor actor){
        this.updateActor(0, actor);
    }

    public void updateActor(int client, Actor actor){
        boolean isNew = false;
        if (actor.id < 0){ // new actor
            int id = actorIDUtil.nextID();
            actor.id = id;
            actor.owner = client;
            isNew = true;
            ServerLogger.LOGGER.log(Level.INFO, String.format("Actor added: created by %d, id %d", client, actor.id));
        }else{
            ServerLogger.LOGGER.log(Level.INFO, String.format("Actor updated: id %d", actor.id), actor.id);
        }
        super.updateActor(actor);

        if (!isNew || !(actor instanceof ImageActor)){
            Iterator<Map.Entry<Integer, ClientCallback>> entryIterator = clients.entrySet().iterator();
            while (entryIterator.hasNext()){
                Map.Entry<Integer, ClientCallback> entry = entryIterator.next();
                if (entry.getKey() != client){
                    try{
                        entry.getValue().updateActor(actor);
                    }catch (RemoteException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private static class IDUtil{
        public IDUtil(){
            NOW_ID = 0;
        }
        private int NOW_ID;
        private void reset(){
            NOW_ID  = 0;
        }
        private int nextID(){
            NOW_ID ++;
            return NOW_ID;
        }
    }

    TimerTask clientObserver = new TimerTask() {
        @Override
        public void run() {
            Iterator<Map.Entry<Integer, ClientCallback>> entryIterator = clients.entrySet().iterator();
            while (entryIterator.hasNext()){
                Map.Entry<Integer, ClientCallback> entry = entryIterator.next();
                ClientCallback callback = entry.getValue();
                if (callback == null) continue;
                try {
                    boolean isAlive = callback.isAlive();
                    if (!isAlive)
                        removeDevice(entry.getKey());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    removeDevice(entry.getKey());
                }
            }
        }
    };

    class SynActorTask extends TimerTask{
        @Override
        public void run() {
            System.out.println("Server sync actor");
            Iterator<Map.Entry<Integer, ClientCallback>> entryIterator = clients.entrySet().iterator();
            while (entryIterator.hasNext()){
                Map.Entry<Integer, ClientCallback> entry = entryIterator.next();
                try{
                    entry.getValue().synActor(getActors());
                }catch (RemoteException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    /** Game observer */
    BarrierActor aboveBarrier;
    BarrierActor belowBarrier;
    CircleActor ball;
    Timer gameObserverTask;
    boolean isOver = false;

    class GameObserverTask extends TimerTask{
        @Override
        public void run() {
            if (isOver) return;
            /** player 1 loose */
            if (ball.py - ball.ph/2 < aboveBarrier.py - aboveBarrier.ph/2){
                gameOver(getDevices().get(1), getDevices().get(0));
                return ;
            }
            /** player 2 loose */
            if (ball.py + ball.ph/2 > belowBarrier.py + belowBarrier.ph/2){
                gameOver(getDevices().get(0), getDevices().get(0));
                return ;
            }
            /** check ball with above */
            if (ball.py - ball.ph/2 <= aboveBarrier.py){
                ball.revertY();
            }

            /** check ball with below */
            if (ball.py + ball.ph/2 >= belowBarrier.py){
                ball.revertY();
            }

            System.out.println("Game observer doing stuff");
        }
    }

    public void gameOver(Device winner, Device looser){
        /** cancel observer */
        isOver = true;
        gameObserverTask.cancel();

        /** notify player */
        try {
            clients.get(winner.id).gameOver(true);
            clients.get(looser.id).gameOver(false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        /** remove all actor */
        removeActor(ball.id);
        removeActor(aboveBarrier.id);
        removeActor(belowBarrier.id);

    }

    @Override
    public void removeActor(int id){
        super.removeActor(id);
        ServerLogger.LOGGER.log(Level.INFO, String.format("Actor removed: id %d", id));

        Iterator<Map.Entry<Integer, ClientCallback>> entryIterator = clients.entrySet().iterator();
        while (entryIterator.hasNext()){
            Map.Entry<Integer, ClientCallback> entry = entryIterator.next();
            try{
                entry.getValue().removeActor(id);
            }catch (RemoteException ex){
                ex.printStackTrace();
            }
        }

    }

    public void kickOutAll(){
        Iterator<Map.Entry<Integer, ClientCallback>> entryIterator = clients.entrySet().iterator();
        while (entryIterator.hasNext()){
            final Map.Entry<Integer, ClientCallback> entry = entryIterator.next();
                doBackgroundWork(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            entry.getValue().kickout();
                        }catch (RemoteException ex){
                            ex.printStackTrace();
                        }
                    }
                });
        }
        clients.clear();
    }

    public void kickOut(int id){
        final ClientCallback callback = clients.get(id);
        doBackgroundWork(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.kickout();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        clients.remove(id);
        removeDevice(id);
    }

    @Override
    public void removeDevice(int id){
        super.removeDevice(id);
        clients.remove(id);
        ServerLogger.LOGGER.log(Level.INFO, String.format("Client disconnected: ID: %d", id));

        Iterator<Map.Entry<Integer, ClientCallback>> entryIterator = clients.entrySet().iterator();
        while (entryIterator.hasNext()){
            Map.Entry<Integer, ClientCallback> entry = entryIterator.next();
            try{
                entry.getValue().removeDevice(id);
            }catch (RemoteException ex){
                ex.printStackTrace();
            }
        }
    }

    public void sendMessageToClients(final int msg){
        doBackgroundWork(new Runnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<Integer, ClientCallback>> entryIterator = clients.entrySet().iterator();
                while (entryIterator.hasNext()){
                    Map.Entry<Integer, ClientCallback> entry = entryIterator.next();
                    try{
                        entry.getValue().doSomeStuff(msg);
                    }catch (RemoteException ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    protected void doBackgroundWork(final Runnable runnable){
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                runnable.run();
                return null;
            }
        }.execute();
    }


}
