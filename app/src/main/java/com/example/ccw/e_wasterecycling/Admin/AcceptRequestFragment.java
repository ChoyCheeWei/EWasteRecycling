package com.example.ccw.e_wasterecycling.Admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ccw.e_wasterecycling.Model.Product;
import com.example.ccw.e_wasterecycling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AcceptRequestFragment extends Fragment {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private AcceptRequestAdapter acceptRequestAdapter;

    private List<Product> productList;
    private List<String> userList;
    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.accept_request_fragment, container, false);
        // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        productList = new ArrayList<>();
        userList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        GetUser(firebaseUser.getEmail());

        return view;
    }

    private void GetUser(final String Email) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        Query query = databaseReference.orderByChild("email").equalTo(Email);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userList.add(String.valueOf(snapshot.getValue()));

                    }
                    String adminName = userList.get(7);
                    String adminImage = userList.get(3);

                    Display(Email, adminName, adminImage);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Display(final String Email, final String adminName, final String adminImage) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Product Info");

        /*Query query = databaseReference.orderByChild("AdminEmail").equalTo(Email);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    productList.add(dataSnapshot.getValue(Product.class));
                }
                acceptRequestAdapter = new AcceptRequestAdapter(getActivity(), productList, Email, adminName, adminImage);
                recyclerView.setAdapter(acceptRequestAdapter);
                acceptRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    if(product.getAdminEmail().equals(firebaseUser.getEmail())){
                        productList.add(product);
                    }
                }
                acceptRequestAdapter = new AcceptRequestAdapter(getActivity(), productList, Email, adminName, adminImage);
                recyclerView.setAdapter(acceptRequestAdapter);

                acceptRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                final Toast error = Toasty.error(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT, true);
                error.show();
            }
        });
    }
}
