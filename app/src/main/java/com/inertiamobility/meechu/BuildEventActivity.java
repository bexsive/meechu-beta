package  com.inertiamobility.meechu;
import android.Manifest;
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
    TextView textView, setStartDateButton, setEndDateButton, setStartTimeButton, setEndTimeButton;
    EditText eventNameEditText;
    Button setVenueButton, inviteFriendsButton, postEventButton;
    Event newEvent = new Event();
    private SharedPreferenceConfig preferenceConfig;

    //Pictures
    Bitmap bitmap;
    ImageView img;

    int startDateYear, startDateMonth, startDateDay, startTimeHour, startTimeMin, endDateYear, endDateMonth, endDateDay, endTimeHour, endTimeMin;

    private static final String TAG = "BuildEvent";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int IMG_REQUEST = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_event);

        textView = findViewById(R.id.buildEventHeader);
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
        roundToNextWholeHour(calendar);
        SimpleDateFormat HHMMformat = new SimpleDateFormat("h:mm a");
        setStartTimeButton.setText("Start Time: " + HHMMformat.format(calendar.getTime()));

        SimpleDateFormat DDMMformat = new SimpleDateFormat("MMM d");
        setStartDateButton.setText("Start Date: " + DDMMformat.format(calendar.getTime()));
        startDateYear = calendar.get(Calendar.YEAR);
        startDateMonth = calendar.get(Calendar.MONTH);
        startDateDay = calendar.get(Calendar.DAY_OF_MONTH);
        startTimeHour = calendar.get(Calendar.HOUR);
        startTimeMin = calendar.get(Calendar.MINUTE);

        roundToNextWholeHour(calendar);
        setEndTimeButton.setText("End Time: " + HHMMformat.format(calendar.getTime()));

        setEndDateButton.setText("End Date: " + DDMMformat.format(calendar.getTime()));

        endDateYear = calendar.get(Calendar.YEAR);
        endDateMonth = calendar.get(Calendar.MONTH);
        endDateDay = calendar.get(Calendar.DAY_OF_MONTH);
        endTimeHour = calendar.get(Calendar.HOUR);
        endTimeMin = calendar.get(Calendar.MINUTE);
    }
    public void roundToNextWholeHour(Calendar calendar) {
        calendar.add(Calendar.HOUR, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
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
                    e.printStackTrace();
                }
            }
        });

        inviteFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            selectImage();
            }
        });

        postEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEvent.setName(eventNameEditText.getText().toString());
                newEvent.setUserId(String.valueOf(preferenceConfig.readUserId()));

                newEvent.setStartTime(componentTimeToTimestamp(startDateYear, startDateMonth, startDateDay, startTimeHour, startTimeMin));
                newEvent.setEndTime(componentTimeToTimestamp(endDateYear, endDateMonth, endDateDay, endTimeHour, endTimeMin));

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
                finish();

            }
        });

    }
    @Override
    public void returnStartDate(int year, int month, int day) {
        startDateYear = year;
        startDateMonth = month;
        startDateDay = day;
        setStartDateButton.setText("Start Date: " + month + "/" + day);
        returnEndDate(year, month, day);
    }
    @Override
    public void returnEndDate(int year, int month, int day) {
        endDateYear = year;
        endDateMonth = month;
        endDateDay = day;
        setEndDateButton.setText("End Date: " + month + "/" + day);
    }

    @Override
    public void returnStartTime(int hour, int min) {
        startTimeHour = hour;
        startTimeMin = min;
        setStartTimeButton.setText("Start Time: " + hour + ":" + min);
        //TODO: Set end time to one hour later (need to account for am/pm, so use DATE object. Too tired now
    }
    @Override
    public void returnEndTime(int hour, int min) {
        endTimeHour = hour;
        endTimeMin = min;
        setEndTimeButton.setText("End Time: " + hour + ":" + min);
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
}
