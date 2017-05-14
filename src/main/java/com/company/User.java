package com.company;

/**
 * Created by marcodeltoro on 5/13/17.
 */
public  class User {
    private String ip;
    private String nickname;

    public User(String ip, String nickname) {
        this.ip = ip;
        this.nickname = nickname;
    }

    public User(String nickname){
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return ip + " - " + nickname;
    }

}
