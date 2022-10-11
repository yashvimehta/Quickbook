package com.example.quickbook;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quickbook.ApiHelper.ApiInterface;
import com.example.quickbook.ApiHelper.BFResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;

public class HomePage extends AppCompatActivity {

    ImageView imageImageView, logoImageView, cameraImageView , galleryImageView;
    TextView messageTextView;
    public static final int CAMERA_REQUEST=100;
    public static final int RESULT_LOAD_IMAGE = 150;
    public static final int CROP_PIC=200;
    private Uri picUri;
    ActivityResultLauncher<String> mgetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        imageImageView = findViewById(R.id.imageImageView);
        galleryImageView = findViewById(R.id.galleryImageView);
        imageImageView.setVisibility(View.INVISIBLE);
        logoImageView = findViewById(R.id.logoImageView);
        cameraImageView = findViewById(R.id.cameraImageView);
        messageTextView = findViewById(R.id.messageTextView);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    public void openGallery(View view){
        Log.i("Started","start gallery");
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void openCamera ( View view){
        Log.i("Started","start camera");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            Log.i("Yash", bitmap.toString());
            imageImageView.setImageBitmap(bitmap);
            connectServer(bitmap);
            logoImageView.setVisibility(View.INVISIBLE);
            imageImageView.setVisibility(View.VISIBLE);
            cameraImageView.setVisibility(View.INVISIBLE);
            galleryImageView.setVisibility(View.INVISIBLE);
            messageTextView.setVisibility(View.INVISIBLE);

        }
        else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Log.i("Checking","Checking");
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Log.i("Checking","Success");
            } catch (IOException e) {
                Log.i("Checking","Fail");
                e.printStackTrace();
            }
            //Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            imageImageView.setImageBitmap(bitmap);
            connectServer(bitmap);
            Log.i("Yash", bitmap.toString());
            logoImageView.setVisibility(View.INVISIBLE);
            imageImageView.setVisibility(View.VISIBLE);
            cameraImageView.setVisibility(View.INVISIBLE);
            galleryImageView.setVisibility(View.INVISIBLE);
            messageTextView.setVisibility(View.INVISIBLE);
        }
    }

    public Uri saveBitmapImage(Context inContext, Bitmap inImage) {
        Log.i("SAVE", "saving image...");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Quickbook" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public String getFilePathFromUri(Uri uri) {
        String path = "";
        if (HomePage.this.getContentResolver() != null) {
            Cursor cursor = HomePage.this.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }

        return path;
    }

    void connectServer(Bitmap bitmap){
//        Uri tempUri = saveBitmapImage(HomePage.this, bitmap);
//        String filePath = getFilePathFromUri(tempUri);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//
//        final File file = new File(filePath);
////        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        String postUrl1= "http://20.219.149.149:5000/get_book_data_api";
//        RequestBody postBodyImage = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
//                    .build();
//
//        postRequest(postUrl1, postBodyImage);
//        Log.i("connects", "");

//        Uri tempUri = saveBitmapImage(HomePage.this, bitmap);
//        String filePath = getFilePathFromUri(tempUri);
//
//        final File file = new File(filePath);
//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(ApiInterface.BASE_URL_PREDICTOR)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
//        retrofit2.Call<RecogniseBookAndFace> mCall = apiInterface.sendImage(body);
        try {

            Uri tempUri = saveBitmapImage(HomePage.this, bitmap);
            String filePath = getFilePathFromUri(tempUri);

            final File file = new File(filePath);
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiInterface.BASE_URL_PREDICTOR)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            //Call<FoodPredictorResult> mCall = apiInterface.sendImage(body);
            //Call<IdentifyResult> mCall = apiInterface.sendImage(body);
            Call<BFResult> mCall = apiInterface.sendImage(body);
            mCall.enqueue(new Callback<BFResult>() {
                @Override
                public void onResponse(Call<BFResult> call, Response<BFResult> response) {
                    BFResult mResult = response.body();
                    if (mResult.getGeneralSuccess()) {
                        Log.i("Success Checking", mResult.getName() + " "+mResult.getIsbn());

                        String text = "Success";
                        messageTextView.setText(text);



                    } else {
                        String text = "Failure";
                        messageTextView.setText(text);

                        Log.i("Success Checking", mResult.getBookError()+" "+mResult.getFaceError()+" "+mResult.getGeneralError());

                    }

                    messageTextView.setVisibility(View.VISIBLE);



                    if (file.exists()) {
                        file.delete();
                    }
                }

                @Override
                public void onFailure(Call<BFResult> call, Throwable t) {
                    Log.i("Failure Checking", "There was an error " + t.getMessage());

                    String text = "There was some error";
                    messageTextView.setText(text);
                    messageTextView.setVisibility(View.VISIBLE);

                    if (file.exists()) {
                        file.delete();
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();

            String text = "There was some error";
            messageTextView.setText(text);
            messageTextView.setVisibility(View.VISIBLE);
        }
    }


//    void postRequest(String postUrl, RequestBody postBody) {
//
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(postUrl)
//                .post(postBody)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // Cancel the post on failure.
//                call.cancel();
//
//                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.i("Failed!", "could not send request");
//                        Log.i ("Failed", e.toString());
//
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.i("Success!", "Sent request");
//                        try{
//                            Log.i("Success Response", response.body().string() );
//                        }
//                        catch(Exception e){
//                            Log.i("Exception", e.toString());
//                        }
//                    }
//                });
//            }
//        });
//    }

}
