package com.foodie.restaurantmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Orders extends AppCompatActivity {

    private ListView listView;
    private OrderAdapter mAdapter;
    ArrayList<Order> ordersList;
    private static final int EditACTIVITY_REQUEST_CODE = 0;
    private JSONArray orderResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        String json = MyJSON.getData(getBaseContext(),2);

        listView = (ListView) findViewById(R.id.orderList);

        updateListView();


        listView.setOnItemClickListener(new  AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Order setItem = (Order) listView.getItemAtPosition(position); //
                Integer val = setItem.getOrderID();
                Intent intent = new Intent(getBaseContext(), OrderDetail.class);
                intent.putExtra("id", val.toString());
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch(requestCode) {
//            case (EditACTIVITY_REQUEST_CODE) : {
//                if (resultCode == Activity.RESULT_OK) {
//                    // TODO Extract the data returned from the child Activity.
//                    Order item = (Order)data.getSerializableExtra("item");
//                    Integer id = Integer.parseInt(data.getStringExtra("id"));
//                    View v = listView.getChildAt(id-1);
//
//                    if(v == null)
//                        return;
//                    try {
//                        for (int i=0; i < orderResult.length(); i++){
//                            JSONObject itemArr = (JSONObject)orderResult.get(i);
//                            if(itemArr.get("orderID").equals(id)){
//                                itemArr.put("orderID", item.getOrderID());
//                                itemArr.put("customerName", item.getcustomerName());
//                                itemArr.put("status", item.getstatus());
//                                itemArr.put("notes", item.getnotes());
//                                itemArr.put("lunchTime", item.getlunchTime());
//                                itemArr.put("meals", item.getorder());
//                            }
//                        }
//                    }
//                    catch (JSONException e) {
//                        Log.e("MYAPP", "unexpected JSON exception", e);
//                    }
//                    MyJSON.saveData(getBaseContext(), orderResult.toString(),1);
//                    updateListView();
//                }
//                break;
//            }
//        }
//    }
    public void updateListView(){
        ordersList = new ArrayList<Order>();
        try {
            String json = MyJSON.getData(getBaseContext(),2);
            orderResult = new JSONArray(json);
            for (int i=0; i<orderResult.length(); i++) {
                JSONObject order = orderResult.getJSONObject(i);
                Integer orderID = order.getInt("orderID");
                String customerName = order.getString("customerName");
                String status = order.getString("status");
                String notes = order.getString("notes");
                String lunchTime = order.getString("lunchTime");
                String meals = order.getString("meals");
                ordersList.add(new Order(orderID, customerName, status, notes, lunchTime, meals));
            }
            mAdapter = new OrderAdapter(this,ordersList);
            listView.setAdapter(mAdapter);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
