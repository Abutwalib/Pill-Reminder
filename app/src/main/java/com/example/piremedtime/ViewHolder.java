package com.example.piremedtime;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView sender,condition,description;
    View mview;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mview=itemView;
        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v,getAdapterPosition());
            }
        });
        // item long click listener
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v,getAdapterPosition());
                return true;
            }
        });
        // initialize views with model_layout.xml
        sender=itemView.findViewById(R.id.phone);
        condition=itemView.findViewById(R.id.condition);
        description=itemView.findViewById(R.id.description);


    }
    private ViewHolder.ClickListener mClickListener;
    //interface for click listener
    public interface  ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }
    public void setOnclickListener(ViewHolder.ClickListener clickListener){
        mClickListener=clickListener;
    }
}
