package com.mirea.kt.ribo.afishamoscow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mirea.kt.ribo.afishamoscow.databinding.FragmentEventPageBinding;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EventPageFragment extends Fragment {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Handler backgroundHandler, mainHandler;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentEventPageBinding binding = FragmentEventPageBinding.bind(view);
        TextView tvTitle = binding.title;
        TextView tvAge = binding.age;
        TextView tvDate = binding.date;
        TextView tvLocation = binding.location;
        TextView tvDescription = binding.description;
        TextView tvPrice = binding.price;
        ImageView ivImage = binding.image;
        TextView tvSite = binding.site;
        TextView tvAddress = binding.address;
        Bundle bundle = getArguments();
        String eventID = bundle.getString("id");
        BackgroundHandlerThread backgroundHandlerThread = new BackgroundHandlerThread("bht");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
        backgroundHandler.post(() -> {
            try {
                Event event = zagruzEvent(eventID);
                mainHandler.post(() -> {
                    //переделать здесь все
                    tvTitle.setText(Html.fromHtml(event.getName()));
                    tvAge.setText(Html.fromHtml(event.getAge()));
                    tvDate.setText(Html.fromHtml(event.getDate()));
                    tvLocation.setText(Html.fromHtml(event.getLocation()));
                    tvDescription.setText(Html.fromHtml(event.getDesription()));
                    tvPrice.setText(Html.fromHtml(event.getPrice()));
                    tvAddress.setText(Html.fromHtml(event.getAddress()));
                    tvAddress.setOnClickListener(v -> {
                        Uri urilocation = Uri.parse("geo:" + event.getLat() + ", " + event.getLon());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(urilocation);
                        startActivity(intent);
                    });
                    tvSite.setText(Html.fromHtml(event.getSite()));
                    tvSite.setMovementMethod(LinkMovementMethod.getInstance());
                    Glide.with(this)
                            .load(event.getPhoto())
                            .centerCrop()
                            .into(ivImage);
                });
            } catch (Exception e) {
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentEventPageBinding binding = FragmentEventPageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    //парсинг события
    private Event zagruzEvent(String idEv) throws IOException, ParseException {
        Event event = new Event();
        OkHttpClient client = new OkHttpClient();
        String currentDate = dateFormat.format(calendar.getTime());
        String urlEv = "https://kudago.com/public-api/v1.4/events/" + idEv;
        Log.i("Ok", urlEv);
        Gson gson = new Gson();

        Request requestEvents = new Request.Builder()
                .url(urlEv)
                .build();
        try {
            Response response2 = client.newCall(requestEvents).execute();
            assert response2.body() != null;
            String responseData2 = response2.body().string();
            JsonObject jsonObject2 = gson.fromJson(responseData2, JsonObject.class);
            // парсинг названия
            String title = jsonObject2.get("title").getAsString();
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
            title = "<b>Название:</b> " + "<i>"+title+"</i>";
            Log.i("Ok", title);
            // парсинг даты
            JsonArray datesArray = jsonObject2.getAsJsonArray("dates");
            String date;
            long curDate = dateFormat.parse(currentDate).getTime() / 1000;
            int flag = 0;
            String dateStart;
            String dateEnd;
            while (!(curDate > datesArray.get(flag).getAsJsonObject().get("start").getAsLong() || curDate < datesArray.get(flag).getAsJsonObject().get("end").getAsLong())) {
                flag++;
                Log.i("date", Boolean.toString(curDate > datesArray.get(flag).getAsJsonObject().get("start").getAsLong()));
            }

            if (datesArray.get(datesArray.size() - 1).getAsJsonObject().get("start").getAsLong() > 0) {
                SimpleDateFormat sdt = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                long startDate = datesArray.get(datesArray.size() - 1).getAsJsonObject().get("start").getAsLong();
                date = "<b>Дата и время:</b> "+sdt.format(new Date(startDate * 1000));

                Log.i("Ok", date + "|||" + curDate + "|||" + startDate);
            } else {
                date = "<b>Дата и время:</b> Каждый день";
            }
            // парсинг локации
            String placeId;
            String place = null, placeTitle = null, placeAddress = null, lon = null, lat = null;
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
                placeTitle = "<b>Место:</b> " + jsonObjectPlace.get("title").getAsString();
                JsonObject placeCoords = jsonObjectPlace.getAsJsonObject("coords");
                lat = placeCoords.get("lat").getAsString();
                lon = placeCoords.get("lon").getAsString();
                Log.d("coords", lat + "  " + lon);
                placeAddress ="<b>Адрес:</b> " + "<u><font color='#0000FF'>"+jsonObjectPlace.get("address").getAsString()+"</font></u>";
                Log.i("Ok", placeTitle);
            } catch (Exception e) {
                placeTitle = "<b>Место:</b> -";
                placeAddress = "<b>Адрес:</b> -";
            }
            // парсинг описания
            String description = "<b>Описание:</b> " + jsonObject2.get("description").getAsString().replaceAll("<[^>]*>", "");
            ;
            // парсинг фото
            JsonArray imageArray = jsonObject2.getAsJsonArray("images");
            String photo = imageArray.get(0).getAsJsonObject().get("image").getAsString();
            Log.i("Ok", photo);
            // парсинг цены
            String price;
            if (jsonObject2.get("price").getAsString().isEmpty()) {
                price = "<b>Цена:</b> бесплатно";
            } else {
                price = "<b>Цена:</b> "+jsonObject2.get("price").getAsString();
            }
            //парсинг возрастных ограничений
            String age = "<b>Возрастные ограничения:</b> " + jsonObject2.get("age_restriction").getAsString();
            // парсинг сайта
            String site = "<b>Сайт:</b> " + "<a href='" + jsonObject2.get("site_url").getAsString() + "'>Ссылка на событие</a>";

            event = new Event(title, date, placeTitle, placeAddress, description, photo, idEv, null, price, age, site, lon, lat);
        } catch (Exception e) {
            Log.e("NeOK", e.toString());
        }
        return event;
    }
}
