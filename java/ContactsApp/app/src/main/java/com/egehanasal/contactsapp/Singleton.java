package com.egehanasal.contactsapp;

public class Singleton {

    private Contact sentContact;
    private static Singleton singleton;

    private Singleton(){

    }

    public Contact getSentContact(){
        return this.sentContact;
    }

    public void setSentContact(Contact contact){
        this.sentContact = contact;
    }

    public static Singleton getInstance(){
        if(singleton == null){
            singleton = new Singleton();
        }
        return singleton;
    }
}
