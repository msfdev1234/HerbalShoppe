package com.madmobiledevs.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView ayurvedic_category;

    private Button LogoutBtn,CheckOrdersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
         ayurvedic_category=(ImageView)findViewById(R.id.ayurvedic_category_ImageView);

         LogoutBtn=(Button) findViewById(R.id.admin_Logout_Btn);
         CheckOrdersBtn= (Button) findViewById(R.id.admin_check_Orders_Btn);

         LogoutBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent= new Intent(AdminCategoryActivity.this,MainActivity.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent);
                 finish();

             }
         });

         CheckOrdersBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(AdminCategoryActivity.this,AdminNewOrdersActivity.class);
                 startActivity(intent);
             }
         });

        ayurvedic_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","ayurvedic_category");
                startActivity(intent);
            }
        });
    }
}
