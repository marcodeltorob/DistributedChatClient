package com.company;

import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by marcodeltoro on 5/7/17.
 */
public class PostNewStatus {
    private JPanel postPanel;
    private JTextField postJTextField;
    private JButton postButton;
    private JFrame postNewStatusFrame;

    public PostNewStatus(final FacebookClient fbClient) {

        renderUI();
        postButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handlePost(fbClient);
            }
        });
    }


    private void renderUI(){

        postNewStatusFrame = new JFrame("Post new status");
        postNewStatusFrame.setContentPane(postPanel);
        postNewStatusFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        postNewStatusFrame.pack();
        postNewStatusFrame.setVisible(true);

    }

    private void handlePost(FacebookClient fbClient) {

        String message = postJTextField.getText();

        if(!message.equals("") && message != null){
            FacebookType response =  fbClient.publish("me/feed", FacebookType.class, Parameter.with("message", message));
            System.out.println("fb.com/"+response.getId());
            JOptionPane.showMessageDialog(null, "Post URL: fb.com/"+response.getId());
            postNewStatusFrame.dispose();

        }else{
            JOptionPane.showMessageDialog(null, "Message can not be empty");
        }

    }

}
