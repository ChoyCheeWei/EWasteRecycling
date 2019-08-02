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

import com.example.ccw.e_wasterecycling.Model.Contacts;
import com.example.ccw.e_wasterecycling.Notification.Token;
import com.example.ccw.e_wasterecycling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Contacts> contactsList;
    private DatabaseReference databaseReference;
    private ChatAdapter chatAdapter;
    private FirebaseUser firebaseUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.chat_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        contactsList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DisplayUser();
        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void DisplayUser() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Contacts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                contactsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contacts contacts = snapshot.getValue(Contacts.class);
                    if (contacts.getAdmin_Email().equals(firebaseUser.getEmail())) {
                        contactsList.add(contacts);
                    }
                }
                chatAdapter = new ChatAdapter(getActivity(), contactsList);
                Collections.sort(contactsList);
                recyclerView.setAdapter(chatAdapter);
                chatAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void updateToken(String token) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);

    }

}
