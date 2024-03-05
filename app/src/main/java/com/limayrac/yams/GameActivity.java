package com.limayrac.yams;

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
    private Button rollDiceButton, validateSelectionButton;
    private LinearLayout diceContainer;
    private TableLayout scoreTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Récupérer le nombre de joueurs passé par l'intent de l'activité précédente
        int numberOfPlayers = getIntent().getIntExtra("numberOfPlayers", 1);

        // Initialiser l'interface utilisateur et configurer les écouteurs d'événements
        initializeUI();

        // Configurer les dés et le tableau des scores en fonction du nombre de joueurs
        setupDiceImages();
        setupScoreTable(numberOfPlayers);
    }

    private void initializeUI() {
        // Initialisation des composants de l'interface utilisateur à partir du layout
        diceContainer = findViewById(R.id.diceImagesLayout);
        scoreTableLayout = findViewById(R.id.scoreTableLayout);
        launchCounterTextView = findViewById(R.id.launchCounterTextView);
        rollDiceButton = findViewById(R.id.rollDiceButton);
        validateSelectionButton = findViewById(R.id.validateSelectionButton);

        // Configuration des écouteurs d'événements pour les boutons
        rollDiceButton.setOnClickListener(v -> rollDice());
        validateSelectionButton.setOnClickListener(v -> validateSelection());

        // Au début, le bouton de validation est désactivé jusqu'à ce que les dés soient lancés
        validateSelectionButton.setEnabled(false);
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
            validateSelectionButton.setEnabled(true); // Active le bouton de validation pour permettre le choix du score
        }
    }

    private void toggleDiceSelection(int index) {
        diceToKeep[index] = !diceToKeep[index]; // Basculer l'état de conservation du dé
        updateDiceAppearance(index); // Mettre à jour l'apparence du dé
    }

    private void updateDiceImages() {
        for (int i = 0; i < diceImages.length; i++) {
            int resId = getResources().getIdentifier("dice_face_" + diceValues[i], "drawable", getPackageName());
            diceImages[i].setImageResource(resId);
        }
    }

    private void updateDiceAppearance(int index) {
        if (diceToKeep[index]) {
            // Si le dé doit être conservé, on peut par exemple changer le fond pour indiquer sa sélection
            diceImages[index].setBackground(ContextCompat.getDrawable(this, R.drawable.selected_dice_border)); // Assurez-vous que `dice_selected_background` est défini dans vos ressources drawable
        } else {
            // Sinon, on rétablit l'apparence par défaut
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
            int finalI = i;
            diceImage.setOnClickListener(v -> toggleDiceSelection(finalI));
            diceContainer.addView(diceImage); // Ajouter l'ImageView au LinearLayout
            diceImages[i] = diceImage; // Stocker la référence de l'ImageView dans le tableau
        }
    }

    private void setupScoreTable(int numberOfPlayers) {
        scoreTableLayout = findViewById(R.id.scoreTableLayout); // Assurez-vous que scoreTableLayout est votre TableLayout

        // Liste des catégories de score
        String[] categories = {
                "1", "2", "3", "4", "5", "6",
                "Total", "Bonus", "Total intermédiaire",
                "Brelan", "Carré", "Full House",
                "Petite Suite", "Grande Suite",
                "Yam", "Chance", "Total final"
        };

        for (String category : categories) {
            TableRow row = new TableRow(this);
            TextView textView = new TextView(this);
            textView.setText(category);
            textView.setPadding(5, 5, 5, 5); // Exemple de padding
            row.addView(textView);

            // Ajouter une TextView pour chaque joueur
            for (int i = 0; i < numberOfPlayers; i++) {
                TextView scoreView = new TextView(this);
                scoreView.setText("0"); // Initialiser tous les scores à 0
                scoreView.setGravity(Gravity.CENTER);
                scoreView.setPadding(5, 5, 5, 5);
                row.addView(scoreView);
            }

            scoreTableLayout.addView(row); // Ajouter la ligne au TableLayout
        }
    }

    private void validateSelection() {
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

    private void updateScoreInTable(String category, int score) {
        // Cette méthode doit être implémentée pour mettre à jour l'interface utilisateur
        // avec le score calculé. Ceci peut impliquer de trouver la vue correspondante
        // dans le TableLayout et de mettre à jour son contenu.
    }

    private void resetForNextTurn() {
        Arrays.fill(diceToKeep, false);
        Arrays.fill(diceValues, 0);
        launchCount = 0;
        rollDiceButton.setEnabled(true);
        validateSelectionButton.setEnabled(false);
        updateDiceImages();
        launchCounterTextView.setText("Lancers: 0");
    }




}