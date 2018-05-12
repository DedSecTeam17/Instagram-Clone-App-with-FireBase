package com.example.mohamed.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
private FirebaseAuth mFireAuth;
    private   EditText email;
    private  EditText password;
    private DatabaseReference mDbRefUser;
    private ProgressDialog progressDialog;
    private ImageButton googleButton;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "Message";

    private  FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.api_key))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        setContentView(R.layout.activity_login);
         email=(EditText)findViewById(R.id.textEmail);
         password=(EditText)findViewById(R.id.textPassword);
          Button login=(Button)findViewById(R.id.login);
        googleButton=(ImageButton) findViewById(R.id.googlebtn);

//

        mFireAuth=FirebaseAuth.getInstance();
        try {
            mDbRefUser.keepSynced(true);
        }catch (Exception e){
            e.printStackTrace();
        }

progressDialog=new ProgressDialog(this);

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() !=null){
                    Intent intent=new Intent(getApplicationContext(),Blog.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        mDbRefUser= FirebaseDatabase.getInstance().getReference().child("users");


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });




        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }
    private void signIn() {
        Intent signInIntent =  mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(mFireAuth.getCurrentUser() != null)
        { checkacconut();


        }

        mFireAuth.addAuthStateListener(mAuthListener);

    }

    private void  SignIn(){

        String email=this.email.getText().toString();
        String password=this.password.getText().toString();

if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
    progressDialog.setMessage("login..");
    progressDialog.show();
    mFireAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){
                checkacconut();
//                Toast.makeText(login.this,"email or address not valid ",Toast.LENGTH_LONG).show();
            }
//            else {
//                progressDialog.dismiss();
//                Toast.makeText(login.this,"Error ",Toast.LENGTH_LONG).show();
//            }
        }
    });
}else {
    progressDialog.dismiss();
    Toast.makeText(login.this,"Empity field not allowed ",Toast.LENGTH_LONG).show();

}

    }

    private void checkacconut() {
        final String uid= mFireAuth.getCurrentUser().getUid();
        mDbRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid)){
                    progressDialog.dismiss();
                    Intent intent=new Intent(getApplicationContext(),Blog.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(login.this,"welcome user ",Toast.LENGTH_LONG).show();
                }else {
                    progressDialog.dismiss();
//                    Toast.makeText(login.this,"setup an account ",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(getApplicationContext(),SetupActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void signOut() {
        // Firebase sign out
        mFireAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mFireAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFireAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFireAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getBaseContext(),"Failed",Toast.LENGTH_LONG).show();
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


}
