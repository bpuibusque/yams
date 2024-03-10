package com.limayrac.yams;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ScoreAdapter extends BaseAdapter {
    private Context context;
    private List<ScoreRecord> scoreList;

    public ScoreAdapter(Context context, List<ScoreRecord> scoreList) {
        this.context = context;
        this.scoreList = scoreList;
    }

    @Override
    public int getCount() {
        return scoreList.size();
    }

    @Override
    public Object getItem(int position) {
        return scoreList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_score, null);
        }

        TextView tvPlayerName = view.findViewById(R.id.tvPlayerName);
        TextView tvScore = view.findViewById(R.id.tvScore);

        ScoreRecord record = scoreList.get(position);
        tvPlayerName.setText(record.getPlayerName());
        tvScore.setText(String.valueOf(record.getScore()));

        return view;
    }
}
