package com.example.ccw.e_wasterecycling.Admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccw.e_wasterecycling.Model.Product;
import com.example.ccw.e_wasterecycling.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.suke.widget.SwitchButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.os.Looper.getMainLooper;

public class AcceptRequestAdapter extends RecyclerView.Adapter<AcceptRequestAdapter.ViewHolder> {

    String memail, Time;
    private Context mContext;
    private List<Product> products;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String adminname, adminimage;
    private FirebaseUser firebaseUser;

    public AcceptRequestAdapter(Context context, List<Product> product, String email, String adminName, String adminImage) {
        mContext = context;
        products = product;
        memail = email;
        adminname = adminName;
        adminimage = adminImage;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.accept_request_adapter, viewGroup, false);

        progressDialog = new ProgressDialog(mContext);
        firebaseDatabase = FirebaseDatabase.getInstance();

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final AcceptRequestAdapter.ViewHolder viewHolder, final int i) {

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
        viewHolder.userid.setText(product.getUserId());
        viewHolder.username.setText(product.getUserName());
        viewHolder.userimage.setText(product.getUserImage());


        Picasso.get().load(product.getImageUrl())
                .fit()
                .centerCrop()
                .into(viewHolder.imageView);

        viewHolder.switchButton.setChecked(true);
        viewHolder.switchButton.isChecked();//switch state
        viewHolder.switchButton.toggle(true);//switch without animation
        viewHolder.switchButton.setShadowEffect(true);//disable shadow effect
        viewHolder.switchButton.setEnableEffect(true);//disable the switch animation
        //find switchbutton function, make sure after checked it will remain the current status

        final Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewHolder.time.setText(new SimpleDateFormat("HH:mm:ss", Locale.UK).format(new Date()));
                Time = viewHolder.time.getText().toString();

                handler.postDelayed(this, 1000);
            }
        }, 10);


        final String Email = viewHolder.email.getText().toString();
        final String Id = viewHolder.id.getText().toString();
        final String Tat = viewHolder.tat.getText().toString();
        final String Tad = viewHolder.tad.getText().toString();
        final String Fad = viewHolder.fad.getText().toString();
        final String Fat = viewHolder.fat.getText().toString();
        final String Condition = viewHolder.condition.getText().toString();
        final String image = product.getImageUrl();
        final String Address = viewHolder.address.getText().toString();
        final String Categories = viewHolder.categories.getText().toString();
        final String UserID = viewHolder.userid.getText().toString();
        final String Username = viewHolder.username.getText().toString();
        final String UserImage = viewHolder.userimage.getText().toString();


        viewHolder.switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Do your want to accept this request? ");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ManageRequest(Id, Email, Address, Condition, Fad, Tad, Fat, Tat, Categories, UserID, Username, UserImage, image, Time);
                            }
                        }
                );

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }


    public void ManageRequest(final String Id, final String Email, final String Address, final String Condition, final String Fad,
                              final String Tad, final String Fat, final String Tat, final String Categories, final String UserID,
                              final String Username, final String UserImage, final String image, final String Time) {


        progressDialog.setMessage("Accepting.........");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final String newStatus = "Accepted";

        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Uid", Id);
        hashMap.put("email", Email);
        hashMap.put("address", Address);
        hashMap.put("condition", Condition);
        hashMap.put("FAD", Fad);
        hashMap.put("TAD", Tad);
        hashMap.put("FAT", Fat);
        hashMap.put("TAT", Tat);
        hashMap.put("categories", Categories);
        hashMap.put("status", newStatus);
        hashMap.put("UserId", UserID);
        hashMap.put("imageUrl", image);
        hashMap.put("AdminEmail", memail);

        databaseReference = FirebaseDatabase.getInstance().getReference("Accepted Product");
        databaseReference.child(Id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product Info");
                    db.child(Id).removeValue();

                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    final String adminId = firebaseUser.getUid();
                    final String key = UserID + adminId;
                    final DatabaseReference newReference = FirebaseDatabase.getInstance().getReference("Contacts").child(key);
                    newReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.child(key).exists()) {
                                HashMap<String, Object> hashMap2 = new HashMap<>();
                                hashMap2.put("adminId", adminId);
                                hashMap2.put("username", Username);
                                hashMap2.put("imageUrl", UserImage);
                                hashMap2.put("Uid", UserID);
                                hashMap2.put("email", Email);
                                hashMap2.put("adminName", adminname);
                                hashMap2.put("adminImage", adminimage);
                                hashMap2.put("admin_Email", memail);

                                newReference.updateChildren(hashMap2);

                                progressDialog.dismiss();
                                Toasty.success(mContext, "Accept Request Successful!!!", Toast.LENGTH_SHORT, true).show();

                            } else {
                                Toasty.warning(mContext, "What happened?????", Toast.LENGTH_SHORT, true).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fad, tad, fat, tat, condition, status, address, email, id, categories, userid, username, userimage, time;
        CircleImageView imageView;
        SwitchButton switchButton;

        public ViewHolder(View itemView) {
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
            imageView = itemView.findViewById(R.id.image);
            switchButton = itemView.findViewById(R.id.switch_button);
            userid = itemView.findViewById(R.id.UserId);
            username = itemView.findViewById(R.id.Username);
            userimage = itemView.findViewById(R.id.UserImage);
            time = itemView.findViewById(R.id.time);

        }


    }


}
