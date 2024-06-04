package com.mirea.kt.ribo.afishamoscow;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private ArrayList<Event> events;
    private NavController controller;


    public EventAdapter(ArrayList<Event> events, NavController controller) {
        this.events = events;
        this.controller = controller;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView location;
        private final ImageView photo;
        private final TextView name;

        ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            location = view.findViewById(R.id.location);
            photo = view.findViewById(R.id.photo);
            name = view.findViewById(R.id.title);
        }
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.date.setText(event.getDate());
        holder.location.setText(event.getLocation());
        Glide.with(holder.itemView.findViewById(R.id.photo).getContext())
                .load(event.getPhoto())
                .centerCrop()
                .into(holder.photo);
        holder.name.setText(event.getName());
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("id", event.getIdEvent());
            bundle.putString("title", event.getName());
            controller.navigate(R.id.eventPageFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

}
