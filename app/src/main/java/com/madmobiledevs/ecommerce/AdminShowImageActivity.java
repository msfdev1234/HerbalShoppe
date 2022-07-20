package com.madmobiledevs.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class AdminShowImageActivity extends AppCompatActivity {

    ImageView ID_ImgVw;
    private String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_image);

        ID_ImgVw = findViewById(R.id.Id_ImageView);

        downloadUrl = getIntent().getStringExtra("imageLink");

        Picasso.get().load(downloadUrl).into(ID_ImgVw);


    }
}