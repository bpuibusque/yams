package com.limayrac.yams;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GameActivity extends AppCompatActivity {
    private static final int DICE_ACTIVITY_REQUEST_CODE = 1;
    private int[] lastDiceValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        int numberOfPlayers = getIntent().getIntExtra("numberOfPlayers", 1);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        initializeScoreTable(tableLayout, numberOfPlayers);

        Button btnGoToDice = findViewById(R.id.btnGoToDice);
        btnGoToDice.setOnClickListener(view -> {
            Intent intent = new Intent(GameActivity.this, DiceActivity.class);
            startActivityForResult(intent, DICE_ACTIVITY_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DICE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            lastDiceValues = data.getIntArrayExtra("diceValues");
            String scoreCategory = data.getStringExtra("scoreCategory");

            if (lastDiceValues == null || scoreCategory == null) {
                Toast.makeText(GameActivity.this, "Erreur lors de la récupération des données des dés.", Toast.LENGTH_LONG).show();
                return;
            }

            int score = calculateScoreForCategory(scoreCategory, lastDiceValues);
            int playerIndex = data.getIntExtra("playerIndex", 1);
            int position = getCategoryPosition(scoreCategory);
            updateScoreInTable(position, playerIndex, score);
        }
    }




    private String getCategoryName(int categoryIndex) {
        switch (categoryIndex) {
            case 1: return "1";
            case 2: return "2";
            // Autres cases pour les différentes catégories
            default: return "Inconnu";
        }
    }

    private void onScoreCellClicked(int categoryIndex, TextView scoreView) {
        if (lastDiceValues == null) {
            Toast.makeText(this, "Aucun lancer de dés détecté. Veuillez lancer les dés d'abord.", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryName = getCategoryName(categoryIndex);
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle("Confirmer le score pour " + categoryName);
        int calculatedScore = calculateScoreForCategory(categoryName, lastDiceValues);
        builder.setMessage("Le score calculé est : " + calculatedScore);

        builder.setPositiveButton("Confirmer", (dialog, which) -> scoreView.setText(String.valueOf(calculatedScore)));
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private Map<Integer, Integer> countDiceValues(int[] diceValues) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int value : diceValues) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                counts.put(value, counts.getOrDefault(value, 0) + 1);
            }
        }
        return counts;
    }

    private boolean hasPetiteSuite(int[] diceValues) {
        String str = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            str = Arrays.stream(diceValues).sorted().distinct()
                    .mapToObj(String::valueOf).collect(Collectors.joining());
        }
        return str.contains("1234") || str.contains("2345") || str.contains("3456");
    }

    private boolean hasGrandeSuite(int[] diceValues) {
        String str = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            str = Arrays.stream(diceValues).sorted().distinct()
                    .mapToObj(String::valueOf).collect(Collectors.joining());
        }
        return str.equals("12345") || str.equals("23456");
    }

    private int calculateBrelan(int[] diceValues) {
        Map<Integer, Integer> counts = countDiceValues(diceValues);
        for (int count : counts.values()) {
            if (count >= 3) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return Arrays.stream(diceValues).sum();
                }
            }
        }
        return 0;
    }

    private int calculateCarre(int[] diceValues) {
        Map<Integer, Integer> counts = countDiceValues(diceValues);
        for (int count : counts.values()) {
            if (count >= 4) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return Arrays.stream(diceValues).sum(); // Score = somme de tous les dés
                }
            }
        }
        return 0; // Pas de carré
    }

    private boolean hasFull(int[] diceValues) {
        Map<Integer, Integer> counts = countDiceValues(diceValues);
        boolean hasThreeOfAKind = false;
        boolean hasPair = false;
        for (int count : counts.values()) {
            if (count == 3) hasThreeOfAKind = true;
            else if (count == 2) hasPair = true;
        }
        return hasThreeOfAKind && hasPair; // True si full présent
    }

    private int calculateChance(int[] diceValues) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Arrays.stream(diceValues).sum();
        }
        return 0;
    }

    private boolean hasYams(int[] diceValues) {
        Map<Integer, Integer> counts = countDiceValues(diceValues);
        return counts.containsValue(5);
    }


    private int calculateScoreForCategory(String category, int[] diceValues) {
        switch (category) {
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
                return calculateTotalOfNumber(diceValues, Integer.parseInt(category));
            case "Brelan":
                return calculateBrelan(diceValues);
            case "Carré":
                return calculateCarre(diceValues);
            case "Full":
                return hasFull(diceValues) ? 25 : 0;
            case "Petite Suite":
                return hasPetiteSuite(diceValues) ? 30 : 0;
            case "Grande Suite":
                return hasGrandeSuite(diceValues) ? 40 : 0;
            case "Yam's":
                return hasYams(diceValues) ? 50 : 0;
            case "Chance":
                return calculateChance(diceValues);
            default:
                return 0;
        }
    }
    private int calculateTotalOfNumber(int[] diceValues, int number) {
        int total = 0;
        for (int value : diceValues) {
            if (value == number) {
                total += value;
            }
        }
        return total;
    }


    private int getCategoryPosition(String scoreCategory) {
        Map<String, Integer> categoryPositions = new HashMap<>();
        categoryPositions.put("1", 1);
        categoryPositions.put("2", 2);
        categoryPositions.put("3", 3);
        categoryPositions.put("4", 4);
        categoryPositions.put("5", 5);
        categoryPositions.put("6", 6);
        categoryPositions.put("Total", 7);
        categoryPositions.put("Bonus", 8);
        categoryPositions.put("Total 1", 9);
        categoryPositions.put("Brelan", 10);
        categoryPositions.put("Carré", 11);
        categoryPositions.put("Full", 12);
        categoryPositions.put("Petite Suite", 13);
        categoryPositions.put("Grande Suite", 14);
        categoryPositions.put("Yam's", 15);
        categoryPositions.put("Chance", 16);
        categoryPositions.put("Total 2", 17);
        categoryPositions.put("Grand Total", 18);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return categoryPositions.getOrDefault(scoreCategory, -1);
        }
        return 0;
    }

    private void updateScoreInTable(int position, int playerIndex, int score) {
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        if (position >= 0 && position < tableLayout.getChildCount()) {
            TableRow row = (TableRow) tableLayout.getChildAt(position);

            if (playerIndex + 1 < row.getChildCount()) {
                TextView scoreView = (TextView) row.getChildAt(playerIndex + 1);
                scoreView.setText(String.valueOf(score));
            }
        }
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
            for (int j = 1; j <= numberOfPlayers; j++) {
                addColumnSeparator(row);
                final TextView scoreView = new TextView(this);
                scoreView.setText("0");
                scoreView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                scoreView.setTextSize(16);
                scoreView.setPadding(10, 10, 10, 10);
                scoreView.setBackgroundResource(android.R.drawable.btn_default);
                scoreView.setClickable(true);
                final int categoryIndex = j;
                scoreView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onScoreCellClicked(categoryIndex, scoreView);
                    }
                });

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
        layoutParams.setMargins(0, 0, 0, 0);
        separator.setLayoutParams(layoutParams);
        separator.setBackgroundColor(Color.GRAY);
        row.addView(separator);
    }
}
