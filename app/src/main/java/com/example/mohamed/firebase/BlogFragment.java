package com.example.mohamed.firebase;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlogFragment extends Fragment {
        private RecyclerView mBlogRecycler;
        private FirebaseRecyclerAdapter<blogPost, holder> firebaseRecyclerAdapter;
        private Query databaseReference;
        private FirebaseAuth mFireAuth;
        private FirebaseAuth.AuthStateListener mFireAuthListener;
        private DatabaseReference mUserRef;
        private DatabaseReference mLikeRef;
        private StorageReference mStorgeLikeRef;
        private DatabaseReference mDataBaseCurrentUser;
        private Query mQueryCurrentUser;
        private boolean like = true;




        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            View mainView=inflater.inflate(R.layout.fragment_blog, container, false);




            mBlogRecycler = (RecyclerView) mainView.findViewById(R.id.myblog_list);

            mBlogRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            mBlogRecycler.setHasFixedSize(true);


            mFireAuth = FirebaseAuth.getInstance();
            mFireAuthListener = new FirebaseAuth.AuthStateListener() {

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        Intent signin = new Intent(getActivity(), Regestration.class);
                        signin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(signin);
                    }
                }
            };


//

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
            mUserRef = FirebaseDatabase.getInstance().getReference().child("users");
            mLikeRef = FirebaseDatabase.getInstance().getReference().child("likes");
            mStorgeLikeRef = FirebaseStorage.getInstance().getReference().child("likesimages");
            mDataBaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Blog");
            mUserRef.keepSynced(true);
            mLikeRef.keepSynced(true);

//        mQueryCurrentUser=databaseReference.orderByChild("username") .startAt(Searchdata).endAt(Searchdata+"\uf8ff");
            Query myq = databaseReference.orderByKey();
            FirebaseRecyclerOptions<blogPost> options = new FirebaseRecyclerOptions.Builder<blogPost>()
                    .setQuery(myq, blogPost.class)
                    .setLifecycleOwner(this)
                    .build();
            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<blogPost, holder>(options) {


                @Override
                public holder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_row, parent, false));
                }

                @NonNull
                @Override
                public blogPost getItem(int position) {

                    return super.getItem(getItemCount() - 1 - position);

                }


                @Override
                protected void onBindViewHolder(@NonNull final holder holder, final int position, @NonNull final blogPost model) {

                    final String post_key = getRef(position).getKey();


                    holder.bindDescription(model.getDescription());

                    holder.bindImageUri(model.getImageuri());
                    holder.bindProfileImageUri(model.getprofile_pic());
                    holder.bindUsername("@"+model.getUsername());
                    holder.bindLikeBtn(post_key);

                    mLikeRef.child(post_key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long count=dataSnapshot.getChildrenCount();

                            if (count==0){
                                holder.bindTitle("No likes");
                            }else  if (count==1){
                                holder.bindTitle("1 like");
                            }else if (count>=2){
                                holder.bindTitle(String.valueOf(dataSnapshot.getChildrenCount())+"likes");
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    holder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "now you are commenting new things in this post", Toast.LENGTH_LONG).show();
//user id
//                        post id
//                        intent to move into comment id
                            Intent intent = new Intent(getActivity(), CommentActivity.class);
                            intent.putExtra("user_id", mFireAuth.getUid());
                            intent.putExtra("post_id", post_key);
                            startActivity(intent);
                        }
                    });
                    holder._itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent topostdetail = new Intent(getActivity(), PostDeatils.class);
                            topostdetail.putExtra("post_id", post_key);
                            startActivity(topostdetail);
                        }
                    });
                    holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (like) {
                                mLikeRef.child(post_key).child(mFireAuth.getCurrentUser().getUid()).setValue("like your post");
                                Toast.makeText(getContext(), "like", Toast.LENGTH_LONG).show();
//                           holder.likeBtn.setImageResource(R.drawable.like);
                                like = false;
                            } else {

                                mLikeRef.child(post_key).child(mFireAuth.getCurrentUser().getUid()).removeValue();
//                           holder.likeBtn.setImageResource(R.drawable.dislike);
                                Toast.makeText(getContext(), "dislike", Toast.LENGTH_LONG).show();
                                like = true;

                            }


                        }
                    });


                }
            };
            mBlogRecycler.setAdapter(firebaseRecyclerAdapter);
            checkacconut();

            return mainView;
        }

        @Override
        public void onStart() {
            super.onStart();
            firebaseRecyclerAdapter.startListening();
            mFireAuth.addAuthStateListener(mFireAuthListener);
        }


        @Override
        public void onStop() {
            super.onStop();
            firebaseRecyclerAdapter.stopListening();
            mFireAuth.removeAuthStateListener(mFireAuthListener);
        }






        private void checkacconut() {
            final String uid = mFireAuth.getCurrentUser().getUid();
            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(uid)) {
//                    progressDialog.dismiss();
                        Intent intent = new Intent(getActivity(), SetupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "welcome user ", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        private void logotu() {
            mFireAuth.signOut();
        }

        public static String EncodeString(String string) {
            return string.replace(".", ",");
        }

        public static String DecodeString(String string) {
            return string.replace(",", ".");
        }

        class holder extends RecyclerView.ViewHolder {
            TextView title;
            TextView description;
            ImageView image_post;
            CircleImageView profile_image;
            TextView username;
            ImageButton likeBtn;
            ImageView comment;
            FirebaseAuth mFireAuth;
            DatabaseReference mDataLikeRef;

            View _itemView;

            holder(View itemView) {
                super(itemView);
                this._itemView = itemView;
                title = (TextView) itemView.findViewById(R.id.post_title);
                description = (TextView) itemView.findViewById(R.id.post_description);
                image_post = (ImageView) itemView.findViewById(R.id.post_image);
                profile_image = (CircleImageView) itemView.findViewById(R.id.user_profo_image);
                username = (TextView) itemView.findViewById(R.id.uname);
                likeBtn = (ImageButton) itemView.findViewById(R.id.likebutton);
                comment = (ImageView) itemView.findViewById(R.id.add_comment);
                mFireAuth = FirebaseAuth.getInstance();
                mDataLikeRef = FirebaseDatabase.getInstance().getReference().child("likes");

                mDataLikeRef.keepSynced(true);


            }

            public void bindTitle(String _title) {
                title.setText(_title);


            }

            public void bindDescription(String desc) {
                description.setText(desc);

            }

            public void bindImageUri(String imageuri) {
                Picasso.get().load(imageuri).into(image_post);


            }

            public void bindProfileImageUri(String imageuri) {
                Picasso.get().load(imageuri).into(profile_image);
            }

            public void bindUsername(String uname) {
                username.setText(uname);

            }

            public void bindLikeBtn(final String post_id) {
                mDataLikeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(post_id).hasChild(mFireAuth.getCurrentUser().getUid())) {
//                        mLikeRef.child(post_id).child(mFireAuth.getCurrentUser().getUid()).removeValue();
                            likeBtn.setImageResource(R.drawable.like);
//                        Toast.makeText(getBaseContext(),"Deleted",Toast.LENGTH_LONG).show();
                        } else {
//                        mLikeRef.child(post_id).child(mFireAuth.getCurrentUser().getUid()).setValue("like your post");
//                        Toast.makeText(getBaseContext(),"Like added",Toast.LENGTH_LONG).show();
                            likeBtn.setImageResource(R.drawable.dislike);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        }
    }








