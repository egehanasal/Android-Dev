package com.egehanasal.advancedcontactsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.egehanasal.advancedcontactsapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ArrayList<Contact> contacts;
    ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        contacts = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new ContactAdapter(contacts);
        binding.recyclerView.setAdapter(contactAdapter);

        getData();
    }

    // Menu'yu MainActivity'e bağladık
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.contact_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Menu'deki ilgili alana tıklanınca ne olacak
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_contact) {
            Intent intent = new Intent(MainActivity.this, ContactActivity.class);
            intent.putExtra("info", "new");
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.delete_all) {
            contacts.clear();
            contactAdapter.notifyDataSetChanged();
            SQLiteDatabase database = this.openOrCreateDatabase("ContactDatabase", MODE_PRIVATE, null);
            String sqlString = "DELETE FROM contactInfo";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getData() {
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("ContactDatabase", MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT * FROM contactInfo", null);
            int nameIndex = cursor.getColumnIndex("name");
            int idIndex = cursor.getColumnIndex("id");
            while(cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                int id = cursor.getInt(idIndex);
                Contact contact = new Contact(name, id);
                contacts.add(contact);
            }

            contactAdapter.notifyDataSetChanged();
            cursor.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}