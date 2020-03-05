package com.foodie.restaurantmanager;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Pratik.
 */
public class MyJSON {

    static String fileName = "myFile.json";
    static String[] files = {"profile","meals","orders"};
    static String profile = "{name:'Happy Meal', email: 'hm@gmail.com', description: 'Food and chips.', phone:'3411223344', address:'via Android 123 Centro'," +
            "openhours: '12pm - 5pm'  }";
    static String meals ="[" +
            "{id:1,menuImg:'milkshake',menuName:'milkshake',menuDesc:'hot milkshake',menuPrice:10.0,menuQty:10}," +
            "{id:2,menuImg:'blueberries',menuName:'blueberries',menuDesc:'clean blueberries',menuPrice:10.0,menuQty:10}," +
            "{id:3,menuImg:'oranges',menuName:'juices',menuDesc:'pure orange juice',menuPrice:10.0,menuQty:10}," +
            "{id:4,menuImg:'pizza',menuName:'pizza',menuDesc:'margerita',menuPrice:10.0,menuQty:10}," +
            "{id:5,menuImg:'turkey',menuName:'turkey biryani',menuDesc:'rice rice',menuPrice:10.0,menuQty:10}," +
            "{id:9,menuImg:'hamburger',menuName:'hamburger',menuDesc:'hot hamburger',menuPrice:10.0,menuQty:10}" +
            "]";
    static String orders = "[" +
            "{orderID:1,customerName:'Panther',status:1,lunchTime:'10:00',notes:'Add extra chips', meals:[" +
            "{id:1,menuImg:'milkshake',menuName:'milkshake',menuDesc:'hot milkshake',menuPrice:10.0,menuQty:10}," +
            "{id:3,menuImg:'oranges',menuName:'juices',menuDesc:'pure orange juice',menuPrice:10.0,menuQty:10}]}," +
            "{orderID:1,customerName:'Panther',status:2,lunchTime:'10:00',notes:'Add extra chips', meals:[" +
            "{id:1,menuImg:'milkshake',menuName:'milkshake',menuDesc:'hot milkshake',menuPrice:10.0,menuQty:10}," +
            "{id:3,menuImg:'oranges',menuName:'juices',menuDesc:'pure orange juice',menuPrice:10.0,menuQty:10}]}," +
            "{orderID:1,customerName:'Panther',status:3,lunchTime:'10:00',notes:'Add extra chips', meals:[" +
            "{id:1,menuImg:'milkshake',menuName:'milkshake',menuDesc:'hot milkshake',menuPrice:10.0,menuQty:10}," +
            "{id:3,menuImg:'oranges',menuName:'juices',menuDesc:'pure orange juice',menuPrice:10.0,menuQty:10}]}" +
            "]";


    //  0= profile, 1= meal, 2= orders
    public static void saveData(Context context, String mJsonResponse, Integer num) {
        try {
            FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + files[num]);
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
    }

    public static String getData(Context context, Integer num) {
        try {
            File f = new File(context.getFilesDir().getPath() + "/" + files[num]);
            f.createNewFile();
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String result = new String(buffer);
            if( result.isEmpty())
            {
                switch(num) {
                    case 0:
                        saveData(context, profile, num);
                        result = profile;
                        break;
                    case 1:
                        saveData(context, meals, num);
                        result = meals;
                        break;
                    case 2:
                        saveData(context, orders, num);
                        result = orders;
                        break;
                }
            }
            return result;
        } catch (IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }
}