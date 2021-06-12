package com.schnopsn;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.schnopsn.core.server.client.GameClient;

public class HomeScreenActivity extends AppCompatActivity {
    private Button goBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        goBtn = findViewById(R.id.searchBtn);
        goBtn.setOnClickListener(v -> findGame());
    }

    public void findGame(){

    }
}
