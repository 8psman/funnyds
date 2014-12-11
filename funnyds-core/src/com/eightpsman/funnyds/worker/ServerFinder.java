package com.eightpsman.funnyds.worker;

import com.eightpsman.funnyds.core.Constants;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

/**
 * FunnyDS
 * Created by 8psman on 11/24/2014.
 * Email: 8psman@gmail.com
 */
public class ServerFinder {

    private static int TIME_OUT = 5000;
    DatagramSocket datagramSocket;

    public ServerFinder(){

    }

    public String start(){
        String host = null;
        // Timeout
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (datagramSocket != null)
                    if (!datagramSocket.isClosed())
                        datagramSocket.close();
            }
        }, TIME_OUT);

        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
            byte[] sendData = Constants.GREETING_MESSAGE.getBytes();

            //Try the 255.255.255.255 first
            DatagramPacket sendPacket1 = new DatagramPacket(
                    sendData,
                    sendData.length,
                    InetAddress.getByName("255.255.255.255"),
                    Constants.SERVER_GREETER_SOCKET);
            datagramSocket.send(sendPacket1);

            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) continue;
                    // Send the broadcast package!
                    DatagramPacket sendPacket2 = new DatagramPacket(sendData, sendData.length, broadcast, Constants.SERVER_GREETER_SOCKET);
                    System.out.println(broadcast.getHostAddress());
                    try{
                        datagramSocket.send(sendPacket2);
                    }catch (SocketException ex){
//                        ex.printStackTrace();
                    }

                }
            }

            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            datagramSocket.receive(receivePacket);

            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            if (message.equals(Constants.GREETING_MESSAGE)){
                System.out.println("Found server: " + receivePacket.getAddress().getHostAddress());
                host = receivePacket.getAddress().getHostAddress();
            }

            //Close the port!
            datagramSocket.close();
            datagramSocket = null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return host;
    }
}
