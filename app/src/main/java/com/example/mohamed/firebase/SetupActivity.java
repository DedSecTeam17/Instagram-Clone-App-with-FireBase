package com.example.mohamed.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class SetupActivity extends AppCompatActivity {
    private ImageButton choose_profile;
    private EditText name;
    private Button contnue_register;
    private static final int  CHOOSE_IMAGE = 2;
    private  Uri uri=null;
    private StorageReference mSRf;
    private DatabaseReference mDbref;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private  FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        choose_profile=(ImageButton)findViewById(R.id.choose_profile);
        name=(EditText)findViewById(R.id.logical_name);
        contnue_register=(Button)findViewById(R.id.contnue);
progressDialog=new ProgressDialog(this);

      mSRf= FirebaseStorage.getInstance().getReference().child("users_profiles");
        mDbref= FirebaseDatabase.getInstance().getReference().child("users");
        mAuth=FirebaseAuth.getInstance();

        choose_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,CHOOSE_IMAGE);

            }
        });

        contnue_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startRegester();

            }
        });
    }



    private void startRegester() {

        final String name=this.name.getText().toString().trim();
        String id=mAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(name) && this.uri!=null){
            progressDialog.setMessage("wait...");
            progressDialog.show();
            final DatabaseReference start_put=mDbref.child(id);
            StorageReference uploadimage=mSRf.child(this.uri.getLastPathSegment());
            uploadimage.putFile(this.uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloaduri=taskSnapshot.getDownloadUrl();

                    start_put.child("username").setValue(name);
                    start_put.child("image").setValue(downloaduri.toString());
                    progressDialog.dismiss();
//                    move to your blog
                    Intent intent=new Intent(SetupActivity.this,Blog.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

        }else {
            Toast.makeText(getBaseContext(),"Empty field not allowed here",Toast.LENGTH_LONG).show();
        }




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CHOOSE_IMAGE){
            Uri uri=data.getData();
            CropImage.activity(uri)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                this.uri=resultUri;
                choose_profile.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
