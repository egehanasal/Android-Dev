package com.egehanasal.contactsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.egehanasal.contactsapp.databinding.ActivityContactInfoBinding;

public class ContactInfoActivity extends AppCompatActivity {

    private ActivityContactInfoBinding binding;
    Contact selectedContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        Singleton singleton = Singleton.getInstance();
        selectedContact = singleton.getSentContact();

        binding.nameText.setText("Name: " + selectedContact.getName());
        binding.surnameText.setText("Surname: " + selectedContact.getSurname());
        binding.phoneNumberText.setText("+90 " + selectedContact.getPhoneNumber());
        binding.photo.setImageResource(selectedContact.getPhoto());
    }
}