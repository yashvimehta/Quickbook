package com.example.quickbook.FragmentSetAdmin;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.Data;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quickbook.ApiHelper.ApiInterface;
import com.example.quickbook.ApiHelper.SearchBookResult;
import com.example.quickbook.BookInfo;
import com.example.quickbook.R;
import com.example.quickbook.ResultsActivity;
import com.example.quickbook.SearchCardAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminIssuedBooksFragment extends Fragment {

    ListView mListView;
    EditText searchByID;
    CustomCardAdapter mCustomCardAdapter;
    Button searchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_admin_issued_books, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mListView = view.findViewById(R.id.issuedBooks);
        searchByID = view.findViewById(R.id.searchByID);
        searchButton=view.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchName = searchByID.getText().toString();
                if(searchName.equals("")){
                    Toast.makeText(getContext(), "Member ID cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    db.collection("Transactions")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int val = 0;
                                    if (task.isSuccessful()) {
                                        ArrayList<String[]> stringArrayList = new ArrayList<String[]>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String bookName = String.valueOf(document.getData().get("bookName"));
                                            String issuerID = String.valueOf(document.getData().get("issuerID"));
                                            Timestamp javaDate1 = (Timestamp) document.getData().get("returnDate");
                                            Date javaDate = javaDate1.toDate();
                                            String[] returnDate = String.valueOf(javaDate).split(" GMT");
                                            String returnn = returnDate[0].substring(0, returnDate[0].length() - 9);
                                            if (searchName.equals(issuerID)) {
                                                stringArrayList.add(new String[]{bookName, issuerID, returnn});
                                                val++;
                                            }
                                        }
                                        if (val == 0) {
                                            Toast.makeText(getContext(), "Member ID not found", Toast.LENGTH_SHORT).show();
                                        }
                                        mCustomCardAdapter = new CustomCardAdapter(requireContext(), stringArrayList);
                                        mListView.setAdapter(mCustomCardAdapter);
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            }
        });


        db.collection("Transactions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int val = 0;
                        if (task.isSuccessful()) {
                            ArrayList<String[]> stringArrayList=new ArrayList<String[]>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String bookName = String.valueOf(document.getData().get("bookName"));
                                String issuerID = String.valueOf(document.getData().get("issuerID"));
                                Timestamp javaDate1 = (Timestamp) document.getData().get("returnDate");
                                Date javaDate = javaDate1.toDate();
                                String[] returnDate  = String.valueOf(javaDate).split(" GMT") ;
                                String returnn = returnDate[0].substring(0, returnDate[0].length() - 9);
                                Log.i("date", returnn);
                                stringArrayList.add(new String[]{bookName , issuerID , returnn});
                                Log.i("set ", "yayayay");
                                Log.i("set1 ", "yayayay");
                            }
                            mCustomCardAdapter = new CustomCardAdapter(requireContext(),stringArrayList);
                            mListView.setAdapter(mCustomCardAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        Log.i("setttt", "ppp");
        return view;
    }
}