package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmobiledevs.ecommerce.Model.Users;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinNowButton,loginButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton = (Button) findViewById(R.id.main_join_btn);
        loginButton = (Button) findViewById(R.id.main_Login_btn);

        loadingBar = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Paper.init(this);

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey != "") {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                loadingBar.setTitle("Already Logged In");
                loadingBar.setMessage("Please wait....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                AllowAccess(UserPhoneKey, UserPasswordKey);
            }
        }
    }

    private void AllowAccess(final String phoneNumber, final String password) {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phoneNumber).exists()){
                    Users userData=dataSnapshot.child("Users").child(phoneNumber).getValue(Users.class);


                    assert userData != null;
                    if (userData.getPhoneNumber().equals(phoneNumber)){
                        if (userData.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "Login successfull", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                            intent.putExtra("ischkRememberme",1);
                            finish();
                            startActivity(intent);

                        }
                        else {
                            Toast.makeText(MainActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                }else {
                    Toast.makeText(MainActivity.this, "Account with this "+phoneNumber+" do not exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
