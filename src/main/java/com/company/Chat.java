package com.company;

import com.google.gson.Gson;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by marcodeltoro on 5/7/17.
 */
public class Chat {
    private JButton sendFileButton;
    private JTextPane writeTextPane;
    private JTextPane readTextPane;
    private JButton blockButton;
    private JPanel chatPanel;
    private JLabel infoLabel;
    private JLabel infoServerLabel;
    private JButton sendMessageButton;
    private JFrame chatScreen;
    private String to_nickname;

    public Chat(String name, String ipServer){

        this.to_nickname = name;
        renderUI(name, ipServer);

        setListeners();
    }

    private void setListeners() {
        blockButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                blockButtonHandler();
            }
        });
        sendMessageButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                sendMessageButtonHandler();
            }
        });
        sendFileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                sendFileButtonHandler();
            }
        });
    }


    private void sendFileButtonHandler() {


    }

    private void sendMessageButtonHandler() {

        int timeout = 50000;
        DatagramSocket mySocket = null;
        InetAddress receiverHost;
        DatagramPacket datagram;



        UdpMessage messageToSend = new UdpMessage();
        messageToSend.tag = "PRIVATE_MSG";
        messageToSend.to_nickname = to_nickname;
        messageToSend.message = writeTextPane.getText();

        Gson gson = new Gson();
        String json = gson.toJson(messageToSend);

        try {
            receiverHost = InetAddress.getByName("192.168.100.8");
            int receiverPort = Integer.parseInt("8000");


            // instantiates a datagram socket for sending the data
            mySocket = new DatagramSocket();
            byte[ ] buffer = json.getBytes( );
            datagram =
                    new DatagramPacket(buffer, buffer.length,
                            receiverHost, receiverPort);
            mySocket.send(datagram);
            //mySocket.close( );

            // Wait for OK Forward Private Message
            mySocket.setSoTimeout(timeout);
            byte[] bufferReceive = new byte[512];
            DatagramPacket responseDatagramPacket = new DatagramPacket(bufferReceive, bufferReceive.length);

            try {
                mySocket.receive(responseDatagramPacket);
                String s = new String(responseDatagramPacket.getData(), 0, responseDatagramPacket.getLength());
                System.out.println(s);
                UdpMessage responseUdpMessage = gson.fromJson(s, UdpMessage.class);
                if(responseUdpMessage.tag.equals("OK_PRIVATE_MSG")) {


                }else{
                    JOptionPane.showMessageDialog(null, "Unable to Login try later", "Unable Login",JOptionPane.INFORMATION_MESSAGE);
                }
            }
            catch (SocketTimeoutException e) {
                // timeout exception.
                System.out.println("5 seconds timeout for user reached in brodcast" + e);
            }

        } // end try
        catch (Exception ex) {
            ex.printStackTrace( );
        }finally {
            mySocket.close();
        }

    }

    private void blockButtonHandler() {


    }

    private void renderUI(String name, String ipServer){

        chatScreen = new JFrame("Chat");
        chatScreen.setContentPane(chatPanel);
        chatScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatScreen.setResizable(false);
        chatScreen.pack();
        chatScreen.setVisible(true);

        infoLabel.setText(name);
        infoServerLabel.setText("Server: <" + ipServer + ">");

    }


    public void appendToChat(String s){
        String sdf = new SimpleDateFormat("HH:mm:ss").format(new Date());
        readTextPane.setText(sdf + " "+ infoLabel.getText() +":  " + s + "\n");
    }


}
