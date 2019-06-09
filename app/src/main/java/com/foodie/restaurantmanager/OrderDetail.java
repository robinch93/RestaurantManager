package com.foodie.restaurantmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class OrderDetail extends AppCompatActivity {
    private ListView listView;
    private MealAdapter mAdapter;
    ArrayList<Meal> mealsList;
    private static final int RiderACTIVITY_REQUEST_CODE = 0;
    private String  mealResultString;
    private DatabaseReference mDatabase;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);

        Intent intent = getIntent();
        order = (Order)intent.getSerializableExtra("item");
        final Integer id = Integer.parseInt(intent.getStringExtra("id"));
        TextView orderID = (TextView)findViewById(R.id.orderID);
        TextView customerName = (TextView)findViewById(R.id.customerName);
        TextView status = (TextView)findViewById(R.id.status);
//        imageView = (ImageButton) findViewById(R.id.profImgBtn);
        orderID.setText(order.o_id);
        customerName.setText(order.customerName);
        String[] statusString = { "Created", "Prepared", "Delivering", "Completed" };
        status.setText(statusString[order.status]);
//        mealResultString = item.items.toString();
//        updateListView();

        listView = (ListView) findViewById(R.id.list);
        getItems();

        Button saveBtn = (Button)findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SearchRider_map.class);
                startActivityForResult(intent, RiderACTIVITY_REQUEST_CODE);
            }
        });

        ImageButton backButton = (ImageButton)this.findViewById(R.id.btnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    public void getItems(ArrayList<Meal> mealsList){
////        mealsList = new ArrayList<>();
//        mAdapter = new MealAdapter(getApplicationContext(),mealsList);
//        listView.setAdapter(mAdapter);
//    }

    public void getItems(){
        mealsList = new ArrayList<>();
        mAdapter = new MealAdapter(getApplicationContext(),mealsList);
        listView.setAdapter(mAdapter);
        mDatabase = FirebaseDatabase.getInstance().getReference("restaurants");
        mDatabase.child(order.r_id).child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Meal meal=dataSnapshot.getValue(Meal.class);
                for (Meal m: order.items){
                    if(m.m_id.equals(meal.m_id)){
                        mealsList.add(meal);
                        mAdapter.notifyDataSetChanged();
                    }
                }
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (RiderACTIVITY_REQUEST_CODE) : {
                Log.d("OrderDetails:", " ");
                if (resultCode == Activity.RESULT_OK) {
                    order.d_id = data.getStringExtra("d_id");
                    Log.d("OrderDetails d_id:",order.r_id + " ");
                    mDatabase = FirebaseDatabase.getInstance().getReference("orders");
                    mDatabase.child(order.o_id).child("d_id").setValue(order.d_id);
                    mDatabase.child(order.o_id).child("status").setValue(1);
                }
                break;
            }
        }
    }
}
