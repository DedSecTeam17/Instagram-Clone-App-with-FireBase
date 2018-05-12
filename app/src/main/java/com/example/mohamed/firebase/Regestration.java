package com.example.mohamed.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Regestration extends AppCompatActivity {
    private EditText username;
    private   EditText email;
    private  EditText password;
    private Button register;
    private Button login;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference mDbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regestration);
        username=(EditText) findViewById(R.id.user_name);
        password=(EditText) findViewById(R.id.password);
        email=(EditText) findViewById(R.id.Textemail);
        register=(Button)findViewById(R.id.register);
        login=(Button)findViewById(R.id.login);

mAuth=FirebaseAuth.getInstance();
        mDbRef= FirebaseDatabase.getInstance().getReference().child("users");
        progressDialog=new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Regestration.this,login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _email=email.getText().toString();
                String  _password=password.getText().toString();
                progressDialog.setMessage("registering...");
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(_email,_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            DatabaseReference adduserinfo=mDbRef.child(mAuth.getCurrentUser().getUid());
                            adduserinfo.child("username").setValue(username.getText().toString());
                            adduserinfo.child("image").setValue("ddd");
                            progressDialog.dismiss();

                            Intent intent=new Intent(Regestration.this,SetupActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(),task.getResult().toString(),Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
