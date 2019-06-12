package com.foodie.restaurantmanager;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderDetail extends AppCompatActivity {
    private static final String TAG = "OrderDetails";

    private ListView listView;
    private MealAdapter mAdapter;
    ArrayList<Meal> mealsList;
    private static final int RiderACTIVITY_REQUEST_CODE = 0;
    private String  mealResultString;
    private DatabaseReference mDatabase;
    private Order order;
    private Customer customer;
    private Restaurant restaurant;
    private LatLng restaurantLatLng;
    private LatLng customerLatLng;
    private Float distance;

    Button selectButton;
    Button pickedButton;
    TextView status;
    String[] statusString = { "Created", "Prepared", "Delivering", "Completed" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);

        Intent intent = getIntent();
        order = (Order)intent.getSerializableExtra("item");
        TextView orderID = (TextView)findViewById(R.id.orderID);
        TextView customerName = (TextView)findViewById(R.id.customerName);
        status = (TextView)findViewById(R.id.status);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        TextView comments = (TextView)findViewById(R.id.comments);
        if(order.rating != null){
            comments.setText(order.rating.comment);
            String stars = (order.rating.stars != null) ? order.rating.stars.toString() : "0.0";
            ratingBar.setRating(Float.parseFloat(stars));
        }
//        imageView = (ImageButton) findViewById(R.id.profImgBtn);
        orderID.setText(order.o_id);
        customerName.setText(order.customerName);
        status.setText(statusString[order.status]);

        selectButton = (Button)findViewById(R.id.selectButton);
        pickedButton = (Button)findViewById(R.id.pickedButton);
        // status of Order
        if(order.status == 0){
            selectButton.setVisibility(View.VISIBLE);
        } else if(order.status == 1){
            pickedButton.setVisibility(View.VISIBLE);
        } else if(order.status == 2){
            // do nothing
        } else if(order.status == 3){
            TextView commentsText = (TextView)findViewById(R.id.commentsText);
            TextView ratingsText = (TextView)findViewById(R.id.ratingsText);
            commentsText.setVisibility(View.VISIBLE);
            ratingsText.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
            comments.setVisibility(View.VISIBLE);
        }
//        mealResultString = item.items.toString();
//        updateListView();

        listView = (ListView) findViewById(R.id.list);
        getItems();

        getRestaurant(order.r_id);
        getCustomer(order.c_id);


        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SearchRider_map.class);
                startActivityForResult(intent, RiderACTIVITY_REQUEST_CODE);
            }
        });

        pickedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  get distance btw two points
                float[] results = new float[1];
                Location.distanceBetween(restaurantLatLng.latitude, restaurantLatLng.longitude,
                        customerLatLng.latitude, customerLatLng.longitude,
                        results);
                distance = results[0];
                Log.d(TAG, "distance:" + results[0]);
                order.status = 2;
                mDatabase = FirebaseDatabase.getInstance().getReference("orders");
                mDatabase.child(order.o_id).child("status").setValue(order.status);
                mDatabase.child(order.o_id).child("distance").setValue(distance);
                pickedButton.setVisibility(View.GONE);
                status.setText(statusString[order.status]);
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
    private void getCustomer(String c_id ){
        mDatabase = FirebaseDatabase.getInstance().getReference("customers");
        mDatabase.child(c_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                customer = snapshot.getValue(Customer.class);
                Log.d(TAG ,"customer.c_id: "+customer.c_id);
                customerLatLng = getLocationFromAddress(customer.address);
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());
            }
        });
    }
    private void getRestaurant(String r_id ){
        mDatabase = FirebaseDatabase.getInstance().getReference("restaurants");
        mDatabase.child(r_id).child("profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                restaurant = snapshot.getValue(Restaurant.class);
                Log.d(TAG ,"restaurant.r_id: "+restaurant.r_id);
                restaurantLatLng = getLocationFromAddress(restaurant.address);
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());
            }
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
                    order.status = 1;
                    Log.d("OrderDetails d_id:",order.r_id + " ");
                    mDatabase = FirebaseDatabase.getInstance().getReference("orders");
                    mDatabase.child(order.o_id).child("d_id").setValue(order.d_id);
                    mDatabase.child(order.o_id).child("status").setValue(order.status);
                    selectButton.setVisibility(View.GONE);
                    pickedButton.setVisibility(View.VISIBLE);
                    status.setText(statusString[order.status]);
                }
                break;
            }
        }
    }
    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;
        LatLng p1 = null;
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }
}
