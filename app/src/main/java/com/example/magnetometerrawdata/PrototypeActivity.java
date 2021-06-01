package com.example.magnetometerrawdata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class PrototypeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prototype);
        context = this;
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.getTabAt(1).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("info", "chouette " + tab.getText());
                if(tab.getText().equals("development")){
                    Log.i("info", "chouette equals acc");
                    finish();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}