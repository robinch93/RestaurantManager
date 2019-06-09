package com.foodie.restaurantmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

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

public class Orders extends AppCompatActivity {

    private ListView listView;
    private OrderAdapter mAdapter;
    ArrayList<Order> ordersList;
    private static final int EditACTIVITY_REQUEST_CODE = 0;
    private DatabaseReference mDatabase;
    private String restaurantid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        String json = MyJSON.getData(getBaseContext(),2);
        mDatabase = FirebaseDatabase.getInstance().getReference("orders");
        restaurantid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        listView = (ListView) findViewById(R.id.orderList);

//        updateListView();
        getItems(restaurantid);

        listView.setOnItemClickListener(new  AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Order setItem = (Order) listView.getItemAtPosition(position); //
                String val = setItem.o_id;
                Intent intent = new Intent(getBaseContext(), OrderDetail.class);
                intent.putExtra("id", val);
                intent.putExtra("item", setItem);
                startActivity(intent);
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

    public void getItems(String restaurantId){
        ordersList = new ArrayList<>();
        mAdapter = new OrderAdapter(this,ordersList);
        listView.setAdapter(mAdapter);
        mDatabase.orderByChild("r_id").equalTo(restaurantId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Order order=dataSnapshot.getValue(Order.class);
                ordersList.add(order);
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
