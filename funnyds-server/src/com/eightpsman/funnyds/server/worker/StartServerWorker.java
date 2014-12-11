package com.eightpsman.funnyds.server.worker;

import com.eightpsman.funnyds.rmi.RMIConstants;
import com.eightpsman.funnyds.rmi.ServerRemote;
import com.eightpsman.funnyds.server.ServerLogger;
import com.eightpsman.funnyds.server.ServerRemoteImpl;
import com.eightpsman.funnyds.server.SocketServerListener;

import javax.swing.*;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public class StartServerWorker extends SwingWorker<Void, Void>{

    public static Logger LOGGER = ServerLogger.LOGGER;

    boolean isRMIEnable;
    boolean isSocketEnable;
    Handler handler;
    public StartServerWorker(Handler handler, boolean isRMIEnable, boolean isSocketEnable) {
        super();
        this.isRMIEnable = isRMIEnable;
        this.isSocketEnable = isSocketEnable;
        this.handler = handler;
        LOGGER.addHandler(handler);
    }

    @Override
    protected Void doInBackground(){
        LOGGER.log(Level.INFO, "Starting server...");
        /** Start socket server */
        if (isSocketEnable){
            LOGGER.log(Level.INFO, "Starting SocketServer...");

            try {
                new SocketServerListener().start();
                LOGGER.log(Level.INFO, "Start SocketServer successful!");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.log(Level.INFO, "Error: " + e.getMessage());
                LOGGER.removeHandler(handler);
                return null;
            }
        }

        /** Start RMI Registry */
        try {

            LOGGER.log(Level.INFO, "Starting RMI Registry...");
            Registry registry = LocateRegistry.createRegistry(RMIConstants.RMI_PORT);
            ServerRemoteImpl remote = new ServerRemoteImpl();
            System.setProperty("java.rmi.server.codebase",
                    ServerRemote.class.getProtectionDomain().getCodeSource().getLocation().toString());
            registry.rebind(RMIConstants.RMI_NAME, remote);
            LOGGER.log(Level.INFO, "Start RMI Registry successful!");
        } catch (RemoteException e) {
            e.printStackTrace();
            LOGGER.log(Level.INFO, "Error: " + e.getMessage());
            LOGGER.removeHandler(handler);
            return null;
        }

        try {
            new ServerGreeter().execute();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        LOGGER.log(Level.INFO, "Success: starting server!");
        LOGGER.removeHandler(handler);
        return null;
    }
}
