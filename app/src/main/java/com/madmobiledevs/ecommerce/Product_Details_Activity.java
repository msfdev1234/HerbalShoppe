package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmobiledevs.ecommerce.Model.Products;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;
import com.rey.material.app.Dialog;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class Product_Details_Activity extends AppCompatActivity {

    private Button addToCartButton,buyNowBtn;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice,productDescription, productName;
    private String productId= "", ptype, pPrice, quantity;

    private EditText raw_Quantity_EditText;

    private Boolean isBuyNow;

    private LinearLayout linearLayout_for_quantity;

    private ProgressDialog loadingBar;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details_);

        Paper.init(this);

        loadingBar=new ProgressDialog(this);
        productId=getIntent().getStringExtra("pid");

        productImage= (ImageView) findViewById(R.id.product_Image_details);
        productDescription= (TextView) findViewById(R.id.product_Description_Details);
        productName= (TextView) findViewById(R.id.product_Name_details);
        productPrice= (TextView) findViewById(R.id.product_Price_Details);
        numberButton= (ElegantNumberButton) findViewById(R.id.number_Button);
        addToCartButton=(Button) findViewById(R.id.pd_Add_To_Cart_button);
        spinner= (Spinner) findViewById(R.id.spinner);
        raw_Quantity_EditText=(EditText) findViewById(R.id.quantity_Entered);
        linearLayout_for_quantity=(LinearLayout)findViewById(R.id.take_Quantity_Linear);

        buyNowBtn= (Button) findViewById(R.id.Buy_Now_Btn);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Product_Details_Activity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.spinner_Names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);



        getProductDetails(productId);

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBuyNow=true;

                if (ptype.equals("raw") && raw_Quantity_EditText.getText().toString().equals("")){

                    Toast.makeText(Product_Details_Activity.this, "Please enter quantity in grams", Toast.LENGTH_SHORT).show();
                }else {
                    if (ptype.equals("raw")) {
                        quantity = raw_Quantity_EditText.getText().toString();
                    } else if (ptype.equals("pack")) {
                        quantity = numberButton.getNumber();
                    }

                    loadingBar.setTitle("Please wait..");
                    loadingBar.setMessage("Processing Your request");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    addingToCartList();
                }

            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBuyNow=false;

                if (ptype.equals("raw") && raw_Quantity_EditText.getText().toString().equals("")){
                    Toast.makeText(Product_Details_Activity.this, "Please enter quantity in grams", Toast.LENGTH_SHORT).show();
                }

                else {
                    if (ptype.equals("raw")) {
                        quantity = raw_Quantity_EditText.getText().toString();
                    } else if (ptype.equals("pack")) {
                        quantity = numberButton.getNumber();
                    }

                    loadingBar.setTitle("Please wait..");
                    loadingBar.setMessage("Processing Your request");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    addingToCartList();
                }
            }
        });

    }

    private void getProductDetails(String productId) {

        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Products products= dataSnapshot.getValue(Products.class);

                    productName.setText(products.getPname());

                    ptype= products.getPtype();
                    pPrice= products.getPrice();

                        if (products.getPtype().equals("raw")){
                            productPrice.setText("INR "+products.getPrice()+" per gram");
                            numberButton.setRange(1,1);
                        }
                        else if (products.getPtype().equals("pack")){
                            productPrice.setText("INR "+products.getPrice());
                            linearLayout_for_quantity.setVisibility(View.INVISIBLE);
                            RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) numberButton.getLayoutParams();
                            params.addRule(RelativeLayout.BELOW, productPrice.getId());
                            numberButton.setLayoutParams(params);

                        }

                    //      productDescription.setText(R.string.Strng_productDescription+"\n\n"+products.getDescription());
                    productDescription.append("\n\n"+products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addingToCartList() {

        String saveCurrentDate, saveCurrentsTime;


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate= currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentsTime= currentDate.format(calForDate.getTime());

        final DatabaseReference cartListRef =FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap= new HashMap<>();

        cartMap.put("pid",productId);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",pPrice);
        cartMap.put("ptype",ptype);
        cartMap.put("date",saveCurrentDate);
        cartMap.put("orderPlaced","false");
        cartMap.put("time",saveCurrentsTime);
        cartMap.put("quantity",quantity);
        cartMap.put("discount","");

        final String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey).toString();

        if (UserPhoneKey == "") {
            cartListRef.child("User View").child(Prevalent.currentOnlineUser
                    .getPhoneNumber()).child(productId)
                    .updateChildren(cartMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                cartListRef.child("Admin View").child(Prevalent.currentOnlineUser
                                        .getPhoneNumber()).child(productId)
                                        .updateChildren(cartMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Product_Details_Activity.this, "Added to CartList", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(Product_Details_Activity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
        else {
            cartListRef.child("User View").child(UserPhoneKey)
                    .child(productId)
                    .updateChildren(cartMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                cartListRef.child("Admin View").child(UserPhoneKey)
                                        .child(productId)
                                        .updateChildren(cartMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Product_Details_Activity.this, "Added to CartList", Toast.LENGTH_SHORT).show();
                                                    if (isBuyNow==false) {
                                                        Intent intent = new Intent(Product_Details_Activity.this, HomeActivity.class);
                                                        if (UserPhoneKey != "") {
                                                            intent.putExtra("ischkRememberme", 1);

                                                        } else {
                                                            intent.putExtra("ischkRememberme", 0);
                                                        }
                                                        loadingBar.dismiss();
                                                        startActivity(intent);

                                                    }
                                                    else if (isBuyNow==true){
                                                        Intent intent = new Intent(Product_Details_Activity.this,CartActivity.class);
                                                        loadingBar.dismiss();
                                                        startActivity(intent);

                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }


    }


}
