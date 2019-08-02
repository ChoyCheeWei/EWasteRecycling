package com.example.ccw.e_wasterecycling.Users;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ccw.e_wasterecycling.Model.Product;
import com.example.ccw.e_wasterecycling.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;


public class ViewAcceptedRequestFragment extends Fragment {

    private List<Product> productList;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ViewAcceptedRequestAdapter viewAcceptedRequestAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_view_accept_request, container, false);

        productList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference("Accepted Product");

        viewAcceptedRequestAdapter = new ViewAcceptedRequestAdapter(getContext(), productList);
        recyclerView.setAdapter(viewAcceptedRequestAdapter);

        String Email = getArguments().getString("Email");

        Query query = databaseReference.orderByChild("email").equalTo(Email);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                productList.add(dataSnapshot.getValue(Product.class));
                viewAcceptedRequestAdapter.notifyDataSetChanged();

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


}
