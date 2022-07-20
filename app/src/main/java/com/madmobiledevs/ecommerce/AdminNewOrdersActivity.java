package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.madmobiledevs.ecommerce.Model.AdminOrders;
import com.madmobiledevs.ecommerce.Model.Cart;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    private DatabaseReference OrderItemsRef;
    private DatabaseReference UserOrderViewRef;

    private Cart orderItems;

    private String downloadUrl, pid, pname, price, ptype, quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        OrderItemsRef = FirebaseDatabase.getInstance().getReference().child("orderItems");
        UserOrderViewRef = FirebaseDatabase.getInstance().getReference().child("UserOrderView");

        ordersList = (RecyclerView) findViewById(R.id.Orders_List);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef,AdminOrders.class)
                .build();


        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder adminOrdersViewHolder, int i, @NonNull final AdminOrders adminOrders) {
                        adminOrdersViewHolder.userName.setText("Name: "+ adminOrders.getName());
                        adminOrdersViewHolder.userPhoneNumber.setText("Phone:  "+ adminOrders.getPhone());
                        adminOrdersViewHolder.userTotalPrice.setText("Total Amount: "+ adminOrders.getGrandTotal()+"₹");
                        adminOrdersViewHolder.userDateTime.setText("Ordered at: "+ adminOrders.getDate()+"  "+adminOrders.getTime());
                        adminOrdersViewHolder.userShippingAddress.setText("Shipping Address: "+ adminOrders.getAddress()+", "+adminOrders.getCity());
                        adminOrdersViewHolder.paymentStatus.setText("Payment Status : "+adminOrders.getPaymentStatus());
                        adminOrdersViewHolder.transaction_TxtVw.setText("Trans Id : "+adminOrders.getTransactionId());

                        adminOrdersViewHolder.print_Bill_Btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DatabaseReference orderProductsRef = FirebaseDatabase.getInstance().getReference().child("orderItems").child(adminOrders.getPhoneKey());

                                orderProductsRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        orderItems = dataSnapshot.getValue(Cart.class);

                                        pid = orderItems.getPid();
                                        pname = orderItems.getPname();
                                        price = orderItems.getPrice();
                                        quantity = orderItems.getQuantity();
                                        ptype = orderItems.getPtype();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                });

                                Intent intent = new Intent(AdminNewOrdersActivity.this,PrintOrderBillActivity.class);
                                intent.putExtra("totalAmount", adminOrders.getTotalAmount());
                                intent.putExtra("cGst", adminOrders.getcGst());
                                intent.putExtra("gst", adminOrders.getGst());
                                intent.putExtra("grandTotal", adminOrders.getGrandTotal());
                                intent.putExtra("name", adminOrders.getName());
                                intent.putExtra("phoneKey", adminOrders.getPhoneKey());
                                intent.putExtra("orderId", adminOrders.getOrderId());
                                intent.putExtra("deliveryCharge", adminOrders.getDeliveryCharges());

                                startActivity(intent);

                            }
                        });


                        adminOrdersViewHolder.showUser_Id.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(AdminNewOrdersActivity.this,AdminShowImageActivity.class);
                                intent.putExtra("imageLink",adminOrders.getUserID());
                                startActivity(intent);
                            }
                        });

                        adminOrdersViewHolder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(AdminNewOrdersActivity.this,AdminUserProductsActivity.class);
                                intent.putExtra("UserPhoneKey",adminOrders.getPhoneKey());
                                intent.putExtra("orderId",adminOrders.getOrderId());
                                startActivity(intent);
                            }
                        });

                        adminOrdersViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Yes",
                                        "No"
                                };
                                final AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Order delivered successfully?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0){
                                            ordersRef.child(adminOrders.getOrderId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    UserOrderViewRef.child(adminOrders.getPhoneKey()).child(adminOrders.getOrderId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            OrderItemsRef.child(adminOrders.getPhoneKey()).child(adminOrders.getOrderId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(AdminNewOrdersActivity.this, "Order removed from database successfully", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }

                                        if (which == 1){
                                            dialog.dismiss();
                                        }
                                    }
                                });
                                builder.show();

                                return true;
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return new AdminOrdersViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress, paymentStatus, transaction_TxtVw;
        public Button ShowOrdersBtn, showUser_Id,print_Bill_Btn;


        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.order_User_Name);
            userPhoneNumber=itemView.findViewById(R.id.order_Phone_Number);
            userShippingAddress=itemView.findViewById(R.id.order_Address_City);
            userDateTime=itemView.findViewById(R.id.order_Date_Time);
            userTotalPrice=itemView.findViewById(R.id.order_Total_Price);
            ShowOrdersBtn=itemView.findViewById(R.id.order_show_All_Products_Btn);
            paymentStatus= itemView.findViewById(R.id.payment_Status);
            transaction_TxtVw=itemView.findViewById(R.id.transaction_Id);

            showUser_Id=itemView.findViewById(R.id.order_show_User_Id_Btn);
            print_Bill_Btn=itemView.findViewById(R.id.order_Print_Bill_Btn);



        }
    }
}
