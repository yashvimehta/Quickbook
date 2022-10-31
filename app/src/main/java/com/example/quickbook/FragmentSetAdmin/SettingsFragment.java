package com.example.quickbook.FragmentSetAdmin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickbook.R;
import com.google.android.material.textfield.TextInputEditText;


public class SettingsFragment extends Fragment {
    EditText issueDurationInputText;
    EditText consecutiveIssualsInputText;
    EditText fineInputText;
    Button confirmRulesButton;
    EditText emailInputText;
    EditText pwdInputText;
    EditText confirmPwdInputText;
    Button editProfileButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        issueDurationInputText=view.findViewById(R.id.issueDurationInputText);
        consecutiveIssualsInputText=view.findViewById(R.id.consecutiveIssualsInputText);
        fineInputText=view.findViewById(R.id.fineInputText);
        confirmRulesButton=view.findViewById(R.id.confirmRulesButton);
        //TODO set above input text values from db

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
                    //TODO Store values in DB
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