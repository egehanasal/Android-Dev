package com.egehanasal.storingdata;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);

        // Verileri cihazda saklamış olacağız
        // Bu veritabanına sadece bizim app'ten ulaşılacak, private verdik
        sharedPreferences = this.getSharedPreferences("com.egehanasal.storingdata", Context.MODE_PRIVATE);

    }

    public void save(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Save");
        alert.setMessage("Are you sure?");
        alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this,"Saved",Toast.LENGTH_LONG).show();
                if(editText.getText().toString().matches("")){
                    return;
                }
                int age = Integer.parseInt(editText.getText().toString());
                textView.setText("Your age: " + age);

                // Veriyi cihazda saklamış oluyoruz
                sharedPreferences.edit().putInt("storedAge", age).apply();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this,"Not saved", Toast.LENGTH_LONG).show();
            }
        });
        alert.show();
    }

    public void getAge(View view){
        // App başladığı anda en son kaydedilen yaş ekranda olacak, aksi takdirde -1
        int storedAge = sharedPreferences.getInt("storedAge",-1);
        textView.setText("Your age: " + storedAge);
    }

    public void delete(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_LONG).show();
                if (sharedPreferences.getInt("storedAge", -1) != -1) {
                    sharedPreferences.edit().remove("storedAge").apply();
                    textView.setText("Your age:");
                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Not deleted", Toast.LENGTH_LONG).show();
            }
        });

        alert.show();
    }
}


















