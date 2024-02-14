package com.limayrac.yams;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class DiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);

        Button rollButton = findViewById(R.id.btnRollDice);
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rollDice();
            }
        });

        Button btnBackToScore = findViewById(R.id.btnBackToScore);
        btnBackToScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    private void rollDice() {
        int[] diceImageViews = {
                R.id.die1, R.id.die2, R.id.die3,
                R.id.die4, R.id.die5, R.id.die6
        };

        for (int id : diceImageViews) {
            int randomNumber = new Random().nextInt(6) + 1;
            String imageName = "dice_face_" + randomNumber;
            int resourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());

            ImageView dieImageView = findViewById(id);
            dieImageView.setImageResource(resourceId);
        }
    }

}
