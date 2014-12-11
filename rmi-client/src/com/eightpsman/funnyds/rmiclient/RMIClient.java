package com.eightpsman.funnyds.rmiclient;

import com.eightpsman.funnyds.rmicommon.TestRMI;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by 8psman on 11/19/2014.
 */
public class RMIClient{

    public static void  main(String args[]){
        System.out.println("Hello RMI from client!");

        try {
            TestRMI testRMI = (TestRMI) Naming.lookup("rmi://localhost:4456/TestRMI");

            String hello = testRMI.sayHello("8psman");

            System.out.println(hello);

        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
