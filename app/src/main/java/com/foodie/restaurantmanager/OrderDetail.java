package com.foodie.restaurantmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderDetail extends AppCompatActivity {


    private ListView listView;
    private MealAdapter mAdapter;
    ArrayList<Meal> mealsList;
    private String  mealResultString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);

        Intent intent = getIntent();
        Order item = (Order)intent.getSerializableExtra("item");
        final Integer id = Integer.parseInt(intent.getStringExtra("id"));
        TextView orderID = (TextView)findViewById(R.id.orderID);
        TextView customerName = (TextView)findViewById(R.id.customerName);
        TextView status = (TextView)findViewById(R.id.status);
//        imageView = (ImageButton) findViewById(R.id.profImgBtn);
        orderID.setText(item.getOrderID().toString());
        customerName.setText(item.getcustomerName());
        status.setText(item.getstatus().toString());
        mealResultString = item.getmeals();

        listView = (ListView) findViewById(R.id.list);

        updateListView();



        ImageButton backButton = (ImageButton)this.findViewById(R.id.btnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void updateListView(){
        mealsList = new ArrayList<>();
        try {
            JSONArray  mealResult = new JSONArray(mealResultString);
            for (int i=0; i<mealResult.length(); i++) {
                JSONObject meal = mealResult.getJSONObject(i);
                Integer id = meal.getInt("id");
                String menuImg = meal.getString("menuImg");
                String menuName = meal.getString("menuName");
                String menuDesc = meal.getString("menuDesc");
                Double menuPrice = meal.getDouble("menuPrice");
                Integer menuQty = meal.getInt("menuQty");
                mealsList.add(new Meal(id, menuImg, menuName, menuDesc, menuPrice, menuQty));
            }
            mAdapter = new MealAdapter(this,mealsList);
            listView.setAdapter(mAdapter);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
