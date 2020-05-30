package com.company;

import java.util.List;

public class User  {
    private String name;
    private String surname;
    private Account account;
    private Car  car;
    public void setCar(final Car car){this.car=car;}
    public Car getCar(){return this.car;}
    public void setName(String name){this.name=name;}
    public String getName(){return this.name;}
    public void setSurname(String surname){this.surname=surname;}
    public String getSurname(){return this.surname;}
    public Account getAccount(){return this.account;}
    User(){ account=new Account();}
    public String getUserData() {
        return (this.getName()+"\n"+this.getSurname()+"\n"+this.account.getLogin()+"\n"+this.account.getPassword()+"\n");
    }
    public static String findLogin(final List<User> users,String surname){
        for(User user:users){
            if(user.getSurname().equalsIgnoreCase(surname)){
                return user.getAccount().getLogin();
            }
        }
        return surname;
    }
}
