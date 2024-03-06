package com.limayrac.yams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
        if (diceToKeep[index]) {
            diceImages[index].setBackgroundResource(R.drawable.selected_dice_border);
        } else {
            diceImages[index].setBackground(null);
        }
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
        scoreTableLayout.removeAllViews(); // Assurez-vous de nettoyer le tableau avant de le remplir à nouveau

        String[] categories = {
                "1", "2", "3", "4", "5", "6", "Total", "Bonus", "Total intermédiaire",
                "Brelan", "Carré", "Full House", "Petite Suite", "Grande Suite", "Yam", "Chance", "Total final"
        };

        List<String> autoCategories = Arrays.asList("Total", "Bonus", "Total intermédiaire", "Total final");

        for (String category : categories) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView textView = new TextView(this);
            textView.setText(category);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(5, 15, 5, 15);
            textView.setTypeface(null, Typeface.BOLD);
            row.addView(textView);

            for (int i = 0; i < numberOfPlayers; i++) {
                final int playerIndex = i;
                TextView scoreView = new TextView(this);
                scoreView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                scoreView.setGravity(Gravity.CENTER);
                scoreView.setPadding(5, 15, 5, 15);
                scoreView.setText("0");
                scoreView.setTag(category);

                if (!autoCategories.contains(category)) {
                    scoreView.setOnClickListener(v -> showScoreDialog(scoreView, category, playerIndex));
                }

                row.addView(scoreView);
            }

            scoreTableLayout.addView(row);
        }
    }

    private void showScoreDialog(TextView scoreView, String category, int playerIndex) {
        final int potentialScore = calculateScoreForCategory(category, diceValues);
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle("Confirmer le score");
        builder.setMessage("Voulez-vous ajouter " + potentialScore + " points à " + category + " ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scoreView.setText(String.valueOf(potentialScore));
                // Mettre à jour le score dans la logique de l'application ici
                updateScoreInTable(category, potentialScore, playerIndex);
                resetForNextTurn();
            }
        });
        builder.setNegativeButton("Non", (dialog, which) -> dialog.cancel());
        builder.show();
    }



    /*private void validateSelection() {
        // Cette méthode doit être modifiée pour capturer la catégorie sélectionnée par l'utilisateur.
        // Pour cet exemple, "Full House" est sélectionné par défaut.
        String selectedCategory = "Full House"; // Ceci doit être remplacé par la catégorie réellement sélectionnée.
        int scoreToAdd = calculateScoreForCategory(selectedCategory, diceValues);

        if (scoreToAdd > 0) {
            updateScoreInTable(selectedCategory, scoreToAdd);
            Toast.makeText(this, "Score ajouté : " + scoreToAdd, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sélection invalide pour cette catégorie", Toast.LENGTH_SHORT).show();
        }

        // Réinitialiser pour le prochain tour
        resetForNextTurn();
    }*/

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
            // Pour les scores simples (1 à 6)
            default:
                try {
                    int number = Integer.parseInt(category);
                    if (number >= 1 && number <= 6) {
                        return calculateTotalOfNumber(number);
                    }
                } catch (NumberFormatException e) {
                    // Gérer l'exception si nécessaire
                }
                return 0;
        }
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
    private int calculateTotalOfNumber(int number) {
        int total = 0;
        for (int value : diceValues) {
            if (value == number) {
                total += value;
            }
        }
        return total;
    }

    private void updateScoreInTable(String category, int score, int playerIndex) {
        // Parcourez chaque TableRow dans le TableLayout pour trouver la catégorie correspondante
        for (int i = 0; i < scoreTableLayout.getChildCount(); i++) {
            View view = scoreTableLayout.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                TextView categoryView = (TextView) row.getChildAt(0); // La catégorie est dans la première cellule
                if (category.equalsIgnoreCase(categoryView.getText().toString())) {
                    // Assurez-vous d'ajuster l'index pour correspondre à l'emplacement réel de la cellule du joueur dans la TableRow
                    // L'index du joueur dans la UI commence généralement après la cellule de la catégorie, donc +1
                    TextView scoreView = (TextView) row.getChildAt(playerIndex + 1);
                    scoreView.setText(String.valueOf(score));
                    break; // Sortez de la boucle une fois la catégorie trouvée et le score mis à jour
                }
            }
        }
    }


    private void resetForNextTurn() {
        Arrays.fill(diceToKeep, false);
        Arrays.fill(diceValues, 0);
        launchCount = 0;
        rollDiceButton.setEnabled(true);
        //validateSelectionButton.setEnabled(false);
        updateDiceImages();
        launchCounterTextView.setText("Lancers: 0");
    }




}