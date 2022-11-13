package com.example.quickbook;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SearchCardAdapter extends ArrayAdapter<String[]> {

    Context mContext;
    ArrayList<String[]> mArrayList;

    public SearchCardAdapter(@NonNull Context context, ArrayList<String[]> stringArrayList) {
        super(context, R.layout.search_card,stringArrayList);
        this.mContext = context;
        this.mArrayList = stringArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        @SuppressLint("ViewHolder") View view = mLayoutInflater.inflate(R.layout.search_card, null, true);

        TextView bookNameTextView = view.findViewById(R.id.bookNameTextView);
        final ImageView bookImageView = view.findViewById(R.id.bookImageView);
        TextView authorNameTextView=view.findViewById(R.id.authorNameTextView);
        bookNameTextView.setText(mArrayList.get(position)[0]);
        authorNameTextView.setText(mArrayList.get(position)[1]);
        Glide.with(getContext())
                .load(mArrayList.get(position)[5])
                .placeholder(R.drawable.image_progress)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(bookImageView);
        return view;
    }
}
