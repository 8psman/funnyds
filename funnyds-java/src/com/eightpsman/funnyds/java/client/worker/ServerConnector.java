package com.eightpsman.funnyds.java.client.worker;

import com.eightpsman.funnyds.core.Actor;
import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.Constants;
import com.eightpsman.funnyds.core.Device;
import com.eightpsman.funnyds.java.JavaImageActorConverter;
import com.eightpsman.funnyds.java.client.ClientCallbackImpl;
import com.eightpsman.funnyds.rmi.ClientCallback;
import com.eightpsman.funnyds.rmi.RMIConstants;
import com.eightpsman.funnyds.rmi.ServerRemote;
import com.eightpsman.funnyds.socket.SocketClientHandler;
import com.eightpsman.funnyds.socket.SocketServerRemote;
import com.eightpsman.funnyds.util.AppUtils;
import com.eightpsman.funnyds.socket.SocketMessage;
import com.eightpsman.funnyds.worker.ServerFinder;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FunnyDS
 * Created by 8psman on 11/23/2014.
 * Email: 8psman@gmail.com
 */
public class ServerConnector extends SwingWorker{

    static AppUtils.IDUtil IDUtil = AppUtils.newIDUtil();
    Logger logger = Logger.getLogger("ServerConnectorLogger" + IDUtil.nextID());

    Handler handler;
    ClientManager manager;
    String host;
    Constants.ConnectingMethod method;

    public ServerConnector(Constants.ConnectingMethod method, String host, ClientManager manager, Handler handler){
        this.method = method;
        this.host = host;
        this.manager = manager;
        this.handler = handler;
        logger.addHandler(handler);
    }

    @Override
    protected Object doInBackground(){
        /** find server */
        if (host == null){
            host = new ServerFinder().start();
            if (host == null){ /** server not found */
                logger.log(Level.INFO, "Error: Server not found!");
                logger.removeHandler(handler);
                return null;
            }
        }

        /** init manager */
        manager.init();
        Device localDevice = manager.getLocalDevice();
        List<Device> devices = null;
        List<Actor> actors   = null;
        ServerRemote remote;

        if (method == Constants.ConnectingMethod.RMI){
            /** create RMI service*/
            try {
                remote = (ServerRemote) Naming.lookup(
                        String.format("rmi://%s:%d/%s", host, RMIConstants.RMI_PORT, RMIConstants.RMI_NAME)
                );
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.INFO, "Error: " + e.getMessage());
                logger.removeHandler(handler);
                return null;
            }

            /** create client call back */
            ClientCallback callback;
            try {
                callback = new ClientCallbackImpl(manager);
            } catch (RemoteException e) {
                e.printStackTrace();
                logger.log(Level.INFO, "Error: " + e.getMessage());
                logger.removeHandler(handler);
                return null;
            }

            /** get data */
            try {
                localDevice = remote.joinDevice(manager.getMode(), localDevice, callback);
                if (localDevice.id < 0){
                    logger.log(Level.INFO, "Error: " + "Rejected by Server!");
                    logger.removeHandler(handler);
                    return null;
                }
                devices = remote.getDevices(localDevice.id);
                actors = remote.getActors(localDevice.id);
                if (devices == null || actors == null || localDevice == null)
                    throw new RemoteException();
            } catch (RemoteException e) {
                e.printStackTrace();
                logger.log(Level.INFO, "Error: " + e.getMessage());
                logger.removeHandler(handler);
                return null;
            }
        }
        else{ /** connect through socket */
            Socket socket;
            try {
                socket = new Socket(host, Constants.SERVER_SOCKET_PORT);
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(Level.INFO, "Error: " + e.getMessage());
                logger.removeHandler(handler);
                return null;
            }

            ObjectOutputStream writer;
            ObjectInputStream reader;
            try {
                writer = new ObjectOutputStream(socket.getOutputStream());
                reader = new ObjectInputStream(socket.getInputStream());
                /** send join message */
                writer.writeObject(new SocketMessage(SocketMessage.MSG_JOIN, manager.getMode(), localDevice));

                long timeToWait = 10000; // 10 seconds
                long startTime = System.currentTimeMillis();
                localDevice = null;
                while (true){
                    try{
                        Object object = reader.readObject();
                        if (object instanceof Device){
                            localDevice = (Device) object;
                            break;
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        if (System.currentTimeMillis() - startTime > timeToWait)
                            break;
                    }
                }

                if (localDevice == null){
                    logger.log(Level.INFO, "Error: " + "Could not get data from Server");
                    logger.removeHandler(handler);
                    return null;
                }

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
                    logger.log(Level.INFO, "Error: " + "Could not get data from Server");
                    logger.removeHandler(handler);
                    return null;
                }

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
                    logger.log(Level.INFO, "Error: " + "Could not get data from Server");
                    logger.removeHandler(handler);
                    return null;
                }

            } catch (IOException e) {
                e.printStackTrace();
                logger.log(Level.INFO, "Error: " + e.getMessage());
                logger.removeHandler(handler);
                return null;
            }

            SocketClientHandler clientHandler = new SocketClientHandler(manager, socket, reader, writer);
            remote = new SocketServerRemote(clientHandler);
        }

        /** setup manager */
        for (Device dv : devices)
            if (dv.id == localDevice.id){
                localDevice = dv;
            }
        manager.setLocalDevice(localDevice);
        manager.setOriginDevices(devices);
        manager.setImageActorConverter(new JavaImageActorConverter());
        manager.setOriginActors(actors);
        manager.setServerRemote(remote);

        logger.log(Level.INFO, "Success");
        logger.removeHandler(handler);

        return null;
    }
}
