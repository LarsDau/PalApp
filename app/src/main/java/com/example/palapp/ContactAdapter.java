package com.example.palapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private ArrayList<ContactItem> mContactList;

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView textView1;
        public TextView textView2;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.contactName);
            textView2 = itemView.findViewById(R.id.lastMessage);
        }
    }

    public ContactAdapter(ArrayList<ContactItem> contactList){
        mContactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        ContactViewHolder contactViewHolder = new ContactViewHolder(v);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactItem current = mContactList.get(position);

        holder.textView1.setText(current.getmText1());
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    public void clear() {
        mContactList.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<ContactItem> list) {
        mContactList.addAll(list);
        notifyDataSetChanged();
    }
}
