package com.example.ccw.e_wasterecycling.Users;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ccw.e_wasterecycling.Model.Product;
import com.example.ccw.e_wasterecycling.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewUserRequestAdapter extends RecyclerView.Adapter<ViewUserRequestAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Product> productList;
    private OnItemClickListener mListener;


    public ViewUserRequestAdapter(Context context, List<Product> mProduct) {
        mContext = context;
        productList = mProduct;
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.view_user_request_adapter, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewUserRequestAdapter.ImageViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.email.setText(product.getEmail());
        holder.id.setText(product.getUid());
        holder.tat.setText(product.getTAT());
        System.out.println("haha " + product.getTAT());
        holder.tad.setText(product.getTAD());
        holder.fad.setText(product.getFAD());
        holder.fat.setText(product.getFAT());
        holder.condition.setText(product.getCondition());
        holder.status.setText(product.getStatus());
        holder.address.setText(product.getAddress());
        holder.categories.setText(product.getCategories());

        Picasso.get().load(product.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.circleImageView);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setOnItemClickListener(ViewUserRequestAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    public interface OnItemClickListener {

        void onDeleteClick(int position);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView fad, tad, fat, tat, condition, status, address, email, id, categories;
        public CircleImageView circleImageView;


        public ImageViewHolder(View itemView) {
            super(itemView);

            categories = itemView.findViewById(R.id.categories);
            email = itemView.findViewById(R.id.email);
            id = itemView.findViewById(R.id.id);
            fad = itemView.findViewById(R.id.from_available_date);
            tad = itemView.findViewById(R.id.to_available_date);
            fat = itemView.findViewById(R.id.from_available_time);
            tat = itemView.findViewById(R.id.to_available_time);
            condition = itemView.findViewById(R.id.product_condition);
            status = itemView.findViewById(R.id.product_status);
            address = itemView.findViewById(R.id.user_address);
            circleImageView = itemView.findViewById(R.id.image);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Cancel");

            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onDeleteClick(position);
                            return true;

                    }
                }
            }
            return false;
        }

    }
}