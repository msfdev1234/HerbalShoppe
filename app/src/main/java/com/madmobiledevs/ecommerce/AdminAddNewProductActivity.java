package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, Description, Price , Pname, saveCurrentDate, saveCurrentTime, ShortDescription;
    private Button AddNewProductButton;
    private EditText InputProductName,InputProductDescription,InputProductPrice, InputProductShortDescription;
    private ImageView InputProductImage;
    private static final int GalleryPick=1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

         loadingBar=new ProgressDialog(this);

        categoryName= getIntent().getExtras().get("category").toString();
        Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();
      //  ProductImagesRef= FirebaseStorage.getInstance().getReference().child("Product Images");
      //  ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");

        AddNewProductButton=(Button)findViewById(R.id.add_New_Product);
        InputProductName=(EditText)findViewById(R.id.product_Name);
        InputProductDescription=(EditText)findViewById(R.id.product_Description);
        InputProductShortDescription=(EditText)findViewById(R.id.product_Short_Description);
        InputProductPrice=(EditText)findViewById(R.id.product_Price);
        InputProductImage=(ImageView)findViewById(R.id.select_Product_Image);
        spinner= (Spinner) findViewById(R.id.product_Type);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(AdminAddNewProductActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Admin_Spinner_Names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });


        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

    }
    private void OpenGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && data!=null){
            ImageUri=data.getData();
            InputProductImage.setImageURI(ImageUri);

        }
    }

    private void ValidateProductData(){
        Description=InputProductDescription.getText().toString();
        ShortDescription=InputProductShortDescription.getText().toString();
        Pname=InputProductName.getText().toString();
        Price=InputProductPrice.getText().toString();

        if (ImageUri==null){
            Toast.makeText(this, "Product Image is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Pname)){
            Toast.makeText(this, "Product Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Product Description is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Product Price is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(ShortDescription)){
            Toast.makeText(this, "Product Short Description is mandatory", Toast.LENGTH_SHORT).show();
        }
        else {
             StoreProductInformation();
        }
        
    }

    private void StoreProductInformation() {


        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Dear Admin, Please wait while we are Adding the new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        productRandomKey=saveCurrentDate + saveCurrentTime;

        final StorageReference filePath =  FirebaseStorage.getInstance().getReference().child("Product Images")
                .child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminAddNewProductActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product Image Uploaded successfully..", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask= uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl=task.getResult().toString();

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "got the product image Url successfully...", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void SaveProductInfoToDatabase() {
        HashMap<String,Object> productMap= new HashMap<>();

        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("shortDescription", ShortDescription);
        productMap.put("ptype", spinner.getSelectedItem().toString());
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category",categoryName);
        productMap.put("price", Price);
        productMap.put("pname", Pname);

        FirebaseDatabase.getInstance().getReference().child("Products")
                .child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                            startActivity(intent);
                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}
