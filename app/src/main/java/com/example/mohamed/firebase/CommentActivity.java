package com.example.mohamed.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {
    private Button mAddComment;
    private EditText mCommentBody;
    private String mUserKey;
    private String mPostID;
    private DatabaseReference mCommentDataRef;
    private DatabaseReference mCurrentPostData;
    private DatabaseReference mOtherUserWhoCommentData;
    private CircleImageView mUserImage;
    private TextView mUserName;
    private TextView mPostBody;
    private CircleImageView mCurrentUseImage;
    private FirebaseRecyclerAdapter<comment_Data, holder> firebaseRecyclerAdapter;
    private RecyclerView recyclerView;
    private List<Integer> list;

    private int x = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mAddComment = (Button) findViewById(R.id.comment_add_comment);
        mCommentBody = (EditText) findViewById(R.id.comment_comment_body);
//
        mUserImage = (CircleImageView) findViewById(R.id.post_comment_user_image);
        mUserName = (TextView) findViewById(R.id.post_comment_user_name);
        mPostBody = (TextView) findViewById(R.id.post_comment_post_comment);


        mCurrentUseImage = (CircleImageView) findViewById(R.id.post_comment_current_user_image);
//        set Recycler view

        Bundle bundle = getIntent().getExtras();


        mPostID = bundle.getString("post_id");
        mUserKey = bundle.getString("user_id");
        mCommentDataRef = FirebaseDatabase.getInstance().getReference().child("comments").child(mPostID);
        mCurrentPostData = FirebaseDatabase.getInstance().getReference().child("Blog").child(mPostID);
        mOtherUserWhoCommentData = FirebaseDatabase.getInstance().getReference().child("users");
        Toast.makeText(getBaseContext(), mPostID + "\t" + mUserKey, Toast.LENGTH_LONG).show();

        recyclerView = (RecyclerView) findViewById(R.id.comments_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        mAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addComment(mPostID, mUserKey, mCommentBody.getText().toString());

            }
        });
// set current Post data
        setPostData();
//        set current user image for comment
        setImageForCurrentUser();

//        show all comment in current post
        Query myq = mCommentDataRef.orderByKey();
        FirebaseRecyclerOptions<comment_Data> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<comment_Data>()
                .setQuery(myq, comment_Data.class).
                        setLifecycleOwner(this)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<comment_Data, holder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final holder holder, int position, @NonNull comment_Data model) {

                holder.bindCommentBody(model.getComment());

                mOtherUserWhoCommentData.child(model.getUser_id()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("username").getValue().toString();
                        String imageuri = dataSnapshot.child("image").getValue().toString();
                        holder.bindUserName(name);
                        holder.bindImge(imageuri);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public holder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false));
            }
        };


        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    private void setImageForCurrentUser() {
        mOtherUserWhoCommentData.child(mUserKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                bindCurrentUserImage(dataSnapshot.child("image").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void bindCurrentUserImage(String image_uri) {
        Picasso.get().load(image_uri).into(mCurrentUseImage);

    }

    private void setPostData() {
        mCurrentPostData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("username").getValue().toString();
                String profile_pic = dataSnapshot.child("profile_pic").getValue().toString();
                String description = dataSnapshot.child("description").getValue().toString();
                Picasso.get().load(profile_pic).into(mUserImage);
                mUserName.setText(name);
                mPostBody.setText(description);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addComment(String post_id, String user_id, String comment_body) {
        Map<String, String> map = new HashMap();
        map.put("user_id", user_id);
        map.put("comment", comment_body);
        mCommentDataRef.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_LONG).show();

                }
            }
        });


    }


    class holder extends RecyclerView.ViewHolder {
        public View view;
        private CircleImageView mUserImage;
        private TextView mUserName;
        private TextView mCommentBody;


        public holder(View itemView) {
            super(itemView);
            this.view = itemView;
            mUserImage = (CircleImageView) itemView.findViewById(R.id.comment_user_image);
            mUserName = (TextView) itemView.findViewById(R.id.comment_username);
            mCommentBody = (TextView) itemView.findViewById(R.id.comment_body);


        }

        public void bindImge(String image_uri) {
            Picasso.get().load(image_uri).into(mUserImage);

        }

        public void bindCommentBody(String comment_body) {
            mCommentBody.setText(comment_body);

        }

        public void bindUserName(String username) {
            mUserName.setText(username);

        }


    }

    public static class comment_Data {

        private String comment;
        private String user_id;

        public comment_Data() {
        }

        public comment_Data(String comment, String user_id) {
            this.comment = comment;
            this.user_id = user_id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}
