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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
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
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File file = new File(directory, "UniqueFileNameee" + ".jpg");
            if (!file.exists()) {
                Log.d("path", file.toString());
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    connectServer(file.toString());
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
            logoImageView.setVisibility(View.INVISIBLE);
            imageImageView.setVisibility(View.VISIBLE);
            cameraImageView.setVisibility(View.INVISIBLE);
            galleryImageView.setVisibility(View.INVISIBLE);
            messageTextView.setVisibility(View.INVISIBLE);

        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            logoImageView.setVisibility(View.INVISIBLE);
            imageImageView.setVisibility(View.VISIBLE);
            cameraImageView.setVisibility(View.INVISIBLE);
            galleryImageView.setVisibility(View.INVISIBLE);
            messageTextView.setVisibility(View.INVISIBLE);
        }
    }

    void connectServer(String file){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(file, options);
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
        Log.i("connects", "");
//        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "Title", null);
//        Uri uri= Uri.parse(path);
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        File file = new File(directory, "UniqueFileName" + ".jpg");
//        if (!file.exists()) {
//            Log.d("path", file.toString());
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                fos.flush();
//                fos.close();
//                readFile(file.toString());
//            } catch (java.io.IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

//    void readFile(String path){
//        File file = new File(path);
//        int size = (int) file.length();
//        byte[] bytes = new byte[size];
//        try {
//            InputStream buf = new ByteArrayInputStream(file.getBytes());
//            buf.read(bytes, 0, bytes.length);
//            buf.close();
//            byte[] byteArray = buf.readAllBytes();
//            String postUrl1= "http://20.219.149.149:5000/get_book_data_api";
//            String postUrl2= "http://20.219.149.149:5000/identify";
//
//            RequestBody postBodyImage = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
//                    .build();
//
//            postRequest(postUrl1, postBodyImage);
//            postRequest(postUrl2, postBodyImage);
//            buf.close();
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

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
