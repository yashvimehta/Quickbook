package com.example.quickbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread timer= new Thread()
        {
            public void run()
            {
                try
                {
                    //Display for 3 seconds
                    sleep(3000);
                }
                catch (InterruptedException e)
                {
                    Intent i = new Intent(SplashScreen.this, SplashScreen.class);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    e.printStackTrace();
                }
                finally
                {
                    //Goes to Activity  StartingPoint.java(STARTINGPOINT)
                    Intent intent=new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timer.start();
    }
}