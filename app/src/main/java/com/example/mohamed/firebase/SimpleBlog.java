package com.example.mohamed.firebase;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

/**
 * Created by mohamed on 4/2/2018.
 */

public class SimpleBlog extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        Firebase.setAndroidContext(this);


        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        builder.indicatorsEnabled(false);
        builder.loggingEnabled(true);
        Picasso.setSingletonInstance(built);



    }
}
