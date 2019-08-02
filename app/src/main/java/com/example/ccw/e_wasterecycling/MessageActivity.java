package com.example.ccw.e_wasterecycling;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccw.e_wasterecycling.Model.Chat;
import com.example.ccw.e_wasterecycling.Model.Contacts;
import com.example.ccw.e_wasterecycling.Model.User;
import com.example.ccw.e_wasterecycling.Notification.Client;
import com.example.ccw.e_wasterecycling.Notification.Data;
import com.example.ccw.e_wasterecycling.Notification.MyResponse;
import com.example.ccw.e_wasterecycling.Notification.Sender;
import com.example.ccw.e_wasterecycling.Notification.Token;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    private final static int PICK_IMAGE_REQUEST = 1;
    private final static int CAMERA = 2;
    CircleImageView profile;
    TextView username, id, time;
    EditText message;
    ImageButton send, plus;
    Intent intent;
    ValueEventListener seenListener;
    String userid, Time, type;
    APIService apiService;
    boolean notify = false;
    private Uri imageUri;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Chat> mChat;
    private StorageReference mImageStorage;
    private StorageTask mUploadTask;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile = findViewById(R.id.profile);
        username = findViewById(R.id.username);
        id = findViewById(R.id.id);
        message = findViewById(R.id.text_send);
        send = findViewById(R.id.btn_send);
        time = findViewById(R.id.time);
        plus = findViewById(R.id.plus);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        storageReference = FirebaseStorage.getInstance().getReference().child("Chat Images");

        firebaseDatabase = FirebaseDatabase.getInstance();
        intent = getIntent();
        userid = intent.getStringExtra("userid");

        final Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                time.setText(new SimpleDateFormat("HH:mm:ss", Locale.UK).format(new Date()));
                Time = time.getText().toString();

                handler.postDelayed(this, 1000);
            }
        }, 10);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seenMessage(userid);
                finish();
                System.out.println("haha 123");

            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        type = "text";

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        GetUserInfo(userid);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                SaveMessage(userid, firebaseUser.getUid(), Time);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showPictureDialog();
            }
        });

    }

    private void selectImage(){
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                selectImage();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }

        if (requestCode == CAMERA && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap)  extras.get("data");

            imageUri = getImageUri(getApplicationContext(),bitmap);
        }

        final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                + "." + getFileExtension(imageUri));

        final String type = "image";

        mUploadTask = fileReference.putFile(imageUri);

        mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {
                    String downloadUrl = task.getResult().toString();

                    SendMessage(firebaseUser.getUid(),userid,downloadUrl, Time,type);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                System.out.println("haha chat e " + e);
            }
        });
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void seenMessage(final String userid) {

        final HashMap<String, Object> hashMap = new HashMap<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                        hashMap.put("isseen", true);
                    }
                    snapshot.getRef().updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void GetUserInfo(final String userid) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                id.setText(firebaseUser.getUid());

                Picasso.get().load(user.getImageUrl()).into(profile);

                ReadMessage(firebaseUser.getUid(), userid, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void SaveMessage(String userid, String id, String time) {
        final String Message = message.getText().toString();

        if (TextUtils.isEmpty(Message)) {
            Toasty.warning(getApplicationContext(), "You can't send a empty message",
                    Toast.LENGTH_SHORT, true).show();
        } else {
            SendMessage(id, userid, Message, time,type);
            message.setText("");
        }

    }

    public void SendMessage(final String Sender, final String Receiver, String Message, final String time, final String type) {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats");

        String key = firebaseDatabase.getReference("Chats").push().getKey();

        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Key", key);
        hashMap.put("Sender", Sender);
        hashMap.put("Receiver", Receiver);
        hashMap.put("Message", Message);
        hashMap.put("isseen", false);
        hashMap.put("time", time);
        hashMap.put("type", type);

        databaseReference.child(key).updateChildren(hashMap);

        final DatabaseReference senderReference = FirebaseDatabase.getInstance().getReference("Contacts");
        senderReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contacts contacts = snapshot.getValue(Contacts.class);
                    if (contacts.getAdminId().equals(Sender) && contacts.getUid().equals(Receiver) ||
                            contacts.getUid().equals(Sender) && contacts.getAdminId().equals(Receiver)) {
                        HashMap<String, Object> hashMap1 = new HashMap<>();

                        hashMap1.put("time", time);
                        senderReference.child(contacts.getUid() + contacts.getAdminId()).updateChildren(hashMap1);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final String msg = Message;

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(Receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");

        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), username + ": " + message, "New message",
                            userid, R.mipmap.ic_launcher);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {

                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toasty.warning(MessageActivity.this, "Failed", Toast
                                                    .LENGTH_SHORT, true).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<MyResponse> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }


    private void ReadMessage(final String myid, final String userid, final String imageurl) {
        mChat = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mChat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl, myid);
                    recyclerView.setAdapter(messageAdapter);
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
     /*   databaseReference.removeEventListener(seenListener);
        currentUser("none");*/
    }
}
