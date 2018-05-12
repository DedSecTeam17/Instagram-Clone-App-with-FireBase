package com.example.mohamed.firebase;

import android.*;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import static android.app.Activity.RESULT_OK;


public class AddPostFragment extends Fragment {
    private Button post;
    private ImageButton selectImage;
    private EditText title;
    private EditText description;
    private Uri imageUri = null;
    private StorageReference mStorgeRef;
    private DatabaseReference mDataBaseRef;
    private ProgressDialog mProgressD;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDbRef;

    private static final int SELECT_PIC = 2;
    private static final int TAKE_IMAGE_PERMISSION = 670;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mainView=inflater.inflate(R.layout.fragment_add_post, container, false);

        post = (Button) mainView.findViewById(R.id.post);
        selectImage = (ImageButton) mainView.findViewById(R.id.imagepost);
        title = (EditText) mainView.findViewById(R.id.title);
        description = (EditText) mainView.findViewById(R.id.description);

        mStorgeRef = FirebaseStorage.getInstance().getReference();
        mDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Blog");
        mProgressD = new ProgressDialog(getActivity());


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDbRef = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid());
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(new CharSequence[]{"PICK AN IMAGE ", "TAKE AN IMAGE"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, SELECT_PIC);
                                break;

                            case 1:
                                check_for_permission();
                                break;
                        }

                    }
                });
                builder.setTitle("Choose your image");
                AlertDialog alertDialog=builder.create();
                alertDialog.show();

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
        return mainView;

    }


    private void startPosting() {
        mProgressD.setMessage("Posting");
        mProgressD.show();


        final String title = this.title.getText().toString().trim();
        final String description = this.description.getText().toString().trim();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && imageUri != null) {
            StorageReference sRef = mStorgeRef.child("plog").child(imageUri.getLastPathSegment());

            sRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloded_image_uri = taskSnapshot.getDownloadUrl();
                    final DatabaseReference posting = mDataBaseRef.push();

//            posting.child("name").setValue(mUser.getDisplayName());


                    mDbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            posting.child("title").setValue(title);
                            posting.child("description").setValue(description);
                            posting.child("imageuri").setValue(downloded_image_uri.toString());
                            posting.child("uid").setValue(mUser.getUid());
                            posting.child("profile_pic").setValue(dataSnapshot.child("image").getValue());
                            posting.child("username").setValue(dataSnapshot.child("username").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(getActivity(), Blog.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mProgressD.dismiss();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Can not upload thos post", Toast.LENGTH_LONG).show();
                }
            });

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PIC) {
            Uri uri = data.getData();
            imageUri = uri;
//            selectImage.setImageURI(uri);
            CropImage.activity(uri)
                    .setAspectRatio(3, 3)
                    .setAutoZoomEnabled(false)
                    .start(getActivity());
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    this.imageUri = resultUri;
                    selectImage.setImageURI(uri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case TAKE_IMAGE_PERMISSION: {
                check_for_permission();
            }
        }
    }

    private void askForPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, TAKE_IMAGE_PERMISSION);
    }

    private void check_for_permission() {
        int respone_permission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA);

        if (respone_permission == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.setType("image/*");
            startActivityForResult(intent, SELECT_PIC);
        } else {
            askForPermission();

        }
    }
}
