package com.example.palapp;

import android.icu.text.Transliterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.chatviewholder> {

    public ArrayList<NachrichtItem> mNachrichtItems ;

    public  onItemClickListener mListener;

    public  class chatviewholder extends RecyclerView.ViewHolder implements  View.OnClickListener{
       public TextView mSender ;
       public  TextView mMessage ;
       public  TextView mDate ;


        public chatviewholder(@NonNull View itemView) {
            super(itemView);

           mSender = itemView.findViewById(R.id.Sender_id);
           mMessage = itemView.findViewById(R.id.Nachricht_id);
           mDate = itemView.findViewById(R.id.Date_id);
           itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v,getAdapterPosition());
        }
    }



    public chatAdapter(ArrayList<NachrichtItem> NachrichtItems , onItemClickListener listener){
        mNachrichtItems = NachrichtItems;
        this.mListener = listener;
    }


    @NonNull
    @Override
    public chatviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item , parent,false);
        chatviewholder holder = new chatviewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull chatviewholder holder, int position) {
        NachrichtItem current = mNachrichtItems.get(position);
        holder.mSender.setText(current.getSender());
        holder.mMessage.setText(current.getMessage());
        holder.mDate.setText(current.getDateTime());
    }



    @Override
    public int getItemCount() {
        return mNachrichtItems.size();
    }

    public void updateItems(ArrayList<NachrichtItem> nachrichtItems){
       mNachrichtItems = nachrichtItems;
    }


    public interface onItemClickListener{
        void onItemClick( View view , int position);
    }

}
