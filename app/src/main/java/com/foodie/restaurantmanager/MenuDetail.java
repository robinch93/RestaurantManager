package com.foodie.restaurantmanager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MenuDetail extends AppCompatActivity {

    private Button saveBtn;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private ImageButton menuImgBtn;
    private String imageName;
    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_details);
        Intent intent = getIntent();
        Meal item = (Meal)intent.getSerializableExtra("item");
        final Integer id = Integer.parseInt(intent.getStringExtra("id"));
        menuImgBtn = (ImageButton) findViewById(R.id.menuImgBtn);


        EditText menuNameTxt = (EditText)findViewById(R.id.menuNameTxt);
        EditText menuDescText = (EditText)findViewById(R.id.menuDescText);
        EditText menuPriceTxt = (EditText)findViewById(R.id.menuPriceTxt);
        EditText menuQtyTxt = (EditText)findViewById(R.id.menuQtyTxt);
//        imageView = (ImageButton) findViewById(R.id.profImgBtn);
        menuNameTxt.setText(item.getmenuName());
        menuDescText.setText(item.getmenuDesc());
        menuPriceTxt.setText(item.getmenuPrice().toString());
        menuQtyTxt.setText(item.getmenuQty().toString());
        imageName = item.getmenuImg();
        Log.v("image",imageName);
        if(imageName.startsWith("s_")){
            loadImageFromStorage(imageName, menuImgBtn);
        }else{
            int resID = getResources().getIdentifier(imageName , "drawable", getPackageName());
            menuImgBtn.setImageResource(resID);
        }

        saveBtn = (Button)findViewById(R.id.saveButton);

        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Intent mainAct= new Intent(EditRestaurantAct.this,MainActivity.class);
//                startActivity(mainAct);
                Intent resultIntent = new Intent();
                //        ImageButton menuImgBtn = (ImageButton) findViewById(R.id.menuImgBtn);
                EditText menuNameTxt = (EditText)findViewById(R.id.menuNameTxt);
                EditText menuDescText = (EditText)findViewById(R.id.menuDescText);
                EditText menuPriceTxt = (EditText)findViewById(R.id.menuPriceTxt);
                EditText menuQtyTxt = (EditText)findViewById(R.id.menuQtyTxt);
                Meal newItem = new Meal(id,imageName,menuNameTxt.getText().toString(), menuDescText.getText().toString(), Double.parseDouble(menuPriceTxt.getText().toString()), Integer.parseInt(menuQtyTxt.getText().toString()));
                resultIntent.putExtra("id",id.toString());
                resultIntent.putExtra("item", newItem);
//                resultIntent.putExtra("picturePath", getIntent().getStringExtra("picturePath"));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();

            }
        });

        menuImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 23) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    } else {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        Intent cameraIntent = new Intent(Intent.ACTION_PICK,
//                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//            Uri selectedImage = data.getData();
//            menuImgBtn.setImageURI(selectedImage);
            photo = (Bitmap) data.getExtras().get("data");
            saveToInternalStorage(photo);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            loadImageFromStorage(imageName, menuImgBtn);
        }
    }
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        imageName = "s_" + imageName;
        File mypath=new File(directory,imageName);
        Log.v("Image",mypath.toString());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void loadImageFromStorage(String imageName, ImageView imageView)
    {
        try {
            ContextWrapper cw = new ContextWrapper(getBaseContext());
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
