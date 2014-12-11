package com.eightpsman.funnyds.android.worker;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.eightpsman.funnyds.android.actor.AndroidImageActorConverter;
import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.Constants;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.socket.SocketClientHandler;
import com.eightpsman.funnyds.socket.SocketServerRemote;
import com.eightpsman.funnyds.util.SocketMessage;
import com.eightpsman.funnyds.worker.ServerFinder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public class ServerConnector extends AsyncTask<Void, Void, Void> {

    Handler handler;
    ClientManager manager;
    String host;
    public ServerConnector(String host, ClientManager manager, Handler handler){
        this.host = host;
        this.manager = manager;
        this.handler = handler;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        /** find server */
        if (host == null){
            host = new ServerFinder().start();
            if (host == null){
                Message message = handler.obtainMessage(FAIL, "Could not find server!");
                handler.sendMessage(message);
                return null;
            }
        }

        Socket socket;
        try {
            socket = new Socket(host, Constants.SERVER_SOCKET_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            Message message = handler.obtainMessage(FAIL, "Could not connect to server!\n" + e.getMessage());
            handler.sendMessage(message);
            return null;
        }

        Device device = manager.getLocalDevice();
        manager.init();

        ObjectOutputStream writer;
        ObjectInputStream reader;
        List<Device> devices = null;
        List<Actor> actors = null;

        try {
            writer = new ObjectOutputStream(socket.getOutputStream());
            reader = new ObjectInputStream(socket.getInputStream());
            /** send join message */
            writer.writeObject(new SocketMessage(SocketMessage.MSG_JOIN, manager.getMode(), device));
            Log.d(Constants.TAG, "Sent device to server");
            // get info from server

            long timeToWait = 10000; // 10 seconds
            Log.d(Constants.TAG, "Waiting for device");
            long startTime = System.currentTimeMillis();
            device = null;
            while (true){
                try{
                    Object object = reader.readObject();
                    if (object instanceof Device){
                        device = (Device) object;
                        break;
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    if (System.currentTimeMillis() - startTime > timeToWait)
                        break;
                }
            }

            if (device == null){
                handler.sendMessage(handler.obtainMessage(FAIL, "Could not get data from server"));
                return null;
            }
            Log.d(Constants.TAG, "Waiting for devices");
            startTime = System.currentTimeMillis();
            while (true){
                try{
                    devices = (List<Device>)reader.readObject();
                    break;
                }catch (Exception ex){
                    ex.printStackTrace();
                    if (System.currentTimeMillis() - startTime > timeToWait)
                        break;
                }
            }

            if (devices == null){
                handler.sendMessage(handler.obtainMessage(FAIL, "Could not get data from server"));
                return null;
            }

            Log.d(Constants.TAG, "Waiting for actors");
            startTime = System.currentTimeMillis();
            while (true){
                try{
                    actors = (List<Actor>) reader.readObject();
                    break;
                }catch (Exception ex){
                    ex.printStackTrace();
                    if (System.currentTimeMillis() - startTime > timeToWait)
                        break;
                }
            }

            if (actors == null){
                handler.sendMessage(handler.obtainMessage(FAIL, "Could not get data from server"));
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            handler.sendMessage(handler.obtainMessage(FAIL, "Socket error! Could not write/read data!"));
            return null;
        }

        manager.setLocalDevice(device);
        manager.setMode(Constants.MODE_EXHI);
        manager.setOriginDevices(devices);
        manager.setImageActorConverter(new AndroidImageActorConverter());
        manager.setOriginActors(actors);

        SocketClientHandler clientHandler = new SocketClientHandler(manager, socket, reader, writer);
        SocketServerRemote remote = new SocketServerRemote(clientHandler);
        manager.setServerRemote(remote);

        handler.sendMessage(handler.obtainMessage(SUCCESS, manager));
        return null;
    }

    public static final int SUCCESS = 1101;
    public static final int FAIL    = 1102;
}
