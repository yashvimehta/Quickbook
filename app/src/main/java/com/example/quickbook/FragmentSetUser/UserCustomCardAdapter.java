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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserCustomCardAdapter extends ArrayAdapter<String[]> {

    Context mContext;
    ArrayList<String[]>mArrayList;
    Button payFine;
    public static boolean paymentSuccessful;
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
        if(fineAmount!=0){
            payFine.setVisibility(View.VISIBLE);
            payFine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        transact(fineAmount);
                    }
                    catch(Exception e) {}
                }
            });
        }
        else{
            payFine.setVisibility(View.INVISIBLE);
        }

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
