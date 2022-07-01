package com.egehanasal.contactsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.egehanasal.contactsapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    static ArrayList <Contact> contactArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        contactArrayList = new ArrayList<>();

        Contact contact1 = new Contact("name1", "surname1", "12345678", R.drawable.colloseum);
        Contact contact2 = new Contact("name2", "surname2", "98765432", R.drawable.eiffel);
        Contact contact3 = new Contact("name3", "surname3", "13244657", R.drawable.londonbridge);
        Contact contact4 = new Contact("name4", "surname4", "47585123", R.drawable.pisa);

        contactArrayList.add(contact1);
        contactArrayList.add(contact2);
        contactArrayList.add(contact3);
        contactArrayList.add(contact4);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ContactAdapter contactAdapter = new ContactAdapter(contactArrayList);
        binding.recyclerView.setAdapter(contactAdapter);
    }
}