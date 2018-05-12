package com.example.mohamed.firebase;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by mohamed on 4/7/2018.
 */

public class blog_services extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Task to

        return super.onStartCommand(intent, flags, startId);
    }
}
