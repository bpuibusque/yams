package com.limayrac.yams;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private static final int MAX_LAUNCHES = 3;
    private int launchCount = 0;
    private ImageView[] diceImages = new ImageView[5];
    private boolean[] diceToKeep = new boolean[5];
    private int[] diceValues = new int[5];
    private Random random = new Random();

    private TextView launchCounterTextView;
    private Button rollDiceButton;
    private LinearLayout diceContainer;
    private TableLayout scoreTableLayout;

    private int currentPlayer = 0;
    private int numberOfPlayers;
    private boolean scoreSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        numberOfPlayers = getIntent().getIntExtra("numberOfPlayers", 1);

        initializeUI();
        setupDiceImages();
        setupScoreTable(numberOfPlayers);
    }

    private void initializeUI() {
        diceContainer = findViewById(R.id.diceImagesLayout);
        scoreTableLayout = findViewById(R.id.scoreTableLayout);
        launchCounterTextView = findViewById(R.id.launchCounterTextView);
        rollDiceButton = findViewById(R.id.rollDiceButton);

        rollDiceButton.setOnClickListener(v -> rollDice());


    }
    private void rollDice() {
        if (launchCount == 0) {
            for (ImageView diceImage : diceImages) {
                diceImage.setVisibility(View.VISIBLE);
            }
        }
        if (launchCount < MAX_LAUNCHES) {
            for (int i = 0; i < 5; i++) {
                if (!diceToKeep[i]) {
                    diceValues[i] = random.nextInt(6) + 1;
                }
            }
            launchCount++;
            updateDiceImages();
            launchCounterTextView.setText("Lancers: " + launchCount);
        }

        if (launchCount == MAX_LAUNCHES) {
            rollDiceButton.setEnabled(false);
        }
    }


    private void updateDiceImages() {
        for (int i = 0; i < diceImages.length; i++) {
            int resId = getResources().getIdentifier("dice_face_" + diceValues[i], "drawable", getPackageName());
            diceImages[i].setImageResource(resId);
        }
    }
    private void toggleDiceSelection(int index) {
        diceToKeep[index] = !diceToKeep[index];
        updateDiceAppearance(index);
        //Toast.makeText(this, "Dé " + (index + 1) + (diceToKeep[index] ? " sélectionné" : " désélectionné"), Toast.LENGTH_SHORT).show();
    }


    private void updateDiceAppearance(int index) {
        ImageView diceImage = diceImages[index];
        if (diceToKeep[index]) {
            diceImage.setBackgroundResource(R.drawable.selected_dice_border); // Dés sélectionnés
        } else {
            diceImage.setBackground(null); // Dés non sélectionnés
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.dice_size),
                getResources().getDimensionPixelSize(R.dimen.dice_size));
        layoutParams.gravity = Gravity.CENTER;
        diceImage.setLayoutParams(layoutParams);
    }


    private void setupDiceImages() {
        diceContainer = findViewById(R.id.diceImagesLayout);

        for (int i = 0; i < diceImages.length; i++) {
            ImageView diceImage = new ImageView(this);
            diceImage.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            diceImage.setAdjustViewBounds(true);
            diceImage.setMaxHeight(100);
            diceImage.setMaxWidth(100);
            diceImage.setImageResource(R.drawable.dice_face_1);
            int finalI = i;
            diceImage.setOnClickListener(v -> toggleDiceSelection(finalI));
            diceContainer.addView(diceImage);
            diceImages[i] = diceImage;
        }
        updateDiceImages();
    }

    private void setupScoreTable(int numberOfPlayers) {
        scoreTableLayout.removeAllViews();

        TableRow playerHeaderRow = new TableRow(this);
        TextView emptyCornerView = new TextView(this);
        applyStyle(emptyCornerView, "TableHeaderStyle");
        playerHeaderRow.addView(emptyCornerView);

        for (int i = 1; i <= numberOfPlayers; i++) {
            TextView playerHeaderView = new TextView(this);
            playerHeaderView.setText("J" + i);
            applyStyle(playerHeaderView, "TableHeaderStyle");
            playerHeaderRow.addView(playerHeaderView);
            playerHeaderView.setBackgroundResource(R.drawable.table_cell_clickable_background);
            playerHeaderView.setTextAppearance(this, R.style.TableCellStyle);
        }
        scoreTableLayout.addView(playerHeaderRow);

        String[] categories = {
                "1", "2", "3", "4", "5", "6",
                "Total", "Bonus", "Total intermédiaire",
                "Brelan", "Carré", "Full House",
                "Petite Suite", "Grande Suite",
                "Yam", "Chance", "Total final"
        };

        for (String category : categories) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView textView = new TextView(this);
            textView.setText(category);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(8, 8, 8, 8);

            if (Arrays.asList("Total", "Bonus", "Total intermédiaire", "Total final").contains(category)) {
                textView.setBackgroundResource(R.drawable.cell_background_score_set);
                textView.setTextAppearance(this, R.style.TableHeaderStyle);
            } else {
                textView.setBackgroundResource(R.drawable.table_cell_clickable_background);
                textView.setTextAppearance(this, R.style.TableCellStyle);
            }
            row.addView(textView);

            for (int i = 0; i < numberOfPlayers; i++) {
                final TextView scoreView = new TextView(this);
                scoreView.setText("0");
                scoreView.setGravity(Gravity.CENTER);
                scoreView.setPadding(8, 8, 8, 8);
                scoreView.setBackgroundResource(R.drawable.table_cell_clickable_background);

                if (!Arrays.asList("Total", "Bonus", "Total intermédiaire", "Total final").contains(category)) {
                    scoreView.setBackgroundResource(R.drawable.table_cell_clickable_background);
                    final String finalCategory = category;
                    final int finalI = i;
                    scoreView.setOnClickListener(v -> onScoreCellClicked(finalCategory, finalI, scoreView));
                } else {
                    scoreView.setBackgroundResource(R.drawable.cell_background_score_set);
                    scoreView.setClickable(false);
                }
                row.addView(scoreView);
            }
            scoreTableLayout.addView(row);
        }
    }

    private void applyStyle(TextView textView, String styleName) {
        Context context = textView.getContext();
        int styleId = context.getResources().getIdentifier(styleName, "style", context.getPackageName());
        if (styleId != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(styleId);
            } else {
                textView.setTextAppearance(context, styleId);
            }
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        int paddingInPx = (int) (16 * scale + 0.5f);
        textView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
    }





    private void updateScoresForPlayer() {
        int totalScore = 0;
        for (int i = 1; i <= 6; i++) {
            TableRow row = (TableRow) scoreTableLayout.getChildAt(i);
            TextView scoreView = (TextView) row.getChildAt(currentPlayer + 1);
            try {
                totalScore += Integer.parseInt(scoreView.getText().toString());
            } catch (NumberFormatException e) {
                // Ignorer l'exception si le texte n'est pas un nombre valide
            }
        }
        int bonus = totalScore >= 63 ? 35 : 0;
        int totalIntermediate = totalScore + bonus;

        // Mise à jour des scores intermédiaires pour le joueur actuel
        updateScoreViewByText("Total", currentPlayer + 1, String.valueOf(totalScore));
        updateScoreViewByText("Bonus", currentPlayer + 1, String.valueOf(bonus));
        updateScoreViewByText("Total intermédiaire", currentPlayer + 1, String.valueOf(totalIntermediate));

        // Le calcul du "Total final" est maintenant déplacé dans sa propre méthode pour éviter les conflits
        updateTotalFinal();
    }

    private void updateScoreViewByText(String category, int playerColumn, String score) {
        for (int i = 0; i < scoreTableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) scoreTableLayout.getChildAt(i);
            TextView categoryView = (TextView) row.getChildAt(0);
            if (category.equals(categoryView.getText().toString())) {
                TextView scoreView = (TextView) row.getChildAt(playerColumn);
                scoreView.setText(score);
                return; // Sortie anticipée une fois le score mis à jour
            }
        }
    }

    private void updateTotalFinal() {
        for (int playerIndex = 1; playerIndex <= numberOfPlayers; playerIndex++) {
            int totalIntermediateIndex = findCategoryIndex("Total intermédiaire");
            int totalFinal = 0;

            // Si l'index de "Total intermédiaire" est trouvé, commencer à calculer à partir de là
            if (totalIntermediateIndex != -1) {
                for (int i = totalIntermediateIndex; i < scoreTableLayout.getChildCount() - 1; i++) {
                    // Exclure "Total final" de la boucle en s'arrêtant juste avant
                    TableRow row = (TableRow) scoreTableLayout.getChildAt(i);
                    TextView scoreView = (TextView) row.getChildAt(playerIndex);

                    String scoreText = scoreView.getText().toString();
                    if (!scoreText.isEmpty()) {
                        try {
                            totalFinal += Integer.parseInt(scoreText);
                        } catch (NumberFormatException e) {
                            // Ignorer si le texte n'est pas un nombre valide
                        }
                    }
                }
            }

            // Mettre à jour le "Total final" pour le joueur actuel
            TableRow finalRow = (TableRow) scoreTableLayout.getChildAt(scoreTableLayout.getChildCount() - 1);
            TextView finalScoreView = (TextView) finalRow.getChildAt(playerIndex);
            finalScoreView.setText(String.valueOf(totalFinal));
        }
    }

    private int findCategoryIndex(String category) {
        for (int i = 0; i < scoreTableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) scoreTableLayout.getChildAt(i);
            TextView categoryView = (TextView) row.getChildAt(0);
            if (category.equals(categoryView.getText().toString())) {
                return i; // Retourne l'index de la catégorie recherchée
            }
        }
        return -1; // Retourne -1 si la catégorie n'est pas trouvée
    }



    private void onScoreCellClicked(String category, int playerIndex, TextView scoreView) {
        if (!scoreView.isClickable()) return;

        final int potentialScore = calculateScoreForCategory(category, diceValues);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmer le score");
        builder.setMessage("Voulez-vous ajouter " + potentialScore + " points à " + category + " pour le joueur " + (currentPlayer + 1) + "?");
        builder.setPositiveButton("Oui", (dialogInterface, i) -> {
            scoreView.setText(String.valueOf(potentialScore));
            scoreView.setClickable(false); // Rendre la cellule non cliquable après la mise à jour du score.
            scoreView.setBackgroundResource(R.drawable.cell_background_score_set); // Changez le fond pour indiquer que le score est fixé.
            updateScoreInTable(category, potentialScore, currentPlayer);
            updateScoresForPlayer();
            scoreSelected = true; // Indique qu'un score a été sélectionné.

            // Ajoutez ici la logique pour passer au joueur suivant si cela est conforme à vos règles.
            prepareForNextPlayer();
        });
        builder.setNegativeButton("Non", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }




    private boolean hasFullHouse() {
        Map<Integer, Integer> diceCounts = new HashMap<>();
        for (int value : diceValues) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                diceCounts.put(value, diceCounts.getOrDefault(value, 0) + 1);
            }
        }
        boolean hasThreeOfAKind = false;
        boolean hasPair = false;
        for (int count : diceCounts.values()) {
            if (count == 3) hasThreeOfAKind = true;
            if (count == 2) hasPair = true;
        }
        return hasThreeOfAKind && hasPair;
    }

    private boolean hasYams() {
        Map<Integer, Integer> diceCount = new HashMap<>();
        for (int value : diceValues) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                diceCount.put(value, diceCount.getOrDefault(value, 0) + 1);
            }
        }
        return diceCount.containsValue(5);
    }

    private boolean hasSmallStraight(int[] diceValues) {
        Arrays.sort(diceValues);
        int consecutiveCount = 1;
        for (int i = 1; i < diceValues.length; i++) {
            if (diceValues[i] - diceValues[i - 1] == 1) {
                consecutiveCount++;
                if (consecutiveCount >= 4) {
                    return true;
                }
            } else if (diceValues[i] != diceValues[i - 1]) {
                consecutiveCount = 1;
            }
        }
        return false;
    }

    private boolean hasLargeStraight(int[] diceValues) {
        Arrays.sort(diceValues);
        for (int i = 1; i < diceValues.length; i++) {
            if (diceValues[i] - diceValues[i - 1] != 1) {
                return false;
            }
        }
        return true;
    }



    private int calculateScoreForCategory(String category, int[] diceValues) {
        switch (category) {
            case "Full House":
                return hasFullHouse() ? 25 : 0;
            case "Brelan":
                return calculateTotalForNOfAKind(3);
            case "Carré":
                return calculateTotalForNOfAKind(4);
            case "Yam":
                return hasYams() ? 50 : 0;
            case "Petite Suite":
                return hasSmallStraight(diceValues) ? 30 : 0;
            case "Grande Suite":
                return hasLargeStraight(diceValues) ? 40 : 0;

            case "Chance":
                return calculateTotalOfChance(diceValues);
            default:
                try {
                    int number = Integer.parseInt(category);
                    if (number >= 1 && number <= 6) {
                        return calculateTotalOfNumber(number, diceValues);
                    }
                } catch (NumberFormatException e) {
                }
                return 0;
        }
    }

    private int calculateTotalOfChance(int[] diceValues) {
        int total = 0;
        for (int value : diceValues) {
            total += value;
        }
        return total;
    }

    private int calculateTotalForNOfAKind(int N) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int value : diceValues) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                counts.put(value, counts.getOrDefault(value, 0) + 1);
            }
        }
        for (int count : counts.values()) {
            if (count >= N) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return Arrays.stream(diceValues).sum();
                }
            }
        }
        return 0;
    }

    private int calculateTotalOfNumber(int number, int[] diceValues) {
        int total = 0;
        for (int value : diceValues) {
            if (value == number) {
                total += value;
            }
        }
        return total;
    }

    private void updateScoreInTable(String category, int score, int playerIndex) {
        for (int i = 0; i < scoreTableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) scoreTableLayout.getChildAt(i);
            TextView categoryView = (TextView) row.getChildAt(0);
            if (category.equalsIgnoreCase(categoryView.getText().toString())) {
                TextView scoreView = (TextView) row.getChildAt(playerIndex + 1);
                scoreView.setText(String.valueOf(score));
                break;
            }
        }
    }



    private void resetForNextTurn() {
        Arrays.fill(diceToKeep, false);
        Arrays.fill(diceValues, 0);

        // Réinitialiser le compteur de lancers
        launchCount = 0;
        rollDiceButton.setEnabled(true); // Réactive le bouton pour lancer les dés
        scoreSelected = false; // Réinitialise la sélection de score

        // Mise à jour de l'apparence des dés et de leur valeur affichée
        for (int i = 0; i < diceImages.length; i++) {
            updateDiceAppearance(i); // Met à jour l'apparence visuelle des dés
            updateDiceValueDisplay(i); // Met à jour l'affichage des valeurs des dés
        }

        // Mise à jour du texte indiquant le nombre de lancers
        launchCounterTextView.setText("Lancers: 0");
    }

    private void updateDiceValueDisplay(int index) {
        int resId = getResources().getIdentifier("dice_face_" + diceValues[index], "drawable", getPackageName());
        diceImages[index].setImageResource(resId);
    }

    private void checkForEndGame() {
        // Vérifier si toutes les cellules de score sont non cliquables ou non initialisées (-1)
        boolean allScoresFinalized = true;
        for (int rowIndex = 0; rowIndex < scoreTableLayout.getChildCount() - 1; rowIndex++) { // Exclure le "Total final"
            TableRow row = (TableRow) scoreTableLayout.getChildAt(rowIndex);
            TextView categoryView = (TextView) row.getChildAt(0);

            // Ignorer les lignes de total qui sont calculées automatiquement
            if (Arrays.asList("Total", "Bonus", "Total intermédiaire", "Total final").contains(categoryView.getText().toString())) {
                continue;
            }

            for (int colIndex = 1; colIndex < row.getChildCount(); colIndex++) {
                TextView scoreView = (TextView) row.getChildAt(colIndex);
                if (scoreView.isClickable() || "-1".equals(scoreView.getText().toString())) {
                    allScoresFinalized = false;
                    break;
                }
            }
            if (!allScoresFinalized) {
                break;
            }
        }

        if (allScoresFinalized) {
            // Tous les scores ont été finalisés, procéder à déterminer le gagnant et fin de jeu
            determineWinnerAndEndGame();
        }
    }

    private void determineWinnerAndEndGame() {
        // Logique pour déterminer le gagnant et afficher le dialogue de fin de partie
        int highestScore = 0;
        int winningPlayerIndex = 0; // Assumer le premier joueur comme gagnant par défaut
        for (int playerIndex = 1; playerIndex <= numberOfPlayers; playerIndex++) {
            TableRow finalRow = (TableRow) scoreTableLayout.getChildAt(scoreTableLayout.getChildCount() - 1);
            TextView scoreView = (TextView) finalRow.getChildAt(playerIndex);
            int score = Integer.parseInt(scoreView.getText().toString());
            if (score > highestScore) {
                highestScore = score;
                winningPlayerIndex = playerIndex;
            }
        }
        showWinnerDialog(winningPlayerIndex, highestScore);
    }


    private void prepareForNextPlayer() {
        currentPlayer = (currentPlayer + 1) % numberOfPlayers;
        launchCount = 0; // Réinitialiser le compteur de lancers pour le nouveau joueur
        launchCounterTextView.setText("Lancers: " + launchCount); // Mettre à jour l'affichage du nombre de lancers
        rollDiceButton.setEnabled(true); // Réactiver le bouton de lancement des dés
        scoreSelected = false; // Réinitialiser l'indicateur de sélection de score pour le nouveau joueur

        // Cacher les images des dés et réinitialiser la sélection
        for (int i = 0; i < diceImages.length; i++) {
            diceImages[i].setVisibility(View.INVISIBLE); // Utiliser INVISIBLE ou GONE selon la préférence
            diceToKeep[i] = false; // Réinitialiser la sélection des dés
            updateDiceAppearance(i); // Mettre à jour l'apparence pour refléter la réinitialisation
        }

        updateCurrentPlayerIndicator(currentPlayer);

        if (currentPlayer == 0) {
            checkForEndGame();
        } else {
            resetForNextTurn();
        }
    }




    private void updateCurrentPlayerIndicator(int currentPlayer) {
        TextView currentPlayerIndicator = findViewById(R.id.currentPlayerIndicator);
        currentPlayerIndicator.setText("Joueur " + (currentPlayer + 1));
    }



    private boolean allScoresFilled() {
        for (int rowIndex = 0; rowIndex < scoreTableLayout.getChildCount(); rowIndex++) {
            TableRow scoreRow = (TableRow) scoreTableLayout.getChildAt(rowIndex);
            for (int colIndex = 1; colIndex < scoreRow.getChildCount(); colIndex++) {
                TextView scoreView = (TextView) scoreRow.getChildAt(colIndex);
                String scoreText = scoreView.getText().toString();
                // Considérer une case comme non remplie si sa valeur est toujours "-1"
                if ("-1".equals(scoreText)) {
                    return false;
                }
            }
        }
        return true; // Toutes les cases ont été remplies (ou marquées avec un score)
    }


    private void showWinnerDialog(final int playerIndex, int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Félicitations, Joueur " + playerIndex + "!");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setMessage("Vous avez le score le plus élevé de " + score + " points. Entrez votre nom pour le record :");
        builder.setPositiveButton("OK", (dialog, which) -> {
            String playerName = input.getText().toString();
            saveHighScore(playerName, score);
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveHighScore(String playerName, int score) {
        SharedPreferences prefs = getSharedPreferences("GameRecords", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(playerName, score);
        editor.apply();
        Intent intent = new Intent(this, RecordsActivity.class);
        startActivity(intent);
    }


}