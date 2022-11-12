package com.example.quickbook;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class SearchCardAdapter extends ArrayAdapter<String[]> {

    Context mContext;
    ArrayList<String[]> mArrayList;

    public SearchCardAdapter(@NonNull Context context, ArrayList<String[]> stringArrayList) {
        super(context, R.layout.custom_card,stringArrayList);
        this.mContext = context;
        this.mArrayList = stringArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        @SuppressLint("ViewHolder") View view = mLayoutInflater.inflate(R.layout.search_card, null, true);

        TextView bookNameTextView = view.findViewById(R.id.bookNameTextView);
        ImageView bookImageView = view.findViewById(R.id.bookImageView);
        TextView authorNameTextView=view.findViewById(R.id.authorNameTextView);
        bookNameTextView.setText(mArrayList.get(position)[0]);
        authorNameTextView.setText(mArrayList.get(position)[1]);

        return view;
    }
}
