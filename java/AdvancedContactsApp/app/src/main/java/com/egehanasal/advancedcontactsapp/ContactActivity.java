package com.egehanasal.advancedcontactsapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.egehanasal.advancedcontactsapp.databinding.ActivityContactBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.Currency;

public class ContactActivity extends AppCompatActivity {

    SQLiteDatabase database;
    private ActivityContactBinding binding;
    private boolean flag = false;
    private boolean imageFlag;

    ActivityResultLauncher <Intent> activityResultLauncher;
    ActivityResultLauncher <String> permissionLauncher;

    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();
        imageFlag = false;

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
                int imageIndex = cursor.getColumnIndex("image");

                while(cursor.moveToNext()) {
                    binding.nameText.setText(cursor.getString(nameIndex));
                    binding.surnameText.setText(cursor.getString(surnameIndex));
                    binding.phoneText.setText(cursor.getString(phoneNumberIndex));

                    byte[] bytes = cursor.getBlob(imageIndex);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);
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

        if(name.equals("")) {
            Toast.makeText(ContactActivity.this, "Contact must have name", Toast.LENGTH_LONG).show();
            return;
        }

        if(!imageFlag) {
            selectedImage = BitmapFactory.decodeResource(ContactActivity.this.getResources(), R.drawable.defaultpicture);
        }
        Bitmap smallImage = shrinkImage(selectedImage, 300);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArr = outputStream.toByteArray();

        try {
            database = this.openOrCreateDatabase("ContactDatabase", MODE_PRIVATE, null); //, image BLOB
            database.execSQL("CREATE TABLE IF NOT EXISTS contactInfo (id INTEGER PRIMARY KEY, name VARCHAR, surname VARCHAR, phoneNumber VARCHAR, image BLOB)");

            String sqlString = "INSERT INTO contactInfo (name, surname, phoneNumber, image) VALUES (?,?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, name);
            sqLiteStatement.bindString(2, surname);
            sqLiteStatement.bindString(3, phoneNumber);
            sqLiteStatement.bindBlob(4, byteArr);
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

            Bitmap smallImage = shrinkImage(selectedImage, 300);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            byte[] byteArr = outputStream.toByteArray();

            String sqlString = "UPDATE contactInfo SET name = ?, surname=?, phoneNumber=?, image=? WHERE id=" + contact_id;
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, new_name);
            sqLiteStatement.bindString(2, new_surname);
            sqLiteStatement.bindString(3, new_phone_number);
            sqLiteStatement.bindBlob(4, byteArr);
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

    public void selectImage(View view) {
        // İzin verilmemiş ise
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // izin iste
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }
            else {
                // izin iste
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else {
            // Galeriye git
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }

    private void registerLauncher() {

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null) {
                        Uri imageData = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData));
                        try {
                            if(Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                                imageFlag = true;
                            }
                            else {
                                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                                imageFlag = true;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result) {
                    // izin iste
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
                else {
                    // izin reddedildi
                    Toast.makeText(ContactActivity.this, "Permission needed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public Bitmap shrinkImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        }
        else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }
}