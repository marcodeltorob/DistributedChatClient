package com.company;

/**
 * Created by marcodeltoro on 5/14/17.
 */
public class ClientBinder extends Thread {

    UdpMessage udpMessage;
    InitialScreen initialScreen;



    public ClientBinder(UdpMessage udpMessage, InitialScreen InitialScreen) {
        this.udpMessage = udpMessage;
        this.initialScreen = initialScreen;

    }

    public void run() {



        if ("FORWARD_PRIVATE_MSG".equals(udpMessage.tag)) {



        } else if ("FORWARD_BROADCAST_MSG".equals(udpMessage.tag)) {



        } else  {

        }
    }



}
