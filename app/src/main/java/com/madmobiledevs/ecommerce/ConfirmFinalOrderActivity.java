package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;

    private String totalAmount = "";

    private List<String> productIds;
    private List<String> productQuantitys;
    private List<String> productNames;
    private List<String> productPrices;
    private List<String> productPtypes;

    private DatabaseReference DeliveryChrgRefHyd;

    private String value_DelivaryCharges, value_Gst, value_CGst;

    private ImageView IdProof_ImgView;
    private static final int GalleryPick = 1;
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        IdProof_ImgView = findViewById(R.id.select_Document_ImgView);

        productIds = new ArrayList<String>();
        productQuantitys = new ArrayList<String>();
        productNames = new ArrayList<String>();
        productPrices = new ArrayList<String>();
        productPtypes = new ArrayList<String>();

        productIds = getIntent().getStringArrayListExtra("productIds");
        productQuantitys = getIntent().getStringArrayListExtra("productQuantitys");
        productNames = getIntent().getStringArrayListExtra("productNames");
        productPrices = getIntent().getStringArrayListExtra("productPrices");
        productPtypes = getIntent().getStringArrayListExtra("productPtypes");

        totalAmount = getIntent().getStringExtra("Total Price");

        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.shipment_Name);
        phoneEditText = (EditText) findViewById(R.id.shipment_Phone_Number);
        addressEditText = (EditText) findViewById(R.id.shipment_Adrress);
        cityEditText = (EditText) findViewById(R.id.shipment_City);

        DeliveryChrgRefHyd = FirebaseDatabase.getInstance().getReference().child("values").child("deliveryHyd");


        IdProof_ImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        DeliveryChrgRefHyd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                value_DelivaryCharges = dataSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference gstValueRef = FirebaseDatabase.getInstance().getReference().child("values").child("gst");

        gstValueRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value_Gst = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference cGstValueRef = FirebaseDatabase.getInstance().getReference().child("values").child("cGst");

        cGstValueRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value_CGst = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && data != null) {
            ImageUri = data.getData();
            IdProof_ImgView.setImageURI(ImageUri);

        }
    }


    private void check() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            Toast.makeText(this, "Enter your Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
            Toast.makeText(this, "Enter your Phone Number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(this, "Enter your Complete Address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cityEditText.getText().toString())) {
            Toast.makeText(this, "Enter your Name", Toast.LENGTH_SHORT).show();
        } else if (ImageUri == null) {
            Toast.makeText(this, "Your ID Proof Image is mandatory", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(ConfirmFinalOrderActivity.this, BillingDetailsActivity.class);

            intent.putExtra("totalAmount", totalAmount);
            intent.putExtra("name", nameEditText.getText().toString());
            intent.putExtra("phone", phoneEditText.getText().toString());
            intent.putExtra("address", addressEditText.getText().toString());
            intent.putExtra("city", cityEditText.getText().toString());
            intent.putExtra("imageUri", ImageUri.toString());

            intent.putStringArrayListExtra("productIds", (ArrayList<String>) productIds);
            intent.putStringArrayListExtra("productQuantitys", (ArrayList<String>) productQuantitys);
            intent.putStringArrayListExtra("productNames", (ArrayList<String>) productNames);
            intent.putStringArrayListExtra("productPrices", (ArrayList<String>) productPrices);
            intent.putStringArrayListExtra("productPtypes", (ArrayList<String>) productPtypes);

            intent.putExtra("value_DelivaryCharges", value_DelivaryCharges);
            intent.putExtra("value_Gst", value_Gst);
            intent.putExtra("value_CGst", value_CGst);

            startActivity(intent);
        }

    }
}
/*
    private void ConfirmOrder() {

        final String saveCurrentDate, saveCurrentsTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate= currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentsTime= currentDate.format(calForDate.getTime());

        Paper.init(this);

        final String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);

        final DatabaseReference ordersRef;

        if (UserPhoneKey!=""){
            ordersRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Orders")
                                                .child(UserPhoneKey);
        }
        else {
            ordersRef = FirebaseDatabase.getInstance().getReference()
                    .child("Orders")
                    .child(Prevalent.currentOnlineUser.getPhoneNumber());
        }


        HashMap<String, Object> ordersMap = new HashMap<>();

        ordersMap.put("totalAmount",totalAmount);
        ordersMap.put("name",nameEditText.getText().toString());
        ordersMap.put("phone",phoneEditText.getText().toString());
        ordersMap.put("address",addressEditText.getText().toString());
        ordersMap.put("city",cityEditText.getText().toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentsTime);
        ordersMap.put("state","not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(UserPhoneKey)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Cart List")
                                            .child("Admin View")
                                            .child(UserPhoneKey)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(ConfirmFinalOrderActivity.this, "Payment mode", Toast.LENGTH_SHORT).show();
                                                    Intent intent= new Intent(ConfirmFinalOrderActivity.this,FinalCheckOutActivity.class);
                                                    //intent.putExtra("ischkRememberme",1);
                                                    startActivity(intent);
                                                }
                                            });
                                }
                            });
                }
            }
        });


    }
}*/
