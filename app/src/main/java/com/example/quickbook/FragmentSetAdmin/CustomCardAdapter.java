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
import java.util.Calendar;
import java.util.Date;

public class CustomCardAdapter extends ArrayAdapter<String[]> {

    Context mContext;
    ArrayList<String[]>mArrayList;
    boolean showLikes;
    Button endIssueButton , reIssueButton;
    EditText setLateFeeEditText;

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
        reIssueButton = view.findViewById(R.id.reIssue);
        bookName.setText(mArrayList.get(position)[0]);
        ID.setText("Member ID: "+mArrayList.get(position)[1] );
        returnDate.setText("Return Date: "+mArrayList.get(position)[2]);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                                    db.collection("Transactions").document(documentID).update("feeValue", 0);
                                    incrementBook(mArrayList.get(position)[1]);
                                }
                                else {
                                    Toast.makeText(getContext(), "Fees has been charged", Toast.LENGTH_SHORT).show();
                                    String documentID = mArrayList.get(position)[3];
                                    int feeValueDB=Integer.parseInt(setLateFeeEditText.getText().toString());
                                    db.collection("Transactions").document(documentID).update("endIssue", true);
                                    db.collection("Transactions").document(documentID).update("feeValue", feeValueDB);
                                    incrementBook(mArrayList.get(position)[1]);
                                }
                                }
                        });
                builder.setNegativeButton("Cancel return", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                setLateFeeEditText = customLayout.findViewById(R.id.setLateFeeEditText);
                setLateFeeEditText.setText(mArrayList.get(position)[4]);
                dialog.show();
            }});


        reIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (Integer.parseInt(mArrayList.get(position)[4])!=0){
                    Toast.makeText(getContext(), "Cannot reissue books when fee is pending. ", Toast.LENGTH_SHORT).show();
                }
                else if( Integer.parseInt(mArrayList.get(position)[6] ) ==  Integer.parseInt(mArrayList.get(position)[7]) ){
                    Toast.makeText(getContext(), "Cannot reissue books more than " + mArrayList.get(position)[7]+ " times. ", Toast.LENGTH_SHORT).show();
                }
                else{ //re issue book
                    final int[] val = {0};
                    DocumentReference reIssue = db.collection("Transactions").document(mArrayList.get(position)[3]);
                    reIssue.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                val[0] = Integer.parseInt(String.valueOf(task.getResult().getData().get("reIssue")));
                            }
                        }
                    });
                    //increment reissue number
                    db.collection("Transactions").document(mArrayList.get(position)[3]).update("reIssue",val[0]+1 );

                    //update issuer date
                    db.collection("Transactions").document(mArrayList.get(position)[3]).update("issuerDate",new Timestamp(new Date()));

                    //update return date
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE, Integer.parseInt(mArrayList.get(position)[8]));
                    c.set(Calendar.HOUR_OF_DAY, 23);
                    c.set(Calendar.MINUTE,59);
                    c.set(Calendar.SECOND,59);
                    db.collection("Transactions").document(mArrayList.get(position)[3]).update("returnDate",c.getTime());

                    Toast.makeText(getContext(), "Book Re-Issued! ", Toast.LENGTH_SHORT).show();
                }

            }});
        return view;
    }
    public void incrementBook(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(String.valueOf(document.getData().get("ISBN")).equals(id)){
                                    String copies = String.valueOf(document.getData().get("Copies"));
                                    int newCopies = Integer.parseInt(copies ) + 1;
                                    db.collection("Books").document(document.getId()).update("Copies", newCopies);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

