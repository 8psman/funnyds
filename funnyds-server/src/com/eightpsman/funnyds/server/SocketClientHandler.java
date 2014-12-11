package com.eightpsman.funnyds.server;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.core.ImageActorData;
import com.eightpsman.funnyds.util.SocketMessage;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * FunnyDS
 * Created by 8psman on 11/20/2014.
 * Email: 8psman@gmail.com
 */
public class SocketClientHandler {

    Socket socket;
    ObjectInputStream reader;
    ObjectOutputStream writer;
    ServerManager manager;
    int mode;

    public SocketClientHandler(Socket socket) throws IOException{
        this.socket = socket;
        reader = new ObjectInputStream(socket.getInputStream());
        writer = new ObjectOutputStream(socket.getOutputStream());
        ListenWorker.execute();
    }

    public boolean isAlive(){
        if (socket == null) return false;
        return (!socket.isClosed());
    }

    private void handleSocketMessage(SocketMessage message){
        switch (message.message){
            case SocketMessage.MSG_JOIN:
                handleJoinDevice(message.param, (Device) message.object);
                break;

            case SocketMessage.MSG_DISCONNECT:
                if (manager != null)
                    manager.removeDevice((Integer) message.object);
                break;

            case SocketMessage.MSG_UPDATE_DEVICE:
                Device device = (Device)message.object;
                System.out.println(String.format("In socket client handler Client update device: %d: %f, %f", device.id, device.x, device.y));
                if (manager != null)
                    manager.updateDevice(message.param, (Device) message.object);
                break;

            case SocketMessage.MSG_UPDATE_ACTOR:
                if (manager != null)
                    manager.updateActor(message.param, (Actor) message.object);
                break;

            case SocketMessage.MSG_NEW_ACTOR:
                if (manager != null)
                    manager.newImageActor(message.param, (ImageActorData) message.object);
                break;

        }
    }

    private void handleObject(Object object){
        if (object instanceof SocketMessage)
            handleSocketMessage((SocketMessage) object);
    }

    private void handleJoinDevice(int mode, Device device){
        this.mode = mode;
        this.manager = ServerManager.getInstance(mode);
        SocketClientCallback callback = new SocketClientCallback(this);
        Device newDevice     = manager.joinDevice(device, callback);
        List<Device> devices = manager.getDevices();
        List<Actor> actors   = manager.getOriginActors();
        send(newDevice);
        send(devices);
        send(actors);
    }

    private void closeSocket(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Object object){
        try {
            writer.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
            closeSocket();
        }
    }

    SwingWorker ListenWorker = new SwingWorker() {
        @Override
        protected Object doInBackground(){
            while (true){
                try {
                    Object object = reader.readObject();
                    handleObject(object);
                } catch (IOException e) {
                    e.printStackTrace();
                    closeSocket();
                    return null;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    closeSocket();
                    return null;
                }
            }
        }
    };
}
