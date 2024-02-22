package com.limayrac.yams;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.Random;

public class DiceActivity extends AppCompatActivity {
    private ImageView[] diceImages = new ImageView[5];
    private boolean[] diceToReroll = new boolean[5];
    private int launchCount = 0;
    private TextView tvLancerCompteur;
    private final int MAX_LAUNCHES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);

        initializeDiceImages();
        tvLancerCompteur = findViewById(R.id.tvLancerCompteur);

        Button btnRollDice = findViewById(R.id.btnRollDice);
        btnRollDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (launchCount < MAX_LAUNCHES) {
                    rerollSelectedDice();
                    launchCount++;
                    tvLancerCompteur.setText("Lancers: " + launchCount); // Mise à jour du compteur sur l'UI
                }
                if (launchCount >= MAX_LAUNCHES) {
                    btnRollDice.setEnabled(false);
                    btnRollDice.setText("Lancer fini");
                }
            }
        });

        Button btnBackToScore = findViewById(R.id.btnBackToScore);
        btnBackToScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onBackToScoreClicked();
                } catch (Exception e) {
                    Log.e("DiceActivity", "Error in sending back dice values", e);
                    Toast.makeText(DiceActivity.this, "Erreur lors de l'envoi des valeurs des dés", Toast.LENGTH_SHORT).show();
                }
            }
        });

    };

    private void onBackToScoreClicked() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("diceValues", getDiceValues());
        returnIntent.putExtra("scoreCategory", "VotreCatégorieDeScore");
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    private void initializeDiceImages() {
        diceImages[0] = findViewById(R.id.dice1);
        diceImages[1] = findViewById(R.id.dice2);
        diceImages[2] = findViewById(R.id.dice3);
        diceImages[3] = findViewById(R.id.dice4);
        diceImages[4] = findViewById(R.id.dice5);

        for (int i = 0; i < diceImages.length; i++) {
            final int index = i;
            diceImages[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    diceToReroll[index] = !diceToReroll[index];
                    updateDiceAppearance(index);
                }
            });
        }
    }

    private void rerollSelectedDice() {
        Random random = new Random();
        for (int i = 0; i < diceToReroll.length; i++) {
            if (diceToReroll[i] || launchCount == 0) {
                int newDiceValue = random.nextInt(6) + 1;
                updateDiceValue(diceImages[i], newDiceValue);
                diceToReroll[i] = false;
                updateDiceAppearance(i);
            }
        }
    }

    private void updateDiceValue(ImageView dice, int value) {
        int resId = getResources().getIdentifier("dice_face_" + value, "drawable", getPackageName());
        dice.setImageResource(resId);
    }

    private void updateDiceAppearance(int index) {
        FrameLayout parent = (FrameLayout) diceImages[index].getParent();
        if (diceToReroll[index]) {
            parent.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_dice_border));
        } else {
            parent.setBackground(null);
        }
    }

    private int[] getDiceValues() {
        int[] values = new int[diceImages.length];
        for (int i = 0; i < diceImages.length; i++) {
            String resourceName = getResources().getResourceEntryName(diceImages[i].getId());
            int diceValue = Character.getNumericValue(resourceName.charAt(resourceName.length() - 1));
            values[i] = diceValue;
        }
        return values;
    }


}
