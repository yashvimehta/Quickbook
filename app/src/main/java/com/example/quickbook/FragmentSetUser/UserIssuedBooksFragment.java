package com.example.quickbook.FragmentSetUser;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quickbook.FragmentSetAdmin.CustomCardAdapter;
import com.example.quickbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class UserIssuedBooksFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ListView mListView;
    UserCustomCardAdapter mUserCustomCardAdapter;
    final String[] memberid = new String[1];
    final String[] vall = new String[2];
    int perDayFine;
    public static String docID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_issued_books, container, false);
        mListView = view.findViewById(R.id.userIssuedBooks);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        db= FirebaseFirestore.getInstance();
        DocumentReference userDocumentRef = db.collection("Users").document(user.getUid());
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
        userDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    memberid[0] = String.valueOf(task.getResult().getData().get("memberID"));
                    setData();
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);


        return view;
    }

    public void sort(ArrayList<String[]> stringArrayList){
        Collections.sort(stringArrayList, new Comparator<String[]>() {

            @Override
            public int compare(String[] s1, String[] s2) {
                long s1Time = Long.valueOf(s1[3]);
                long s2Time=Long.valueOf(s2[3]);
                return (int)(s2Time-s1Time);
            }
        });}

    public void setData(){
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
                                String value="0";
                                if (endIssue){
                                    value=String.valueOf(document.getData().get("feeValue"));
                                    Log.i("Fees",bookName+":"+String.valueOf(value));
                                }
                                Timestamp javaDate1 = (Timestamp) document.getData().get("returnDate");
                                Date javaDate = javaDate1.toDate();
                                String[] returnDate  = String.valueOf(javaDate).split(" GMT") ;
                                String returnn = returnDate[0].substring(0, returnDate[0].length() - 9);
                                if (issuerID.equals(memberid[0])) {
                                    String[] arrayListFeeder=new String[]{bookName, returnn, value, String.valueOf(javaDate1.getSeconds()),document.getId(), String.valueOf(getFine(javaDate1)), reIssue, vall[0], vall[1], String.valueOf(endIssue)};
                                    stringArrayList.add(arrayListFeeder);
                                    val++;
                                }
                            }
                            if (val == 0) {
                                Toast.makeText(getContext(), "No books issued", Toast.LENGTH_SHORT).show();
                            }
                            Log.i("Length",String.valueOf(stringArrayList.size()));
                            sort(stringArrayList);

                            mUserCustomCardAdapter = new UserCustomCardAdapter(requireContext(),stringArrayList);
                            mListView.setAdapter(mUserCustomCardAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
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
}