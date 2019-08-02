package com.example.ccw.e_wasterecycling.Admin;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ccw.e_wasterecycling.MessageActivity;
import com.example.ccw.e_wasterecycling.Model.Chat;
import com.example.ccw.e_wasterecycling.Model.Contacts;
import com.example.ccw.e_wasterecycling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private String sender, Time, theLastMessage;
    private Context mContext;
    private List<Contacts> contactsList;
    private List<Chat> chatList;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Chat chat;


    public ChatAdapter(Context context, List<Contacts> mContacts) {
        mContext = context;
        contactsList = mContacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final Contacts contacts = contactsList.get(i);

        viewHolder.id.setText(contacts.getUid());
        viewHolder.username.setText(contacts.getUsername());

        lastMessage(contacts.getUid(), viewHolder.last_msg, viewHolder.time);

        Picasso.get().load(contacts.getImageUrl())
                .fit()
                .centerCrop()
                .into(viewHolder.imageView);

        System.out.println("haha chatadpter " + contacts.getImageUrl());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int unread = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()) {
                        unread++;
                        sender = chat.getSender();
                    }
                }

                if (unread == 0) {
                    viewHolder.unread.setVisibility(View.GONE);
                    viewHolder.unread_img.setVisibility(View.GONE);
                } else if (viewHolder.id.getText().toString().equals(sender)) {
                    viewHolder.unread_img.setVisibility(View.VISIBLE);
                    viewHolder.unread.setText("" + unread);
                    if (chat.isIsseen() && viewHolder.id.getText().toString().equals(sender)) {
                        viewHolder.unread_img.setVisibility(View.GONE);
                        viewHolder.unread.setVisibility(View.GONE);
                    }
                }
                if (unread > 99 && viewHolder.id.getText().toString().equals(sender)) {
                    viewHolder.unread_img.setVisibility(View.VISIBLE);
                    viewHolder.unread.setText("...");
                    if (chat.isIsseen() && viewHolder.id.getText().toString().equals(sender)) {
                        viewHolder.unread_img.setVisibility(View.GONE);
                        viewHolder.unread.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Collections.sort(contactsList);


       /* if (ischat) {
            if (user.getStatus().equals("online")) {
                viewHolder.img_on.setVisibility(View.VISIBLE);
                viewHolder.img_off.setVisibility(View.GONE);
            } else {
                viewHolder.img_on.setVisibility(View.GONE);
                viewHolder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.img_on.setVisibility(View.GONE);
            viewHolder.img_off.setVisibility(View.GONE);
        }*/


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", contacts.getUid());
                intent.putExtra("id", firebaseUser.getUid());
                mContext.startActivity(intent);
            }
        });
    }

    //check last message
    private void lastMessage(final String userid, final TextView last_msg, final TextView time) {
        theLastMessage = "default";

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                        theLastMessage = chat.getMessage();
                        Time = chat.getTime();
                    }

                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("No message");
                        time.setVisibility(View.GONE);
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        time.setText(Time);
                        break;
                }
                theLastMessage = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id, username, last_msg, unread, time;
        public CircleImageView imageView, img_on, img_off, unread_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            username = itemView.findViewById(R.id.username);
            imageView = itemView.findViewById(R.id.image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_message);
            unread = itemView.findViewById(R.id.unread);
            time = itemView.findViewById(R.id.time);
            unread_img = itemView.findViewById(R.id.unread_img);
        }
    }
}
