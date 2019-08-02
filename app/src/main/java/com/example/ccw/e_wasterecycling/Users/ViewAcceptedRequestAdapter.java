package com.example.ccw.e_wasterecycling.Users;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ccw.e_wasterecycling.Model.Product;
import com.example.ccw.e_wasterecycling.R;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAcceptedRequestAdapter extends RecyclerView.Adapter<ViewAcceptedRequestAdapter.ViewHolder> {

    public List<String> admin;
    private Context mContext;
    private List<Product> products;
    private FirebaseStorage firebaseStorage;
    private ProgressDialog progressDialog;


    public ViewAcceptedRequestAdapter (Context context, List<Product> product){
        mContext = context;
        products = product;
    }

    @NonNull
    @Override
    public ViewAcceptedRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.view_accepted_request_adapter, viewGroup, false);

        firebaseStorage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(mContext);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAcceptedRequestAdapter.ViewHolder viewHolder, int i) {

        Product product = products.get(i);
        viewHolder.email.setText(product.getEmail());
        viewHolder.id.setText(product.getUid());
        viewHolder.tat.setText(product.getTAT());
        viewHolder.tad.setText(product.getTAD());
        viewHolder.fad.setText(product.getFAD());
        viewHolder.fat.setText(product.getFAT());
        viewHolder.condition.setText(product.getCondition());
        viewHolder.status.setText(product.getStatus());
        viewHolder.address.setText(product.getAddress());
        viewHolder.categories.setText(product.getCategories());


        Picasso.get().load(product.getImageUrl())
                .fit()
                .centerCrop()
                .into(viewHolder.circleImageView);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView fad, tad, fat, tat, condition, status, address, email, id, categories;
        public CircleImageView circleImageView;

        public ViewHolder(@NonNull View itemView) {
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
        }
    }
}
