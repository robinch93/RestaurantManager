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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        String json = MyJSON.getData(getBaseContext(),1);
        mDatabase = FirebaseDatabase.getInstance().getReference("restaurants");
        String restaurantid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                    // TODO Extract the data returned from the child Activity.
                    Meal item = (Meal)data.getSerializableExtra("item");
                    Integer id = Integer.parseInt(data.getStringExtra("id"));
                    View v = listView.getChildAt(id-1);

                    if(v == null)
                        return;
                    try {
                        for (int i=0; i < mealResult.length(); i++){
                            JSONObject itemArr = (JSONObject)mealResult.get(i);
                            Log.v("image1:82",id +": " + itemArr.get("id"));
                            if(itemArr.get("id").equals(id)){
                                Log.v("image1:83",item.getmenuImg());
                                itemArr.put("menuImg", item.getmenuImg());
                                itemArr.put("menuName", item.getmenuName());
                                itemArr.put("menuDesc", item.getmenuDesc());
                                itemArr.put("menuPrice", item.getmenuPrice());
                                itemArr.put("menuQty", item.getmenuQty());
                            }
                        }
                    }
                    catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                    }
                    MyJSON.saveData(getBaseContext(), mealResult.toString(),1);
                    updateListView();
//                    TextView someText = (TextView) v.findViewById(R.id.sometextview);
//                    someText.setText("Hi! I updated you manually!");
                }
                break;
            }
        }
    }
    public void updateListView(){
        mealsList = new ArrayList<>();
        try {
            String json = MyJSON.getData(getBaseContext(),1);
            mealResult = new JSONArray(json);
            for (int i=0; i<mealResult.length(); i++) {
                JSONObject meal = mealResult.getJSONObject(i);
                Integer id = meal.getInt("id");
                String menuImg = meal.getString("menuImg");
                Log.v("image1",menuImg);
                String menuName = meal.getString("menuName");
                String menuDesc = meal.getString("menuDesc");
                Double menuPrice = meal.getDouble("menuPrice");
                Integer menuQty = meal.getInt("menuQty");
                mealsList.add(new Meal(id.toString(), menuImg, menuName, menuDesc, menuPrice, menuQty));
            }
            mAdapter = new MealAdapter(this,mealsList);
            listView.setAdapter(mAdapter);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void getItems(final String restaurantId){
        mealsList = new ArrayList<>();
        mDatabase.child(restaurantId).child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("meal", "onChildAdded:" + dataSnapshot.getKey());
                Meal meal=dataSnapshot.getValue(Meal.class);
                mealsList.add(meal);
                mAdapter = new MealAdapter(getApplicationContext(),mealsList);
                listView.setAdapter(mAdapter);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
