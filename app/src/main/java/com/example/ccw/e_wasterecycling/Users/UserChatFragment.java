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

import com.example.ccw.e_wasterecycling.Admin.ChatAdapter;
import com.example.ccw.e_wasterecycling.Model.Contacts;
import com.example.ccw.e_wasterecycling.Model.User;
import com.example.ccw.e_wasterecycling.Notification.Token;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UserChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Contacts> contactsList;
    private DatabaseReference databaseReference;
    private UserChatAdapter userChatAdapter;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.user_fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        contactsList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Displayuser();
        //GetUser();

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void Displayuser() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Contacts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                contactsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contacts contacts = snapshot.getValue(Contacts.class);
                    if (contacts.getEmail().equals(firebaseUser.getEmail())) {
                        contactsList.add(contacts);
                    }
                }
                userChatAdapter = new UserChatAdapter(getActivity(), contactsList);
                Collections.sort(contactsList);
                recyclerView.setAdapter(userChatAdapter);
                userChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToken(String token){
        databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);

    }

    private void GetUser() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Contacts");

        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                contactsList.add(dataSnapshot.getValue(Contacts.class));

                userChatAdapter = new UserChatAdapter(getActivity(), contactsList);
                recyclerView.setAdapter(userChatAdapter);
                userChatAdapter.notifyDataSetChanged();

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


}
