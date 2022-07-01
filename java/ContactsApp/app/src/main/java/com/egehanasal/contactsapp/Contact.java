package com.egehanasal.contactsapp;

public class Contact {

    private String name;
    private String surname;
    private String phoneNumber;
    private int photo;

    public Contact(String name, String surname, String phoneNumber, int photo) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getPhoto(){
        return photo;
    }
}
