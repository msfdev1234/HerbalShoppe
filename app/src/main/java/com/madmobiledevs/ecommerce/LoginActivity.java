package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmobiledevs.ecommerce.Model.Users;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText inputphoneNumber,inputpassword;
    private Button loginButton;
    private ProgressDialog loadingBar;
    public String parentDbname="Users";
    private CheckBox chkBoxRememberMe;
    private TextView AdminLink,NotAdminLink;
    private int isCheckRememberMe=0;
    private Boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isAdmin=false;

        inputphoneNumber=(EditText)findViewById(R.id.login_phoneNumber_input);
        inputpassword=(EditText)findViewById(R.id.login_password_input);
        loginButton=(Button)findViewById(R.id.login_btn);
        loadingBar=new ProgressDialog(this);
        chkBoxRememberMe=(CheckBox)findViewById(R.id.remember_Me_chkBox);

        AdminLink=(TextView)findViewById(R.id.admin_panel_link);
        NotAdminLink=(TextView)findViewById(R.id.not_Admin_panel_link);

        Paper.init(this);

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAdmin=true;
                parentDbname="Admins";
                loginButton.setText("Login Admin");
                NotAdminLink.setVisibility(View.VISIBLE);
                AdminLink.setVisibility(View.INVISIBLE);
            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAdmin=false;
                parentDbname="Users";
                loginButton.setText("Login");
                NotAdminLink.setVisibility(View.INVISIBLE);
                AdminLink.setVisibility(View.VISIBLE);
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });
    }

    private void LoginUser() {
        String phoneNumber=inputphoneNumber.getText().toString();
        String password=inputpassword.getText().toString();

        if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(LoginActivity.this, "please enter your phoneNumber", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "please enter your password", Toast.LENGTH_SHORT).show();
        }
        else {

            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phoneNumber,password);
        }
    }


    private void AllowAccessToAccount(final String phoneNumber, final String password) {
        if (isAdmin!=true){
                Paper.book().write(Prevalent.UserPhoneKey,phoneNumber);
                Paper.book().write(Prevalent.UserPasswordKey,password);
                isCheckRememberMe=1;

        }

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbname).child(phoneNumber).exists()){

                    Users userData=dataSnapshot.child(parentDbname).child(phoneNumber).getValue(Users.class);

                    if (userData.getPhoneNumber().equals(phoneNumber)){
                        if (userData.getPassword().equals(password)){

                            if (parentDbname.equals("Admins")){
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Login successfull Admin "+ userData.getName().toString(), Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(LoginActivity.this,AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDbname.equals("Users")){
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Login successfull", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                intent.putExtra("ischkRememberme",isCheckRememberMe);
                                Prevalent.currentOnlineUser = userData;
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(intent);
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                }else {
                    Toast.makeText(LoginActivity.this, "Account with this "+phoneNumber+" do not exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
