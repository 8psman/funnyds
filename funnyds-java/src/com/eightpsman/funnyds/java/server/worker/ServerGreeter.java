package com.eightpsman.funnyds.java.server.worker;

import com.eightpsman.funnyds.core.Constants;

import javax.swing.*;
import java.io.IOException;
import java.net.*;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public class ServerGreeter extends SwingWorker{

    DatagramSocket socket;

    public ServerGreeter() throws UnknownHostException, SocketException {
        super();
        //Keep a socket open to listen to all the UDP traffic that is destined for this port
        socket = new DatagramSocket
                (Constants.SERVER_GREETER_SOCKET, InetAddress.getByName("0.0.0.0"));
        socket.setBroadcast(true);
    }

    @Override
    protected Object doInBackground() throws Exception {
        if (socket == null) return null;
        try {
            while (true) {
                // Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                //See if the packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                if (message.equals(Constants.GREETING_MESSAGE)) {
                    byte[] sendData = Constants.GREETING_MESSAGE.getBytes();

                    //Send a response
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
