package com.limayrac.yams;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Map;

public class RecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        ListView lvRecords = findViewById(R.id.lvRecords);
        ArrayList<String> recordsList = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("GameRecords", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            recordsList.add(entry.getKey() + ": " + entry.getValue().toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recordsList);
        lvRecords.setAdapter(adapter);
    }
}
