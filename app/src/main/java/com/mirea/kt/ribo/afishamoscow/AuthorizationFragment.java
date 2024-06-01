package com.mirea.kt.ribo.afishamoscow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mirea.kt.ribo.afishamoscow.databinding.FragmentAuthorizationBinding;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthorizationFragment extends Fragment {
    private Handler backgroundHandler, mainHandler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentAuthorizationBinding binding = FragmentAuthorizationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Button btnEnter = binding.btnEnter;
        EditText etLogin = binding.etLogin;
        EditText etPassword = binding.etPassword;
        TextView tvAuth = binding.tvAuthorization;
        NavController controller = Navigation.findNavController(requireActivity(), R.id.fragment_container_view);
        BackgroundHandlerThread backgroundHandlerThread = new BackgroundHandlerThread("bht");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
        btnEnter.setOnClickListener(v -> {
            if (v.getId() == R.id.btnEnter) {
                if (etLogin.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Invalid terms", Toast.LENGTH_SHORT).show();
                } else {
                    backgroundHandler.post(() -> {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("lgn", etLogin.getText().toString()).add("pwd", etPassword.getText().toString()).add("g", "RIBO-02-22")
                                .build();
                        Request request = new Request.Builder()
                                .url("https://android-for-students.ru/coursework/login.php")
                                .post(formBody)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String resp = response.body().string();
                            Gson gson = new Gson();
                            JsonObject jsonObject = gson.fromJson(resp, JsonObject.class);
                            int result = jsonObject.get("result_code").getAsInt();
                            Log.i("res", Integer.toString(result));
                            mainHandler.post(()->{
                            if (result == 1) {
                                tvAuth.setText(R.string.welcome);
                                controller.navigate(R.id.mainFragment);
                            } else {
                                tvAuth.setText(R.string.try_again);
                                tvAuth.setTextSize(28);
                            }});
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }});
            return view;
        }

        @Override
        public void onActivityCreated (@Nullable Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);
        }

    }