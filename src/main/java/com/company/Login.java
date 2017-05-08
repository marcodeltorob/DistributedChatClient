package com.company;

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
import java.util.Locale;

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

        openInitialScreen("Marco Del Toro", null);
//        openInitialScreen(user.getName(), fbClient);


    }

    private void openInitialScreen(String name, FacebookClient fbClient) {

        mainFrame.dispose();
        new InitialScreen(name, fbClient);

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
