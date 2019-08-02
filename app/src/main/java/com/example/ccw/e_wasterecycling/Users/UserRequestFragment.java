package com.example.ccw.e_wasterecycling.Users;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ccw.e_wasterecycling.PlaceAutoCompleteAdapter;
import com.example.ccw.e_wasterecycling.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class UserRequestFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {

    private final static int PICK_IMAGE_REQUEST = 1;
    private final static int CAMERA = 2;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-20, -20), new LatLng(10, 10)
    );
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    View view;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button button;
    CircleImageView circleImageView;
    TextView date_1, date_2, time_1, time_2, userId, username, userimage, error;
    Spinner spinner;
    String adminEmail = "";
    private AutoCompleteTextView autoCompleteTextView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private StorageTask mUploadTask;
    private String link;
    private Bitmap bitmap;
    private Uri imageUri;
    private GoogleApiClient googleApiClient;
    private List<String> userList;
    private FirebaseUser firebaseUser;
    private RelativeLayout relativeLayout, relativeLayout1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.user_request_fragment, container, false);

        date_1 = view.findViewById(R.id.date_1);
        date_2 = view.findViewById(R.id.date_2);
        time_1 = view.findViewById(R.id.time_1);
        time_2 = view.findViewById(R.id.time_2);

        autoCompleteTextView = view.findViewById(R.id.auto_complete);
        radioGroup = view.findViewById(R.id.condition);
        button = view.findViewById(R.id.submit);
        circleImageView = view.findViewById(R.id.imageview);
        userId = view.findViewById(R.id.cheat);
        username = view.findViewById(R.id.username);
        userimage = view.findViewById(R.id.userimage);
        error = view.findViewById(R.id.error);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        assert getArguments() != null;

        spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.Categories,
                R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        relativeLayout = view.findViewById(R.id.relativeLayout);
        relativeLayout1 = view.findViewById(R.id.relativeLayout1);

        firebaseDatabase = FirebaseDatabase.getInstance();

        googleApiClient = new GoogleApiClient
                .Builder(Objects.requireNonNull(getActivity()))
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        PlaceAutoCompleteAdapter autocompleteAdapter = new PlaceAutoCompleteAdapter(getActivity(), googleApiClient,
                LAT_LNG_BOUNDS, null);

        autoCompleteTextView.setAdapter(autocompleteAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Product Info");
        storageReference = FirebaseStorage.getInstance().getReference("Product Info");

        //get date
        calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        //get time
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        date_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        date_1.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                    }
                }, year, month, day); //changed from day,month,year   to  year,month,day
                datePickerDialog.show();
            }

        });

        date_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        date_2.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                    }
                }, year, month, day); //changed from day,month,year   to  year,month,day
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });

        time_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePickerDialog = new TimePickerDialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time_1.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        time_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePickerDialog = new TimePickerDialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time_2.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        String Email = firebaseUser.getEmail();

        Query query = databaseReference.orderByChild("email").equalTo(Email);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userList.add(String.valueOf(snapshot.getValue()));
                    }
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    userId.setText(firebaseUser.getUid());
                    username.setText(userList.get(7));
                    userimage.setText(userList.get(3));

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckProduct();

            }
        });
        requestMultiplePermissions();

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

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null)
            googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage((Objects.requireNonNull(getActivity())));
            googleApiClient.disconnect();
        }
    }

    public void selectImage() {
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = Objects.requireNonNull(getActivity()).getContentResolver();
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

    @SuppressLint("SetTextI18n")
    public void CheckProduct() {
        int RadioButtonId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(RadioButtonId);

        final String address = autoCompleteTextView.getText().toString();
        final String f_a_d = date_1.getText().toString();
        final String f_a_t = time_1.getText().toString();
        final String t_a_d = date_2.getText().toString();
        final String t_a_t = time_2.getText().toString();
        final String condition = radioButton.getText().toString();
        final String categories = spinner.getSelectedItem().toString();
        final String UserId = userId.getText().toString();
        final String Username = username.getText().toString();
        final String UserImage = userimage.getText().toString();

        if ((TextUtils.isEmpty(address))) {
            autoCompleteTextView.setError("address is required");
            autoCompleteTextView.requestFocus();
            return;
        }
        if (imageUri == null) {
            error.setError("Please select an image");
            error.requestFocus();
            return;
        }
        if (date_2.getText().toString().equals("Select date")) {
            date_1.setError("Please choose your available date");
            Toasty.warning(getActivity(), "Please select your available time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (date_1.getText().toString().equals("Select date")) {
            date_2.setError("Please choose your available date");
            Toasty.warning(getActivity(), "Please select your available time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time_1.getText().toString().equals("Select time")) {
            time_1.setError("Please choose your available time");
            Toasty.warning(getActivity(), "Please select your available time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time_2.getText().toString().equals("Select time")) {
            time_2.setError("Please choose your available time");
            Toasty.warning(getActivity(), "Please select your available time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinner.getSelectedItemPosition() == 0) {
            TextView errorText = (TextView) spinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Selected an category");
            spinner.requestFocus();
            return;
        }

        relativeLayout.setVisibility(View.INVISIBLE);
        relativeLayout1.setVisibility(View.VISIBLE);

        assert getArguments() != null;
        String Email = firebaseUser.getEmail();
        String key = firebaseDatabase.getReference("Product Info").push().getKey();

        String status = "Pending";
        AddProduct(Email, key, address, condition, f_a_d, f_a_t, t_a_d, t_a_t, status, categories, UserId, Username, UserImage);
    }

    private void AddProduct(final String Email, final String key, final String address, final String condition, final String f_a_d,
                            final String f_a_t, final String t_a_d, final String t_a_t, final String status, final String categories,
                            final String UserId, final String Username, final String UserImage) {
        double latitude = 0.0;
        double longtitude = 0.0;

        Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {

                latitude = addresses.get(0).getLatitude();
                longtitude = addresses.get(0).getLongitude();

                adminEmail = GetAdminEmail(latitude, longtitude, adminEmail);
            }
        } catch (Exception ee) {

        }


        databaseReference = FirebaseDatabase.getInstance().getReference("Product Info").child(key);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    if (imageUri != null) {
                        final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                                + "." + getFileExtension(imageUri));

                        mUploadTask = fileReference.putFile(imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri downloadUri) {
                                                link = downloadUri.toString();

                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("email", Email);
                                                hashMap.put("Uid", key);
                                                hashMap.put("address", address);
                                                hashMap.put("condition", condition);
                                                hashMap.put("FAD", f_a_d);
                                                hashMap.put("FAT", f_a_t);
                                                hashMap.put("TAD", t_a_d);
                                                hashMap.put("TAT", t_a_t);
                                                hashMap.put("status", status);
                                                hashMap.put("categories", categories);
                                                hashMap.put("imageUrl", link);
                                                hashMap.put("UserId", UserId);
                                                hashMap.put("Username", Username);
                                                hashMap.put("UserImage", UserImage);
                                                hashMap.put("AdminEmail", adminEmail);

                                                databaseReference.updateChildren(hashMap);
                                                Clear();
                                                Toasty.success(getActivity(), "Upload successful", Toast.LENGTH_LONG, true).show();
                                                relativeLayout1.setVisibility(View.GONE);
                                                relativeLayout.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    }
                                });
                    }
                } else {
                    relativeLayout1.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    Toasty.success(getActivity(), "Problem Found!!!!!!!!", Toast.LENGTH_LONG, true).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private String GetAdminEmail(double latitude, double longtitude, String adminEmail) {

        List<Double> X_coordinate = new ArrayList<>();
        List<Double> Y_coordinate = new ArrayList<>();
        List<Double> coordinate = new ArrayList<>();

        double a[] = new double[3];
        a[0] = 2.641200;
        a[1] = 3.133040;
        a[2] = 1.514620;


        double b[] = new double[3];
        b[0] = 101.774100;
        b[1] = 101.684230;
        b[2] = 103.757400;

        for (int i = 0; i < a.length; i++) {
            double x = Math.abs(a[i] - latitude);
            X_coordinate.add(x);
        }

        for (int i = 0; i < a.length; i++) {
            double y = Math.abs(b[i] - longtitude);
            Y_coordinate.add(y);
        }

        for (int i = 0; i < X_coordinate.size(); i++) {
            double result = X_coordinate.get(i) + Y_coordinate.get(i);
            coordinate.add(result);
        }

        int index = coordinate.indexOf(Collections.min(coordinate));

        if (index == 0) {
            adminEmail = "negerisembilancenviro@gmail.com";
        }

        if (index == 1) {
            adminEmail = "klcenviro@gmail.com";
        }
        if (index == 2) {
            adminEmail = "johorcenviro@gmail.com";
        }


        return adminEmail;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void Clear(){
        circleImageView.setImageResource(R.drawable.image);
        date_1.setText("Select date");
        date_2.setText("Select date");
        time_1.setText("Select time");
        time_2.setText("Select time");
        autoCompleteTextView.setText("");
        spinner.setSelection(0);
        error.setError(null);
        date_1.setError(null);
        date_2.setError(null);
        time_1.setError(null);
        time_2.setError(null);
    }
}
