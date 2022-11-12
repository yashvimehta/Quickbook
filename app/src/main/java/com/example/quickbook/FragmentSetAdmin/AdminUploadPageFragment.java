package com.example.quickbook.FragmentSetAdmin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickbook.ApiHelper.ApiInterface;
import com.example.quickbook.ApiHelper.BFResult;
import com.example.quickbook.AdminHomePage;
import com.example.quickbook.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
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

public class AdminUploadPageFragment extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    private static final int STORAGE_REQUEST = 7;
    private static final int SELECT_FILE = 8;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    String isbnNumber;

    Bitmap bitmap;
    ImageView imageView;
    TextView messageTextView;
    Button retryButton, gotoResultButton;
    Uri currentImageUri;

    TextInputLayout titleTextInputLayout;
    EditText titleInputText;
    TextInputLayout nameTextInputLayout;
    EditText nameInputText;
    TextInputLayout isbnTextInputLayout;
    EditText isbnInputText;

    @SuppressLint("StaticFieldLeak")
    static ProgressBar progressBar;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            } else {
                Toast.makeText(getActivity(), "Provide permission to access camera!", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == STORAGE_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_FILE);

            } else {
                Toast.makeText(getActivity(), "Provide permission to access your images!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public Uri saveBitmapImage(Context inContext, Bitmap inImage) {
        Log.i("SAVE", "saving image...");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, ts, null);
        return Uri.parse(path);
    }

    public String getFilePathFromUri(Uri uri) {
        String path = "";
        if (AdminHomePage.contextOfApplication.getContentResolver() != null) {
            Cursor cursor = AdminHomePage.contextOfApplication.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }

        return path;
    }

    public void getPredictionsFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        messageTextView.setVisibility(View.INVISIBLE);

        try {
            Bitmap photo = MediaStore.Images.Media.getBitmap(AdminHomePage.contextOfApplication.getContentResolver(), currentImageUri);
            imageView.setImageBitmap(photo);

            Uri tempUri = saveBitmapImage(getContext(), photo);
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
            Call<BFResult> mCall = apiInterface.sendImage(body);
            mCall.enqueue(new Callback<BFResult>() {
                @Override
                public void onResponse(Call<BFResult> call, Response<BFResult> response) {
                    BFResult mResult = response.body();
                    isbnNumber = mResult.getIsbn();
                    if (mResult.getGeneralSuccess()) {
                        Log.i("Success Checking", mResult.getName() + " "+mResult.getIsbn());

                        messageTextView.setVisibility(View.INVISIBLE);
                        nameInputText.setText(mResult.getName());
                        isbnInputText.setText(mResult.getIsbn());
                        titleInputText.setText(mResult.getTitle());
                        nameTextInputLayout.setVisibility(View.VISIBLE);
                        isbnTextInputLayout.setVisibility(View.VISIBLE);
                        titleTextInputLayout.setVisibility(View.VISIBLE);

                        gotoResultButton.setVisibility(View.VISIBLE);
                        retryButton.setVisibility(View.INVISIBLE);

                    } else {
                        String text = "Failure";
                        messageTextView.setText(text);
                        messageTextView.setVisibility(View.VISIBLE);
                        nameTextInputLayout.setVisibility(View.INVISIBLE);
                        nameInputText.setText("na");
                        isbnTextInputLayout.setVisibility(View.INVISIBLE);
                        isbnInputText.setText("na");
                        titleTextInputLayout.setVisibility(View.INVISIBLE);
                        titleInputText.setText("na");
                        Log.i("Success Checking", mResult.getBookError()+" "+mResult.getFaceError()+" "+mResult.getGeneralError());

                    }


                    progressBar.setVisibility(View.INVISIBLE);



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
                    nameTextInputLayout.setVisibility(View.INVISIBLE);
                    nameInputText.setText("na");
                    isbnTextInputLayout.setVisibility(View.INVISIBLE);
                    isbnInputText.setText("na");
                    titleTextInputLayout.setVisibility(View.INVISIBLE);
                    titleInputText.setText("na");
                    progressBar.setVisibility(View.INVISIBLE);

                    retryButton.setVisibility(View.VISIBLE);

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
            progressBar.setVisibility(View.INVISIBLE);

            retryButton.setVisibility(View.VISIBLE);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Image using camera
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Log.i("12345",data.getExtras().toString());
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

            Uri tempUri = saveBitmapImage(getContext(), photo);
            CropImage.activity(tempUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(getContext(), this);
        }

        // Image from gallery
        if (requestCode == SELECT_FILE && resultCode == Activity.RESULT_OK) {
            Uri imageLocation = data.getData();
//            currentImageUri=imageLocation;
//            getPredictionsFromServer();
            CropImage.activity(imageLocation)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(getContext(), this);

            Log.i("WILL", "will call cropper");
        }

        // Image cropper
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            currentImageUri = result.getUri();
            Log.i("IMG CROPPER", "In cropper");

            getPredictionsFromServer();

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        assert mUser != null;
        DocumentReference mDocumentReference = db.collection("Users").document(mUser.getUid());

        View view = inflater.inflate(R.layout.fragment_admin_upload_page, container, false);
        messageTextView = view.findViewById(R.id.messageTextView);

        retryButton = view.findViewById(R.id.buttonDetect);
        gotoResultButton = view.findViewById(R.id.gotoResultButton);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        titleTextInputLayout=view.findViewById(R.id.titleTextInputLayout);
        titleInputText=view.findViewById(R.id.titleInputText);
        isbnTextInputLayout=view.findViewById(R.id.isbnTextInputLayout);
        isbnInputText=view.findViewById(R.id.isbnInputText);
        nameTextInputLayout=view.findViewById(R.id.nameTextInputLayout);
        nameInputText=view.findViewById(R.id.issueDurationInputText);


        imageView = view.findViewById(R.id.imageViewSelectImage);

        ImageView camera = view.findViewById(R.id.imageViewCamera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryButton.setVisibility(View.INVISIBLE);

                gotoResultButton.setVisibility(View.INVISIBLE);

                if (AdminHomePage.contextOfApplication.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);

                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        ImageView gallery = view.findViewById(R.id.imageViewGallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryButton.setVisibility(View.INVISIBLE);

                gotoResultButton.setVisibility(View.INVISIBLE);

                if (AdminHomePage.contextOfApplication.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST);

                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                }

            }
        });
        gotoResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=nameInputText.getText().toString();
                String isbn=isbnInputText.getText().toString();
                String title=titleInputText.getText().toString();
                if (name.equals("na")){
                    Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(isbn.equals("na")){
                    Toast.makeText(getContext(), "ISBN cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(title.equals("na")){
                    Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    messageTextView.setText("Select or click an Image");
                    messageTextView.setVisibility(View.VISIBLE);
                    nameTextInputLayout.setVisibility(View.INVISIBLE);
                    nameInputText.setText("na");
                    isbnTextInputLayout.setVisibility(View.INVISIBLE);
                    isbnInputText.setText("na");
                    titleTextInputLayout.setVisibility(View.INVISIBLE);
                    titleInputText.setText("na");
                    gotoResultButton.setVisibility(View.INVISIBLE);
                    retryButton.setVisibility(View.INVISIBLE);
                    imageView.setImageResource(R.drawable.logo);

                    mDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error == null) {
                                if (value != null && value.exists()) {

                                    ArrayList<String> issuedBooks = (ArrayList<String>) Objects.requireNonNull(value.get("issuedBooks"));
                                    issuedBooks.add(isbnNumber);
                                } else {
                                    Log.i("RES", "Data is NULL");
                                }

                            } else {
                                Log.i("ERR", error.toString());
                            }
                        }
                    });
                    Toast.makeText(getContext(), "Book issued successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPredictionsFromServer();
                messageTextView.setText("Select or click an Image");
                messageTextView.setVisibility(View.VISIBLE);
                nameTextInputLayout.setVisibility(View.INVISIBLE);
                nameInputText.setText("na");
                isbnTextInputLayout.setVisibility(View.INVISIBLE);
                isbnInputText.setText("na");
                titleTextInputLayout.setVisibility(View.INVISIBLE);
                titleInputText.setText("na");
                retryButton.setVisibility(View.INVISIBLE);

            }
        });

        return view;
    }

}