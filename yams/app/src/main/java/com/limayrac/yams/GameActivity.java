package com.limayrac.yams;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        int numberOfPlayers = getIntent().getIntExtra("numberOfPlayers", 1);
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        initializeScoreTable(tableLayout, numberOfPlayers);

        Button btnGoToDice = findViewById(R.id.btnGoToDice);
        btnGoToDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, DiceActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeScoreTable(TableLayout tableLayout, int numberOfPlayers) {
        String[] categories = {
                "1", "2", "3", "4", "5", "6",
                "Total", "Bonus", "Total 1",
                "Brelan", "Carré", "Full",
                "Petite Suite", "Grande Suite",
                "Yam's", "Chance", "Total 2",
                "Grand Total"
        };
        addSeparator(tableLayout);

        TableRow headerRow = new TableRow(this);
        addColumnSeparator(headerRow);
        TextView headerView = new TextView(this);
        headerView.setText("Catégories");
        headerView.setTextSize(22);
        headerView.setPadding(20, 10, 20, 10);
        headerView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        headerRow.addView(headerView);
        addSeparator(tableLayout);
        for (int i = 1; i <= numberOfPlayers; i++) {
            addColumnSeparator(headerRow);
            TextView textView = new TextView(this);
            textView.setText("J" + i);
            textView.setTextSize(22);
            textView.setPadding(20, 10, 20, 10);
            textView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            headerRow.addView(textView);
        }
        addColumnSeparator(headerRow);

        tableLayout.addView(headerRow);
        addSeparator(tableLayout);

        for (String category : categories) {
            TableRow row = new TableRow(this);
            addColumnSeparator(row);
            TextView textView = new TextView(this);
            textView.setText(category);
            textView.setTextSize(22);
            textView.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
            textView.setPadding(20, 10, 20, 10);
            row.addView(textView);
            addSeparator(tableLayout);

            for (int j = 1; j <= numberOfPlayers; j++) {
                addColumnSeparator(row);
                TextView scoreView = new TextView(this);
                scoreView.setText("0");
                scoreView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                scoreView.setTextSize(16);
                scoreView.setPadding(10, 10, 10, 10);
                row.addView(scoreView);
            }
            addColumnSeparator(row);
            tableLayout.addView(row);
            addSeparator(tableLayout);

        }
    }

    private void addSeparator(TableLayout tableLayout) {
        View separator = new View(this);
        separator.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 1));
        separator.setBackgroundColor(Color.BLACK);
        tableLayout.addView(separator);
    }

    private void addColumnSeparator(TableRow row) {
        View separator = new View(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(2, TableRow.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 0); // Ajustez selon les besoins
        separator.setLayoutParams(layoutParams);
        separator.setBackgroundColor(Color.GRAY);
        row.addView(separator);
    };


}
