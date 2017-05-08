package com.company;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {
        if (args.length != 3)
            System.out.println ("This program requires three command line arguments");
        else {
            try {
                InetAddress receiverHost = InetAddress.getByName(args[0]);
                int receiverPort = Integer.parseInt(args[1]);
                String message = args[2];

                // instantiates a datagram socket for sending the data
                DatagramSocket  mySocket = new DatagramSocket();
                byte[ ] buffer = message.getBytes( );
                DatagramPacket datagram =
                        new DatagramPacket(buffer, buffer.length,
                                receiverHost, receiverPort);
                mySocket.send(datagram);
                mySocket.close( );
            } // end try
            catch (Exception ex) {
                ex.printStackTrace( );
            }
        } // end else
    }
}
