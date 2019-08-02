package com.example.ccw.e_wasterecycling.Users;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccw.e_wasterecycling.Model.Contacts;
import com.example.ccw.e_wasterecycling.Model.User;
import com.example.ccw.e_wasterecycling.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class UserProfile extends Fragment {

    private final static int PICK_IMAGE_REQUEST = 1;
    private final static int CAMERA = 2;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String link;
    private Uri imageUri;
    private CircleImageView circleImageView;
    private TextView save, email;
    private EditText nameText, passwordText, confirmPasswordText, mobileText, addressText, uid;
    private List<User> userList;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private RelativeLayout relativeLayout;
    private Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.user_profile, container, false);
        // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        nameText = view.findViewById(R.id.input_name);
        passwordText = view.findViewById(R.id.input_password);
        confirmPasswordText = view.findViewById(R.id.input_reEnterPassword);
        mobileText = view.findViewById(R.id.input_mobile);
        addressText = view.findViewById(R.id.input_address);
        circleImageView = view.findViewById(R.id.circleImageView);
        uid = view.findViewById(R.id.uid);
        save = view.findViewById(R.id.Save);
        email = view.findViewById(R.id.email);

        firebaseStorage = FirebaseStorage.getInstance();

        relativeLayout = view.findViewById(R.id.relativeLayout);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("Profile pictures");

        requestMultiplePermissions();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getEmail().equals(firebaseUser.getEmail())) {
                        userList.add(user);

                        email.setText(user.getEmail());
                        nameText.setText(user.getUsername());
                        passwordText.setText(user.getPassword());
                        confirmPasswordText.setText(user.getPassword());
                        mobileText.setText(user.getPhone());
                        addressText.setText(user.getAddress());
                        uid.setText(user.getUid());
                        Picasso.get().load(user.getImageUrl())
                                .fit()
                                .centerCrop()
                                .into(circleImageView);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAccount();
            }
        });

        return view;
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
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

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    public void selectImage() {
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            circleImageView.setImageURI(imageUri);

        }

        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");

            circleImageView.setImageBitmap(bitmap);
            imageUri = getImageUri(getContext(), bitmap);

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            //finish();
                            Toast.makeText(getContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();

                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    public void UpdateAccount() {

        final String Email = email.getText().toString();
        final String username = nameText.getText().toString();
        final String pass = passwordText.getText().toString();
        final String phone = mobileText.getText().toString();
        final String comfirmpass = confirmPasswordText.getText().toString();
        final String address = addressText.getText().toString();
        final String Uid = uid.getText().toString();


        if ((TextUtils.isEmpty(username))) {
            nameText.setError("Username is required");
            nameText.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(pass))) {
            passwordText.setError("Password is required");
            passwordText.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(phone))) {
            mobileText.setError("Phone number is required");
            mobileText.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(comfirmpass))) {
            confirmPasswordText.setError("Confirm password is required");
            confirmPasswordText.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(address))) {
            addressText.setError("address is required");
            addressText.requestFocus();
            return;
        }
        if (!passwordText.getText().toString().equals(confirmPasswordText.getText().toString())) {
            passwordText.setError("Password does not match with confirm password");
            confirmPasswordText.setError("Password does not match with confirm password");
            passwordText.requestFocus();
            confirmPasswordText.requestFocus();
            return;
        }

        if (pass.length() <= 7) {
            passwordText.setError("Please Enter at least 8 digit password");
            passwordText.requestFocus();
            return;
        }
        if (comfirmpass.length() <= 7) {
            confirmPasswordText.setError("Please Enter at least 8 digit password");
            confirmPasswordText.requestFocus();
            return;
        }


        relativeLayout.setVisibility(View.VISIBLE);

        UpdateUser(Email, Uid, username, pass, phone, comfirmpass, address);

    }


    public void UpdateUser(final String UserEmail, final String Uid, final String Username, final String Password,
                           final String PhoneNumber, final String confirmPassword, final String Address) {


        final HashMap<String, Object> hashMap = new HashMap<>();
        final HashMap<String, Object> hashMap1 = new HashMap<>();

        final DatabaseReference contactreference = FirebaseDatabase.getInstance().getReference("Contacts");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final User user = snapshot.getValue(User.class);
                    if (user.getUid().equals(firebaseUser.getUid())) {
                        if (imageUri != null) {
                            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                                    + "." + getFileExtension(imageUri));

                            StorageTask mUploadTask = fileReference.putFile(imageUri);

                            mUploadTask.continueWithTask(new Continuation() {
                                @Override
                                public Object then(@NonNull Task task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return fileReference.getDownloadUrl();
                                }
                            })
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                Uri downloadUrl = task.getResult();
                                                link = downloadUrl.toString();

                                                StorageReference imageRef = firebaseStorage.getReferenceFromUrl(user.getImageUrl());
                                                imageRef.delete();

                                                hashMap.put("Uid", Uid);
                                                hashMap.put("email", UserEmail);
                                                hashMap.put("username", Username);
                                                hashMap.put("password", Password);
                                                hashMap.put("phone", PhoneNumber);
                                                hashMap.put("address", Address);
                                                hashMap.put("imageUrl", link);
                                                hashMap.put("status", "offline");

                                                snapshot.getRef().updateChildren(hashMap);

                                                contactreference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                            Contacts contacts = dataSnapshot1.getValue(Contacts.class);
                                                            if (contacts.getUid().equals(firebaseUser.getUid())) {
                                                                hashMap1.put("imageUrl", link);
                                                                dataSnapshot1.getRef().updateChildren(hashMap1);

                                                                System.out.println("haha update " + contacts.getUid());
                                                                System.out.println("haha update " + firebaseUser.getUid());
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                                Toasty.success(getContext(), "Update Successful!!!", Toast.LENGTH_SHORT).show();
                                                relativeLayout.setVisibility(View.INVISIBLE);


                                            } else if (!task.isSuccessful()) {
                                                Toasty.error(getContext(), "Task is not succeed", Toast.LENGTH_SHORT, true).show();
                                            }

                                        }
                                    });
                        } else {
                            hashMap.put("Uid", Uid);
                            hashMap.put("email", UserEmail);
                            hashMap.put("username", Username);
                            hashMap.put("password", Password);
                            hashMap.put("phone", PhoneNumber);
                            hashMap.put("address", Address);

                            snapshot.getRef().updateChildren(hashMap);

                            relativeLayout.setVisibility(View.INVISIBLE);
                            Toasty.success(getContext(), "Update Successful!!!", Toast.LENGTH_SHORT, true).show();


                        }
                    } else {

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

}