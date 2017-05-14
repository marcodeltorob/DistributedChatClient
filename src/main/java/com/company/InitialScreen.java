package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.restfb.FacebookClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.StringReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by marcodeltoro on 5/7/17.
 */
public class InitialScreen {
    private JPanel InitialPanel;
    private JButton newBroadcastButton;
    private JButton refreshUsersButton;
    private JButton refreshStatusButton;
    private JTable usersTable;
    private JLabel userInfoJLabel;
    private JLabel infoServerLabel;
    private JFrame initialScreen;
    private FacebookClient fbClient;
    List<String> onlineUsersList;
    DefaultTableModel onlineUsersTableModel;

    public InitialScreen(String name, final String ipServer , FacebookClient fbClient) {

        this.fbClient = fbClient;
        renderUI(name,ipServer);
        addActionListeners();

        usersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) { // check if a double click
                    // your code here
                    int i = usersTable.getSelectedRow();
                    openChatScreen(onlineUsersList.get(i), ipServer);

                }
            }
        });

        refreshUsersButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                requestUsers();

            }
        });
    }

    private void addActionListeners() {
        refreshStatusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshStatusHandler();
            }
        });
    }

    private void refreshStatusHandler() {
        new PostNewStatus(fbClient);
    }

    public void requestUsers(){

        int timeout = 10000;
        DatagramSocket mySocket = null;
        InetAddress receiverHost;
        DatagramPacket datagram;



        UdpMessage messageToSend = new UdpMessage();
        messageToSend.tag = "REQUEST_USERS";


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
            //mySocket.setSoTimeout(timeout);
            byte[] bufferReceive = new byte[512];
            DatagramPacket responseDatagramPacket = new DatagramPacket(bufferReceive, bufferReceive.length);

            try {
                mySocket.receive(responseDatagramPacket);
                String s = new String(responseDatagramPacket.getData(), 0, responseDatagramPacket.getLength());
                System.out.println(s);
                UdpMessage responseUdpMessage = gson.fromJson(s, UdpMessage.class);
                if(responseUdpMessage.tag.equals("OK_REQUEST_USERS")) {
                    onlineUsersList = Arrays.asList(responseUdpMessage.message.split(","));

                    clearTable(onlineUsersTableModel);
                        for (String username: onlineUsersList) {
                            addUserToTable(username);
                        }

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

    private void clearTable(DefaultTableModel dm) {

        int rowCount = dm.getRowCount();

        for (int i = rowCount - 1; i >= 0; i--) {
            dm.removeRow(i);
        }

    }

    private void setupOnlineUsersJTable() {

        class MyTableModel extends DefaultTableModel {
            public boolean isCellEditable(int row, int column){
                return false;
            }
        }


        onlineUsersTableModel = new MyTableModel();
        onlineUsersTableModel.addColumn("Username");
        usersTable.setModel(onlineUsersTableModel);

    }

    private void addUserToTable(String s) {
        onlineUsersTableModel.addRow(new Object[]{s});
    }


    private void renderUI(String name,String ipServer){

        requestUsers();
        setupOnlineUsersJTable();
        initialScreen = new JFrame("InitialScreen");
        initialScreen.setContentPane(InitialPanel);
        initialScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialScreen.setResizable(false);
        initialScreen.pack();
        initialScreen.setVisible(true);


        String ip  = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        userInfoJLabel.setText(name + " "+ "<" + ip + ">");
        infoServerLabel.setText("Server: <" + ipServer + ">");




    }


    private void openChatScreen(String name, String ipServer) {

        new Chat(name, ipServer);

    }


    private void setToReceiveMessages(final InitialScreen initialScreen) {

        new Thread(new Runnable() {
            public void run() {
                int port = 8000;
                final int MAX_LEN = 512;
                UdpMessage udpMessage = null;
                String jsonMessage;
                DatagramSocket mySocket = null;

                while (true) {
                    try {
                        mySocket = new DatagramSocket(port);

                        byte[] buffer = new byte[MAX_LEN];
                        DatagramPacket datagram = new DatagramPacket(buffer, MAX_LEN);

                        mySocket.receive(datagram);

                        jsonMessage = new String(datagram.getData(), 0, datagram.getLength());

                        Gson gson = new GsonBuilder().create();
                        udpMessage = gson.fromJson(jsonMessage, UdpMessage.class);


                        udpMessage.ip = datagram.getAddress().getHostAddress();

                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        mySocket.close();
                    }

                    System.out.println("Message received: " + udpMessage);

                    ClientBinder s = new ClientBinder(udpMessage, initialScreen );
                    s.start();

                } // End while
            }
        }).start();

    }


}
