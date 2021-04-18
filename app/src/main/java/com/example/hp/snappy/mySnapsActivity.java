package com.example.hp.snappy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class mySnapsActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> users;
    ArrayList<DataSnapshot> snaps;
    ArrayAdapter<String> arrayAdapter;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_snaps);

        listView = (ListView)findViewById(R.id.listView);
        users = new ArrayList<>();
        snaps = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        arrayAdapter = new ArrayAdapter<String>(mySnapsActivity.this,android.R.layout.simple_list_item_1,users);

        listView.setAdapter(arrayAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://snap-pie-default-rtdb.firebaseio.com/");
        database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                users.add(dataSnapshot.child("from").getValue().toString());
                snaps.add(dataSnapshot);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                String key = dataSnapshot.getKey().toString();
                int index = 0;

                for(DataSnapshot snap : snaps)
                {
                    if(key.equals(snap.getKey().toString()))
                    {
                        snaps.remove(index);
                        users.remove(index);
                        break;
                    }
                    index++;
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                    DataSnapshot snapInfo = snaps.get(i);
                    Intent intent = new Intent(mySnapsActivity.this,displaySnapActivity.class);
                    intent.putExtra("imageUrl",snapInfo.child("imageUrl").getValue().toString());
                    intent.putExtra("imageName",snapInfo.child("imageName").getValue().toString());
                    intent.putExtra("imageKey",snapInfo.getKey());
                    startActivity(intent);


            }
        });
    }
}
