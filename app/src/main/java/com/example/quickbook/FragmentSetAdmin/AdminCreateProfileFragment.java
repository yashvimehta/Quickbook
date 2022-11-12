package com.example.quickbook.FragmentSetAdmin;
import static android.content.ContentValues.TAG;

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
import com.example.quickbook.ApiHelper.RegisterResult;
import com.example.quickbook.AdminHomePage;
import com.example.quickbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
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



public class AdminCreateProfileFragment extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    private static final int STORAGE_REQUEST = 7;
    private static final int SELECT_FILE = 8;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    FirebaseAuth firebaseAuth;

    Bitmap bitmap;
    ImageView imageView;
    TextView messageTextView;
    Button registerButton;
    Uri currentImageUri;

    TextInputLayout nameTextInputLayout;
    EditText nameInputText;

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
        String memberid=nameInputText.getText().toString();
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, ts+"_"+memberid, null);
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
        registerButton.setVisibility(View.INVISIBLE);
        try {
            Bitmap photo = MediaStore.Images.Media.getBitmap(AdminHomePage.contextOfApplication.getContentResolver(), currentImageUri);
//            imageView.setImageBitmap(photo);

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
            Call<RegisterResult> mCall = apiInterface.sendImageandName(body);
            mCall.enqueue(new Callback<RegisterResult>() {
                @Override
                public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
                    RegisterResult mResult = response.body();
                    if (mResult.getSuccess()) {
                        Log.i("Success Checking", "success");
//                        createUser();
                        nameInputText.setText("na");
                    } else {
                        Toast.makeText(getContext(), "There was some error. Please retry", Toast.LENGTH_SHORT).show();

                        Log.i("Success Checking",  ""+mResult.getError());

                    }
                    imageView.setImageResource(R.drawable.logo);
                    registerButton.setVisibility(View.INVISIBLE);
                    messageTextView.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.INVISIBLE);



                    if (file.exists()) {
                        file.delete();
                    }
                }

                @Override
                public void onFailure(Call<RegisterResult> call, Throwable t) {
                    Log.i("Failure Checking", "There was an error " + t.getMessage());
                    messageTextView.setVisibility(View.VISIBLE);
                    registerButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);


                    if (file.exists()) {
                        file.delete();
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();

            messageTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

            registerButton.setVisibility(View.VISIBLE);
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

            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(AdminHomePage.contextOfApplication.getContentResolver(), currentImageUri);
                imageView.setImageBitmap(photo);
                registerButton.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //getPredictionsFromServer();

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_admin_create_profile, container, false);

        messageTextView = view.findViewById(R.id.messageTextView);

        registerButton = view.findViewById(R.id.RegisterButton);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        nameTextInputLayout=view.findViewById(R.id.nameTextInputLayout);
        nameInputText=view.findViewById(R.id.issueDurationInputText);


        imageView = view.findViewById(R.id.imageViewSelectImage);

        ImageView camera = view.findViewById(R.id.imageViewCamera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButton.setVisibility(View.INVISIBLE);

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

                registerButton.setVisibility(View.INVISIBLE);

                if (AdminHomePage.contextOfApplication.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST);

                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                }

            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memberId=nameInputText.getText().toString();
                String email1 = "yashvi.mehta@spit.ac.in";
                if (memberId.equals("na")){
                    Toast.makeText(getContext(), "Member ID cannot be empty", Toast.LENGTH_SHORT).show();
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("Users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                int val = 0;
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String id = String.valueOf(document.getData().get("memberID"));
                                        String email = String.valueOf(document.getData().get("email"));
                                        if(id.equals(memberId) || email.equals(email1)){
                                            val++;
                                        }
                                    }
                                    if(val==0){
                                        getPredictionsFromServer();
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Member ID/Email already exists", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        return view;
    }
    public void createUser(){
        String memberId = nameInputText.getText().toString();
        String email = "new.email@spit.ac.in";
        firebaseAuth.createUserWithEmailAndPassword(email, memberId)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> mMap = new HashMap<>();
                            mMap.put("email", email);
                            mMap.put("memberId", memberId);
                            mMap.put("password", memberId);
                            mMap.put("issuedBooks", new ArrayList<String>());
                            mMap.put("Name", "name" );
                            assert user != null;
                            db.collection("Users").document(user.getUid()).set(mMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Member registered successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Log.i("FAIL", "Sign Up failed " + task.getException());
                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        //TODO remove user from azure
                                    }
                                }
                            });

                        } else {
                            Log.i("FAIL", "Sign Up failed " + task.getException());
                        }
                    }
                });
    }

}
