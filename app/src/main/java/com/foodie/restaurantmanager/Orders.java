package com.foodie.restaurantmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

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
import java.util.Collections;
import java.util.Comparator;

public class Orders extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ListView listView;
    private OrderAdapter mAdapter;
    ArrayList<Order> ordersList;
    private static final int EditACTIVITY_REQUEST_CODE = 0;
    private DatabaseReference mDatabase;
    private String restaurantid;

    private Integer statusFilter = 0;
    private Spinner spinner;
    //    private static final String[] paths = {"item 1", "item 2", "item 3"};
    String[] statusString = { "ALL", "Created", "Prepared", "Delivering", "Completed" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        String json = MyJSON.getData(getBaseContext(),2);
        mDatabase = FirebaseDatabase.getInstance().getReference("orders");
        restaurantid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        listView = (ListView) findViewById(R.id.orderList);

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,statusString);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        // if notification open selected
        Intent i = getIntent();
        if(i.hasExtra("o_id")){
            String o_id = i.getStringExtra("o_id");
            spinner.setSelection(2);
        }
//        updateListView();
//        getItems(restaurantid);

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
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        Log.d("DeliveryRequestActivity",  "-position: " + position);
        statusFilter = position;
        getItems(restaurantid);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    public void getItems(String restaurantId){
        ordersList = new ArrayList<>();
        mAdapter = new OrderAdapter(this,ordersList);
        listView.setAdapter(mAdapter);
        mDatabase.orderByChild("r_id").equalTo(restaurantId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Order order=dataSnapshot.getValue(Order.class);
                if(statusFilter == 0){
                    ordersList.add(order);
                } else if(statusFilter-1 == order.status){
                    ordersList.add(order);
                }
                Collections.sort(ordersList, new OrderTimeComparator());
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
    public class OrderTimeComparator implements Comparator<Order>
    {
        public int compare(Order left, Order right) {
            return right.orderTime.compareTo(left.orderTime);
        }
    }
}
