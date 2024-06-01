package com.mirea.kt.ribo.afishamoscow;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION); // скрыть кнопки навигации
        setContentView(R.layout.activity_main);

        // Использование в активности или фрагменте
        FragmentManager fragmentManager = getSupportFragmentManager();
        int activeFragmentCount = getActiveFragmentCount(fragmentManager);
        Log.d("FragmentCount", "Active fragments count: " + activeFragmentCount);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) { // метод для скрытия кнопок навигации
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    int getActiveFragmentCount(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        int count = 0;
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isAdded()) {
                count++;
            }
        }
        return count;
    }

}