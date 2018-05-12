package com.example.mohamed.firebase;


import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;


public class Blog extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        firebaseAuth=FirebaseAuth.getInstance();

        setFragment(new BlogFragment());
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.main_nav);

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.home:
                        setFragment(new BlogFragment());
                        break;
                    case R.id.add:
                        setFragment(new AddPostFragment());
                        break;
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        setFragment(new BlogFragment());
                        return true;
                    case R.id.add:
                        setFragment(new AddPostFragment());
                        return  true;
                    case R.id.search:
                        setFragment(new SearchFragment());
                        return  true;
                    case R.id.love:
                        setFragment(new LovedPostFragment());
                        return  true;
                    case R.id.profile:
                        setFragment(new UserProfileFragment());
                        return  true;
                        default:
                            return false;
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.logout:
                firebaseAuth.signOut();
                break;


        }


        return super.onOptionsItemSelected(item);
    }

    private  void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

}
