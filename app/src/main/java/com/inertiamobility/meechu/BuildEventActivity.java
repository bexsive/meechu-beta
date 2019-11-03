package  com.inertiamobility.meechu;
import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuildEventActivity extends AppCompatActivity implements TimePickerFragment.TheListener, DatePickerFragment.TheListener {
    TextView setStartDateButton, setEndDateButton, setStartTimeButton, setEndTimeButton;
    EditText eventNameEditText;
    Button setVenueButton, inviteFriendsButton, postEventButton;
    Event newEvent = new Event();
    private SharedPreferenceConfig preferenceConfig;

    //Pictures
    Bitmap bitmap;
    ImageView img;

    static int startDateYear, startDateMonth, startDateDay, startTimeHour, startTimeMin, endDateYear, endDateMonth, endDateDay, endTimeHour, endTimeMin;

    private static final String TAG = "BuildEvent";
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int IMG_REQUEST = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_event);

        eventNameEditText = findViewById(R.id.eventName);
        setStartDateButton = findViewById(R.id.setStartDate);
        setEndDateButton = findViewById(R.id.setEndDate);
        setStartTimeButton = findViewById(R.id.setStartTime);
        setEndTimeButton = findViewById(R.id.setEndTime);
        setVenueButton = findViewById(R.id.setVenue);
        inviteFriendsButton = findViewById(R.id.inviteFriends);
        postEventButton = findViewById(R.id.postEvent);

        //pictures
        img = findViewById(R.id.image);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        setTimeButtons();
        init();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    private void setTimeButtons(){

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        calendar.set(Calendar.MINUTE, 0);

        setTime(setStartTimeButton, calendar);
        setDate(setStartDateButton, calendar, 0);
        timeHelper(calendar, 0);

        calendar.add(Calendar.HOUR, 1);
        setTime(setEndTimeButton, calendar);
        setDate(setEndDateButton, calendar, 1);
        timeHelper(calendar, 1);
    }

    private void init() {

        setStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("DATE",1);

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);

                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        setEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("DATE",2);

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        setStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("TIME",1);

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);

                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        setEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("TIME",2);

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "timePicker");

            }
        });

        setVenueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(BuildEventActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace ();
                }
            }
        });

        inviteFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //selectImage();
                Toast.makeText(BuildEventActivity.this, "The feature is not ready yet " , Toast.LENGTH_LONG).show();


            }
        });

        postEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                newEvent.setName(eventNameEditText.getText().toString());
                newEvent.setUserId(String.valueOf(preferenceConfig.readUserId()));

                newEvent.setStartTime(componentTimeToTimestamp(startDateYear, startDateMonth, startDateDay, startTimeHour, startTimeMin));
                newEvent.setEndTime(componentTimeToTimestamp(endDateYear, endDateMonth, endDateDay, endTimeHour, endTimeMin));

                // Event Name input validation
                if (newEvent.getName().equals("")){
                    Toast.makeText(BuildEventActivity.this, "An Event has no name " , Toast.LENGTH_LONG).show();
                    return;
                }
                // Event time/date input validation
                if (!startBeforeEnd()){
                    Toast.makeText(BuildEventActivity.this, "When? Event Ends before it begins " , Toast.LENGTH_LONG).show();
                    return;
                }
                // Event location input validation
                if(newEvent.getVenueName() == null || newEvent.getLat() == null || newEvent.getLng() == null){
                    Toast.makeText(BuildEventActivity.this, "Where? Select a location " , Toast.LENGTH_LONG).show();
                    return;

                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Api.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Api api = retrofit.create(Api.class);
                HashMap<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                Call<ResponseBody> call = api.postEvent(headerMap,
                        newEvent );

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            String json = response.body().toString();
                            Log.d(TAG, "onResponse: json: " + json);
                            JSONObject data = new JSONObject(json);
                        }catch (JSONException e){
                            Log.e(TAG, "onResponse: JSONException: " + e.getMessage() );
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(BuildEventActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
                //TODO:  Make a loading, and check for response before closing activity
                finish();

            }
        });

    }
    @Override
    public void returnStartDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        startDateYear = year;
        startDateMonth = month;
        startDateDay = day;
        setDate(setStartDateButton, cal, 0);

    }
    @Override
    public void returnEndDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        endDateYear = year;
        endDateMonth = month;
        endDateDay = day;
        setDate(setEndDateButton, cal, 1);
    }

    @Override
    public void returnStartTime(int hour, int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        startTimeHour = hour;
        startTimeMin = min;
        setTime(setStartTimeButton, cal);
    }
    @Override
    public void returnEndTime(int hour, int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);

        endTimeHour = hour;
        endTimeMin = min;
        setTime(setEndTimeButton, cal);
    }

    @TargetApi(Build.VERSION_CODES.N)
    String componentTimeToTimestamp(int year, int month, int day, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute, 0);
        long dv = Long.valueOf(c.getTimeInMillis());// its need to be in milisecond
        Date df = new Date(dv);
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US).format(df);
    }


    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                newEvent.setVenueName((String) place.getName());
                newEvent.setLat(Double.toString(place.getLatLng().latitude));
                newEvent.setLng(Double.toString(place.getLatLng().longitude));
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null){
           Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                img.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Helpers

    public void setTime(TextView textview, Calendar calendar){
        SimpleDateFormat HHMMformat = new SimpleDateFormat("h:mm a");
        textview.setText("Time: " + HHMMformat.format(calendar.getTime()));
    }
    public void setDate(TextView textview, Calendar calendar,int flag) {
        SimpleDateFormat DDMMformat = new SimpleDateFormat("MMM d");;
        if (flag > 0) {
            textview.setText("End Date: " + DDMMformat.format(calendar.getTime()));
        }
        else {
            textview.setText("Start Date: " + DDMMformat.format(calendar.getTime()));
        }
    }
    public void timeHelper (Calendar cal, int flag){
        if(flag > 0) {
            endDateYear = cal.get(Calendar.YEAR);
            endDateMonth = cal.get(Calendar.MONTH);
            endDateDay = cal.get(Calendar.DAY_OF_MONTH);
            endTimeHour = cal.get(Calendar.HOUR_OF_DAY);
            endTimeMin = cal.get(Calendar.MINUTE);
        }
        else {
            startDateYear = cal.get(Calendar.YEAR);
            startDateMonth = cal.get(Calendar.MONTH);
            startDateDay = cal.get(Calendar.DAY_OF_MONTH);
            startTimeHour = cal.get(Calendar.HOUR_OF_DAY);
            startTimeMin = cal.get(Calendar.MINUTE);
        }
    }
    public boolean startBeforeEnd(){
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.set(Calendar.YEAR, startDateYear);
        startCal.set(Calendar.MONTH, startDateMonth);
        startCal.set(Calendar.DAY_OF_MONTH, startDateDay);
        startCal.set(Calendar.HOUR_OF_DAY, startTimeHour);
        startCal.set(Calendar.MINUTE, startTimeMin);

        endCal.set(Calendar.YEAR, endDateYear);
        endCal.set(Calendar.MONTH, endDateMonth);
        endCal.set(Calendar.DAY_OF_MONTH, endDateDay);
        endCal.set(Calendar.HOUR_OF_DAY, endTimeHour);
        endCal.set(Calendar.MINUTE, endTimeMin);

        if( startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            return false;
        }
        else{
            return true;
        }
    }
}
