package com.company;

import com.google.gson.Gson;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by marcodeltoro on 5/14/17.
 */
public class ClientBinder extends Thread {

    UdpMessage udpMessage;
    ArrayList<Chat> openChats = new ArrayList<Chat>();
    InitialScreen initialScreen;
    int auxPort;




    public ClientBinder(UdpMessage udpMessage, InitialScreen initialScreen, int auxPort) {
        this.udpMessage = udpMessage;
        this.initialScreen = initialScreen;
        this.auxPort = auxPort;
    }

    public void run() {

        if ("FORWARD_PRIVATE_MSG".equals(udpMessage.tag)) {

            privateMessageHandler();

        } else if ("FORWARD_BROADCAST_MSG".equals(udpMessage.tag)) {

            broadcastMessageHandler();

        } else if ("FORWARD_FILE".equals(udpMessage.tag)){
            fordwardFileHandler();
        }else  {

        }
    }

    private void fordwardFileHandler() {
        FileClient fileClient = new FileClient();
        fileClient.start();
    }

    private void broadcastMessageHandler() {

        int timeout = 10000;
        DatagramSocket mySocket = null;
        InetAddress receiverHost;
        DatagramPacket datagram;


        UdpMessage messageToSend = new UdpMessage();
        messageToSend.tag = "OK_FORWARD_BROADCAST_MSG";

        Gson gson = new Gson();
        String json = gson.toJson(messageToSend);

        try {
            receiverHost = InetAddress.getByName("192.168.100.8");
            int receiverPort = auxPort;
            // instantiates a datagram socket for sending the data
            mySocket = new DatagramSocket();
            byte[] buffer = json.getBytes();
            datagram =
                    new DatagramPacket(buffer, buffer.length,
                            receiverHost, receiverPort);
            mySocket.send(datagram);
            //mySocket.close( );
        }catch (Exception ex) {
            ex.printStackTrace( );
        }finally {
            mySocket.close();
        }
        JOptionPane.showMessageDialog(null,udpMessage.message,"Broadcast Message",JOptionPane.INFORMATION_MESSAGE);

    }

    private void privateMessageHandler() {


        int timeout = 10000;
        DatagramSocket mySocket = null;
        InetAddress receiverHost;
        DatagramPacket datagram;

        List<String> keys = Arrays.asList(udpMessage.message.split(":"));


        System.out.println(keys);

        String name = keys.get(0);
        String message = keys.get(1);
        Chat auxChat = initialScreen.getOpenChatOrNull(name);



        if (auxChat != null) {
            auxChat.appendToChat(name + ": " + message);
        } else {
            auxChat = initialScreen.openChatScreen(name, initialScreen.getIpServer());
            auxChat.appendToChat(name + ": " + message);
        }

        UdpMessage messageToSend = new UdpMessage();
        messageToSend.tag = "OK_FORWARD_PRIVATE_MSG";

        Gson gson = new Gson();
        String json = gson.toJson(messageToSend);

        try {
            receiverHost = InetAddress.getByName("192.168.100.8");
            int receiverPort = auxPort;
            // instantiates a datagram socket for sending the data
            mySocket = new DatagramSocket();
            byte[] buffer = json.getBytes();
            datagram =
                    new DatagramPacket(buffer, buffer.length,
                            receiverHost, receiverPort);
            mySocket.send(datagram);
            //mySocket.close( );
        }catch (Exception ex) {
                ex.printStackTrace( );
        }finally {
                mySocket.close();
        }

    }

    public class FileClient extends Thread {
        public void run() {

            Socket socket;
            OutputStream out;
            InputStream in;
            try {
                socket = new Socket(udpMessage.message, 3000);
                in = socket.getInputStream();
                out = new FileOutputStream("recv.txt");
                copy(in, out);
                out.close();
                in.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void copy(InputStream in, OutputStream out) throws IOException {
            byte[] buf = new byte[8192];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        }
    }


}
