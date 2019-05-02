package com.example.restaurantmanager;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton buttonEdit;
    private static final int EditACTIVITY_REQUEST_CODE = 0;
    public static final String Profile_data = "profile_data";
    private JSONObject profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        String json = MyJSON.getData(getBaseContext(),0);
        try{
            profile = new JSONObject(json);
            TextView nameTv = (TextView)findViewById(R.id.nameTv);
            TextView emailTv = (TextView)findViewById(R.id.emailTv);
            TextView phoneTv = (TextView)findViewById(R.id.phoneTv);
            TextView descriptionTv = (TextView)findViewById(R.id.descriptionTv);
            TextView addressTv = (TextView)findViewById(R.id.addressTv);
            TextView openhoursTv = (TextView)findViewById(R.id.openhoursTv);
            nameTv.setText(profile.getString("name"));
            emailTv.setText(profile.getString("email"));
            phoneTv.setText(profile.getString("phone"));
            descriptionTv.setText(profile.getString("description"));
            addressTv.setText(profile.getString("address"));
            openhoursTv.setText(profile.getString("openhours"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        buttonEdit = (ImageButton)findViewById(R.id.editButton);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        ImageView profImgBtn = (ImageView) findViewById(R.id.profImgBtn);
        loadImageFromStorage(directory.toString(), profImgBtn);

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
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                intent.putExtra("picturePath", directory.toString());
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
    public boolean onCreateOptionsMenu(Menu menu) {
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
            Intent intent = new Intent(getBaseContext(), com.example.restaurantmanager.Menu.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(getBaseContext(), Orders.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (EditACTIVITY_REQUEST_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    TextView nameTv = (TextView)findViewById(R.id.nameTv);
                    TextView emailTv = (TextView)findViewById(R.id.emailTv);
                    TextView phoneTv = (TextView)findViewById(R.id.phoneTv);
                    TextView descriptionTv = (TextView)findViewById(R.id.descriptionTv);
                    TextView addressTv = (TextView)findViewById(R.id.addressTv);
                    TextView openhoursTv = (TextView)findViewById(R.id.openhoursTv);
                    ImageView profImgBtn = (ImageView) findViewById(R.id.profImgBtn);
                    nameTv .setText(data.getStringExtra("nameTxt"));
                    emailTv .setText(data.getStringExtra("emailTxt"));
                    phoneTv .setText(data.getStringExtra("phoneTxt"));
                    descriptionTv .setText(data.getStringExtra("descriptionTxt"));
                    addressTv .setText(data.getStringExtra("addressTxt"));
                    openhoursTv .setText(data.getStringExtra("openhoursTxt"));
                    try {
                        profile.put("name", data.getStringExtra("nameTxt"));
                        profile.put("email",data.getStringExtra("emailTxt"));
                        profile.put("phone",data.getStringExtra("phoneTxt"));
                        profile.put("description",data.getStringExtra("descriptionTxt"));
                        profile.put("address",data.getStringExtra("addressTxt"));
                        profile.put("openhour",data.getStringExtra("openhoursTxt"));
                        loadImageFromStorage(data.getStringExtra("picturePath"), profImgBtn);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    MyJSON.saveData(getBaseContext(), profile.toString(),0);
                }
                break;
            }
        }
    }
    private void loadImageFromStorage(String path, ImageView imageView)
    {
        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}


