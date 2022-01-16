package com.example.website.Emails;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.website.R;

import java.util.ArrayList;

public class EmailsRecy extends RecyclerView.Adapter<EmailsRecy.MyViewHolder> {

    private Context ctx;
    private ArrayList<EmailsAdapter> emails;

    public EmailsRecy(Context ctx){
        this.ctx = ctx;
    }

    public EmailsRecy(Context ctx, ArrayList<EmailsAdapter> emails){
        this.ctx = ctx;
        this.emails = emails;

    }


    @Override
    public EmailsRecy.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(ctx);
        view = inflater.inflate(R.layout.cardforemail,parent,false);
        return new EmailsRecy.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailsRecy.MyViewHolder holder, final int position) {
        holder.email_Topic.setText(emails.get(position).getTopic());
        holder.email_Message.setText(emails.get(position).getMessage());
        holder.email_Date.setText(emails.get(position).getDate());
        //ONCLICK
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, EmailDisplay.class);
                intent.putExtra("כותרת",emails.get(position).getTopic());
                intent.putExtra("אימייל",emails.get(position).getMessage());
                intent.putExtra("תאריך",emails.get(position).getDate());
                intent.putExtra("מאת",emails.get(position).getFrom());
                ctx.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView email_Topic,email_Date,email_Message;
        LinearLayout cardLayout;
        CardView cardView;
        public Typeface typeface;

        public MyViewHolder(View itemView){
            super(itemView);
            email_Topic = (TextView)itemView.findViewById(R.id.emailTopic);
            email_Date = (TextView)itemView.findViewById(R.id.emailDate);
            email_Message = (TextView)itemView.findViewById(R.id.emailMessage);
            cardLayout = (LinearLayout)itemView.findViewById(R.id.emailCardLayout);
            cardView = (CardView)itemView.findViewById(R.id.emailcardView_id);
            typeface = Typeface.createFromAsset(itemView.getContext().getAssets(),
                    "font/varela.ttf");
            email_Topic.setTypeface(typeface);
            email_Message.setTypeface(typeface);
            email_Date.setTypeface(typeface);

        }
    }
}
