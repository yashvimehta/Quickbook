package com.example.quickbook.FragmentSetAdmin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.quickbook.R;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CustomCardAdapter extends ArrayAdapter<String[]> {

    Context mContext;
    ArrayList<String[]>mArrayList;
    boolean showLikes;

    public CustomCardAdapter(@NonNull Context context, ArrayList<String[]> stringArrayList) {
        super(context, R.layout.custom_card, stringArrayList);
        this.mContext = context;
        this.mArrayList = stringArrayList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(this.getContext());
        @SuppressLint("ViewHolder") View view = mLayoutInflater.inflate(R.layout.custom_card, null, true);

        TextView bookName = view.findViewById(R.id.bookName);
        TextView ID = view.findViewById(R.id.ID);
        TextView returnDate = view.findViewById(R.id.returnDate);

        bookName.setText(mArrayList.get(position)[0]);
        ID.setText("Member ID: "+mArrayList.get(position)[1] );
        returnDate.setText("Return Date: "+mArrayList.get(position)[2]);

        return view;
    }
}
