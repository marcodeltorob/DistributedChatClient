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
    private String ipServer;
    private String username;

    List<String> onlineUsersList;
    DefaultTableModel onlineUsersTableModel;
    ArrayList<Chat> openChatsWindows = new ArrayList<Chat>();
    public InitialScreen(String username, final String ipServer , FacebookClient fbClient) {

        this.fbClient = fbClient;
        this.ipServer = ipServer;
        this.username = username;

        renderUI(username, ipServer);
        addActionListeners(ipServer);

        setToReceiveMessages(this);

    }


    public String getUsername() {
        return username;
    }

    public String getIpServer() {
        return ipServer;
    }


    private void addActionListeners(final String ipServer) {
        refreshStatusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshStatusHandler();
            }
        });

        usersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) { // check if a double click
                    // your code here
                    int i = usersTable.getSelectedRow();
                    String selectedUser = onlineUsersList.get(i);

                    Chat auxChat = getOpenChatOrNull(selectedUser);

                    if (auxChat == null) {
                        openChatScreen(selectedUser, ipServer);
                    }

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

        newBroadcastButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                sendBroadcastMessage();
            }
        });


    }

    private void sendBroadcastMessage() {

        int timeout = 500;
        DatagramSocket mySocket = null;
        InetAddress receiverHost;
        DatagramPacket datagram;

        String message = JOptionPane.showInputDialog("Please type your message");
        UdpMessage messageToSend = new UdpMessage();

        messageToSend.tag = "BROADCAST_MSG";
        messageToSend.nickname = username;
        messageToSend.message = message;
        System.out.println(messageToSend);

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
                if(responseUdpMessage.tag.equals("OK_BROADCAST_MSG")) {
                    JOptionPane.showMessageDialog(null, responseUdpMessage.message, "Broadcast Message", JOptionPane.INFORMATION_MESSAGE);

                }else{

                }
            }
            catch (SocketTimeoutException e) {
                // timeout exception.
                System.out.println("1 seconds timeout for user reached in brodcast" + e);
            }
        } // end try
        catch (Exception ex) {
            ex.printStackTrace( );
        }finally {
            mySocket.close();
        }


    }

    public Chat getOpenChatOrNull(String key) {

        for (Chat chat: openChatsWindows) {
            if(chat.getKeyChat().equals(key)){
                return chat;
            }
        }
        return null;
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

        //requestUsers();
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


    public Chat openChatScreen(String name, String ipServer) {

        Chat chat =  new Chat(name, ipServer, username, this);
        openChatsWindows.add(chat);
        return chat;

    }


    private void setToReceiveMessages(final InitialScreen initialScreen) {

        new Thread(new Runnable() {
            public void run() {
                int port = 9000; // Listener ChatClient
                final int MAX_LEN = 512;
                UdpMessage udpMessage = null;
                String jsonMessage;
                DatagramSocket mySocket = null;
                int auxPort = 0;

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
                        auxPort = datagram.getPort();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        mySocket.close();
                    }

                    System.out.println("Message received: " + udpMessage);

                    ClientBinder s = new ClientBinder(udpMessage, initialScreen, auxPort);
                    s.start();

                } // End while
            }
        }).start();

    }

    public void removeOpenChatWindows(Chat chat) {

        System.out.println(openChatsWindows);
        openChatsWindows.remove(chat);
        System.out.println(openChatsWindows);
    }

}
