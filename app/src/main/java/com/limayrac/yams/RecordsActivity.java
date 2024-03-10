package com.limayrac.yams;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Modèle de données
class ScoreRecord {
    String playerName;
    int score;

    public ScoreRecord(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
}

// Adapteur personnalisé
class RecordAdapter extends BaseAdapter {
    private Context context;
    private List<ScoreRecord> records;

    public RecordAdapter(Context context, List<ScoreRecord> records) {
        this.context = context;
        this.records = records;
    }

    @Override
    public int getCount() { return records.size(); }

    @Override
    public Object getItem(int position) { return records.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_record, parent, false);
        }
        TextView playerName = convertView.findViewById(R.id.playerName);
        TextView playerScore = convertView.findViewById(R.id.playerScore);
        ScoreRecord record = records.get(position);
        playerName.setText(record.getPlayerName());
        playerScore.setText(String.valueOf(record.getScore()));
        return convertView;
    }
}

public class RecordsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        ListView lvRecords = findViewById(R.id.lvRecords);
        List<ScoreRecord> recordsList = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("GameRecords", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            int score = Integer.parseInt(entry.getValue().toString());
            recordsList.add(new ScoreRecord(key, score));
        }

        RecordAdapter adapter = new RecordAdapter(this, recordsList);
        lvRecords.setAdapter(adapter);

        Button btnReturnHome = findViewById(R.id.btnReturnHome);
        btnReturnHome.setOnClickListener(view -> finish());

        Button btnClearRecords = findViewById(R.id.btnClearRecords);
        btnClearRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(RecordsActivity.this)
                        .setTitle("Confirmer la suppression")
                        .setMessage("Êtes-vous sûr de vouloir supprimer tous les records ?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            clearAllRecords();
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    private void clearAllRecords() {
        SharedPreferences prefs = getSharedPreferences("GameRecords", MODE_PRIVATE);
        prefs.edit().clear().apply();
        // Après suppression, redémarrer l'activité pour rafraîchir la liste des records
        finish();
        startActivity(getIntent());
    }
}
