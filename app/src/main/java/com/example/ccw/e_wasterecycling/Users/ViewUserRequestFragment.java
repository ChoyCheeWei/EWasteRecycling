package com.example.ccw.e_wasterecycling.Users;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ViewUserRequestFragment extends Fragment implements ViewUserRequestAdapter.OnItemClickListener {

    View view;
    private List<Product> productList;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ViewUserRequestAdapter viewUserRequestAdapter;
    private FirebaseStorage firebaseStorage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.view_user_request_fragment, container, false);
        productList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference("Product Info");
        firebaseStorage = FirebaseStorage.getInstance();

        viewUserRequestAdapter = new ViewUserRequestAdapter(getContext(), productList);
        recyclerView.setAdapter(viewUserRequestAdapter);

        viewUserRequestAdapter.setOnItemClickListener(this);

        String Email = getArguments().getString("Email");

        Query query = databaseReference.orderByChild("email").equalTo(Email);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                productList.add(dataSnapshot.getValue(Product.class));
                viewUserRequestAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    public void onDeleteClick(final int position) {

        Product selectedItem = productList.get(position);
        final String selectedKey = selectedItem.getUid();

        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                Toasty.success(getContext(), "Request Cancelled", Toast.LENGTH_SHORT).show();
                productList.remove(position);
                viewUserRequestAdapter.notifyDataSetChanged();

            }
        });

    }


}
