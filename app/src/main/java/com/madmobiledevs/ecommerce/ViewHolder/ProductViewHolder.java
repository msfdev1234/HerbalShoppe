package com.madmobiledevs.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.madmobiledevs.ecommerce.Interface.ItemClickListener;
import com.madmobiledevs.ecommerce.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;


    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView= (ImageView) itemView.findViewById(R.id.product_Image);
        txtProductName= (TextView) itemView.findViewById(R.id.product_Name);
        txtProductDescription= (TextView) itemView.findViewById(R.id.product_Description);
        txtProductPrice= (TextView) itemView.findViewById(R.id.product_Price);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View v) {
        listener.onClick(v,getAdapterPosition(),false);
    }
}
