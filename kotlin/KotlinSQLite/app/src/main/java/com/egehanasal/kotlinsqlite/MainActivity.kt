
package com.egehanasal.kotlinsqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.egehanasal.kotlinsqlite.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var contactList: ArrayList<Contact>
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        contactList = ArrayList<Contact>()
        contactAdapter = ContactAdapter(contactList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = contactAdapter


        try {
            val database = this.openOrCreateDatabase("ContactDatabase", MODE_PRIVATE, null)
            val cursor = database.rawQuery("SELECT * FROM contacts", null)
            val id_index = cursor.getColumnIndex("id")
            val name_index = cursor.getColumnIndex("name")
            val surname_index = cursor.getColumnIndex("surname")

            while(cursor.moveToNext()) {
                val name = cursor.getString(name_index)
                val surname = cursor.getString(surname_index)
                val id = cursor.getInt(id_index)

                val fullName = name.plus(" ").plus(surname)
                val contact = Contact(id, fullName)
                contactList.add(contact)
            }
            contactAdapter.notifyDataSetChanged()
            cursor.close()
        } catch(e: Exception) {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_contact) {
            val intent = Intent(this@MainActivity, DetailsActivity :: class.java)
            intent.putExtra("info", "new")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}