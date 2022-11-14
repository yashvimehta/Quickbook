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
import com.example.quickbook.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
                    functions(isbn, title, name);
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

    public void addBook(String isbn, String name){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> mMap = new HashMap<>();
        mMap.put("Name", name);
        mMap.put("ISBN", isbn);
        mMap.put("Copies", "0");
        db.collection("Books").add(mMap);
    }

    public void functions ( String book_isbn, String  book_name, String member_id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int val = 0;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String isbn = String.valueOf(document.getData().get("ISBN"));
                                if(book_isbn.equals(isbn)){  //if ISBN exists
                                    val++;
                                }
                            }
                            if(val==0){
                                addBook(book_isbn, book_name);  //add new book in DB if ISBN doesn't exist
                            }
                            else{  //update no of copies
                                db.collection("Books")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                int val = 0;
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if(String.valueOf(document.getData().get("ISBN")).equals(book_isbn)) {
                                                            String copies = String.valueOf(document.getData().get("Copies"));
                                                            document.getData().replace("Copies", Integer.valueOf(copies)-1);
                                                        }
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        //create transaction
        Map<String, Object> mMap = new HashMap<>();
        mMap.put("bookISBN", book_isbn);
        mMap.put("bookName", book_name);
        mMap.put("issuerID", member_id);
        mMap.put("issuerDate", new Timestamp(new Date()));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 14);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE,59);
        c.set(Calendar.SECOND,59);
        mMap.put("returnDate", c.getTime());
        db.collection("Transactions").add(mMap);

        //add in user
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String memberID = String.valueOf(document.getData().get("memberID"));
                                if(memberID.equals(member_id)){  //if ISBN exists
                                    ArrayList<String> arrayList = (ArrayList<String>) document.getData().get("issuedBooks");
                                    arrayList.add(book_name);
//                                    Log.i("booksIssued", String.valueOf(arrayList));
                                    db.collection("Users").document(document.getId()).update("issuedBooks", arrayList);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
