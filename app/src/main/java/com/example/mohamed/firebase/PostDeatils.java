package com.example.mohamed.firebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class PostDeatils extends AppCompatActivity {
    private TextView uname;
    private  TextView title;
    private  TextView desc;
    private ImageView postimage;

    private FirebaseAuth mFireAuth;

    private DatabaseReference mFireDb;
    private Button delete_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFireAuth=FirebaseAuth.getInstance();

        setContentView(R.layout.activity_details);

        Bundle bundle=getIntent().getExtras();

        assert bundle != null;
        final String postID=bundle.getString("post_id");


        Toast.makeText(getBaseContext(),postID,Toast.LENGTH_LONG).show();

        mFireDb= FirebaseDatabase.getInstance().getReference().child("Blog");



        uname=(TextView)findViewById(R.id.singlepost_username);
        title=(TextView)findViewById(R.id.singlepost_title);
        desc=(TextView)findViewById(R.id.singlepost_description);
        postimage=(ImageView)findViewById(R.id.singlepost_pic);
        delete_post=(Button)findViewById(R.id.delete_post);

        delete_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert postID != null;
                mFireDb.child(postID).removeValue();

                Intent intent=new Intent(PostDeatils.this,Blog.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });




        mFireDb.child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {



                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator );

                String description= null;
                if (map != null) {
                    description = map.get("description");
                    String _title=map.get("title");
                    String uid=map.get("uid");
                    String username=map.get("username");
                    String imageur=map.get("imageuri");

                    uname.setText(username);
                    desc.setText(description);
                    title.setText(_title);
                    Picasso.get().load(imageur).into(postimage);

                }







            }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });




    }
}
