package com.mirea.kt.ribo.afishamoscow;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mirea.kt.ribo.afishamoscow.databinding.FragmentEventsBinding;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EventsFragment extends Fragment {

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private Handler backgroundHandler, mainHandler;
    private EventAdapter eventAdapter;
    private TextView tvCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentEventsBinding binding = FragmentEventsBinding.bind(view);
        NavController controller = Navigation.findNavController(requireActivity(), R.id.fragment_container_view);
        Bundle bundle = getArguments();
        binding.categoryName.setText(bundle.getString("title"));
        assert bundle != null;
        String categorySlug = bundle.getString("slug");
        tvCount = binding.numbZagruz;

        backgroundHandler.post(() -> {
            int totalCount = totalZagruzCount(categorySlug);
            mainHandler.post(() -> {
                tvCount.setText("Загружено: " + 0 + "/" + totalCount);
            });
            int zagruzCount = 0;
            ArrayList<String> idEvents = loadID(categorySlug);
            mainHandler.post(() -> {
                binding.categoryName.setVisibility(View.GONE);
                binding.progress.setVisibility(View.GONE);
                binding.rvEvents.setVisibility(View.VISIBLE);
                binding.numbZagruz.setVisibility(View.VISIBLE);
            });
            ArrayList<Event> events = new ArrayList<>();
            eventAdapter = new EventAdapter(events, controller);
            mainHandler.post(() -> {
                RecyclerView rvEvents = binding.rvEvents;
                rvEvents.setAdapter(eventAdapter);
            });
            while (isVisible() && zagruzCount < totalCount) {
                String id = idEvents.get(zagruzCount);
                events.add(loadEvents(id));
                zagruzCount++;
                int finalZagruzCount = zagruzCount;
                mainHandler.post(() -> {
                    eventAdapter.notifyItemInserted(events.size() - 1);
                    tvCount.setText("Загружено: " + finalZagruzCount + "/" + totalCount);
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentEventsBinding binding = FragmentEventsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        BackgroundHandlerThread backgroundHandlerThread = new BackgroundHandlerThread("bht");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
        return view;
    }

    private int totalZagruzCount(String categorySlug) {
        OkHttpClient client = new OkHttpClient();
        String currentDate = dateFormat.format(calendar.getTime());
        String url = "https://kudago.com/public-api/v1.4/events/?categories=" + categorySlug + "&fields=id&order_by=id&actual_since=" + currentDate + "&location=msk&page=1";
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            String responseData = response.body().string();
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
            int count = jsonObject.get("count").getAsInt();
            return count;
        } catch (IOException e) {
            return 0;
        }
    }

    private ArrayList<String> loadID(String categorySlug) {
        OkHttpClient client = new OkHttpClient();
        String currentDate = dateFormat.format(calendar.getTime());
        ArrayList<String> idEvents = new ArrayList<>();
        String url = "https://kudago.com/public-api/v1.4/events/?categories=" + categorySlug + "&fields=id&order_by=id&actual_since=" + currentDate + "&location=msk&page=";
        int page = 1;
        while (true) {
            Request request1 = new Request.Builder()
                    .url(url + page)
                    .build();
            Log.i("Ok", url + page);
            try {
                Response response1 = client.newCall(request1).execute();
                assert response1.body() != null;
                String responseData1 = response1.body().string();
                Gson gson = new Gson();
                JsonObject jsonObject1 = gson.fromJson(responseData1, JsonObject.class);
                JsonArray results = jsonObject1.getAsJsonArray("results");
                for (int j = 0; j < results.size(); j++) {
                    idEvents.add(results.get(j).getAsJsonObject().get("id").getAsString());
                }
                String next = null;
                try {
                    next = jsonObject1.get("next").getAsString();
                    if (next != null) {
                        page++;
                    }
                } catch (Exception e) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return idEvents;
    }

    private Event loadEvents(String idEvent) {
        Event event = new Event();
        OkHttpClient client = new OkHttpClient();
        try {
            String urlEv = "https://kudago.com/public-api/v1.4/events/" + idEvent;
            Log.i("Ok", urlEv);
            Request requestEvents = new Request.Builder()
                    .url(urlEv)
                    .build();
            Response response2 = client.newCall(requestEvents).execute();
            assert response2.body() != null;
            String responseData2 = response2.body().string();
            Gson gson = new Gson();
            JsonObject jsonObject2 = gson.fromJson(responseData2, JsonObject.class);
            // парсинг названия
            String title;
            try {
                title = jsonObject2.get("short_title").getAsString();
            } catch (Exception e) {
                title = jsonObject2.get("title").getAsString();
            }
            Log.i("Ok", title);
            //парсинг даты
            JsonArray datesArray = jsonObject2.getAsJsonArray("dates");
            String currentDate = dateFormat2.format(calendar.getTime());
            String date = null;
            long curDate = dateFormat2.parse(currentDate).getTime();
            int index = 0;
            for (JsonElement dateElement : datesArray) {
                JsonObject dateObject = dateElement.getAsJsonObject();
                long startDate = dateObject.get("start").getAsLong() * 1000;
                String start = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date(startDate));
                long endDate = dateObject.get("end").getAsLong() * 1000;
                String end = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date(endDate));
                if (curDate < startDate) {
                    date = start;
                    break;
                } else if (curDate >= startDate && curDate <= endDate) {
                    date = "Открыто до " + end;
                    break;
                } else if (curDate > startDate && index == datesArray.size() - 1) {
                    date = "Открыто с " + start;
                    break;
                }
                index++;
            }
            // парсинг фото
            JsonArray imageArray = jsonObject2.getAsJsonArray("images");
            String photo = imageArray.get(0).getAsJsonObject().get("image").getAsString();
            // парсинг локации
            String placeId;
            String placeTitle = null;
            try {
                placeId = jsonObject2.getAsJsonObject("place").get("id").getAsString();
                String urlPlace = "https://kudago.com/public-api/v1.4/places/" + placeId;
                Request requestPlace = new Request.Builder()
                        .url(urlPlace)
                        .build();
                Response responsePlace = client.newCall(requestPlace).execute();
                assert responsePlace.body() != null;
                String responseDataPlace = responsePlace.body().string();
                JsonObject jsonObjectPlace = gson.fromJson(responseDataPlace, JsonObject.class);
                placeTitle = jsonObjectPlace.get("title").getAsString();
            } catch (Exception e) {
                placeTitle = null;
            }
            event = new Event(title, date, placeTitle, null, null, photo, idEvent, null, null, null, null, null, null);
        } catch (IOException e) {
            Log.e("Ok", e.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return event;
    }
}