package com.foodie.restaurantmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.lang.ref.WeakReference;
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
        final WeakReference<ImageView> imageViewReference = new WeakReference<ImageView>(imageView);;
        Log.d("meal", "mealadapter called");
        String mDrawableName = currentmeal.getmenuImg();
        if(mDrawableName !=null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://foodie-mad.appspot.com/"  );
            storageRef.child(mDrawableName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png''
                    ImageView imageView1 = imageViewReference.get();
                    Glide.with(mContext)
                            .load(uri.toString())
                            .into(imageView1);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
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
}