package com.limayrac.yams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button quitterButton = findViewById(R.id.quitter);
        quitterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        Button jouerButton = findViewById(R.id.jouer);
        jouerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectPlayersActivity.class);
                startActivity(intent);
            }
        });

    }



}