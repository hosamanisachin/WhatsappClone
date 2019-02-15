package com.example.acer.whatsappclone;

public class UserObject {
    private String name, phone;

    public UserObject(String name, String phone){
        this.name = name;
        this.phone = phone;
    }

    //getter

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }
}
