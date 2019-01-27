package com.inertiamobility.meechu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.Objects.*;

public class SearchFriendsActivity extends AppCompatActivity {

    EditText searchBar;
    Button searchButton;
    private static final String TAG = "SearchFriendsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Api.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Api api = retrofit.create(Api.class);
                Call<ResponseUser> call = api.findUsers(searchBar.getText().toString());
                call.enqueue(new Callback<ResponseUser>() {
                    @Override
                    public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {

                        ResponseUser responseUser = response.body();

                        Log.d(TAG, "Data Check " + responseUser.user.getFirstName());
                        Log.d(TAG, "Data Check " + responseUser.user.getLastName());
                        Log.d(TAG, "Data Check " + responseUser.user.getId());
                        // Start new activity passing User Info
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("first_name", responseUser.user.getFirstName());
                        bundle.putString("last_name", responseUser.user.getLastName());
                        bundle.putString("ID", responseUser.user.getId());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onFailure(Call<ResponseUser> call, Throwable t) {
                        //Testing
                        Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();


                    }
                });
            }
        });
    }
}


