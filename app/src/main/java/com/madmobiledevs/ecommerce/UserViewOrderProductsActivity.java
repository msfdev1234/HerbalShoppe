package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madmobiledevs.ecommerce.Model.Cart;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;
import com.madmobiledevs.ecommerce.ViewHolder.CartViewHolder;

import io.paperdb.Paper;

public class UserViewOrderProductsActivity extends AppCompatActivity {

    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference productsRef;
    private String orderId="";
    private String UserPhoneKey="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_order_products);

        productsList= findViewById(R.id.UserView_New_products_List);
        productsList.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);

        orderId= getIntent().getStringExtra("orderId");
        UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey).toString();

        productsRef= FirebaseDatabase.getInstance().getReference().child("orderItems")
                .child(UserPhoneKey).child(orderId);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(productsRef, Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart , CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull Cart cart) {
                if (cart.getPtype().equals("raw")){
                    cartViewHolder.txtProductQuantity.setText("Quantity = "+cart.getQuantity()+" gram");
                }else if (cart.getPtype().equals("pack")){
                    cartViewHolder.txtProductQuantity.setText("Quantity = "+cart.getQuantity());
                }
                cartViewHolder.txtProductPrice.setText("Price ₹"+cart.getPrice());
                cartViewHolder.txtProductName.setText(cart.getPname());
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}
