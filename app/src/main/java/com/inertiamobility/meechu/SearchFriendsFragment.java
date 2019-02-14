package com.inertiamobility.meechu;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFriendsFragment extends Fragment {

    EditText searchBar;
    Button searchButton;
    Context context;
    private static final String TAG = "SearchFriendsFragment";

    public SearchFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_friends, container, false);
            context = view.getContext();
            searchBar = view.findViewById(R.id.search_bar);
            searchButton = view.findViewById(R.id.search_button);

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
                            // Start new activity passing User Info
                            Intent intent = new Intent(context, ProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("first_name", responseUser.user.getFirstName());
                            bundle.putString("last_name", responseUser.user.getLastName());
                            bundle.putString("ID", responseUser.user.getId());
                            intent.putExtras(bundle);
                            startActivity(intent);

                        }

                        @Override
                        public void onFailure(Call<ResponseUser> call, Throwable t) {
                            //Testing
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show();


                        }
                    });
                }
            });

        return view;
    }
}


