package com.example.piremedtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<ViewHolder> {
    ListActivity listActivity;
    List<Model>modelList;
    Context context;

    public CustomerAdapter(ListActivity listActivity, List<Model> modelList) {
        this.listActivity = listActivity;
        this.modelList = modelList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_layout,viewGroup,false);

        ViewHolder viewHolder=new ViewHolder(itemView);
        //handle item clicks here
        viewHolder.setOnclickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //THIS WILL BE CALLED WHEN USER CLICKS ITEM

                //show data in toast on clicking in toast on clicking
                String phone=modelList.get(position).getPhone();
                String condition=modelList.get(position).getCondition();
                String description=modelList.get(position).getDescription();
                Toast.makeText(listActivity,phone+"\n"+description,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                //this will be called when user long clicks item
                //Creating an Alert Dialog
                AlertDialog.Builder builder= new AlertDialog.Builder(listActivity);
                //options to display
                String [] options={"Update","Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            //update is clicked
                            //get data
                            String phone=modelList.get(position).getPhone();
                            String condition=modelList.get(position).getCondition();
                            String desc=modelList.get(position).getDescription();
                           //intent to start activity
                            Intent intent=new Intent(listActivity,DoctorsOptions.class);
                            //put data in the Intent
                            intent.putExtra("PHONE",phone);
                            intent.putExtra("CONDITION",condition);
                            intent.putExtra("DESCRIPTION",desc);
                           //start activity intent
                            listActivity.startActivity(intent);
                        }
                        if (which==1){
                            //delete is clicked
                            listActivity.deleteData(position);
                        }
                    }
                }).create().show();

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.sender.setText(modelList.get(position).getPhone());
        holder.condition.setText(modelList.get(position).getCondition());
        holder.description.setText(modelList.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
