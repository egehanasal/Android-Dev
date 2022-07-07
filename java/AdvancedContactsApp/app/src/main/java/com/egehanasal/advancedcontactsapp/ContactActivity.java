package com.egehanasal.advancedcontactsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.egehanasal.advancedcontactsapp.databinding.ActivityContactBinding;

import java.util.Currency;

public class ContactActivity extends AppCompatActivity {

    SQLiteDatabase database;
    private ActivityContactBinding binding;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if(info.matches("old")) {
            flag = true;
            binding.updateButton.setVisibility(View.INVISIBLE);
            binding.saveButton.setVisibility(View.INVISIBLE);
            int contact_id = intent.getIntExtra("id", 0);
            try {
                database = this.openOrCreateDatabase("ContactDatabase", MODE_PRIVATE, null);
                Cursor cursor = database.rawQuery("SELECT * FROM contactInfo WHERE id = ?", new String[] {String.valueOf(contact_id)});

                int nameIndex = cursor.getColumnIndex("name");
                int surnameIndex = cursor.getColumnIndex("surname");
                int phoneNumberIndex = cursor.getColumnIndex("phoneNumber");

                while(cursor.moveToNext()) {
                    binding.nameText.setText(cursor.getString(nameIndex));
                    binding.surnameText.setText(cursor.getString(surnameIndex));
                    binding.phoneText.setText(cursor.getString(phoneNumberIndex));
                }
                cursor.close();
            } catch (Exception e) {

            }
        }
        else if(info.matches("new")){
            flag = false;
            binding.updateButton.setVisibility(View.INVISIBLE);
            binding.nameText.setText("");
            binding.surnameText.setText("");
            binding.phoneText.setText("");
            binding.saveButton.setVisibility(View.VISIBLE);
        }
    }

    public void save(View view) {
        String name = binding.nameText.getText().toString();
        String surname = binding.surnameText.getText().toString();
        String phoneNumber = binding.phoneText.getText().toString();

        try {
            database = this.openOrCreateDatabase("ContactDatabase", MODE_PRIVATE, null); //, image BLOB
            database.execSQL("CREATE TABLE IF NOT EXISTS contactInfo (id INTEGER PRIMARY KEY, name VARCHAR, surname VARCHAR, phoneNumber VARCHAR)");

            String sqlString = "INSERT INTO contactInfo (name, surname, phoneNumber) VALUES (?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, name);
            sqLiteStatement.bindString(2, surname);
            sqLiteStatement.bindString(3, phoneNumber);
            //sqLiteStatement.bindString(4, null);
            sqLiteStatement.execute();

            Cursor cursor = database.rawQuery("SELECT * FROM contactInfo", null);

            int name_index = cursor.getColumnIndex("name");
            while(cursor.moveToNext()){
                System.out.println("Name: " + cursor.getString(name_index));
            }
            cursor.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(ContactActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void update(View view) {
        Intent intent = getIntent();
        try{
            database = this.openOrCreateDatabase("ContactDatabase", MODE_PRIVATE, null);
            int contact_id = intent.getIntExtra("id", 0);
            System.out.println(contact_id);

            String new_name = binding.nameText.getText().toString();
            String new_surname = binding.surnameText.getText().toString();
            String new_phone_number = binding.phoneText.getText().toString();
            String sqlString = "UPDATE contactInfo SET name = ?, surname=?, phoneNumber=? WHERE id=" + contact_id;

            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, new_name);
            sqLiteStatement.bindString(2, new_surname);
            sqLiteStatement.bindString(3, new_phone_number);
            sqLiteStatement.execute();

        } catch(Exception e) {
            e.printStackTrace();
        }

        intent = new Intent(ContactActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void deleteContact() {
        Intent intent = getIntent();
        try {
            database = this.openOrCreateDatabase("ContactDatabase", MODE_PRIVATE, null);
            int contact_id = intent.getIntExtra("id", 0);
            String sqlString = "DELETE FROM contactInfo WHERE id=" + contact_id;
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        intent = new Intent(ContactActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(flag){
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.edit_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.edit_contact) {
            binding.updateButton.setVisibility(View.VISIBLE);
        }
        else if(item.getItemId() == R.id.delete_contact) {
            deleteContact();
        }
        return super.onOptionsItemSelected(item);
    }
}