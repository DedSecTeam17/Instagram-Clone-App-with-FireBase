package com.example.mohamed.firebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
private  Firebase mRef;
    private ArrayList<String> list=new ArrayList();
    private FirebaseAuth mFireauth;
   private ListView listView;

  private   FirebaseListAdapter<String>  myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendData=(Button)findViewById(R.id.send_data);


//    final     EditText child=(EditText)findViewById(R.id.child);
        final  EditText value=(EditText)findViewById(R.id.value);

        final TextView textView=(TextView)findViewById(R.id.mytext);


         listView=(ListView)findViewById(R.id.mylist);
mFireauth=FirebaseAuth.getInstance();

        final ArrayAdapter<String> arrayadapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(arrayadapter);

//
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference("server/saving-data/fireblog");
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        mRef=new Firebase("https://fir-dbdd7.firebaseio.com/");
     final    Query databaseReference=FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseListOptions<String> options = new FirebaseListOptions.Builder<String>()
                .setQuery(databaseReference, String.class)
                .setLayout(R.layout.row)
                .build();


  myAdapter = new FirebaseListAdapter<String>(options) {
            @Override
            protected void populateView(View view, String s, int i) {
                TextView text = (TextView) view.findViewById(R.id.item1);
                text.setText(s);
            }


        };

        listView.setAdapter(myAdapter);

//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
////                String item=dataSnapshot.getValue(String.class);
////                list.add(item);
////                arrayadapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



//                Map<String,String> map=(Map<String, Object>) dataSnapshot.getValue();

//                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
//                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator );
//
//                String name=map.get("age");
//                String age=map.get("name");
//
//                Log.i("user name:",name);
//                Log.i("age :",age);


//                String name=dataSnapshot.getValue(String.class);
//                Map<String,String> mydata=dataSnapshot.getValue(Map.class);


//                Log.i("my data","Data :"+dataSnapshot.getValue());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String _child=child.getText().toString();
//                String _value=value.getText().toString();


                mFireauth.signOut();


//                databaseReference.child(_child).setValue(_value);

//                databaseReference.push().setValue(_value);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        myAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myAdapter.stopListening();
    }

    class  USER{
        private String name;
        private String age;

        public String getName() {
            return name;
        }

        public String getAge() {
            return age;
        }

        public USER(String name, String age) {
            this.name = name;
            this.age = age;

        }
    }
}
