package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madmobiledevs.ecommerce.Model.Cart;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;
import com.madmobiledevs.ecommerce.ViewHolder.CartViewHolder;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalAmount;

    private int overAllTotalPrice=0;

    private String ptype;

    private List<String> productIds;
    private List<String> productQuantitys;
    private List<String> productNames;
    private List<String> productPrices;
    private List<String> productPtypes;

    private ProgressDialog loadingBar;

    private int TotalAmount ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        productIds = new ArrayList<String>();
        productQuantitys = new ArrayList<String>();
        productNames = new ArrayList<String>();
        productPrices = new ArrayList<String>();
        productPtypes = new ArrayList<String>();

        recyclerView= (RecyclerView) findViewById(R.id.cart_List);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn=(Button) findViewById(R.id.next_Process_Button);
        txtTotalAmount=(TextView) findViewById(R.id.total_price);

        loadingBar=new ProgressDialog(this);

        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (overAllTotalPrice<=0){
                    Toast.makeText(CartActivity.this, "Your Cart is empty please add Products to proceed", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                    intent.putExtra("Total Price", String.valueOf(overAllTotalPrice));

                    intent.putStringArrayListExtra("productIds",(ArrayList<String>) productIds);
                    intent.putStringArrayListExtra("productQuantitys",(ArrayList<String>) productQuantitys);
                    intent.putStringArrayListExtra("productNames",(ArrayList<String>) productNames);
                    intent.putStringArrayListExtra("productPrices",(ArrayList<String>) productPrices);
                    intent.putStringArrayListExtra("productPtypes",(ArrayList<String>) productPtypes);

                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        Paper.init(this);

        final String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);

        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options;

        if (UserPhoneKey == "") {
            options = new FirebaseRecyclerOptions.Builder<Cart>()
                    .setQuery(cartListRef.child("User View")
                            .child(Prevalent.currentOnlineUser.getPhoneNumber()), Cart.class)
                    .build();
        }
        else {
            options = new FirebaseRecyclerOptions.Builder<Cart>()
                    .setQuery(cartListRef.child("User View")
                            .child(UserPhoneKey), Cart.class)
                    .build();
        }

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter= new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {

                ptype = cart.getPtype();

                if (ptype.equals("raw")){
                    cartViewHolder.txtProductQuantity.setText("Quantity = "+cart.getQuantity()+" gram");
                }else if (ptype.equals("pack")){
                    cartViewHolder.txtProductQuantity.setText("Quantity = "+cart.getQuantity());
                }
                cartViewHolder.txtProductPrice.setText("Price ₹"+cart.getPrice());
                cartViewHolder.txtProductName.setText(cart.getPname());


                productIds.add(cart.getPid());
                productQuantitys.add(cart.getQuantity());
                productNames.add(cart.getPname());
                productPrices.add(cart.getPrice());
                productPtypes.add(cart.getPtype());

                update_Cart_Total();

                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "Edit",
                                "Remove"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (UserPhoneKey == "") {
                                    if (which == 0) {
                                        Intent intent = new Intent(CartActivity.this, Product_Details_Activity.class);
                                        intent.putExtra("pid", cart.getPid());
                                        startActivity(intent);
                                    }
                                    if (which == 1) {

                                        cartListRef.child("User View")
                                                .child(Prevalent.currentOnlineUser.getPhoneNumber())
                                                .child(cart.getPid())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        cartListRef.child("Admin View")
                                                                .child(Prevalent.currentOnlineUser.getPhoneNumber())
                                                                .child(cart.getPid())
                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(CartActivity.this, "Product removed from cart", Toast.LENGTH_SHORT).show();
                                                                update_Cart_Total();
                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                }
                                else {

                                    if (which == 0) {
                                        Intent intent = new Intent(CartActivity.this, Product_Details_Activity.class);
                                        intent.putExtra("pid", cart.getPid());
                                        startActivity(intent);
                                    }
                                    if (which == 1) {
                                        loadingBar.setTitle("Loading..");
                                        loadingBar.setMessage("please wait");
                                        loadingBar.setCanceledOnTouchOutside(false);
                                        loadingBar.setCancelable(false);
                                        loadingBar.show();

                                        cartListRef.child("User View")
                                                .child(UserPhoneKey)
                                                .child(cart.getPid())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        cartListRef.child("Admin View")
                                                                .child(UserPhoneKey)
                                                                .child(cart.getPid())
                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(CartActivity.this, "Product removed from cart", Toast.LENGTH_SHORT).show();
                                                                loadingBar.dismiss();
                                                                update_Cart_Total();
                                                            }
                                                        });

                                                    }
                                                });
                                    }
                                }
                            }
                        });
                        builder.show();
                    }
                });


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void update_Cart_Total(){


        DatabaseReference cartTotal_Ref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Paper.book().read(Prevalent.UserPhoneKey).toString());
        TotalAmount = 0;
        cartTotal_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot ) {

                int TotalAmount_Loop = 0;

                for (DataSnapshot child : dataSnapshot.getChildren()){

                    int item_Amount = Integer.parseInt(child.child("price").getValue().toString()) * Integer.parseInt(child.child("quantity").getValue().toString());
                    TotalAmount_Loop += item_Amount;
                }

                setTotalAmount_Text(TotalAmount_Loop);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTotalAmount_Text(int totalAmount_Loop) {

        overAllTotalPrice = totalAmount_Loop;
        if (totalAmount_Loop == 0){

            txtTotalAmount.setText("₹"+Integer.toString(totalAmount_Loop));

        } else {
            txtTotalAmount.setText("₹"+Integer.toString(totalAmount_Loop));
        }

    }
}
