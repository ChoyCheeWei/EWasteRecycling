package com.example.ccw.e_wasterecycling;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;


public class Signup extends AppCompatActivity {

    private final static int PICK_IMAGE_REQUEST = 1;
    private final static int CAMERA = 2;
    CircleImageView circleImageView;
    EditText nameText, addressText, emailText, mobileText, passwordText, reEnterPasswordText;
    Button signupButton;
    TextView loginLink, error;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private StorageTask mUploadTask;
    private String link;
    private Uri imageUri;
    private Bitmap bitmap;
    private FirebaseUser firebaseUser;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameText = (EditText) findViewById(R.id.input_name);
        addressText = (EditText) findViewById(R.id.input_address);
        emailText = (EditText) findViewById(R.id.input_email);
        mobileText = (EditText) findViewById(R.id.input_mobile);
        passwordText = (EditText) findViewById(R.id.input_password);
        reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);
        circleImageView = (CircleImageView) findViewById(R.id.imageView);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        signupButton = (Button) findViewById(R.id.btn_signup);
        loginLink = (TextView) findViewById(R.id.link_login);
        error = (TextView) findViewById(R.id.error);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterAccount();

            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        requestMultiplePermissions();
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
        ContentResolver cR = getContentResolver();
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
            imageUri = getImageUri(getApplicationContext(), bitmap);

        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
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
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public void RegisterAccount() {

        final String email = emailText.getText().toString();
        final String username = nameText.getText().toString();
        final String pass = passwordText.getText().toString();
        final String phone = mobileText.getText().toString();
        final String comfirmpass = reEnterPasswordText.getText().toString();
        final String address = addressText.getText().toString();

        if (imageUri == null) {
            error.setError("Please select an image");
            error.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(email))) {
            emailText.setError("Email address is required");
            emailText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Invalid email");
            emailText.requestFocus();
            return;
        }

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
            reEnterPasswordText.setError("Confirm password is required");
            reEnterPasswordText.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(address))) {
            addressText.setError("address is required");
            addressText.requestFocus();
            return;
        }
        if (passwordText.length() <= 7) {
            passwordText.setError("Please Enter at least 8 digit password");
            passwordText.requestFocus();
            return;
        }
        if (reEnterPasswordText.length() <= 7) {
            reEnterPasswordText.setError("Please Enter at least 8 digit password");
            reEnterPasswordText.requestFocus();
            return;
        }
        if (mobileText.length() <= 7) {
            mobileText.setError("Please Enter at least 10 digit phone number");
            mobileText.requestFocus();
            return;
        }
        if (!passwordText.getText().toString().equals(reEnterPasswordText.getText().toString())) {
            passwordText.setError("Password does not match with confirm password");
            reEnterPasswordText.setError("Password does not match with confirm password");
            passwordText.requestFocus();
            reEnterPasswordText.requestFocus();
            return;
        }

        relativeLayout.setVisibility(View.VISIBLE);

        AddUser(email, username, pass, phone, comfirmpass, address);
    }

    public void AddUser(final String UserEmail, final String Username, final String Password,
                        final String PhoneNumber, final String confirmPassword, final String Address) {

        final DatabaseReference Userdatabase = FirebaseDatabase.getInstance().getReference();

        firebaseAuth.createUserWithEmailAndPassword(UserEmail, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    relativeLayout.setVisibility(View.INVISIBLE);
                    Toasty.error(getApplicationContext(), "The Email you use already Exist !", Toast.LENGTH_SHORT, true).show();
                } else if (task.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();

                    final String key = firebaseUser.getUid();

                    Userdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.child(key).exists()) {
                                if (imageUri != null) {
                                    final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                                            + "." + getFileExtension(imageUri));

                                    mUploadTask = fileReference.putFile(imageUri);

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

                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        hashMap.put("Uid", key);
                                                        hashMap.put("email", UserEmail);
                                                        hashMap.put("username", Username);
                                                        hashMap.put("password", Password);
                                                        hashMap.put("phone", PhoneNumber);
                                                        hashMap.put("address", Address);
                                                        hashMap.put("imageUrl", link);
                                                        hashMap.put("status", "offline");

                                                        relativeLayout.setVisibility(View.INVISIBLE);
                                                        Userdatabase.child("Users").child(key).updateChildren(hashMap);
                                                        Clear();
                                                        Toasty.success(getApplicationContext(), "Register Successful!!!", Toast.LENGTH_SHORT).show();

                                                    } else if (!task.isSuccessful()) {
                                                        relativeLayout.setVisibility(View.INVISIBLE);
                                                        Toasty.error(getApplicationContext(), "Task is not succeed", Toast.LENGTH_SHORT, true).show();
                                                    }
                                                }
                                            });
                                } else {
                                    relativeLayout.setVisibility(View.INVISIBLE);
                                    Toasty.error(getApplicationContext(), "Image leh", Toast.LENGTH_SHORT, true).show();

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            return;
                        }
                    });
                } else {
                    Toasty.warning(getApplicationContext(), "I don't Know", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }

    private void Clear(){
        circleImageView.setImageResource(R.drawable.image);
        emailText.setText("");
        nameText.setText("");
        addressText.setText("");
        mobileText.setText("");
        passwordText.setText("");
        reEnterPasswordText.setText("");
        error.setError(null);

    }
}