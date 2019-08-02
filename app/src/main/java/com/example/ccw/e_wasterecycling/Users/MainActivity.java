package com.example.ccw.e_wasterecycling.Users;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccw.e_wasterecycling.Login;
import com.example.ccw.e_wasterecycling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CircleImageView imageView;
    TextView tvemail;
    Fragment fragment = null;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private List<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.add);

        Intent intent = getIntent();
        String email = firebaseUser.getEmail();

        View view = navigationView.getHeaderView(0);

        imageView = (CircleImageView) view.findViewById(R.id.imageView);
        tvemail = (TextView) view.findViewById(R.id.email);
        tvemail.setText("Hello " + email);

        userList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userList.add(String.valueOf(snapshot.getValue()));
                    }
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    Picasso.get().load(userList.get(3)).into(imageView);
                    System.out.println("haha image" + userList.get(3));

                } else {
                    Toasty.info(getApplicationContext(), "Data not exists " + tvemail.getText().toString().replace("Hello", ""), Toast.LENGTH_SHORT).show();
                }

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

        navigationView.setNavigationItemSelectedListener(this);

           showHome();
    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (fragment instanceof UserRequestFragment){
                super.onBackPressed();
            }
            else {
                showHome();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Toasty.info(getApplicationContext(), "Good bye " + tvemail.getText().toString().replace("Hello", ""), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

      private void showHome(){

          fragment = new UserRequestFragment();

          if (fragment != null){
              FragmentManager fragmentManager = getSupportFragmentManager();
              FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
              fragmentTransaction.add(R.id.frame_layout,fragment);
              fragmentTransaction.commit();
          }
      }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.profile) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", getIntent().getStringExtra("Email"));
            fragment = new UserProfile();
            fragment.setArguments(bundle);

        } else if (id == R.id.add) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", getIntent().getStringExtra("Email"));
            fragment = new UserRequestFragment();
            fragment.setArguments(bundle);

        } else if (id == R.id.view) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", getIntent().getStringExtra("Email"));
            fragment = new ViewUserRequestFragment();
            fragment.setArguments(bundle);

        } else if (id == R.id.view2) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", getIntent().getStringExtra("Email"));
            fragment = new ViewAcceptedRequestFragment();
            fragment.setArguments(bundle);

        } else if (id == R.id.chat) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", getIntent().getStringExtra("Email"));
            fragment = new UserChatFragment();
            fragment.setArguments(bundle);

        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}
