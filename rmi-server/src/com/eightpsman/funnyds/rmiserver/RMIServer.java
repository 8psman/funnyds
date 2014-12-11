package com.eightpsman.funnyds.rmiserver;

import com.eightpsman.funnyds.rmicommon.TestRMI;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by 8psman on 11/19/2014.
 */
public class RMIServer extends UnicastRemoteObject implements TestRMI{

    protected RMIServer() throws RemoteException {
        super(0);
    }

    public static final String RMI_HOST = "localhost";
    public static final int RMI_PORT    = 4456;

    public static void main(String args[]){
        System.out.println("Hello RMI 1");

        try {
            RMIServer server = new RMIServer();
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            System.setProperty("java.rmi.server.codebase", TestRMI.class.getProtectionDomain().getCodeSource().getLocation().toString());
            registry.bind("TestRMI", server);
            System.out.println("RMIServer is now ready...");
        } catch (RemoteException e) {
            e.printStackTrace();
            return;
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String sayHello(String message) throws RemoteException {
        return "Hello " + message;
    }
}
