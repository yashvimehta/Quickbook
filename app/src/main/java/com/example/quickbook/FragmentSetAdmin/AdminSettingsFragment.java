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
import android.widget.Toast;

import com.example.quickbook.MainActivity;
import com.example.quickbook.R;
import com.example.quickbook.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class AdminSettingsFragment extends Fragment {
    EditText issueDurationInputText;
    EditText consecutiveIssualsInputText;
    EditText fineInputText;
    Button confirmRulesButton;
    Button logoutButton;
    EditText pwdInputText;
    EditText confirmPwdInputText;
    Button editProfileButton;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //TODO Timestamp - below 2 lines update the "timeee" field in Rules document of firebase. While testing, go to settings in admin, and it'll be updated there.
        long unixTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + 1209600;
        db.collection("Rules").document("ruless").update("timeee", unixTime);


        firebaseAuth = FirebaseAuth.getInstance();
        final View view = inflater.inflate(R.layout.fragment_admin_settings, container, false);
        issueDurationInputText=view.findViewById(R.id.issueDurationInputText);
        consecutiveIssualsInputText=view.findViewById(R.id.consecutiveIssualsInputText);
        fineInputText=view.findViewById(R.id.fineInputText);
        confirmRulesButton=view.findViewById(R.id.confirmRulesButton);


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

        pwdInputText=view.findViewById(R.id.pwdInputText);
        confirmPwdInputText=view.findViewById(R.id.confirmPwdInputText);
        editProfileButton=view.findViewById(R.id.editProfileButton);
        logoutButton=view.findViewById(R.id.logoutButton);
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
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd=pwdInputText.getText().toString();
                String confirmPwd=confirmPwdInputText.getText().toString();
                boolean correct=true;
                String message="";
                if(pwd.equals("") ||confirmPwd.equals("")){
                    message=message.concat("Fields cannot be empty. ");
                    correct=false;
                }
                if (!pwd.equals(confirmPwd)){
                    message=message.concat("Passwords are not matching");
                    correct=false;
                }
                if(correct){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    db.collection("Admin").document(user.getUid()).update("password", pwd);
                    user.updatePassword(pwd)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("YAYAY", "Password address updated.");
                                    }
                                }
                            });

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