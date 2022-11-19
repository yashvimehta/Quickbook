package com.example.quickbook.FragmentSetUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.quickbook.MainActivity;
import com.example.quickbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UserCustomCardAdapter extends ArrayAdapter<String[]> {

    Context mContext;
    ArrayList<String[]>mArrayList;
    Button payFine, reIssueButton;
    FirebaseFirestore db;
    public static String rzpID;
    public static Button rzpButton;
    public UserCustomCardAdapter(@NonNull Context context, ArrayList<String[]> stringArrayList) {
        super(context, R.layout.custom_card, stringArrayList);
        this.mContext = context;
        this.mArrayList = stringArrayList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(this.getContext());
        @SuppressLint("ViewHolder") View view = mLayoutInflater.inflate(R.layout.user_custom_card, null, true);

        TextView bookName = view.findViewById(R.id.bookName);
        TextView returnDate = view.findViewById(R.id.returnDate);
        bookName.setText(mArrayList.get(position)[0]);
        payFine = view.findViewById(R.id.payFine);
        returnDate.setText("Return Date: "+mArrayList.get(position)[1]);
        int fineAmount=Integer.parseInt(mArrayList.get(position)[2]);
        db = FirebaseFirestore.getInstance();
        reIssueButton = view.findViewById(R.id.reIssue);


        if(mArrayList.get(position)[9].equals("true")){
            reIssueButton.setVisibility(View.INVISIBLE);
        }

        if(fineAmount!=0){
            payFine.setVisibility(View.VISIBLE);
            payFine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        rzpID=mArrayList.get(position)[4];
                        rzpButton=view.findViewById(R.id.payFine);
                        transact(fineAmount);
                    }
                    catch(Exception e) {}
                }
            });
        }
        else{
            payFine.setVisibility(View.INVISIBLE);
        }
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



    public void transact(int amount){
        // initialize Razorpay account.
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_nNQTEixTzHLBjc");
        checkout.setImage(R.drawable.books);

        // initialize json object
        JSONObject object = new JSONObject();
        try {
            object.put("name", "Quickbook");
            object.put("description", "Fee payment");
            object.put("theme.color", "");
            object.put("currency", "INR");
            object.put("amount", amount*100);
            checkout.open((Activity)mContext, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
