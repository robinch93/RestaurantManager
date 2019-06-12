package com.foodie.restaurantmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_app);
//    }
    private static final int RC_SIGN_IN = 0;
//    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build());
//                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build());


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            startActivity(new Intent(this, AppActivity.class));
//            finish();
            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference("restaurants");
            String r_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ValueEventListener restaurantListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Restaurant restaurant;
                    // Get Post object and use the values to update the UI
                    restaurant = dataSnapshot.getValue(Restaurant.class);
                    Log.d("AppActivity", "" + restaurant);
                    Intent i = new Intent(getApplicationContext(), AppActivity.class);
                    if(getIntent().hasExtra("o_id")){
                        String o_id = getIntent().getStringExtra("o_id");
                        i.putExtra("o_id",o_id);
                    }
                    i.putExtra("item", restaurant);
                    startActivity(i);
                    finish();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("Error", "loadPost:onCancelled", databaseError.toException());
                }
            };
            mDatabase.child(r_id).child("profile").addListenerForSingleValueEvent(restaurantListener);
        } else {
            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.restraunt)
                            .build(),
                    RC_SIGN_IN);
        }

        //Create Notification channel --hema
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        //to subscribe notifications
        /*FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        String msg = "Successfull";
                        if(!task.isSuccessful()){
                            msg="Failed";
                        }
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                    }
                });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //to test--hema
        //startActivity(new Intent(this, AppActivity.class));
        //finish();
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token
                        DatabaseReference mDatabase;
                        mDatabase = FirebaseDatabase.getInstance().getReference("restaurants");
                        String restaurantId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String token = task.getResult().getToken();
                        Log.d("Token", token + " ");
                        mDatabase.child(restaurantId).child("profile").child("token").setValue(token);
                        mDatabase.child(restaurantId).child("profile").child("r_id").setValue(restaurantId);
                        startActivity(new Intent(getApplicationContext(), AppActivity.class));
                        finish();
                    }
                });
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

}
