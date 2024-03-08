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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        int numberOfPlayers = getIntent().getIntExtra("numberOfPlayers", 1);

        initializeUI();
        setupDiceImages();
        setupScoreTable(numberOfPlayers);
    }

    private void initializeUI() {
        diceContainer = findViewById(R.id.diceImagesLayout);
        scoreTableLayout = findViewById(R.id.scoreTableLayout);
        launchCounterTextView = findViewById(R.id.launchCounterTextView);
        rollDiceButton = findViewById(R.id.rollDiceButton);
        //validateSelectionButton = findViewById(R.id.validateSelectionButton);

        rollDiceButton.setOnClickListener(v -> rollDice());
        //validateSelectionButton.setOnClickListener(v -> validateSelection());
        //validateSelectionButton.setEnabled(false);
    }
    private void rollDice() {
        if (launchCount < MAX_LAUNCHES) {
            for (int i = 0; i < 5; i++) {
                if (!diceToKeep[i]) {
                    diceValues[i] = random.nextInt(6) + 1; // Générer de nouveaux nombres pour les dés non conservés
                }
            }
            launchCount++;
            updateDiceImages(); // Met à jour l'affichage des dés avec les nouvelles valeurs
            launchCounterTextView.setText("Lancers: " + launchCount);
        }

        if (launchCount == MAX_LAUNCHES) {
            rollDiceButton.setEnabled(false); // Désactive le bouton après le nombre maximum de lancers
           // validateSelectionButton.setEnabled(true); // Active le bouton de validation pour permettre le choix du score
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
        // Ajoutez un log ou un Toast ici pour tester
        Toast.makeText(this, "Dé " + (index + 1) + (diceToKeep[index] ? " sélectionné" : " désélectionné"), Toast.LENGTH_SHORT).show();
    }


    private void updateDiceAppearance(int index) {
        ImageView diceImage = diceImages[index];
        if (diceToKeep[index]) {
            // Appliquez l'encadrement pour indiquer la sélection
            diceImage.setBackgroundResource(R.drawable.selected_dice_border);
        } else {
            // Retirez l'encadrement pour indiquer la désélection
            diceImage.setBackground(null); // Utilisez setBackgroundDrawable(null) pour les versions antérieures
        }

        // Assurez-vous que les paramètres de mise en page restent constants
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.dice_size),
                getResources().getDimensionPixelSize(R.dimen.dice_size));
        layoutParams.gravity = Gravity.CENTER;
        diceImage.setLayoutParams(layoutParams);
    }




    private void setupDiceImages() {
        diceContainer = findViewById(R.id.diceImagesLayout);

        // Création dynamique des ImageView pour chaque dé
        for (int i = 0; i < diceImages.length; i++) {
            ImageView diceImage = new ImageView(this);
            diceImage.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            diceImage.setAdjustViewBounds(true);
            diceImage.setMaxHeight(100); // Taille maximale de l'image du dé
            diceImage.setMaxWidth(100);
            // Définir une image par défaut pour le dé
            diceImage.setImageResource(R.drawable.dice_face_1); // Image par défaut
            int finalI = i; // Nécessaire pour être utilisé dans une expression lambda
            diceImage.setOnClickListener(v -> toggleDiceSelection(finalI));
            diceContainer.addView(diceImage);
            diceImages[i] = diceImage;
        }
        updateDiceImages();
    }

    private void setupScoreTable(int numberOfPlayers) {
        scoreTableLayout.removeAllViews(); // Nettoyez le tableau avant de le remplir

        // Ajoutez une ligne d'en-tête pour les joueurs, si nécessaire
        TableRow playerHeaderRow = new TableRow(this);
        TextView emptyCornerView = new TextView(this); // Coin vide pour l'alignement
        applyStyle(emptyCornerView, "TableHeaderStyle");
        playerHeaderRow.addView(emptyCornerView);

        for (int i = 1; i <= numberOfPlayers; i++) {
            TextView playerHeaderView = new TextView(this);
            playerHeaderView.setText("J" + i);
            applyStyle(playerHeaderView, "TableHeaderStyle");
            playerHeaderRow.addView(playerHeaderView);
            playerHeaderView.setBackgroundResource(R.drawable.table_cell_clickable_background); // Style par défaut
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
            textView.setPadding(8, 8, 8, 8); // Augmenter le padding pour un espace visible

            if (Arrays.asList("Total", "Bonus", "Total intermédiaire", "Total final").contains(category)) {
                textView.setBackgroundResource(R.drawable.cell_background_score_set); // Style pour les catégories calculées automatiquement
                textView.setTextAppearance(this, R.style.TableHeaderStyle);
            } else {
                textView.setBackgroundResource(R.drawable.table_cell_clickable_background); // Style par défaut
                textView.setTextAppearance(this, R.style.TableCellStyle);
            }
            row.addView(textView);

            for (int i = 0; i < numberOfPlayers; i++) {
                final TextView scoreView = new TextView(this);
                scoreView.setText("0"); // Initialisation avec 0
                scoreView.setGravity(Gravity.CENTER);
                scoreView.setPadding(8, 8, 8, 8); // Augmenter le padding pour un espace visible
                scoreView.setBackgroundResource(R.drawable.table_cell_clickable_background); // Style par défaut
                scoreView.setTextAppearance(this, R.style.TableCellStyle);

                if (!Arrays.asList("Total", "Bonus", "Total intermédiaire", "Total final").contains(category)) {
                    scoreView.setBackgroundResource(R.drawable.table_cell_clickable_background); // Style par défaut pour les cellules cliquables
                    final String finalCategory = category;
                    final int finalI = i;
                    scoreView.setOnClickListener(v -> onScoreCellClicked(finalCategory, finalI, scoreView));
                } else {
                    scoreView.setBackgroundResource(R.drawable.cell_background_score_set); // Catégories automatiques non cliquables
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
        // Appliquer manuellement le padding
        final float scale = context.getResources().getDisplayMetrics().density;
        int paddingInPx = (int) (16 * scale + 0.5f);
        textView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
    }



    private void updateAutomaticScores() {
        int totalScore = 0; // Pour les scores des catégories "1" à "6"
        int bonus = 0; // Bonus accordé
        int totalIntermediate = 0; // Total intermédiaire incluant le bonus
        int totalFinal = 0; // Total final incluant tous les scores

        // Calculer le total des scores pour les catégories "1" à "6"
        for (int i = 1; i <= 6; i++) { // Suppose que les scores "1" à "6" sont dans les 6 premières lignes
            TableRow row = (TableRow) scoreTableLayout.getChildAt(i);
            for (int j = 1; j < row.getChildCount(); j++) { // Commence à 1 pour ignorer la colonne de l'en-tête de catégorie
                View view = row.getChildAt(j);
                if (view instanceof TextView) {
                    TextView scoreView = (TextView) view;
                    try {
                        totalScore += Integer.parseInt(scoreView.getText().toString());
                    } catch (NumberFormatException e) {
                        // Ignorer si le texte n'est pas un nombre
                    }
                }
            }
        }

        // Calculer le bonus
        bonus = totalScore >= 63 ? 35 : 0;

        // Total intermédiaire est le total des scores "1" à "6" plus le bonus
        totalIntermediate = totalScore + bonus;

        // Calculer le total final en incluant les scores de toutes les autres catégories
        for (int i = 7; i < scoreTableLayout.getChildCount(); i++) { // Ignorer les lignes d'en-tête et de bonus
            TableRow row = (TableRow) scoreTableLayout.getChildAt(i);
            for (int j = 1; j < row.getChildCount(); j++) {
                View view = row.getChildAt(j);
                if (view instanceof TextView && !row.getChildAt(0).toString().equals("Total final")) { // Vérifier si ce n'est pas la ligne "Total final"
                    TextView scoreView = (TextView) view;
                    try {
                        totalFinal += Integer.parseInt(scoreView.getText().toString());
                    } catch (NumberFormatException e) {
                        // Ignorer ou gérer l'exception
                    }
                }
            }
        }

        // Total final inclut les scores de toutes les catégories plus le total intermédiaire
        totalFinal += totalIntermediate;

        // Mettre à jour l'UI avec les nouveaux totaux
        updateScoreViewByText("Total", String.valueOf(totalScore));
        updateScoreViewByText("Bonus", String.valueOf(bonus));
        updateScoreViewByText("Total intermédiaire", String.valueOf(totalIntermediate));
        updateScoreViewByText("Total final", String.valueOf(totalFinal));
    }




    private void updateScoreViewByText(String categoryText, String score) {
        for (int i = 0; i < scoreTableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) scoreTableLayout.getChildAt(i);
            TextView categoryView = (TextView) row.getChildAt(0); // La catégorie est toujours dans la première cellule
            if (categoryText.equals(categoryView.getText().toString())) {
                TextView scoreView = (TextView) row.getChildAt(1); // Supposons 1 joueur pour simplifier
                scoreView.setText(score);
                break; // Sortir de la boucle après la mise à jour
            }
        }
    }



    private void onScoreCellClicked(String category, int playerIndex, TextView scoreView) {
        if (!scoreView.isClickable()) return; // Si la vue n'est pas cliquable, sortez de la méthode.

        final int potentialScore = calculateScoreForCategory(category, diceValues);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmer le score");
        builder.setMessage("Voulez-vous ajouter " + potentialScore + " points à " + category + " pour le joueur " + (playerIndex + 1) + "?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                scoreView.setText(String.valueOf(potentialScore));
                scoreView.setClickable(false); // Rendre la cellule non cliquable après la mise à jour du score.
                scoreView.setBackgroundResource(R.drawable.cell_background_score_set); // Changez le fond pour indiquer que le score est fixé.
                updateScoreInTable(category, potentialScore, playerIndex);
                updateAutomaticScores(); // Mettez à jour les scores automatiques après chaque changement.
                resetForNextTurn();
            }
        });
        builder.setNegativeButton("Non", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }



    // Vérification des différentes combinaisons
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

    // Calcul des scores pour chaque catégorie
    private int calculateScoreForCategory(String category, int[] diceValues) {
        switch (category) {
            case "Full House":
                return hasFullHouse() ? 25 : 0;
            case "Brelan":
                return calculateTotalForNOfAKind(3);
            case "Carré":
                return calculateTotalForNOfAKind(4);
            case "Yam's":
                return calculateTotalForNOfAKind(5) > 0 ? 50 : 0;
            case "Chance":
                // Pour "Chance", on additionne simplement toutes les valeurs des dés
                return calculateTotalOfChance(diceValues);
            // Ajoutez ici la logique pour d'autres catégories si nécessaire
            default:
                // Pour les scores simples (1 à 6)
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


    // Calcul du score pour les N dés identiques
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

    // Calcul du score pour les dés avec la valeur spécifiée
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
                TextView scoreView = (TextView) row.getChildAt(playerIndex + 1); // +1 car la première cellule est le nom de la catégorie
                scoreView.setText(String.valueOf(score));
                break;
            }
        }
    }



    private void resetForNextTurn() {

        Arrays.fill(diceToKeep, false); // Réinitialisez le suivi des dés à conserver
        Arrays.fill(diceValues, 0); // Réinitialisez les valeurs des dés
        launchCount = 0; // Réinitialisez le compteur de lancers
        rollDiceButton.setEnabled(true); // Réactivez le bouton de lancer

        // Réinitialisez l'apparence de chaque dé
        for (int i = 0; i < diceImages.length; i++) {
            updateDiceAppearance(i); // Cette méthode réinitialisera l'encadrement
        }

        launchCounterTextView.setText("Lancers: 0");
    }

    private void checkForEndGame() {
        boolean isGameFinished = true;
        int highestScore = 0;
        int winningPlayerIndex = -1;

        // Supposer que le score total final est dans la dernière ligne pour chaque joueur
        TableRow finalRow = (TableRow) scoreTableLayout.getChildAt(scoreTableLayout.getChildCount() - 1);
        for (int i = 1; i < finalRow.getChildCount(); i++) {
            TextView scoreView = (TextView) finalRow.getChildAt(i);
            int playerScore = Integer.parseInt(scoreView.getText().toString());
            if (playerScore > highestScore) {
                highestScore = playerScore;
                winningPlayerIndex = i;
            }
        }


        if (isGameFinished && winningPlayerIndex != -1) {
            showWinnerDialog(winningPlayerIndex, highestScore);
        }
    }

    private void showWinnerDialog(final int playerIndex, int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Félicitations, Joueur " + playerIndex + "!");

        // Ajouter un champ de texte à l'AlertDialog pour saisir le nom
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
        // Sauvegardez le score avec le nom du joueur comme clé
        editor.putInt(playerName, score);
        editor.apply();

        Intent intent = new Intent(this, RecordsActivity.class);
        startActivity(intent);


    }



}