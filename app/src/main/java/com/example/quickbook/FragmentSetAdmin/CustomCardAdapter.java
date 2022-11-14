package com.example.quickbook.FragmentSetAdmin;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.quickbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class CustomCardAdapter extends ArrayAdapter<String[]> {

    Context mContext;
    ArrayList<String[]>mArrayList;
    boolean showLikes;
    Button endIssueButton;
    EditText setLateFeeEditText;
    int lateFee;
    String perDayFine;

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
        endIssueButton=view.findViewById(R.id.endIssue);
        bookName.setText(mArrayList.get(position)[0]);
        ID.setText("Member ID: "+mArrayList.get(position)[1] );
        returnDate.setText("Return Date: "+mArrayList.get(position)[2]);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference rulesDocumentRef = db.collection("Rules").document("ruless");
        rulesDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    perDayFine = (String) task.getResult().getData().get("fineAmount(perDay)");
                }
            }
        });



        Log.i("lateee", String.valueOf(lateFee));

        endIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);
                builder.setTitle("Set late fee amount");
                final View customLayout = inflater.inflate(R.layout.alert_admin_set_fine, null);
                builder.setView(customLayout);
                builder.setPositiveButton(
                        "Charge Fees",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String feeValue=setLateFeeEditText.getText().toString();
                                if(feeValue.equals("")||feeValue.equals("0")){
                                    Toast.makeText(getContext(), "No fees charged", Toast.LENGTH_SHORT).show();
                                    String documentID = mArrayList.get(position)[3];
                                    db.collection("Transactions").document(documentID).update("endIssue", true);
                                    //TODO remove books from issue of user? ask M
                                }
                                else {
                                    Toast.makeText(getContext(), "Fees has been charged", Toast.LENGTH_SHORT).show();
                                    String documentID = mArrayList.get(position)[3];
                                    db.collection("Transactions").document(documentID).update("endIssue", true);
                                    //TODO send notification to user saying fee charged
                                }
                                }
                        });
                builder.setNegativeButton("Cancel return", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                //TODO line 120 - 143 - get late fee. shows initial before. ask M
                String documentID = mArrayList.get(position)[3];
                DocumentReference transactionDocumentRef = db.collection("Transactions").document(documentID);
                transactionDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Timestamp javaDate1 = (Timestamp) task.getResult().getData().get("returnDate");
                            long returnTime =  javaDate1.getSeconds();;
                            long currentTime = new Timestamp(new Date()).getSeconds();
                            if(currentTime<=returnTime){
                                lateFee = 0;
                            }
                            else{
                                if( (  currentTime +19800) %86400 <  (returnTime +19800) %86400 ){
                                    lateFee =  (Integer.parseInt(perDayFine) * ( 1 +( (int)(currentTime -  returnTime)/86400)));
                                }
                                else{
                                    lateFee = (Integer.parseInt(perDayFine) * (((  (int) (currentTime -  returnTime))/86400)));

                                }
                            }
                        }
                    }
                });


                AlertDialog dialog = builder.create();
                setLateFeeEditText = customLayout.findViewById(R.id.setLateFeeEditText);
                setLateFeeEditText.setText(String.valueOf(lateFee));
                dialog.show();
            }});

        return view;
    }
}
