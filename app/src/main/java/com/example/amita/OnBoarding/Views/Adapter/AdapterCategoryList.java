package com.example.amita.OnBoarding.Views.Adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amita.OnBoarding.Views.Common.Common;
import com.example.amita.OnBoarding.Views.Interface.ItemClickListener;
import com.example.amita.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView key;
    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        key =itemView.findViewById(R.id.key);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition());
    }
}

public class AdapterCategoryList extends RecyclerView.Adapter<ViewHolder> {
    private final Context context;
    private final ArrayList<String> arrayList;

    public AdapterCategoryList(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_discover__goal_category,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GradientDrawable border = new GradientDrawable();
        String name = (String) arrayList.get(position);
        holder.key.setText(name);

        holder.setItemClickListener((view, position1) -> {
            Common.model_index = position1;
            notifyDataSetChanged();
        });

        try {
            if (Common.model_index == position) {
                border.setStroke(3,  0xff238FCC);
                border.setCornerRadius(50);
                holder.key.setBackgroundDrawable(border);
                holder.key.setTextColor(Color.parseColor("#4F4F4F"));
            } else {
                border.setStroke(3, 0xffBDBDBD);
                border.setCornerRadius(50);
                holder.key.setBackgroundDrawable(border);
                holder.key.setTextColor(Color.parseColor("#BDBDBD"));
            }
        } catch (Exception e){
            Log.d(TAG,"AdapterCategoryList category colour select-----------------------"+e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}