package com.company;

import com.google.gson.Gson;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import com.restfb.types.User;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Locale;
import com.company.UdpMessage;

/**
 * Created by marcodeltoro on 5/7/17.
 */
public class Login {

    private JPanel loginPanel;
    private JButton loginButton;
    private String appId = "626879557515365";
    private String accessToken = "";
    private FacebookClient fbClient;
    private static JFrame mainFrame;

    public Login() {
        setActionListeners();
    }

    private void setActionListeners() {
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginButtonHandler();
            }
        });
    }

    private String getDriverName() {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
                return "chromedriver_mac";
            } else if (OS.indexOf("win") >= 0) {
                return "chromedriver_win.exe";
            } else if (OS.indexOf("nux") >= 0) {
                return "chromedriver_linux_64";
            } else {
                return "error";
            }
    }

    private void loginButtonHandler(){

        int timeout = 10000;
        DatagramSocket mySocket = null;
        InetAddress receiverHost;
        DatagramPacket datagram;

//        String domain = "http://google.com/";
//
//        String authUrl = "https://graph.facebook.com/oauth/authorize?type=user_agent&client_id="+appId+"&redirect_uri="+domain+"&scope=user_about_me,publish_actions";
//
//        System.setProperty("webdriver.chrome.driver", getDriverName());
//
//        WebDriver driver = new ChromeDriver();
//        driver.get(authUrl);
//        String accessToken;
//        User user;
//        while(true){
//
//            if(!driver.getCurrentUrl().contains("facebook.com")){
//                String url = driver.getCurrentUrl();
//                accessToken = url.replaceAll(".*#access_token=(.+)&.*", "$1");
//
//                driver.quit();
//
//                fbClient = new DefaultFacebookClient(accessToken);
//                user = fbClient.fetchObject("me",User.class);
//
//                System.out.println(user.getName());
//                break;
//            }
//
//        }


        UdpMessage messageToSend = new UdpMessage();
        messageToSend.tag = "REQUEST_CONNECTION";
        messageToSend.nickname = "Marco Del Toro";

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

            DatagramPacket responseDatagramPacket = new DatagramPacket(buffer, buffer.length);

            try {
                mySocket.receive(responseDatagramPacket);
                UdpMessage responseUdpMessage = gson.fromJson(new String(responseDatagramPacket.getData(), 0, responseDatagramPacket.getLength()), UdpMessage.class);
                if(responseUdpMessage.tag.equals("OK_REQUEST_CONNECTION")) {
                    openInitialScreen("Marco Del Toro", "192.168.100.8",null);
//                  openInitialScreen(user.getName(),"192.168.100.8" ,fbClient);
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

    private void openInitialScreen(String username, String ipServer,FacebookClient fbClient) {

        mainFrame.dispose();
        new InitialScreen(username, ipServer ,fbClient);

    }

    public static void main(String[] main){

        mainFrame = new JFrame("Login");
        mainFrame.setContentPane(new Login().loginPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }


}
