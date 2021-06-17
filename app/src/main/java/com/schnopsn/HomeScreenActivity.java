package com.schnopsn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.schnopsn.core.server.client.GameClient;
import com.schnopsn.core.server.client.GameInitListener;
import com.schnopsn.core.server.dto.clienttoserver.FindGame;

public class HomeScreenActivity extends AppCompatActivity {
    private Button goBtn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        goBtn = findViewById(R.id.searchBtn);
        goBtn.setOnClickListener(v -> findGame());
        intent = new Intent(this, GameView.class);
        GameClient.getInstance().setGameInitListener(new GameInitListenerImpl());

    }


    public void findGame(){
        Thread thread = new Thread(()->{
            GameClient.getInstance().getClient().sendTCP(new FindGame());
        });
        thread.start();

    }

    class GameInitListenerImpl extends GameInitListener{

        @Override
        public void changeView() {
            startActivity(intent);
        }
    }
}
