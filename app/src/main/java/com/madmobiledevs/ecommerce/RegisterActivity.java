package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountButton;
    private EditText inputName,inputPhoneNumber,inputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loadingBar=new ProgressDialog(this);

        createAccountButton=(Button)findViewById(R.id.register_btn);
        inputName=(EditText)findViewById(R.id.register_name_input);
        inputPhoneNumber=(EditText)findViewById(R.id.register_phoneNumber_input);
        inputPassword=(EditText)findViewById(R.id.register_password_input);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatAccount();
            }
        });
    }

    private void CreatAccount(){

        String name=inputName.getText().toString();
        String phoneNumber=inputPhoneNumber.getText().toString();
        String password=inputPassword.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(RegisterActivity.this, "please enter your name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(RegisterActivity.this, "please enter your phoneNumber", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this, "please enter your password", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            
            validateData(name,phoneNumber,password);


        }
    }

    private void validateData(final String name, final String phoneNumber, final String password) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phoneNumber).exists())){
                    HashMap<String, Object> userDataMap= new HashMap<>();
                    userDataMap.put("name",name);
                    userDataMap.put("phoneNumber",phoneNumber);
                    userDataMap.put("password",password);

                    RootRef.child("Users").child(phoneNumber).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Congratulations Account created successfully", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, "Check your network connection", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });

                }
                else {
                    Toast.makeText(RegisterActivity.this, "User with this number already exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
