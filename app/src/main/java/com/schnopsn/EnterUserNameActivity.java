package com.schnopsn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.schnopsn.core.server.client.GameClient;

public class EnterUserNameActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    private AlertDialog.Builder builder;
    private Intent intent;
    private Button goBtn;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterusername);

        intent = new Intent(this, HomeScreenActivity.class);

        goBtn = findViewById(R.id.goBtn);
        goBtn.setOnClickListener(v->connectToServer());
        userNameTextView = findViewById(R.id.userNameEditText);
    }

    public void connectToServer(){
        String userName = userNameTextView.getText().toString();
        if (userName.matches("")) {
            Toast.makeText(this, "Please enter an username", Toast.LENGTH_SHORT).show();
            return;
        }
        GameClient.getInstance().setUserName(userName);
        new InitClientAsyncTask().execute();
    }

    class InitClientAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            dialog=new ProgressDialog(EnterUserNameActivity.this);
            dialog.setMessage("Connecting to server...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            GameClient.getInstance().init();
            return "Client initialized";
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if (GameClient.getInstance().getClient().isConnected()) {
                startActivity(intent);
            } else {
                builder = new AlertDialog.Builder(EnterUserNameActivity.this);
                builder.setMessage("Could not connect to the servers. Please try again.")
                        .setTitle("Information");
                builder.show();
            }
        }
    }
}
