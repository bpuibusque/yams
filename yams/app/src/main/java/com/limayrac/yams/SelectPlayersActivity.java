package com.limayrac.yams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class SelectPlayersActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private Button jouerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectnumberplayer);

        radioGroup = findViewById(R.id.radioGroup);
        jouerButton = findViewById(R.id.jouerplayer);

        jouerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedId);
                int numberOfPlayers = Integer.parseInt(selectedRadioButton.getText().toString().split(" ")[0]);
                startGame(numberOfPlayers);
            }
        });
    }

    private void startGame(int numberOfPlayers) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("numberOfPlayers", numberOfPlayers);
        startActivity(intent);
    }

}
