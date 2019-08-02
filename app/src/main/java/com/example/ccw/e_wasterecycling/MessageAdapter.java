package com.example.ccw.e_wasterecycling;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ccw.e_wasterecycling.Model.Chat;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> chatList;
    private String imageUrl;
    private String id;


    public MessageAdapter(Context context, List<Chat> mChat, String mimageUrl, String Id) {
        mContext = context;
        chatList = mChat;
        imageUrl = mimageUrl;
        id = Id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == MSG_TYPE_RIGHT) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, viewGroup, false);
            return new ViewHolder(view);

        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, viewGroup, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder viewHolder, final int i) {

        Chat chat = chatList.get(i);

        viewHolder.time.setText(chat.getTime());

        String messageType = chat.getType();

        Picasso.get().load(imageUrl).fit().centerCrop().into(viewHolder.profile);

        if (i == chatList.size() - 1) {
            if (chat.isIsseen()) {
                viewHolder.seen.setText("Seen");
            } else {
                viewHolder.seen.setText("Delivered");
            }
        } else {
            viewHolder.seen.setVisibility(View.GONE);
        }

        if (messageType.equals("text")) {
            viewHolder.showMessage.setText(chat.getMessage());
            viewHolder.showMessage.setVisibility(View.VISIBLE);
        } else if (messageType.equals("image")) {
            viewHolder.showImage.setVisibility(View.VISIBLE);
            Picasso.get().load(chat.getMessage()).placeholder(R.drawable.loading).fit().centerCrop().into(viewHolder.showImage);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getSender().equals(id)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView showMessage, seen, time;
        public ImageView showImage;
        public CircleImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.showMessage);
            profile = itemView.findViewById(R.id.profile);
            seen = itemView.findViewById(R.id.seen);
            time = itemView.findViewById(R.id.time);
            showImage = itemView.findViewById(R.id.showImage);
        }
    }
}
