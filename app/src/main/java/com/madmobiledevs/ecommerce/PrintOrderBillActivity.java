package com.madmobiledevs.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madmobiledevs.ecommerce.Model.Cart;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PrintOrderBillActivity extends AppCompatActivity {

    RecyclerView recyclerView_bill;

    DatabaseReference orderIdRef;

    private String totalAmount, cGst, gst, grandTotal, name, phoneKey, orderId, deliveryCharge;

    private TextView totalAmount_TxtVw, cGst_TxtVw, gst_TxtVw, grandTotal_TxtVw, name_TxtVw, deliveryCharge_TxtVw, date_TxtVw;

    private int serialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_order_bill);

        name_TxtVw = findViewById(R.id.customer_Name_Bill);
        totalAmount_TxtVw = findViewById(R.id.total_bill);
        cGst_TxtVw = findViewById(R.id.cGst_bill);
        gst_TxtVw = findViewById(R.id.gst_bill);
        grandTotal_TxtVw = findViewById(R.id.grandTotal_Bill);
        deliveryCharge_TxtVw = findViewById(R.id.delivery_bill);
        date_TxtVw = findViewById(R.id.Date_Bill);

        serialNumber = 1;

        totalAmount= getIntent().getStringExtra("totalAmount");
        cGst= getIntent().getStringExtra("cGst");
        gst= getIntent().getStringExtra("gst");
        grandTotal= getIntent().getStringExtra("grandTotal");
        name= getIntent().getStringExtra("name");
        phoneKey= getIntent().getStringExtra("phoneKey");
        orderId= getIntent().getStringExtra("orderId");
        deliveryCharge= getIntent().getStringExtra("deliveryCharge");

        orderIdRef = FirebaseDatabase.getInstance().getReference().child("orderItems").child(phoneKey).child(orderId);

        recyclerView_bill = (RecyclerView) findViewById(R.id.recycler_view_bill);
        recyclerView_bill.setLayoutManager(new LinearLayoutManager(this));

        setTextToRemainingTextViews();

    }

    private void setTextToRemainingTextViews() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yy");

        date_TxtVw.append(currentDate.format(calForDate.getTime()));

        name_TxtVw.append(name);
        totalAmount_TxtVw.append(totalAmount);
        cGst_TxtVw.append(cGst);
        gst_TxtVw.append(gst);
        grandTotal_TxtVw.append(grandTotal);
        deliveryCharge_TxtVw.append(deliveryCharge);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(orderIdRef,Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart,BillProductsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, BillProductsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull BillProductsViewHolder billProductsViewHolder, int i, @NonNull Cart cart) {

                        billProductsViewHolder.sNo.setText(Integer.toString(serialNumber));
                        serialNumber= serialNumber+1;

                        billProductsViewHolder.productName.setText(cart.getPname());
                        if (cart.getPtype().equals("raw")){
                            billProductsViewHolder.quantity.setText(cart.getQuantity()+" gram");
                        }
                        else {
                            billProductsViewHolder.quantity.setText(cart.getQuantity());
                        }

                        billProductsViewHolder.rate.setText(cart.getPrice());

                        int int_price = Integer.parseInt(cart.getPrice())*Integer.parseInt(cart.getQuantity());

                        billProductsViewHolder.price.setText(Integer.toString(int_price));

                    }

                    @NonNull
                    @Override
                    public BillProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_bill,parent,false);
                        return new BillProductsViewHolder(view);
                    }
                };

        recyclerView_bill.setAdapter(adapter);
        adapter.startListening();
    }


    public static class BillProductsViewHolder extends RecyclerView.ViewHolder{

        TextView sNo, productName, quantity, price, rate;

        public BillProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            sNo = itemView.findViewById(R.id.sNo_bill);
            productName = itemView.findViewById(R.id.product_Name_bill);
            quantity = itemView.findViewById(R.id.quantity_bill);
            price = itemView.findViewById(R.id.price_bill);
            rate= itemView.findViewById(R.id.rate_bill);

        }
    }
}