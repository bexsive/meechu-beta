package  com.inertiamobility.meechu;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import org.apache.commons.lang3.text.WordUtils;

public class BuildEventActivity extends AppCompatActivity implements TimePickerFragment.TheListener, DatePickerFragment.TheListener {
    TextView setStartDateButton, setEndDateButton, setStartTimeButton, setEndTimeButton;
    EditText eventNameEditText;
    Button setVenueButton, inviteFriendsButton, postEventButton;
    Event newEvent = new Event();
    private SharedPreferenceConfig preferenceConfig;
    //Map Places
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

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
        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_place_api));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
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

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                //TODO Event items needed
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(BuildEventActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
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

                newEvent.setName(WordUtils.capitalize(eventNameEditText.getText().toString()));
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
                Call<EventResponse> call = api.postEvent(headerMap,
                        newEvent );

                call.enqueue(new Callback<EventResponse>() {
                    @Override
                    public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {

                        Intent output = new Intent();
                        output.putExtra("Event ID", response.body().getData());
                        setResult(RESULT_OK, output);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<EventResponse> call, Throwable t) {
                        Toast.makeText(BuildEventActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
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
        //TODO update format
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(df);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                newEvent.setVenueName((String) place.getName());
                newEvent.setLat(Double.toString(place.getLatLng().latitude));
                newEvent.setLng(Double.toString(place.getLatLng().longitude));
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        } else {
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
        } else {
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
        } else{
            return true;
        }
    }
}
