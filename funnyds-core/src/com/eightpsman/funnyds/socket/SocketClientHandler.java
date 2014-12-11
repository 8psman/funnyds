package com.eightpsman.funnyds.socket;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.core.ImageActorData;

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
    ClientManager manager;
    public SocketClientHandler(ClientManager manager, Socket socket, ObjectInputStream reader, ObjectOutputStream writer){
        this.manager = manager;
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
        ListenWorker.start();
    }

    public void stopHandler(){
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
        }
    }

    private void handleSocketMessage(SocketMessage message){
        switch (message.message){
            case SocketMessage.MSG_KICKOUT:
                manager.kickOut();
                break;

            case SocketMessage.MSG_REMOVE_DEVICE:
                manager.remoteRemoveDevice((Integer) message.object);
                break;

            case SocketMessage.MSG_REMOVE_ACTOR:
                manager.removeActor((Integer) message.object);
                break;

            case SocketMessage.MSG_DO_SOME_STUFF:
                manager.remoteDoSomeStuff((Integer) message.object);
                break;

            case SocketMessage.MSG_UPDATE_DEVICE:
                manager.remoteUpdateDevice((Device) message.object);
                break;

            case SocketMessage.MSG_UPDATE_ACTOR:
                manager.remoteUpdateActor((Actor) message.object);
                break;

            case SocketMessage.MSG_SYNC_ACTOR:
                manager.remoteSynActor((List<Actor>) message.object);
                break;

            case SocketMessage.MSG_NEW_ACTOR:
                manager.remoteNewImageActor((ImageActorData) message.object);
                break;
        }
    }

    private void handleObject(Object object){
        if (object instanceof SocketMessage)
            handleSocketMessage((SocketMessage) object);
    }

    /**
     * Listen to socket and receive object
     */
    Thread ListenWorker = new Thread() {
        @Override
        public void run() {
            while (true){
                try {
                    Object object = reader.readObject();
                    handleObject(object);
                } catch (IOException e) {
                    e.printStackTrace();
                    stopHandler();
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    stopHandler();
                    break;
                }
            }
            return;
        }
    };
}
