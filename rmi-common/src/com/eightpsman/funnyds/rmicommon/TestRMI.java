package com.eightpsman.funnyds.rmicommon;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public interface TestRMI extends Remote{

    public String sayHello(String message) throws RemoteException;
}
