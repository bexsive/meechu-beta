package com.inertiamobility.meechu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ProfileActivity extends AppCompatActivity{

    private static final String TAG = "ProfileActivity";

    Button follow_button;
    User user;
    TextView userName, bio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Might not need to assign this to a user, can go straight into template from bundle. We'll see
        //Commented out until API user data is working
        Bundle bundle = getIntent().getExtras();

        Log.d(TAG, "Data Check " + bundle.getString("first_name"));
        Log.d(TAG, "Data Check " + bundle.getString("last_name"));
        Log.d(TAG, "Data Check " + bundle.getString("ID"));

        //user.setFirstName(bundle.getString("first_name"));
        //user.setLastName(bundle.getString("last_name"));
        //user.setId(bundle.getString("ID"));

        userName = findViewById(R.id.textUserName);
        userName.setText(bundle.getString("first_name") + " " + bundle.getString("last_name"));

        follow_button = findViewById(R.id.followButton);

        bio = findViewById(R.id.textUserBio);
        //bio.setText(user.getId());

        follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ProfileActivity.this, "Clicked Follow", Toast.LENGTH_LONG).show();

            }
        });
    }
}
