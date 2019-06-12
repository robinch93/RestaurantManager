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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class EditRestaurantAct extends Activity {

    private Button saveBtn;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private ImageButton imageView;
    private Bitmap photo;
    public static final String Profile_data = "profile_data";
    String[] openhours = { "From - To", "9am - 11am", "12pm - 5pm", "6pm - 10pm"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit);
        EditText nameTxt = (EditText)findViewById(R.id.nameTxt);
        EditText emailTxt = (EditText)findViewById(R.id.emailTxt);
        EditText phoneTxt = (EditText)findViewById(R.id.phoneTxt);
        EditText descriptionTxt = (EditText)findViewById(R.id.descriptionTxt);
        EditText addressTxt = (EditText)findViewById(R.id.addressTxt);
        Spinner openhoursTxt = (Spinner) findViewById(R.id.spinner1);
        imageView = (ImageButton) findViewById(R.id.profImgBtn);
        nameTxt.setText(getIntent().getStringExtra("nameTv"));
        emailTxt.setText(getIntent().getStringExtra("emailTv"));
        phoneTxt.setText(getIntent().getStringExtra("phoneTv"));
        descriptionTxt.setText(getIntent().getStringExtra("descriptionTv"));
        addressTxt.setText(getIntent().getStringExtra("addressTv"));
//        openhoursTxt.setSelected(true);
        String compareValue = getIntent().getStringExtra("openhoursTv");

        String image = getIntent().getStringExtra("picturePath");
        Log.v("picturepath", "" + image);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://foodie-mad.appspot.com/");
        if(image != null && !image.equals("")){
            storageRef.child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter<String> aa1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,openhours);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        openhoursTxt.setAdapter(aa1);
        if (compareValue != null) {
            int spinnerPosition = aa1.getPosition(compareValue);
            openhoursTxt.setSelection(spinnerPosition);
        }

        saveBtn = (Button)findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent mainAct= new Intent(EditRestaurantAct.this,MainActivity.class);
//                startActivity(mainAct);
                Intent resultIntent = new Intent();
                // TODO Add extras or a data URI to this intent as appropriate.
                EditText nameTxt = (EditText)findViewById(R.id.nameTxt);
                EditText emailTxt = (EditText)findViewById(R.id.emailTxt);
                EditText phoneTxt = (EditText)findViewById(R.id.phoneTxt);
                EditText descriptionTxt = (EditText)findViewById(R.id.descriptionTxt);
                EditText addressTxt = (EditText)findViewById(R.id.addressTxt);
                Spinner openhoursTxt = (Spinner)findViewById(R.id.spinner1);
                resultIntent.putExtra("nameTxt", nameTxt.getText().toString());
                resultIntent.putExtra("emailTxt", emailTxt.getText().toString());
                resultIntent.putExtra("phoneTxt", phoneTxt.getText().toString());
                resultIntent.putExtra("descriptionTxt", descriptionTxt.getText().toString());
                resultIntent.putExtra("addressTxt", addressTxt.getText().toString());
                resultIntent.putExtra("openhoursTxt", openhoursTxt.getSelectedItem().toString());
                String r_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String imageName = "images/restaurant/"+ r_id + ".jpg";
                resultIntent.putExtra("picturePath", imageName);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

//        ImageButton photoButton = (ImageButton) this.findViewById(R.id.profImgBtn);
        imageView.setOnClickListener(new View.OnClickListener() {
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
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            }
        });
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            uploadFile(photo);
            ImageView profImgBtn = (ImageView) findViewById(R.id.profImgBtn);
            profImgBtn.setImageBitmap(photo);
        }
    }

    private void uploadFile(Bitmap bitmap) {
        String r_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String imageName = "images/restaurant/"+ r_id + ".jpg";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://foodie-mad.appspot.com/");
        StorageReference mountainImagesRef = storageRef.child(imageName);
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
                Log.d("downloadUrl-->", "abc" );
            }
        });
    }
}
