package com.foodie.restaurantmanager;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    private ListView listView;
    private MealAdapter mAdapter;
    ArrayList<Meal> mealsList;
    private static final int EditACTIVITY_REQUEST_CODE = 0;
    private static final int AddACTIVITY_REQUEST_CODE = 1;
    private JSONArray mealResult;
    private DatabaseReference mDatabase;
    private String restaurantid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        mDatabase = FirebaseDatabase.getInstance().getReference("restaurants");
        restaurantid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        listView = (ListView) findViewById(R.id.menuList);

        getItems(restaurantid);
//        updateListView();

        listView.setOnItemClickListener(new  AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Meal setItem = (Meal) listView.getItemAtPosition(position); //
                Log.v("itemid", position  + " : " +id);
                Intent intent = new Intent(getBaseContext(), MenuDetail.class);
                intent.putExtra("m_id", setItem.getid());
                intent.putExtra("item", setItem);
                startActivityForResult(intent, EditACTIVITY_REQUEST_CODE);
            }
        });
        ImageButton backButton = (ImageButton)this.findViewById(R.id.btnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FloatingActionButton addButton = (FloatingActionButton)this.findViewById(R.id.button_addc);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MenuDetail.class);
                intent.putExtra("m_id", "-1");
                intent.putExtra("item", new Meal());
                startActivityForResult(intent, EditACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (EditACTIVITY_REQUEST_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    getItems(restaurantid);
                }
                break;
            }
        }
    }

    public void getItems(String restaurantId){
        mealsList = new ArrayList<>();
        mAdapter = new MealAdapter(getApplicationContext(),mealsList);
        listView.setAdapter(mAdapter);
        mDatabase.child(restaurantId).child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Meal meal=dataSnapshot.getValue(Meal.class);
                mealsList.add(meal);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}
