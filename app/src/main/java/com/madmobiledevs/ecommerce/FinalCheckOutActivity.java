package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.rey.material.app.Dialog;
import com.rey.material.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;


import io.paperdb.Paper;

public class FinalCheckOutActivity extends AppCompatActivity implements PaymentResultListener {

    private com.rey.material.widget.RadioButton upiBtn,codBtn;
    private RadioGroup radioGroup;
    private com.rey.material.widget.Button placeOrderBtn;

    private String totalAmount,userName,phone,address,city,date,time,state;

    private String deliveryCharges, cGst, Gst, grandTotal;

    private ProgressDialog loadingBar;

    private List<String> productIds;
    private List<String> productQuantitys;
    private List<String> productNames;
    private List<String> productPrices;
    private List<String> productPtypes;

    private String paymentStatus;

    final int UPI_PAYMENT = 0;

    private String image_path;
    private Uri imageUri;

    private String ID_Prf_RandomKey, downloadImageUrl;

    private String UserPhoneKey, saveCurrentDate, saveCurrentsTime, OrderId, upiId;

    private String transactionId;

    DatabaseReference ordersRef;
    DatabaseReference orderItemRef;
    DatabaseReference userOrdersView;

    private Boolean isProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_check_out);
        isProcessing =false;

        UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);

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

        loadingBar=new ProgressDialog(this);

        upiBtn=(RadioButton) findViewById(R.id.Upi_Option_Toggle);
        codBtn=(RadioButton) findViewById(R.id.COD_OptionToggle);
        radioGroup=(RadioGroup) findViewById(R.id.Rg);
        placeOrderBtn=(com.rey.material.widget.Button)findViewById(R.id.place_Order_btn);

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

        imageUri = Uri.parse(image_path);


        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (codBtn.isChecked()==false && upiBtn.isChecked()==false){
                    Toast.makeText(FinalCheckOutActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                }
                else {
                    check();
                }
            }
        });



        upiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codBtn.setCheckedImmediately(false);

            }

        });

        codBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upiBtn.setCheckedImmediately(false);
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (isProcessing == true){
            Toast.makeText(this, "Please Wait..", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }

    }


    private void check() {

        if (upiBtn.isChecked()){
            paymentStatus="Completed";
            processPaymentAndPlaceOrder();
        }
        else if (codBtn.isChecked()){
            Toast.makeText(this, "Cod", Toast.LENGTH_SHORT).show();
            paymentStatus="COD";
            loadingBar.setTitle("Please wait..");
            loadingBar.setMessage("Placing Your Order");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            codPlaceOrder();
        }


    }

    private void codPlaceOrder() {
        PlaceOrder();
    }

    private void processPaymentAndPlaceOrder() {

    /*    Intent intent = new Intent(FinalCheckOutActivity.this,PaymentActivity.class);
        intent.putExtra("totalAmount",totalAmount);
        intent.putExtra("name",name);
        startActivity(intent);*/





        String amount = grandTotal;
        String note = "Order Place";
        String name = userName;
        String upiId1 = upiId;
  //      payUsingUpi(amount, upiId1, name, note);

        startPayment(Double.parseDouble(amount),name);

    }

    private void startPayment(double amount,String userName) {

        Activity activity = this;

        Checkout checkout = new Checkout();

        try {


            JSONObject options = new JSONObject();

            options.put("name", userName);
            options.put("description", "Payment for products from HerbalShoppe");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", amount*100);//pass amount in currency subunits

            JSONObject preFill = new JSONObject();
            preFill.put("email", "YourMail@example.com");
            preFill.put("contact", UserPhoneKey);

            options.put("prefill", preFill);



            checkout.open(activity, options);
        } catch(Exception e) {
            Toast.makeText(activity, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPaymentSuccess(String s) {

        transactionId =s;

        isProcessing =true;

        PlaceOrder();

    }

    @Override
    public void onPaymentError(int i, String s) {

        isProcessing = false;

        Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();

    }

    private void payUsingUpi(String amount, String upiId, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(FinalCheckOutActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(FinalCheckOutActivity.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(FinalCheckOutActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();

                Log.d("UPI", "responseStr: "+approvalRefNo);
                String transactionId= approvalRefNo;

                PlaceOrder();

            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(FinalCheckOutActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(FinalCheckOutActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(FinalCheckOutActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }





    private void PlaceOrder() {
        isProcessing = false;

        loadingBar.setTitle("Please wait..");
        loadingBar.setMessage("Placing Your Order");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.setCancelable(false);
        loadingBar.show();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentsTime = currentTime.format(calForDate.getTime());

        ID_Prf_RandomKey = saveCurrentDate + saveCurrentsTime;


        Paper.init(this);

        UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);

        OrderId = UserPhoneKey + " " + saveCurrentDate + " " + saveCurrentsTime;

        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("User Documents")
                .child(imageUri.getLastPathSegment() + ID_Prf_RandomKey + ".jpg");

        if (UserPhoneKey != "") {
            ordersRef = FirebaseDatabase.getInstance().getReference()
                    .child("Orders")
                    .child(OrderId);
            orderItemRef = FirebaseDatabase.getInstance().getReference()
                    .child("orderItems")
                    .child(UserPhoneKey)
                    .child(OrderId);
            userOrdersView = FirebaseDatabase.getInstance().getReference()
                    .child("UserOrderView")
                    .child(UserPhoneKey)
                    .child(OrderId);
        } else {
            ordersRef = FirebaseDatabase.getInstance().getReference()
                    .child("Orders")
                    .child(Prevalent.currentOnlineUser.getPhoneNumber() + " " + saveCurrentDate + " " + saveCurrentsTime);
            orderItemRef = FirebaseDatabase.getInstance().getReference()
                    .child("orderItems")
                    .child(UserPhoneKey)
                    .child(OrderId);
            userOrdersView = FirebaseDatabase.getInstance().getReference()
                    .child("UserOrderView")
                    .child(UserPhoneKey);

        }

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FinalCheckOutActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            downloadImageUrl = task.getResult().toString();

                            DataBaseUpdate();

                        }
                    }
                });
            }
        });
    }

    private void DataBaseUpdate(){

        final HashMap<String, Object> ordersMap = new HashMap<>();

        ordersMap.put("totalAmount",totalAmount);

        ordersMap.put("deliveryCharges",deliveryCharges);
        ordersMap.put("cGst",cGst);
        ordersMap.put("Gst",Gst);
        ordersMap.put("grandTotal",grandTotal);

        ordersMap.put("name",userName);
        ordersMap.put("phone",phone);
        ordersMap.put("phoneKey",UserPhoneKey);
        ordersMap.put("paymentStatus",paymentStatus);
        ordersMap.put("address",address);
        ordersMap.put("city",city);
        ordersMap.put("orderId",OrderId);
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentsTime);
        ordersMap.put("state","not shipped");
        ordersMap.put("userID",downloadImageUrl);
        ordersMap.put("transactionId",transactionId);

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

                                                    for(int i=0;i<productIds.size();i++){

                                                        String pid=productIds.get(i);
                                                        String pName=productNames.get(i);
                                                        String pQuantity=productQuantitys.get(i);
                                                        String pPrice=productPrices.get(i);
                                                        String pType=productPtypes.get(i);

                                                        HashMap<String, Object> hashMap = new HashMap<>();

                                                        hashMap.put("pname",pName);
                                                        hashMap.put("quantity",pQuantity);
                                                        hashMap.put("price",pPrice);
                                                        hashMap.put("pid",pid);
                                                        hashMap.put("ptype",pType);

                                                        orderItemRef.child(pid)
                                                                .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                userOrdersView.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {



                                                                        loadingBar.dismiss();
                                                                        show_order_placed_dialogue();
                                                                    //    Toast.makeText(FinalCheckOutActivity.this, "Order Placed Successfully we'll contact you shortly", Toast.LENGTH_LONG).show();

                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            });
                }
            }
        });


    }

    private void show_order_placed_dialogue(){

        final Dialog dialog;

        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.order_placed_dialogue_box);
        Button button = dialog.findViewById(R.id.Btn_Ok_Order_dialogue);

        dialog.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent =new Intent(FinalCheckOutActivity.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("ischkRememberme",1);

                startActivity(intent);
            }
        });
    }


}

