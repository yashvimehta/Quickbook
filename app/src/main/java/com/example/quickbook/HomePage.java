package com.example.quickbook;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomePage extends AppCompatActivity {

    ImageView imageImageView, logoImageView, cameraImageView;
    TextView messageTextView;
    public static final int CAMERA_REQUEST=100;
    public static final int CROP_PIC=200;
    private Uri picUri;
    ActivityResultLauncher<String> mgetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        imageImageView = findViewById(R.id.imageImageView);
        logoImageView = findViewById(R.id.logoImageView);
        cameraImageView = findViewById(R.id.cameraImageView);
        messageTextView = findViewById(R.id.messageTextView);

    }

    public void openCamera ( View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            imageImageView.setImageBitmap(bitmap);
            imageImageView.setVisibility(View.VISIBLE);
            logoImageView.setVisibility(View.INVISIBLE);
            cameraImageView.setVisibility(View.INVISIBLE);
            messageTextView.setVisibility(View.INVISIBLE);


            connectServer(bitmap);
        }
    }

    void connectServer(Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String postUrl1= "http://20.219.149.149:5000/get_book_data_api";
        String postUrl2= "http://20.219.149.149:5000/identify";

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();

        postRequest(postUrl1, postBodyImage);
        postRequest(postUrl2, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Failed!", "could not send request");
                        Log.i ("Failed", e.toString());

                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Success!", "Sent request");
                        try{
                            Log.i("Success Response", response.body().string() );
                        }
                        catch(Exception e){
                            Log.i("Exception", e.toString());
                        }
                    }
                });
            }
        });
    }

}
