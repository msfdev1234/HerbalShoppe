package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BillingDetailsActivity extends AppCompatActivity {

    private TextView  shippingAddress_Billing, itemsTotal_Billing, deliveryCharge_Billing, cGst_Billing, gst_Billing, orderTotal_Billing;
    private Button continue_Btn_Billing;

    private String totalAmount="", orderTotalAmount;

    private String userName,phone,address,city, value_CGST, value_GST, value_DelivaryCharges, value_Gst_No, value_CGst_No;

    String image_path;



    private List<String> productIds;
    private List<String> productQuantitys;
    private List<String> productNames;
    private List<String> productPrices;
    private List<String> productPtypes;

    private DatabaseReference DeliveryChrgRefHyd;
    private DatabaseReference upiIdRef;
    private String value_upi_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_details);

        shippingAddress_Billing= (TextView) findViewById(R.id.address_Billing);
        itemsTotal_Billing= (TextView) findViewById(R.id.itemsTotal_Billing);
        deliveryCharge_Billing= (TextView) findViewById(R.id.deliveryCharges_Billing);
        cGst_Billing= (TextView) findViewById(R.id.C_gst_Billing);
        gst_Billing= (TextView) findViewById(R.id.gst_Billing);
        orderTotal_Billing= (TextView) findViewById(R.id.OrderTotel_Billing);

        continue_Btn_Billing= (Button) findViewById(R.id.continueButton_Billing);

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

        totalAmount = getIntent().getStringExtra("totalAmount");
        userName=getIntent().getStringExtra("name");
        phone=getIntent().getStringExtra("phone");
        address=getIntent().getStringExtra("address");
        city=getIntent().getStringExtra("city");
        image_path=getIntent().getStringExtra("imageUri");

        value_DelivaryCharges=getIntent().getStringExtra("value_DelivaryCharges");
        value_Gst_No=getIntent().getStringExtra("value_Gst");
        value_CGst_No=getIntent().getStringExtra("value_CGst");


        displayValues();

        continue_Btn_Billing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity();
            }
        });

        upiIdRef = FirebaseDatabase.getInstance().getReference().child("values").child("upiId");

        upiIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                value_upi_Id = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void displayValues() {

        shippingAddress_Billing.append(address);
        itemsTotal_Billing.append(totalAmount);

        deliveryCharge_Billing.append(value_DelivaryCharges);

        value_CGST=percentageCalculate(Double.parseDouble(value_CGst_No),Double.parseDouble(totalAmount));
        value_CGST=Integer.toString((int) Math.round(Double.parseDouble(value_CGST)));
        cGst_Billing.append("("+value_CGst_No+"%)                   :     "+value_CGST);

        value_GST=percentageCalculate(Double.parseDouble(value_Gst_No),Double.parseDouble(totalAmount));
        value_GST=Integer.toString((int)Math.round(Double.parseDouble(value_GST)));
        gst_Billing.append("("+value_Gst_No+"%)                     :     "+value_GST);

        orderTotalAmount=Integer.toString(Integer.parseInt(value_GST) + Integer.parseInt(value_CGST));

        int value_Int_Total, value_Int_deliveryChrg;


        value_Int_deliveryChrg= Integer.valueOf(value_DelivaryCharges.trim());
        value_Int_Total= Integer.valueOf(totalAmount.trim());

        orderTotalAmount= Integer.toString(Integer.valueOf(orderTotalAmount.trim())+ (value_Int_deliveryChrg+ value_Int_Total));

        orderTotal_Billing.append("₹"+orderTotalAmount);

   //     orderTotalAmount= Integer.toString(Integer.valueOf(orderTotalAmount) + ( value_Int_Total));




   //     orderTotalAmount = Integer.toString((Integer.valueOf(totalAmount))+ (Integer.valueOf(value_DelivaryCharges)))+ Double.toString((Double.parseDouble(value_CGST))+ (Double.parseDouble(value_GST)));

   //     orderTotal_Billing.setText(orderTotalAmount);
    }

    private String percentageCalculate(double percentageWith, double number) {

        double result = percentageWith/100;
        result = result*number;
        return ( Double.toString(Math.round((result) * 10.0) / 10.0));

        

    }

    private static String round (double value) {
        int scale = 1;
        return  String.valueOf(Math.round(value * scale) / scale);
    }


    private void nextActivity() {

        Intent intent = new Intent(BillingDetailsActivity.this, DisclaimerActivity.class);

        intent.putExtra("totalAmount",totalAmount);

        intent.putExtra("isOrdering","1");

        intent.putStringArrayListExtra("productIds",(ArrayList<String>) productIds);
        intent.putStringArrayListExtra("productQuantitys",(ArrayList<String>) productQuantitys);
        intent.putStringArrayListExtra("productNames",(ArrayList<String>) productNames);
        intent.putStringArrayListExtra("productPrices",(ArrayList<String>) productPrices);
        intent.putStringArrayListExtra("productPtypes",(ArrayList<String>) productPtypes);

        intent.putExtra("name",userName);
        intent.putExtra("phone",phone);
        intent.putExtra("address",address);
        intent.putExtra("city",city);

        intent.putExtra("deliveryCharges",value_DelivaryCharges);
        intent.putExtra("cGst",value_CGST);
        intent.putExtra("Gst",value_GST);
        intent.putExtra("grandTotal",orderTotalAmount);
        intent.putExtra("imageUri",image_path); 

        intent.putExtra("upiId",value_upi_Id);

        startActivity(intent);
    }


}
