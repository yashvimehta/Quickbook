package com.example.quickbook.FragmentSetAdmin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CustomCardAdapter extends ArrayAdapter<String[]> {

    Context mContext;
    ArrayList<String[]>mArrayList;
    boolean showLikes;
    Button endIssueButton;
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
        bookName.setText(mArrayList.get(position)[0]);
        ID.setText("Member ID: "+mArrayList.get(position)[1] );
        returnDate.setText("Return Date: "+mArrayList.get(position)[2]);


        endIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);
                builder.setTitle("Set late fee amount");
                final View customLayout = inflater
                        .inflate(R.layout.alert_admin_set_fine, null);
                builder.setView(customLayout);
                builder.setPositiveButton(
                        "Charge Fees",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String feeValue=setLateFeeEditText.getText().toString();
                                if(feeValue.equals("")||feeValue.equals("0")){
                                    Toast.makeText(getContext(), "No fees charged", Toast.LENGTH_SHORT).show();
                                    //TODO save lateFee=0 in DB for this transaction.
                                }
                                else {
                                    Toast.makeText(getContext(), "Fees has been charged", Toast.LENGTH_SHORT).show();
                                    //TODO Get value from editText and save in DB for this transaction.
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
                //TODO Calculate late fees and display in editText
                setLateFeeEditText.setText("100");
                dialog.show();
            }});

        return view;
    }
}
