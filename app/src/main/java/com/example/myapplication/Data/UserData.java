package com.example.myapplication.Data;

public class UserData {
    public UserData(){}

    private String idToken;
    private String emailId;
    private String name;
    private String Birth;
    private String password;

    //Getter Method
    public String getIdToken(){return idToken;}
    public String getEmailId(){return emailId;}
    public String getName(){return name;}
    public String getBirth(){return Birth;}
    public String getPassword(){return password;}

    //Setter Method
    public void setIdToken(String idToken){
        this.idToken = idToken;
    }
    public void setEmailId(String emailId){this.emailId = emailId;}
    public void setName(String emailId){this.emailId = name;}
    public void setBirth(String emailId){this.emailId = Birth;}
    public void setPassword(String emailId){this.emailId = password;}




}
