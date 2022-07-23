package com.egehanasal.contactsapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egehanasal.contactsapp.databinding.RecyclerRowBinding

class ContactAdapter (private val contactList: ArrayList<Contact>) : RecyclerView.Adapter <ContactAdapter.ContactHolder>() {

    private lateinit var recyclerViewString: String
    class ContactHolder (val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        recyclerViewString = contactList.get(position).name + " " + contactList.get(position).surname
        // contactList.get(position) yerine contactList[position] da yazılabiliyor.
        holder.binding.recyclerRowTextView.text = recyclerViewString

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsActivity :: class.java)
            intent.putExtra("contact", contactList[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}