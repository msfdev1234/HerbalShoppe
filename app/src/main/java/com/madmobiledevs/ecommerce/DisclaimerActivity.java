package com.madmobiledevs.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.rey.material.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class DisclaimerActivity extends AppCompatActivity {

    private CheckBox chk_Box_TnC;
    private Button proceed_TnC_Btn;
    private ImageView back_Arrow_TnC;

    private String totalAmount,userName,phone,address,city,date,time,state;

    private String deliveryCharges, cGst, Gst, grandTotal;

    private String isOrdering;

    private List<String> productIds;
    private List<String> productQuantitys;
    private List<String> productNames;
    private List<String> productPrices;
    private List<String> productPtypes;

    private String image_path, upiId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        isOrdering= getIntent().getStringExtra("isOrdering");

        chk_Box_TnC = (CheckBox) findViewById(R.id.chkBox_agreement);
        proceed_TnC_Btn = (Button) findViewById(R.id.proceed_Agr_Btn);
        back_Arrow_TnC = (ImageView) findViewById(R.id.back_Arrow);

        if (isOrdering.equals("1")){
            chk_Box_TnC.setVisibility(View.VISIBLE);
            proceed_TnC_Btn.setVisibility(View.VISIBLE);
        } else {
            chk_Box_TnC.setVisibility(View.INVISIBLE);
            proceed_TnC_Btn.setVisibility(View.INVISIBLE);
        }

        productIds= new ArrayList<String>();
        productQuantitys= new ArrayList<String>();
        productNames= new ArrayList<String>();
        productPrices= new ArrayList<String>();
        productPtypes= new ArrayList<String>();

        productIds = getIntent().getStringArrayListExtra("productIds");
        productQuantitys = getIntent().getStringArrayListExtra("productQuantitys");
        productNames = getIntent().getStringArrayListExtra("productNames");
        productPrices = getIntent().getStringArrayListExtra("productPrices");
        productPtypes = getIntent().getStringArrayListExtra("productPtypes");

        totalAmount=getIntent().getStringExtra("totalAmount");
        userName=getIntent().getStringExtra("name");
        phone=getIntent().getStringExtra("phone");
        address=getIntent().getStringExtra("address");
        city=getIntent().getStringExtra("city");
        image_path=getIntent().getStringExtra("imageUri");
        upiId=getIntent().getStringExtra("upiId");

        deliveryCharges= getIntent().getStringExtra("deliveryCharges");
        cGst= getIntent().getStringExtra("cGst");
        Gst= getIntent().getStringExtra("Gst");
        grandTotal= getIntent().getStringExtra("grandTotal");

        proceed_TnC_Btn.setAlpha(.4f);

        back_Arrow_TnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });


        chk_Box_TnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chk_Box_TnC.isChecked()){
                    proceed_TnC_Btn.setAlpha(1f);

                }
                else {
                    proceed_TnC_Btn.setAlpha(.4f);

                }
            }
        });

        proceed_TnC_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chk_Box_TnC.isChecked()){
                    proceed_TnC_Btn.setAlpha(1f);

                    Intent intent = new Intent(DisclaimerActivity.this, FinalCheckOutActivity.class);

                    intent.putExtra("totalAmount",totalAmount);

                    intent.putStringArrayListExtra("productIds",(ArrayList<String>) productIds);
                    intent.putStringArrayListExtra("productQuantitys",(ArrayList<String>) productQuantitys);
                    intent.putStringArrayListExtra("productNames",(ArrayList<String>) productNames);
                    intent.putStringArrayListExtra("productPrices",(ArrayList<String>) productPrices);
                    intent.putStringArrayListExtra("productPtypes",(ArrayList<String>) productPtypes);

                    intent.putExtra("name",userName);
                    intent.putExtra("phone",phone);
                    intent.putExtra("address",address);
                    intent.putExtra("city",city);
                    intent.putExtra("imageUri",image_path);

                    intent.putExtra("deliveryCharges",deliveryCharges);
                    intent.putExtra("cGst",cGst);
                    intent.putExtra("Gst",Gst);
                    intent.putExtra("grandTotal",grandTotal);

                    intent.putExtra("upiId",upiId);

                    startActivity(intent);

                }
                else {
                    proceed_TnC_Btn.setAlpha(.4f);
                    Toast.makeText(DisclaimerActivity.this, "Please agree to all the terms and conditions", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
