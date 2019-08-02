package com.example.ccw.e_wasterecycling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccw.e_wasterecycling.Admin.Admins;
import com.example.ccw.e_wasterecycling.Users.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;
    private static final String PREFS_NAME = "PreshFile";
    EditText emailText, passwordText;
    Button loginButton;
    TextView signupLink, forget;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences sharedPreferences;
    private RelativeLayout relativeLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);

        signupLink = (TextView) findViewById(R.id.link_signup);
        forget = (TextView) findViewById(R.id.forget_pass);
        firebaseAuth = FirebaseAuth.getInstance();

        saveLoginCheckBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        getPreferencesData();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
                // ...
            }
        };

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPassword();
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginUser(v);
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }

    private void getPreferencesData() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sharedPreferences.contains("pref_email")) {
            String email = sharedPreferences.getString("pref_email", "not found");
            emailText.setText(email.toString());
        }
        if (sharedPreferences.contains("pref_pass")) {
            String pass = sharedPreferences.getString("pref_pass", "not found");
            passwordText.setText(pass.toString());
        }
        if (sharedPreferences.contains("pref_checked")) {
            Boolean boo = sharedPreferences.getBoolean("pref_checked", false);
            saveLoginCheckBox.setChecked(boo);
        }
    }

    public void loginUser(View v) {
        final String Email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        relativeLayout.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(Email)) {
            emailText.setError("Email is required");
            relativeLayout.setVisibility(View.INVISIBLE);
        }
        if (TextUtils.isEmpty(password)) {
            passwordText.setError("password is required");
            relativeLayout.setVisibility(View.INVISIBLE);
        }
        if ((!TextUtils.isEmpty(Email)) && (!TextUtils.isEmpty(password))) {
            mAuth.signInWithEmailAndPassword(Email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                relativeLayout.setVisibility(View.INVISIBLE);
                                Toasty.error(getApplicationContext(), "Login Failed",
                                        Toast.LENGTH_SHORT, true).show();
                            } else {
                                relativeLayout.setVisibility(View.INVISIBLE);
                                if (password.length() == 6) {
                                    Toasty.success(getApplicationContext(), "Login Success",
                                            Toast.LENGTH_SHORT, true).show();
                                    Intent intent = new Intent(Login.this, Admins.class);
                                    intent.putExtra("Email", emailText.getText().toString());
                                    startActivity(intent);

                                } else if (password.length() > 7) {
                                    Toasty.success(getApplicationContext(), "Login Success",
                                            Toast.LENGTH_SHORT, true).show();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.putExtra("Email", emailText.getText().toString());
                                    startActivity(intent);
                                }

                                if (saveLoginCheckBox.isChecked()) {
                                    Boolean boo = saveLoginCheckBox.isChecked();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("pref_email", emailText.getText().toString());
                                    editor.putString("pref_pass", passwordText.getText().toString());
                                    editor.putBoolean("pref_checked", boo);
                                    editor.apply();

                                } else {
                                    sharedPreferences.edit().clear().apply();
                                }

                            }

                        }
                    });
        }
    }

    public void ForgetPassword() {

        final String Email = emailText.getText().toString();

        if (TextUtils.isEmpty(Email)) {
            Toasty.warning(getApplicationContext(), "Please enter your email address for reset password", Toast.LENGTH_SHORT).show();
        }
        if (!TextUtils.isEmpty(Email)) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(Email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toasty.info(getApplicationContext(), "Kindly Check Your Email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
