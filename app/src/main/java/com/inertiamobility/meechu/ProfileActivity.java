package com.inertiamobility.meechu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProfileActivity extends AppCompatActivity{

    private static final String TAG = "ProfileActivity";
    private SharedPreferenceConfig preferenceConfig;

    Button follow_button;
    TextView userName, bio;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        Bundle bundle = getIntent().getExtras();

        user = new User();
        user.setFirstName(bundle.getString("first_name"));
        user.setLastName( bundle.getString("last_name"));
        user.setId(bundle.getString("ID"));

        userName = findViewById(R.id.textUserName);
        userName.setText(user.getFirstName() + " " + user.getLastName());

        follow_button = findViewById(R.id.followButton);

        bio = findViewById(R.id.textUserBio);
        //bio.setText("User ID is: " + bundle.getString("ID"));

        follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Api.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Api api = retrofit.create(Api.class);
                Call<JsonObject> call = api.followStatus(String.valueOf(preferenceConfig.readUserId()), user.getId());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        JsonObject responseMessage = response.body();

                        Log.e(TAG,"Message:" + responseMessage.get("message") );
                        String one = responseMessage.get("message").toString();
                        String two = "user";

                        if(one.contains(two)){
                            Toast.makeText(getApplicationContext(), "Unfollowed", Toast.LENGTH_LONG).show();
                            follow_button.setText("Follow");
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Following", Toast.LENGTH_LONG).show();
                            follow_button.setText("Unfollow");                        }

                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        //Testing
                        Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
