package com.egehanasal.contactsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.egehanasal.contactsapp.databinding.ActivityDetailsBinding
import com.egehanasal.contactsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contactList: ArrayList<Contact>

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        contactList = ArrayList<Contact>()

        val contact1 = Contact("happy", "face", 23, R.drawable.face)
        val contact2 = Contact("normal", "face2", 22, R.drawable.facetwo)
        val contact3 = Contact("sad", "face3", 23, R.drawable.facethree)

        contactList.add(contact1)
        contactList.add(contact2)
        contactList.add(contact3)

        binding.recyclerViewTextView.layoutManager = LinearLayoutManager(this)
        val contactAdapter = ContactAdapter(contactList)
        binding.recyclerViewTextView.adapter = contactAdapter
    }
}