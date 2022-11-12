package com.example.quickbook.FragmentSetAdmin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.quickbook.R;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CustomCardAdapter extends ArrayAdapter<String> {

    Context mContext;
    ArrayList<String> mArrayList;
    ArrayList<Boolean> likedArrayList;
    boolean showLikes;

    public CustomCardAdapter(@NonNull Context context, ArrayList<String> stringArrayList, ArrayList<Boolean> booleanArrayList, boolean isHistory) {
        super(context, R.layout.custom_card, stringArrayList);

        this.mContext = context;
        this.mArrayList = stringArrayList;
        this.likedArrayList = booleanArrayList;
        this.showLikes = isHistory;
    }


    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(this.getContext());
        @SuppressLint("ViewHolder") View view = mLayoutInflater.inflate(R.layout.custom_card, null, true);

        TextView bookName = view.findViewById(R.id.bookName);
        TextView IDandReturn = view.findViewById(R.id.IDandReturn);


        String[] str = mArrayList.get(position).split(" ");

        bookName.setText(str[0]);
        IDandReturn.setText(str[1] + " " + str[2]);

        return view;
    }
}
