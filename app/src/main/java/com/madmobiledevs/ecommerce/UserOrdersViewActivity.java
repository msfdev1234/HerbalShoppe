package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madmobiledevs.ecommerce.Model.AdminOrders;
import com.madmobiledevs.ecommerce.Model.UserOrders;
import com.madmobiledevs.ecommerce.Prevalent.Prevalent;

import io.paperdb.Paper;

public class UserOrdersViewActivity extends AppCompatActivity {

    private RecyclerView User_ordersList;
    private DatabaseReference User_ordersRef;

    String UserPhoneKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders_view);

        Paper.init(this);
        UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey).toString();

        User_ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("UserOrderView")
                .child(UserPhoneKey);

        User_ordersList = (RecyclerView) findViewById(R.id.User_orders_List_rec);
        User_ordersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<UserOrders> options =
                new FirebaseRecyclerOptions.Builder<UserOrders>()
                        .setQuery(User_ordersRef, UserOrders.class)
                        .build();


        FirebaseRecyclerAdapter<UserOrders,UserOrderViewHolder> adapter = new FirebaseRecyclerAdapter<UserOrders, UserOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserOrderViewHolder userOrderViewHolder, int i, @NonNull final UserOrders userOrders) {

                userOrderViewHolder.userName.setText("Name: "+ userOrders.getName());
                userOrderViewHolder.userPhoneNumber.setText("Phone:  "+ userOrders.getPhone());
                userOrderViewHolder.userTotalPrice.setText("Total Amount: "+ userOrders.getGrandTotal()+"₹");
                userOrderViewHolder.userDateTime.setText("Ordered at: "+ userOrders.getDate()+"  "+userOrders.getTime());
                userOrderViewHolder.userShippingAddress.setText("Shipping Address: "+ userOrders.getAddress()+", "+userOrders.getCity());
                userOrderViewHolder.paymentStatus.setText("Payment Status : "+userOrders.getPaymentStatus());

                userOrderViewHolder.Order_products_User_Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserOrdersViewActivity.this, UserViewOrderProductsActivity.class);
                        intent.putExtra("orderId",userOrders.getOrderId());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public UserOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_orders_layout,parent,false);
                return new UserOrderViewHolder(view);

            }
        };

        User_ordersList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class UserOrderViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress, paymentStatus;

        public Button Order_products_User_Btn;


        public UserOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.User_order_User_Name);
            userPhoneNumber = itemView.findViewById(R.id.User_order_Phone_Number);
            userShippingAddress = itemView.findViewById(R.id.User_order_Address_City);
            userDateTime = itemView.findViewById(R.id.User_order_Date_Time);
            userTotalPrice = itemView.findViewById(R.id.User_order_Total_Price);
            Order_products_User_Btn = itemView.findViewById(R.id.User_Order_Details);
            //ShowOrdersBtn = itemView.findViewById(R.id.User_order_show_All_Products_Btn);
            paymentStatus = itemView.findViewById(R.id.User_payment_Status);
        }
    }

}
