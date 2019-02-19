package com.inertiamobility.meechu;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.*;
import static android.content.pm.PackageManager.*;

public class SearchFriendsFragment extends Fragment {

    EditText searchBar;
    Button searchButton, contactsSearchButton;
    Context context;
    private static final String TAG = "SearchFriendsFragment";
    UserRecyclerViewAdapter adapter;

    //Contacts from Phone
    List<User> users = new ArrayList<>();
    private SharedPreferenceConfig preferenceConfig;
    String userID;


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
            contactsSearchButton = view.findViewById(R.id.contacts_search_button);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new UserRecyclerViewAdapter(getContext(), users);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();

        preferenceConfig = new SharedPreferenceConfig(context);

        searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = searchBar.getText().toString();

                    if ( number.length() == 10){
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(Api.BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        Api api = retrofit.create(Api.class);
                        Call<ResponseUser> call = api.findUsers(searchBar.getText().toString());
                        call.enqueue(new Callback<ResponseUser>() {
                            @Override
                            public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {

                                //TODO: Exception handling for no matches
                                ResponseUser responseUser = response.body();
                                // Start new activity passing User Info
                                if (responseUser.user == null){
                                    Toast.makeText(context, "Didn't find anyone", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Intent intent = new Intent(context, ProfileActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("first_name", responseUser.user.getFirstName());
                                    bundle.putString("last_name", responseUser.user.getLastName());
                                    bundle.putString("ID", responseUser.user.getId());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseUser> call, Throwable t) {
                                //Testing
                                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show();


                            }
                        });
                    }
                    else {
                        Toast.makeText(getContext(), "Number should be 10 digits", Toast.LENGTH_LONG).show();
                    }
                }
            });

            contactsSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(context,
                            READ_CONTACTS)
                            != PERMISSION_GRANTED) {

                        requestPermissions(new String[]{READ_CONTACTS}, 1);

                    } else {
                        // Permission has already been granted
                        updateList(get_Contacts());
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        }, 1000);
                    }

                }
            });

        return view;
    }

    public List<String> get_Contacts(){

        //TODO:  Check contacts permissions
        List<String> numbers = new ArrayList<>();

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null, null);
        if (cursor.getCount() > 0 ) {
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , null
                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                            , new String[]{contact_id}
                            , null);
                    while (phoneCursor.moveToNext()) {
                        numbers.add(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\D+", ""));
                        if (numbers.size() > 10) break;
                    }
                    phoneCursor.close();
                }
            }
        }
        cursor.close();

        //TODO: Filter list better in the future..IE if 11 chars, check if first is "1" (US nation code)
        List<String> result = new ArrayList<>();
        for (String number: numbers) {
            if (number.length() == 10) {
                result.add(number);
            }
        }
        Log.d(TAG, "get_Contacts: " + result);
        return result;
    }

    public void updateList(List<String> numbers){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        //pull user_id from shared preferences object
        userID = String.valueOf(preferenceConfig.readUserId());
        Log.d(TAG, userID);
        Call<UserList> call = api.addContacts(userID, numbers);

        call.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                UserList userList = response.body();
                users.clear();
                if (userList.users.size() == 0){
                    Toast.makeText(getContext(), "No new Users :/", Toast.LENGTH_LONG).show();
                }
                else {
                    for (int i = 0; i < userList.users.size(); i++) {
                        users.add(userList.users.get(i));
                    }
                }
            }
            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
              switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    updateList(get_Contacts());
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    }, 1000);
                }
                else {
                    Toast.makeText(context, "Permission denied to read your Contacts", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}


