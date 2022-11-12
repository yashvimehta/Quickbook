package com.example.quickbook.FragmentSetAdmin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.quickbook.R;

import java.util.ArrayList;

public class AdminIssuedBooksFragment extends Fragment {

    Context mContext;
    ArrayList<String> mArrayList;
    ArrayList<Boolean> likedArrayList;
    boolean showLikes;

    public static CustomCardAdapter mAdapter;
    ListView mListView;
    ArrayList<String> stringArrayList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        stringArrayList = new ArrayList<>();
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_issued_books, container, false);
        mAdapter = new CustomCardAdapter(this, stringArrayList, new ArrayList<Boolean>(), false);
        mListView = view.findViewById(R.id.issuedBooks);
        mListView.setAdapter(ResultsActivity.mAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}