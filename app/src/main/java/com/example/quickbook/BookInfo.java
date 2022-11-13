package com.example.quickbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import static com.example.quickbook.MainActivity.isAdmin;

public class BookInfo extends AppCompatActivity {
    TextView titleTextView;
    ImageView thumbnailImageView;
    TextView descriptionTextView;
    TextView authorTextView;
    TextView publishTextView;
    TextView isbnTextView;
    EditText noOfCopiesEditText;
    Button saveCopiesButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        String [] bookData=null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             bookData= extras.getStringArray("bookData");
        }
        titleTextView=findViewById(R.id.titleTextView);
        titleTextView.setText(bookData[0]);

        thumbnailImageView=findViewById(R.id.thumbnailImageView);
        Glide.with(getApplicationContext())
                .load(bookData[5])
                .placeholder(R.drawable.image_progress)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(thumbnailImageView);

        descriptionTextView=findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(Html.fromHtml("<b>" + "Description:" + "<br>"+"</b> " + bookData[4]));
        descriptionTextView.setMovementMethod(new ScrollingMovementMethod());

        authorTextView=findViewById(R.id.authorTextView);
        authorTextView.setText(Html.fromHtml("<b>" + "Author(s):"+"</b> " + bookData[1]));

        isbnTextView=findViewById(R.id.isbnTextView);
        isbnTextView.setText(Html.fromHtml("<b>" + "ISBN:"+"</b> " + bookData[6]));

        publishTextView=findViewById(R.id.publishTextView);
        String publishText="";
        if (!bookData[2].equals("na")){
            publishText+=" <b>by</b> "+bookData[2];
        }
        if(!bookData[3].equals("na")){
            publishText+=" <b>dated</b> "+bookData[3];
        }
        if (!publishText.equals("na")) {
            publishTextView.setText(Html.fromHtml("<b>Published</b>" + publishText));
        }
        else{
            publishTextView.setVisibility(View.INVISIBLE);
        }

        noOfCopiesEditText=findViewById(R.id.noOfCopiesInputText);
        //TODO Get data from Firebase and set
        noOfCopiesEditText.setText("10");
        saveCopiesButton = findViewById(R.id.saveCopiesButton);
        
        if(isAdmin) {
            Log.i("Success","is admin");
            noOfCopiesEditText.setInputType(InputType.TYPE_CLASS_NUMBER);


            saveCopiesButton.setVisibility(View.VISIBLE);
            saveCopiesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String noOfCopies = noOfCopiesEditText.getText().toString();
                    Toast.makeText(BookInfo.this, "No. of copies updated!", Toast.LENGTH_SHORT).show();
                    //TODO Set value in Firebase
                }
            });
        }
        else{
            noOfCopiesEditText.setInputType(InputType.TYPE_NULL);
            saveCopiesButton.setVisibility(View.INVISIBLE);
            Log.i("success","is not admin");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}