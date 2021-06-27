package com.gking.simplemusicplayer.impl;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.Login_cellphone;
import com.gking.simplemusicplayer.activity.MySettings;

import java.util.List;

import gtools.managers.GHolder;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.VHolder> {
    List<GHolder<Object,String>> content;
    Context context;
    public RecyclerViewAdapter(Context context,List<GHolder<Object,String>> content){
        this.content=content;
        this.context=context;
    }
    @NonNull
    @Override
    public RecyclerViewAdapter.VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.recyclerview1,null);
        RecyclerViewAdapter.VHolder vHolder= new RecyclerViewAdapter.VHolder(view);
        return vHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.VHolder holder, int position) {
        GHolder<Object,String> info=content.get(position);
        holder.title.setText(info.get(1));
        switch (info.get(0)){
            case MySettings.login:holder.title.setOnClickListener(view->context.startActivity(new Intent(context, Login_cellphone.class)));
        }
    }
    @Override
    public int getItemCount() {
        return content.size();
    }

    static class VHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public VHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.recycle1title);
        }
    }
    public static GHolder<Object,String> formInfo(String ...args){
        GHolder<Object, String> holder = new GHolder<>();
        for (int i = 0; i < args.length; i++) {
            holder.add(i,args[i]);
        }
        return holder;
    }
}
