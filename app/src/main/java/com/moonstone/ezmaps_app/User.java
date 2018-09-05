package com.moonstone.ezmaps_app;

public class User {
    private String name, email;
    private String imageLink;

    public User(){

    }

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getImageLink(){
        return imageLink;
    }


}




