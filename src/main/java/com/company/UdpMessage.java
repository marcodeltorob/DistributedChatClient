package com.company;
/**
 * Created by marcodeltoro on 5/13/17.
 */


public class UdpMessage {
    public String tag;
    public String nickname;
    public String message;
    public String to_nickname;
    public String ip;
    public String port;


    @Override
    public String toString() {
        return tag + " - " + nickname + " - " + message + " - " + to_nickname + " - " + ip + " - " + port;
    }
}

