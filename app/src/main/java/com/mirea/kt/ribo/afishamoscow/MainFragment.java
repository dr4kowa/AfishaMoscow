package com.mirea.kt.ribo.afishamoscow;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mirea.kt.ribo.afishamoscow.databinding.FragmentMainBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainFragment extends Fragment {

    private Handler backgroundHandler, mainHandler;
    private CategoryAdapter catAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMainBinding binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        NavController controller = Navigation.findNavController(requireActivity(), R.id.fragment_container_view);

        BackgroundHandlerThread backgroundHandlerThread = new BackgroundHandlerThread("bht");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
        backgroundHandler.post(() -> {
            ArrayList<Category> categories = zagruzCat();
            mainHandler.post(() -> {
                RecyclerView rvCategories = binding.rvCategories;
                catAdapter = new CategoryAdapter(categories,controller);
                rvCategories.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                rvCategories.setAdapter(catAdapter);
            });
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvCategories = view.findViewById(R.id.rvCategories);
        rvCategories.setAdapter(catAdapter);
    }

    private ArrayList<Category> zagruzCat() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://kudago.com/public-api/v1.4/event-categories/?fields=name,slug&order_by=id";
        Request request = new Request.Builder()
                .url(url)
                .build();
        ArrayList<Category> categories = new ArrayList<>();
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            String responseData = response.body().string();
            Gson gson = new Gson();
            Type categoryListType = new TypeToken<ArrayList<Category>>() {
            }.getType();
            categories = gson.fromJson(responseData, categoryListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }


}