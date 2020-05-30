package com.company;

import java.io.IOException;

public class Account {
    Account(){}
    private String login;
    private String password;
    private final String secondPassword="333419161";
    public void setLogin(String login){this.login=login;}
    public String getLogin(){return this.login;}
    public void setPassword(String password){this.password=password;}
    public String getPassword(){return this.password;}
    public String getSecondPassword(){return this.secondPassword;}
    public byte[] getLoginPasswordBytes() {
        return (this.getLogin()+"\n"+this.getPassword()).getBytes();
    }
    public int comparison(String login,String password, int a) throws IOException {
        if(this.getLogin().equals(login) && this.getPassword().equals(password)) {
            if(a==1) return 1;
            else return 2;
        }
        else
        if(password.equals(this.getSecondPassword()) && a==1) {
            return 1;
        }
return 0;
    }

}
