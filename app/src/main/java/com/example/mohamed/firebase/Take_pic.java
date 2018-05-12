package com.example.mohamed.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Take_pic extends AppCompatActivity {

    private Button takePhoto;
    private StorageReference mStorgeRefrence;
    private static final int TAKE_PHOTO = 1;
    private ProgressDialog progress;
    private ImageView mypic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);

        mStorgeRefrence = FirebaseStorage.getInstance().getReferenceFromUrl("gs://myapp-cf934.appspot.com");

        progress = new ProgressDialog(this);
    mypic=(ImageView)findViewById(R.id.mypic);
        takePhoto = (Button) findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                startActivityForResult(intent, TAKE_PHOTO);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progress.setMessage("Uploading");
        progress.show();
        final Uri uri = data.getData();

        StorageReference storgeRef = mStorgeRefrence.child("myphoto").child(uri.getLastPathSegment());

        storgeRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_LONG).show();
                progress.dismiss();
                Uri downloadUri=taskSnapshot.getDownloadUrl();


                Picasso.get().load(uri).fit().centerCrop().into(mypic);

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "Fail to uploading", Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        });

    }


}
