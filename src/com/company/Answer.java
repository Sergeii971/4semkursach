package com.company;

public class Answer {
    private Account account;
    private String time;
    private String service;
    private String price;
    public void setPrice(String price) { this.price = price; }
    public String getPrice() { return price; }
    public void setTime(String time) { this.time = time; }
    public String getTime() { return time; }
    public void setAccount(Account account) { this.account = account; }
    public Account getAccount() { return account; }
    public void setService(String service) { this.service = service; }
    public String getService() { return service; }
    public boolean equals(String login){
         if(this.getAccount().getLogin().equals(login)){
             return true;
         }
     return false;
    }
}
