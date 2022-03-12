package com.example.nlorderfood.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nlorderfood.Helpers.DatabaseHelper;
import com.example.nlorderfood.Models.CartItem;
import com.example.nlorderfood.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartItemViewHolder> {
    private ArrayList<CartItem>cartItems;
    public  DatabaseHelper dbHelper;
    public CartAdapter(ArrayList<CartItem> cartItems, Context context){
        this.cartItems = cartItems;
        dbHelper=new DatabaseHelper(context);
    }

    @Override
    public int getItemCount()
    {
        return cartItems.size();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_template,parent,false);
        return new CartItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        holder.bind(cartItems.get(position));
        CartItem item = cartItems.get(holder.getAdapterPosition());



        holder.decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer quantity=Integer.parseInt(holder.Quantity.getText().toString());
                Integer price= Integer.parseInt(item.getPrice());
                Integer amount=0;
                System.out.println("Tăng lần nhấp vào nút");
                if (quantity==1){
                    holder.decreaseBtn.setEnabled(false);
                }
                else{
                    quantity--;
                    holder.Quantity.setText(quantity.toString());
                    amount= price * quantity;
                    holder.Amount.setText("VND "+amount);
                    boolean isUpdated= dbHelper.updateCartItem(item.getId(),item.getName(),quantity.toString(),amount.toString(),item.getPrice());
                    if (isUpdated){
                        System.out.println(item.getName()+" giá trị đã thay đổi trong giỏ hàng");
                    }
                    else {
                        System.out.println(item.getName()+" Lỗi");
                    }

                }
            }
        });

        holder.increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer quantity=Integer.parseInt(holder.Quantity.getText().toString());
                Integer price= Integer.parseInt(item.getPrice());
                Integer amount=0;
                System.out.println("Tăng số lần khi nhấp vào nút");
                if (quantity<15){
                    quantity++;
                    holder.Quantity.setText(quantity.toString());
                    amount= price * quantity;
                    holder.Amount.setText("VND "+amount);
                    boolean isUpdated= dbHelper.updateCartItem(item.getId(),item.getName(),quantity.toString(),amount.toString(),item.getPrice());
                    if (isUpdated){
                        System.out.println(item.getName()+" giá trị đã thay đổi trong giỏ hàng");
                    }
                    else {
                        System.out.println(item.getName()+" lỗi khi thêm món ăn");
                    }
                }
                else{
                    holder.increaseBtn.setEnabled(false);
                }
            }
        });

        holder.removeItemBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean isDeleted= dbHelper.deleteCartItem(item.getId());
                if(isDeleted){
                    //cartItems.remove(holder.getAdapterPosition());
                    holder.bind(cartItems.remove(holder.getAdapterPosition()));
                    System.out.println(item.getName()+" xóa từ giỏ hàng");
                    String message="Đã xóa ";
                    Toast.makeText(v.getContext(), message,Toast.LENGTH_SHORT).show();

                }
                else
                    System.out.println("Lỗi khi xóa "+item.getName());

                String message="Lỗi khi xóa ";
                Toast.makeText(v.getContext(), message,Toast.LENGTH_SHORT).show();

            }
        });

    }


    public class CartItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        DatabaseHelper _dbHelper;
        ImageView Url;
        TextView Name, Quantity, Amount;
        Button increaseBtn, decreaseBtn, removeItemBTN;
        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            _dbHelper=new DatabaseHelper(itemView.getContext());
            Name=itemView.findViewById(R.id.name);
            Url = itemView.findViewById(R.id.image);
            Quantity=itemView.findViewById(R.id.quantity);
            Amount=itemView.findViewById(R.id.amount);
            removeItemBTN=itemView.findViewById(R.id.button_remove);
            increaseBtn=itemView.findViewById(R.id.increase);
            decreaseBtn=itemView.findViewById(R.id.decrease);

        }
        public void bind(CartItem cartItem){
            Name.setText(""+cartItem.getName());
            Glide.with(itemView).load(cartItem.getUrl()).into(Url);
            Quantity.setText(""+cartItem.getQuantity());
            Amount.setText(cartItem.getAmount()+" VND");
            removeItemBTN.setOnClickListener(this);
            /*new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Remove Button Has Been Clicked");
                    _dbHelper.deleteCartItem(cartItem.getUrl());
                    Toast.makeText(itemView.getContext(), cartItem.getName()+" is removed from cart",Toast.LENGTH_SHORT).show();
                }
            }*/
        }

        @Override
        public void onClick(View v) {
            notifyDataSetChanged();
        }
    }

}
