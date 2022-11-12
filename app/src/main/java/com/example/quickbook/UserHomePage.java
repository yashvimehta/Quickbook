package com.example.quickbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.os.Bundle;
import com.google.firebase.firestore.FirebaseFirestore;


public class UserHomePage extends AppCompatActivity {

    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        ViewPager viewPager = (ViewPager)findViewById(R.id.pager);

        AdminFragmentAdapter adapter = new AdminFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

        contextOfApplication = getApplicationContext();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
