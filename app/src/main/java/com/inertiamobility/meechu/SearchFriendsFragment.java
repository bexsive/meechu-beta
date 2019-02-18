package com.inertiamobility.meechu;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

public class SearchFriendsFragment extends Fragment {

    EditText searchBar;
    Button searchButton, contactsSearchButton;
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
            contactsSearchButton = view.findViewById(R.id.contacts_search_button);



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

            contactsSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    get_Contacts();

                }
            });

        return view;
    }

    public void get_Contacts(){
//        ArrayList<AndroidContact> contactArrayList = new ArrayList<AndroidContact>();
//
//        Cursor cursor = null;
//        ContentResolver contentResolver = getActivity().getContentResolver();
//        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null, null);
//        if (cursor.getCount() > 0 ) {
//            while (cursor.moveToNext()) {
//                AndroidContact androidContact = new AndroidContact();
//                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                String contact_display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//
//                androidContact.android_Contact_Name = contact_display_name;
//
//                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
//                if (hasPhoneNumber > 0 ) {
//                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
//                            , null
//                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
//                            , new String[]{contact_id}
//                            , null);
//                    while (phoneCursor.moveToNext()) {
//                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        androidContact.android_Contact_phoneNumber = phoneNumber;
//                    }
//                    phoneCursor.close();
//                }
//
//                contactArrayList.add(androidContact);
//            }
//        }
//        cursor.close();
//        if (contactArrayList.size() > 0) {
//            for (int i = 0; i < contactArrayList.size(); i++){
//                Log.d(TAG, contactArrayList.get(i).android_Contact_Name + ": " + contactArrayList.get(i).android_Contact_phoneNumber);
//            }
//        }
//
//        if (contactArrayList.size() > 0) {
//            for (int i = 0; i < contactArrayList.size(); i++){
//                Log.d(TAG, contactArrayList.get(i).android_Contact_Name + ": " + contactArrayList.get(i).android_Contact_phoneNumber);
//            }
//        }

        //Phone Numbers Only
        List<String> number = new ArrayList<String>();

        Cursor cursor = null;
        ContentResolver contentResolver = getActivity().getContentResolver();
        cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null, null);
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
                        number.add(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

                    }
                    phoneCursor.close();
                }
            }
        }
        cursor.close();

        if (number.size() > 0) {
            for (int i = 0; i < number.size(); i++){
                Log.d(TAG, number.get(i));
            }
        }

    }
    public class AndroidContact {
        public String android_Contact_Name = "";
        public String android_Contact_phoneNumber = "";
        public int android_contact_ID = 0;

        public AndroidContact(String android_Contact_Name, String android_Contact_phoneNumber, int android_contact_ID) {
            this.android_Contact_Name = android_Contact_Name;
            this.android_Contact_phoneNumber = android_Contact_phoneNumber;
            this.android_contact_ID = android_contact_ID;
        }

        public AndroidContact() {
        }
    }
}


