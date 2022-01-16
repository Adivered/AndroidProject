package com.example.website.Manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.website.MotionEvents.ItemTouchHelperAdapter;
import com.example.website.MotionEvents.ItemTouchHelperViewHolder;
import com.example.website.MotionEvents.OnStartDragListener;
import com.example.website.R;

import java.util.ArrayList;
import java.util.Collections;

public class UsersRecyclerView extends
        RecyclerView.Adapter<UsersRecyclerView.MyViewHolder>
        implements ItemTouchHelperAdapter {


    interface OnItemCheckListener {
        void onItemCheck(User user);

        void onItemUncheck(User user);
    }

    private OnItemCheckListener mItemCheckListener;
    private Context ctx;
    private final OnStartDragListener mDragStartListener;
    private ArrayList<User> users;

    public UsersRecyclerView(Context ctx, ArrayList<User> users, OnStartDragListener dragStartListener, OnItemCheckListener onItemCheckListener) {
        this.mDragStartListener = dragStartListener;
        this.ctx = ctx;
        this.mItemCheckListener = onItemCheckListener;
        this.users = users;

    }

    public void removeItem(int position) {
        users.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(int position, User user) {
        users.add(position, user);
        notifyItemChanged(position);
    }

    @Override
    public void onItemDismiss(int position) {
        users.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(users, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(users, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public ArrayList<User> getData() {
        return users;
    }


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(ctx);
        view = inflater.inflate(R.layout.cardforuserlist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.user_ID.setText(String.valueOf(users.get(position).getUserID()));
        holder.user_name.setText(users.get(position).getPrivateName());
        holder.user_email.setText(users.get(position).getEmail());
        holder.user_rank.setText(users.get(position).getRank());
        holder.user_checkbox.setTag(users.get(position));
        holder.user_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = (User) holder.user_checkbox.getTag();
                user.setSelected(holder.user_checkbox.isChecked());
                users.get(position).setSelected(holder.user_checkbox.isChecked());
                if (users.get(position).isSelected())
                    mItemCheckListener.onItemCheck(users.get(position));
                else
                    mItemCheckListener.onItemUncheck(users.get(position));

            }
        });

        holder.cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
        //ONCLICK
        holder.Element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, UserInfo.class);
                intent.putExtra("משתמש", users.get(position).getUsername());
                intent.putExtra("סיסמא", users.get(position).getPassword());
                intent.putExtra("אימייל", users.get(position).getEmail());
                intent.putExtra("שם", users.get(position).getPrivateName());
                intent.putExtra("דרגה", users.get(position).getRank());
                intent.putExtra("מספר", String.valueOf(users.get(position).getUserID()));
                ctx.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {
        TextView user_username, user_password, user_ID, user_name, user_email, user_rank;
        LinearLayout Element;
        CardView cardView;
        AppCompatCheckBox user_checkbox;

        public MyViewHolder(View itemView) {
            super(itemView);
            user_ID = (TextView) itemView.findViewById(R.id.user_ID);
            user_email = (TextView) itemView.findViewById(R.id.user_email);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_rank = (TextView) itemView.findViewById(R.id.user_rank);
            user_checkbox = (AppCompatCheckBox) itemView.findViewById(R.id.selectable_userlist_card);
            Element = (LinearLayout) itemView.findViewById(R.id.clickable_linear_usercard);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }

    }
}
