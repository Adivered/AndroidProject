package com.example.website.Tasks;

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

public class RecyvlerViewAdapter extends
        RecyclerView.Adapter<RecyvlerViewAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {


    interface OnItemCheckListener {
        void onItemCheck(Mesimot item);
        void onItemUncheck(Mesimot item);
    }

    private OnItemCheckListener mItemCheckListener;
    private Context ctx;
    private final OnStartDragListener mDragStartListener;
    private ArrayList<Mesimot> mesimot;

    public RecyvlerViewAdapter(Context ctx, ArrayList<Mesimot> mesimot, OnStartDragListener dragStartListener, OnItemCheckListener onItemCheckListener){
        this.mDragStartListener = dragStartListener;
        this.ctx = ctx;
        this.mItemCheckListener = onItemCheckListener;
        this.mesimot = mesimot;

    }

    public void removeItem(int position) {
        mesimot.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(int position, Mesimot item){
        mesimot.add(position,item);
        notifyItemChanged(position);
    }

    @Override
    public void onItemDismiss(int position) {
        mesimot.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mesimot, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mesimot, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public ArrayList<Mesimot> getData() {
        return mesimot;
    }


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(ctx);
        view = inflater.inflate(R.layout.cardsfortasklist,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.task_name.setText(mesimot.get(position).getTaskName());
        holder.task_info.setText(mesimot.get(position).getTaskInfo());
        holder.task_date.setText(mesimot.get(position).getDate());
        holder.task_status.setText(mesimot.get(position).getStatus());
        holder.task_checkbox.setChecked(mesimot.get(position).isSelected());
        holder.task_checkbox.setTag(mesimot.get(position));
        holder.task_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mesimot mesima = (Mesimot)holder.task_checkbox.getTag();
                mesima.setSelected(holder.task_checkbox.isChecked());
                mesimot.get(position).setSelected(holder.task_checkbox.isChecked());
                if(mesimot.get(position).isSelected())
                    mItemCheckListener.onItemCheck(mesimot.get(position));
                else
                    mItemCheckListener.onItemUncheck(mesimot.get(position));

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
                Intent intent = new Intent(ctx,Sub_Loz.class);
                intent.putExtra("משימה",mesimot.get(position).getTaskName());
                intent.putExtra("תוכן",mesimot.get(position).getTaskInfo());
                intent.putExtra("סטטוס",mesimot.get(position).getStatus());
                intent.putExtra("תאריך",mesimot.get(position).getDate());
                ctx.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mesimot.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {
        TextView task_name,task_info,task_date,task_status;
        LinearLayout Element;
        CardView cardView;
        AppCompatCheckBox task_checkbox;

        public MyViewHolder(View itemView){
            super(itemView);
            task_name = (TextView)itemView.findViewById(R.id.task_name);
            task_info = (TextView)itemView.findViewById(R.id.task_info);
            task_date = (TextView)itemView.findViewById(R.id.task_date);
            task_status = (TextView)itemView.findViewById(R.id.task_status);
            task_checkbox = (AppCompatCheckBox)itemView.findViewById(R.id.selectable_tasklist_card);
            Element = (LinearLayout)itemView.findViewById(R.id.clickable_linear_emailcard);
            cardView = (CardView)itemView.findViewById(R.id.cardview_id);
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
