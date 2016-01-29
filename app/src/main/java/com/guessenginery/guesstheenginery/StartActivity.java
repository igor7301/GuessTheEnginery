package com.guessenginery.guesstheenginery;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.view.View;
import android.widget.Button;

import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;



/**
 * Created by Igor on 23.07.15.
 */
public class StartActivity extends FragmentActivity implements View.OnClickListener {

    Button btnStartGame, btnSettings;

    private StartAppAd startAppAd = new StartAppAd(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppSDK.init(this, "106159305", "207342564", true);
        StartAppAd.showSplash(this, savedInstanceState);


        setContentView(R.layout.start_activity);
        btnStartGame = (Button) findViewById(R.id.buttonStartGame);
        btnStartGame.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        startAppAd.onBackPressed();
        super.onBackPressed();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonStartGame:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;


        }
    }







}
