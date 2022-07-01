package com.egehanasal.contactsapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egehanasal.contactsapp.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {

    ArrayList<Contact> contactArrayList;

    public ContactAdapter(ArrayList<Contact> contactArrayList) {
        this.contactArrayList = contactArrayList;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ContactHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        String s = contactArrayList.get(position).getName() + " " + contactArrayList.get(position).getSurname();
        holder.binding.recyclerViewTextView.setText(s);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ContactInfoActivity.class);
                Singleton singleton = Singleton.getInstance();
                singleton.setSentContact(contactArrayList.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder{

        private RecyclerRowBinding binding;

        public ContactHolder(RecyclerRowBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
