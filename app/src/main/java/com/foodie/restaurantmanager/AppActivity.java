package com.foodie.restaurantmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AppActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private ImageButton buttonEdit;
    private static final int EditACTIVITY_REQUEST_CODE = 0;
    public static final String Profile_data = "profile_data";
    private DatabaseReference mDatabase;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // to set the colors of the pages.
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        ContextCompat.getColor(this, R.color.lightGreen);

        mDatabase = FirebaseDatabase.getInstance().getReference("restaurants");
        String restaurantid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getRestaurant(restaurantid);

        buttonEdit = (ImageButton)findViewById(R.id.editButton);

        buttonEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), EditRestaurantAct.class);
                ImageView profImgBtn = (ImageView) findViewById(R.id.profImgBtn);
                TextView nameTv = (TextView)findViewById(R.id.nameTv);
                TextView emailTv = (TextView)findViewById(R.id.emailTv);
                TextView phoneTv = (TextView)findViewById(R.id.phoneTv);
                TextView descriptionTv = (TextView)findViewById(R.id.descriptionTv);
                TextView addressTv = (TextView)findViewById(R.id.addressTv);
                TextView openhoursTv = (TextView)findViewById(R.id.openhoursTv);
                intent.putExtra("nameTv", nameTv.getText().toString());
                intent.putExtra("emailTv", emailTv.getText().toString());
                intent.putExtra("phoneTv", phoneTv.getText().toString());
                intent.putExtra("descriptionTv", descriptionTv.getText().toString());
                intent.putExtra("addressTv", addressTv.getText().toString());
                intent.putExtra("openhoursTv", openhoursTv.getText().toString());
                intent.putExtra("picturePath", restaurant.image);
                startActivityForResult(intent, EditACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
//            Toast.makeText(this, "Clicked item one", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {
//            Toast.makeText(this, "Clicked item two", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), com.foodie.restaurantmanager.Menu.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(getBaseContext(), Orders.class);
            startActivity(intent);
        } else if (id == R.id.nav_search) {
            if(isServicesOK()){
                Intent intent = new Intent(getBaseContext(), SearchRider_map.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isServicesOK(){
       // Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(AppActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
           // Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            //Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(AppActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (EditACTIVITY_REQUEST_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    ImageView profImgBtn = (ImageView) findViewById(R.id.profImgBtn);
                    restaurant.name = data.getStringExtra("nameTxt");
                    restaurant.email = data.getStringExtra("emailTxt");
                    restaurant.phone = data.getStringExtra("phoneTxt");
                    restaurant.description = data.getStringExtra("descriptionTxt");
                    restaurant.address = data.getStringExtra("addressTxt");
                    restaurant.openhours = data.getStringExtra("openhoursTxt");
                    restaurant.r_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    restaurant.image = data.getStringExtra("picturePath");
                    mDatabase.child(restaurant.r_id).child("profile").setValue(restaurant);
                    fillData(restaurant);
                }
                break;
            }
        }
    }
    public void fillData(Restaurant restaurant){
        TextView nameTv = (TextView)findViewById(R.id.nameTv);
        TextView emailTv = (TextView)findViewById(R.id.emailTv);
        TextView phoneTv = (TextView)findViewById(R.id.phoneTv);
        TextView descriptionTv = (TextView)findViewById(R.id.descriptionTv);
        TextView addressTv = (TextView)findViewById(R.id.addressTv);
        TextView openhoursTv = (TextView)findViewById(R.id.openhoursTv);
        nameTv.setText(restaurant.name);
        emailTv.setText(restaurant.email);
        phoneTv.setText(restaurant.phone);
        descriptionTv.setText(restaurant.description);
        addressTv.setText(restaurant.address);
        openhoursTv.setText(restaurant.openhours);
        Log.v("112restaurant", "" + restaurant.image);
        if(restaurant.image != null && !restaurant.image.equals("")){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://foodie-mad.appspot.com/");
            storageRef.child(restaurant.image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    ImageView profImgBtn = (ImageView) findViewById(R.id.profImgBtn);
                    Glide.with(getApplicationContext())
                            .load(uri.toString())
                            .into(profImgBtn);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }
    public void getRestaurant(final String restaurantId){
        ValueEventListener restaurantListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                restaurant = dataSnapshot.getValue(Restaurant.class);
                if(restaurant == null){
                    restaurant = new Restaurant(restaurantId, "", "", "", "", "", "", "","");
                    mDatabase.child(restaurant.r_id).child("profile").setValue(restaurant);
                } else{
                    Log.v("restaurant", restaurant.name);
                }
                fillData(restaurant);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child(restaurantId).child("profile").addListenerForSingleValueEvent(restaurantListener);
    }
}


