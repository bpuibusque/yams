package com.limayrac.ressourcestestr2;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView text = this.findViewById(R.id.text1);
        text.setText(R.string.app_name);
    }
}