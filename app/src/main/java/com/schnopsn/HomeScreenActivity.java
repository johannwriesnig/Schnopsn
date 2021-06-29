package com.schnopsn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.schnopsn.core.server.client.GameClient;
import com.schnopsn.core.server.client.GameInitListener;
import com.schnopsn.core.server.dto.clienttoserver.FindGame;

public class HomeScreenActivity extends AppCompatActivity {
    private Button goBtn;
    private Intent intent;
    private ProgressDialog dialog;
    private AlertDialog.Builder builder;

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
        new FindGameAsyncTask().execute();
    }

    class FindGameAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            dialog=new ProgressDialog(HomeScreenActivity.this);
            dialog.setMessage("searching for a game...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Thread thread = new Thread(()->{
                GameClient.getInstance().getClient().sendTCP(new FindGame());
            });
            thread.start();


            waitFor7Seconds();

            return "Client initialized";
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if (GameClient.getInstance().isInGame()) {
                //startActivity(intent);
            } else {
                builder = new AlertDialog.Builder(HomeScreenActivity.this);
                builder.setMessage("Could not find a game. Please try again.")
                        .setTitle("Information");
                builder.show();
            }
        }
    }

    public void waitFor7Seconds(){
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class GameInitListenerImpl extends GameInitListener{

        @Override
        public void changeView() {
            startActivity(intent);
        }
    }
}
