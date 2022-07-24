package com.egehanasal.kotlinsqlite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.egehanasal.kotlinsqlite.databinding.ActivityDetailsBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.lang.Exception

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var database : SQLiteDatabase

    var selectedBitmap : Bitmap? = null
    var smallBitmap: Bitmap? = null
    var contactName: String? = null
    var contactSurname: String? = null
    var birthYear: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = this.openOrCreateDatabase("ContactDatabase", MODE_PRIVATE, null)

        registerLauncher()

        val intent = intent
        val info = intent.getStringExtra("info")
        if(info.equals("new")) {
            binding.nameText.setText("")
            binding.surnameText.setText("")
            binding.yearText.setText("")
            binding.imageView.setImageResource(R.drawable.selectimage)
            binding.saveButton.visibility = View.VISIBLE
        }
        else if(info.equals("old")) {
            binding.saveButton.visibility = View.INVISIBLE
            val selectedId = intent.getIntExtra("id", 1)

            val cursor = database.rawQuery("SELECT * FROM contacts WHERE id = ?", arrayOf(selectedId.toString()))

            val name_index = cursor.getColumnIndex("name")
            val surname_index = cursor.getColumnIndex("surname")
            val year_index = cursor.getColumnIndex("year")
            val image_index = cursor.getColumnIndex("image")

            while(cursor.moveToNext()) {
                binding.nameText.setText(cursor.getString(name_index))
                binding.surnameText.setText(cursor.getString(surname_index))
                binding.yearText.setText(cursor.getString(year_index))

                val byteArray = cursor.getBlob(image_index)
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                binding.imageView.setImageBitmap(bitmap)

            }
            cursor.close()
        }
    }

    fun saveButtonClicked(view: View) {
        contactName = binding.nameText.text.toString()
        contactSurname = binding.surnameText.text.toString()
        birthYear = binding.yearText.text.toString()
        if(selectedBitmap != null) {
            smallBitmap = makeSmallerImage(selectedBitmap!!, 300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap!!.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            try {
                database.execSQL("CREATE TABLE IF NOT EXISTS contacts(id INTEGER PRIMARY KEY, name VARCHAR, surname VARCHAR, year INTEGER, image BLOB)")

                val sqlString = "INSERT INTO contacts(name, surname, year, image) VALUES (?,?,?,?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, contactName)
                statement.bindString(2, contactSurname)
                statement.bindString(3, birthYear)
                statement.bindBlob(4, byteArray)
                statement.execute()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val intent = Intent(this@DetailsActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun makeSmallerImage(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val ratio: Double = width.toDouble()/height
        if(ratio > 1) {
            // landscape
            width = maximumSize
            val scaledHeight = width/ratio
            height = scaledHeight.toInt()
        }
        else {
            // portrait
            height = maximumSize
            val scaledHeight = height*ratio
            width = scaledHeight.toInt()
        }

        return Bitmap.createScaledBitmap(image, width,height,false)
    }

    fun selectImage(view: View) {

        // İzin verilmediyse
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // İzin alma mantığını göstermek
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //rationale
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", View.OnClickListener {
                    // request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }).show()
            }
            else {
                // request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if(intentFromResult != null) {
                    val imageData = intentFromResult.data
                    //binding.imageView.setImageURI(imageData)
                    if(imageData != null) {
                        try {
                            if(Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(this@DetailsActivity.contentResolver, imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                            else {
                                selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageData)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                        }
                        catch(e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            // permission granted
            if(result) {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else {
                Toast.makeText(this@DetailsActivity, "Permission needed!", Toast.LENGTH_LONG).show()
            }
        }
    }
}