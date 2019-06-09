package com.foodie.restaurantmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class OrderAdapter extends ArrayAdapter<Order> {

    private Context mContext;
    private List<Order> ordersList = new ArrayList<>();

    public OrderAdapter(@NonNull Context context, ArrayList<Order> list) {
        super(context, 0 , list);
        mContext = context;
        ordersList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.orderitems_layout,parent,false);

        Order currentorder = ordersList.get(position);

        TextView orderID = (TextView) listItem.findViewById(R.id.orderID);
        orderID.setText(currentorder.o_id);

        TextView customerName = (TextView) listItem.findViewById(R.id.customerName);
        customerName.setText(currentorder.o_id);

        TextView status = (TextView) listItem.findViewById(R.id.status);
        String[] statusString = { "Created", "Prepared", "Delivering", "Completed" };
        status.setText(statusString[currentorder.status]);

//        TextView notes = (TextView) listItem.findViewById(R.id.notes);
//        notes.setText(currentorder.getnotes());
//
//        TextView lunchTime = (TextView) listItem.findViewById(R.id.lunchTime);
//        lunchTime.setText(currentorder.getlunchTime());


        return listItem;
    }
}