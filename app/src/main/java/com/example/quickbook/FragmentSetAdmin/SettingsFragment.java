package com.example.quickbook.FragmentSetAdmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickbook.HomePage;
import com.example.quickbook.R;
import com.example.quickbook.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class SettingsFragment extends Fragment {
    EditText issueDurationInputText;
    EditText consecutiveIssualsInputText;
    EditText fineInputText;
    Button confirmRulesButton;
    EditText emailInputText;
    EditText pwdInputText;
    EditText confirmPwdInputText;
    Button editProfileButton;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        issueDurationInputText=view.findViewById(R.id.issueDurationInputText);
        consecutiveIssualsInputText=view.findViewById(R.id.consecutiveIssualsInputText);
        fineInputText=view.findViewById(R.id.fineInputText);
        confirmRulesButton=view.findViewById(R.id.confirmRulesButton);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference rulesDocumentRef = db.collection("Rules").document("ruless");
        rulesDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    consecutiveIssualsInputText.setText(String.valueOf(task.getResult().getData().get("consecutiveIssuals")));
                    issueDurationInputText.setText(String.valueOf(task.getResult().getData().get("issueDuration(days)")));
                    fineInputText.setText(String.valueOf(task.getResult().getData().get("fineAmount(perDay)")));
                }
            }
        });
        //fetch from db

        emailInputText=view.findViewById(R.id.emailInputText);
        pwdInputText=view.findViewById(R.id.pwdInputText);
        confirmPwdInputText=view.findViewById(R.id.confirmPwdInputText);
        editProfileButton=view.findViewById(R.id.editProfileButton);

        confirmRulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String issueDuration=issueDurationInputText.getText().toString();
                String consecutiveIssuals=consecutiveIssualsInputText.getText().toString();
                String fine=fineInputText.getText().toString();
                boolean correct=true;
                String message="";
                if(issueDuration.equals("")){
                    message=message.concat("Issue Duration ");
                    correct=false;
                }
                if (consecutiveIssuals.equals("")){
                    message=message.concat("Consecutive Issuals ");
                    correct=false;
                }
                if (fine.equals("")){
                    message=message.concat("Fine Amount ");
                    correct=false;
                }
                if(correct){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Rules").document("ruless").update("issueDuration(days)", issueDuration);
                    db.collection("Rules").document("ruless").update("consecutiveIssuals", consecutiveIssuals);
                    db.collection("Rules").document("ruless").update("fineAmount(perDay)", fine);
                    Toast.makeText(getActivity(), "Values stored successfully", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getActivity(), message+"cannot be empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailInputText.getText().toString();
                String pwd=pwdInputText.getText().toString();
                String confirmPwd=confirmPwdInputText.getText().toString();
                boolean correct=true;
                String message="";
                if(email.equals("") || pwd.equals("") ||confirmPwd.equals("")){
                    message=message.concat("Fields cannot be empty. ");
                    correct=false;
                }
                if (!pwd.equals(confirmPwd)){
                    message=message.concat("Passwords not matching");
                    correct=false;
                }
                if(correct){
                    //TODO Store values in DB
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Users").document(user.getUid()).update("email", email);
                    db.collection("Users").document(user.getUid()).update("password", pwd);
                    DocumentReference rulesDocumentRef = db.collection("Rules").document(user.getUid());
                    rulesDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.i("phone", String.valueOf(task.getResult().getData().get("Phone Number")));
                            }
                        }
                    });
//                    user.updateEmail(email)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.d("YAYAY", "User email address updated.");
//                                    }
//                                }
//                            });

                    Toast.makeText(getActivity(), "Changes stored successfully", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}