package edu.danya140.schedulenew;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    openFileInput("auth.inf")));

            Intent schedule = new Intent(this,ScheduleActivity.class);
            startActivity(schedule);

        } catch (FileNotFoundException ex){
            Intent login = new Intent(this,LoginActivity.class);
            startActivity(login);
        }






    }








}

