package com.company;

import com.restfb.FacebookClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by marcodeltoro on 5/7/17.
 */
public class InitialScreen {
    private JPanel InitialPanel;
    private JButton newBroadcastButton;
    private JButton refreshUsersButton;
    private JButton refreshStatusButton;
    private JTable table1;
    private JLabel userInfoJLabel;
    private JFrame initialScreen;
    private FacebookClient fbClient;

    public InitialScreen(String name, FacebookClient fbClient) {

        this.fbClient = fbClient;

        renderUI(name);
        addActionListeners();

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


    private void renderUI(String name){

        initialScreen = new JFrame("InitialScreen");
        initialScreen.setContentPane(InitialPanel);
        initialScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialScreen.setResizable(false);
        initialScreen.pack();
        initialScreen.setVisible(true);



//        String ip  = null;
//        try {
//            ip = InetAddress.getLocalHost().getHostAddress();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        userInfoJLabel.setText(name + " "+ "<" + ip + ">");



    }

}
