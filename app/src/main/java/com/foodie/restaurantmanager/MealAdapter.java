package com.foodie.restaurantmanager;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class MealAdapter extends ArrayAdapter<Meal> {

    private Context mContext;
    private List<Meal> mealsList = new ArrayList<>();

    public MealAdapter(@NonNull Context context, ArrayList<Meal> list) {
        super(context, 0 , list);
        mContext = context;
        mealsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.menuitems_layout,parent,false);

        Meal currentmeal = mealsList.get(position);

        ImageView imageView = (ImageView)listItem.findViewById(R.id.menuImg);
        String mDrawableName = currentmeal.getmenuImg();
        if(mDrawableName.startsWith("s_")){
            loadImageFromStorage(currentmeal.getmenuImg(), imageView);
        }else{
            int resID = mContext.getResources().getIdentifier(mDrawableName , "drawable", mContext.getPackageName());
            imageView.setImageResource(resID);
        }
        TextView menuName = (TextView) listItem.findViewById(R.id.menuNameTv);
        menuName.setText(currentmeal.getmenuName());

        TextView menuDesc = (TextView) listItem.findViewById(R.id.menuDescTv);
        menuDesc.setText(currentmeal.getmenuDesc());

        TextView menuPrice = (TextView) listItem.findViewById(R.id.menuPriceTv);
        menuPrice.setText(currentmeal.getmenuPrice().toString());

        TextView menuQty = (TextView) listItem.findViewById(R.id.menuQtyTv);
        menuQty.setText(currentmeal.getmenuQty().toString());

        return listItem;
    }
    private void loadImageFromStorage(String imageName, ImageView imageView)
    {
        try {
            ContextWrapper cw = new ContextWrapper(getContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f=new File(directory.toString(),imageName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}