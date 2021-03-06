package com.egehanasal.contactsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.egehanasal.contactsapp.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent = intent
        /*
        val selectedContact = intent.getSerializableExtra("contact") as Contact
         */

        val selectedContact = MySingleton.chosenContact

        selectedContact?.let {
            binding.nameText.text = it.name
            binding.surnameText.text = it.surname
            binding.ageText.text = it.age.toString()
            binding.imageView.setImageResource(it.Image)

            /* Bu şekilde de yazılabilir
            binding.nameText.text = selectedContact!!.name
            binding.surnameText.text = selectedContact!!.surname
            binding.ageText.text = selectedContact!!.age.toString()
            binding.imageView.setImageResource(selectedContact!!.Image)
             */
        }
    }
}