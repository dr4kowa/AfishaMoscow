package com.mirea.kt.ribo.afishamoscow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class EvViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Event>> events = new MutableLiveData<>();
    private final MutableLiveData<Event> selectedEvent = new MutableLiveData<>();

    public LiveData<ArrayList<Event>> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events.setValue(events);
    }

    public LiveData<Event> getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event event) {
        this.selectedEvent.setValue(event);
    }
}