package com.example.quickbook.FragmentSetAdmin;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.example.quickbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AdminIssuedBooksFragment extends Fragment {

    ListView mListView;
    EditText searchByID;
    CustomCardAdapter mCustomCardAdapter;
    Button searchButton;
    FirebaseFirestore db;
    int perDayFine;
    final String[] vall = new String[2];
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_admin_issued_books, container, false);
        db= FirebaseFirestore.getInstance();
        DocumentReference rulesDocumentRef1 = db.collection("Rules").document("ruless");
        rulesDocumentRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    vall[0] = String.valueOf(task.getResult().getData().get("consecutiveIssuals"));
                    vall[1] = String.valueOf(task.getResult().getData().get("issueDuration(days)"));
                }
            }
        });

        mListView = view.findViewById(R.id.issuedBooks);
        searchByID = view.findViewById(R.id.searchByID);
        searchButton=view.findViewById(R.id.searchButton);

        DocumentReference rulesDocumentRef = db.collection("Rules").document("ruless");
        rulesDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    perDayFine=Integer.parseInt((String) task.getResult().getData().get("fineAmount(perDay)"));

                    searchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String searchName = searchByID.getText().toString();
                                setAllData(searchName);
                        }
                    });
                    setAllData("");
                }

            }
        });
        return view;
    }
    public int getFine(Timestamp javaDate1){
        int lateFee=0;
        long returnTime =  javaDate1.getSeconds();
        long currentTime = new Timestamp(new Date()).getSeconds();
        if(currentTime>returnTime){
            if( (currentTime +19800) %86400 <  (returnTime +19800) %86400 ){
                lateFee =  (perDayFine * ( 1 +( (int)(currentTime -  returnTime)/86400)));
            }
            else{
                lateFee = (perDayFine * (((  (int) (currentTime -  returnTime))/86400)));
            }
        }
        return lateFee;
    }

    public void setAllData(String searchName){
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
                                String reIssue = String.valueOf(document.getData().get("reIssue"));
                                Boolean endIssue = Boolean.valueOf(String.valueOf(document.getData().get("endIssue")));
                                Timestamp javaDate1 = (Timestamp) document.getData().get("returnDate");
                                Date javaDate = javaDate1.toDate();
                                String[] returnDate  = String.valueOf(javaDate).split(" GMT") ;
                                String returnn = returnDate[0].substring(0, returnDate[0].length() - 9);
                                String documentID = document.getId();
                                if ((searchName.equals("")||searchName.equals(issuerID))  && !endIssue ) {
                                    String[] arrayListFeeder=new String[]{bookName, issuerID, returnn, documentID,String.valueOf(getFine(javaDate1)),String.valueOf(javaDate1.getSeconds()), reIssue, vall[0], vall[1] };
                                    stringArrayList.add(arrayListFeeder);
                                    val++;
                                }
                            }
                            if (val == 0) {
                                Toast.makeText(getContext(), "Member ID not found", Toast.LENGTH_SHORT).show();
                            }
                            sort(stringArrayList);


                            mCustomCardAdapter = new CustomCardAdapter(requireContext(),stringArrayList);
                            mListView.setAdapter(mCustomCardAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void sort(ArrayList<String[]> stringArrayList){
        Collections.sort(stringArrayList, new Comparator<String[]>() {

            @Override
            public int compare(String[] s1, String[] s2) {
                long s1Time = Long.valueOf(s1[5]);
                long s2Time=Long.valueOf(s2[5]);
                return (int)(s2Time-s1Time);
            }
        });

    }
//    public String getreIssuePeriod(){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//    }
}