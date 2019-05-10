package com.foodie.restaurantmanager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MenuDetail extends AppCompatActivity {

    private Button saveBtn;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private ImageButton menuImgBtn;
    private String imageName;
    private Bitmap photo;
    private DatabaseReference mDatabase;
    private String m_id;
    private String restaurantid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_details);
        Intent intent = getIntent();
        mDatabase = FirebaseDatabase.getInstance().getReference("restaurants");
        m_id = intent.getStringExtra("m_id");
        restaurantid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        final Integer id = 1;
        menuImgBtn = (ImageButton) findViewById(R.id.menuImgBtn);
        if (!m_id.equals("-1")){
            Meal item = (Meal)intent.getSerializableExtra("item");
            EditText menuNameTxt = (EditText)findViewById(R.id.menuNameTxt);
            EditText menuDescText = (EditText)findViewById(R.id.menuDescText);
            EditText menuPriceTxt = (EditText)findViewById(R.id.menuPriceTxt);
            EditText menuQtyTxt = (EditText)findViewById(R.id.menuQtyTxt);
            menuNameTxt.setText(item.getmenuName());
            menuDescText.setText(item.getmenuDesc());
            menuPriceTxt.setText(item.getmenuPrice().toString());
            menuQtyTxt.setText(item.getmenuQty().toString());
            imageName = item.getmenuImg();
            Log.v("menuimg", "" + imageName);
            if(imageName !=null){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://foodie-mad.appspot.com/");
                storageRef.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        ImageView profImgBtn = (ImageView) findViewById(R.id.profImgBtn);
                        new DownloadImageTask(profImgBtn).execute(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            } else{
                int resID = getResources().getIdentifier(imageName , "drawable", getPackageName());
                menuImgBtn.setImageResource(resID);
            }
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
                imageName = "images/r_"+restaurantid+"/" + m_id + ".jpg";
                String restaurantid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(m_id.equals("-1")){
                    DatabaseReference pushedPostRef = mDatabase.child(restaurantid).child("items").push();
                    m_id = pushedPostRef.getKey();
                    Log.v("m_idinside", m_id);
                    resultIntent.putExtra("id",m_id);
                } else{
                    resultIntent.putExtra("id",m_id.toString());
                }
                Meal newItem = new Meal(m_id,imageName,menuNameTxt.getText().toString(), menuDescText.getText().toString(), Double.parseDouble(menuPriceTxt.getText().toString()), Integer.parseInt(menuQtyTxt.getText().toString()));
                resultIntent.putExtra("item", newItem);
                mDatabase.child(restaurantid).child("items").child(m_id).setValue(newItem);
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
            uploadFile(photo);
            menuImgBtn.setImageBitmap(photo);
        }
    }
    private void uploadFile(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://foodie-mad.appspot.com/");
        if(m_id.equals("-1")) {
            DatabaseReference pushedPostRef = mDatabase.child(restaurantid).child("items").push();
            m_id = pushedPostRef.getKey();
        }
        StorageReference mountainImagesRef = storageRef.child("images/r_"+restaurantid+"/" + m_id + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Log.d("downloadUrl-->", "a" );
            }
        });

    }
    private void loadImage(String imageName) throws MalformedURLException {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReferenceFromUrl("gs://foodie-mad.appspot.com/");
//        StorageReference mountainImagesRef = storageRef.child(imageUrl);
//        Uri imageUri = mountainImagesRef.getDownloadUrl().getResult();
        URL url = new URL(imageName);
        try {
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            menuImgBtn.setImageBitmap(bmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
