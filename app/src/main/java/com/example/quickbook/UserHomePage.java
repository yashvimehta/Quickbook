package com.example.quickbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.PaymentResultListener;

import static com.example.quickbook.FragmentSetUser.UserCustomCardAdapter.rzpID;


public class UserHomePage extends AppCompatActivity implements PaymentResultListener {

    public static Context contextOfApplication;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        ViewPager viewPager = (ViewPager)findViewById(R.id.pager);

        UserFragmentAdapter adapter = new UserFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

        contextOfApplication = getApplicationContext();



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
        //TODO Uncomment below
        //db.collection("Transactions").document(documentID).update("feeValue",0);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
    }
}
