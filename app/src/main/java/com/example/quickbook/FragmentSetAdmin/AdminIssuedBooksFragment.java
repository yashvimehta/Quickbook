package com.example.quickbook.FragmentSetAdmin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.quickbook.R;
import com.example.quickbook.ResultsActivity;

public class AdminIssuedBooksFragment extends Fragment {

    ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_admin_issued_books, container, false);

        mListView = view.findViewById(R.id.similarItemsListView);
        mListView.setAdapter(ResultsActivity.mAdapter);
        Log.i("setttt", "ppp");
        return view;
    }
}