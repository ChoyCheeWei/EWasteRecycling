package com.example.ccw.e_wasterecycling.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccw.e_wasterecycling.Login;
import com.example.ccw.e_wasterecycling.R;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class Admins extends AppCompatActivity {

    MenuItem prevMenuItem;
    AcceptRequestFragment acceptRequestFragment;
    AcceptedRequestFragment acceptedRequestFragment;
    ChatFragment chatFragment;
    TextView admin;
    //This is our viewPager
    private ViewPager viewPager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_accept_request:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_accepted_request:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.navigation_chat:
                    viewPager.setCurrentItem(2);
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        admin = (TextView) findViewById(R.id.admin);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {

                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setupViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        Intent intent = getIntent();
        String email = intent.getStringExtra("Email");

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Admins.this, Login.class));

            Toasty.info(getApplicationContext(), "Good bye " + email, Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString("Email", getIntent().getStringExtra("Email"));
        acceptRequestFragment = new AcceptRequestFragment();
        acceptRequestFragment.setArguments(bundle);

        acceptedRequestFragment = new AcceptedRequestFragment();
        acceptedRequestFragment.setArguments(bundle);

        chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);

        adapter.addFragment(acceptRequestFragment);
        adapter.addFragment(acceptedRequestFragment);
        adapter.addFragment(chatFragment);
        viewPager.setAdapter(adapter);
    }

}



