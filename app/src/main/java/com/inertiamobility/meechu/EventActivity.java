package com.inertiamobility.meechu;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventActivity extends AppCompatActivity {

    public static String TAG = "EventActivity";
    TextView eventNameText, venueNameText, startTimeText, distanceAwayText, attendingText;
    Button attendingButton, maybeButton, leaveButton;

    Event event;
    EventParticipant eventParticipant;

    SharedPreferenceConfig preferenceConfig;
    Boolean isChecked, isNew;

    //Date-Time format
    DateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        eventNameText = findViewById(R.id.eventNameText);
        venueNameText = findViewById(R.id.venueNameText);
        startTimeText = findViewById(R.id.startTimeText);
        distanceAwayText = findViewById(R.id.distanceAwayText);
        attendingText = findViewById(R.id.attendingText);

        attendingButton = findViewById(R.id.attendingButton);
        maybeButton = findViewById(R.id.maybeButton);
        leaveButton = findViewById(R.id.leaveButton);

        Bundle bundle = getIntent().getExtras();

        event = new Event();
        event.setId(bundle.getString("event_id"));
        event.setName(bundle.getString("event_name"));
        event.setVenueName( bundle.getString("venue_name"));
        event.setStartTime(bundle.getString("start_time"));
        event.setLat(bundle.getString("lat"));
        event.setLng(bundle.getString("lng"));

        eventNameText.setText(event.getName());
        venueNameText.setText(event.getVenueName());

        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
        try {
            startTimeText.setText(DateUtils.getRelativeTimeSpanString(dateFormat.parse(event.getStartTime()).getTime(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        distanceAwayText.setText(String.format("%.0f", haversine_mi(
                preferenceConfig.readUserLat(),
                preferenceConfig.readUserLng(), Double.valueOf(event.getLat()), Double.valueOf(event.getLng()))) + " Miles Away");

        isChecked = Boolean.FALSE;
        //Set Default
        isNew = Boolean.FALSE;
        eventParticipant = new EventParticipant();

        checkAttendance();

        attendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eventParticipant.setAttending("true");
                updateStatus(isNew);

            }
        });


        maybeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eventParticipant.setAttending("false");
                updateStatus(isNew);

            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: pop-up are you sure, then logic to check if anyone else is going, and delete event or pass admin permissions to next attendee (If none are already set)
                Toast.makeText(getApplicationContext(), "Can't delete events yet, upgrades coming", Toast.LENGTH_LONG).show();
            }
        });

    }

    double haversine_mi(double lat1, double long1, double lat2, double long2) {
        double d2r = (Math.PI / 180.0);
        double dlong = (long2 - long1) * d2r;
        double dlat = (lat2 - lat1) * d2r;
        double a = Math.pow(Math.sin(dlat/2.0), 2) + Math.cos(lat1*d2r) * Math.cos(lat2*d2r) * Math.pow(Math.sin(dlong/2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = 3956 * c;

        return d;
    }

    void checkAttendance(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);
        Call<ResponseEventParticipant> call = api.findEventParticipant(String.valueOf(preferenceConfig.readUserId()), event.getId());

        call.enqueue(new Callback<ResponseEventParticipant>() {
            @Override
            public void onResponse(Call<ResponseEventParticipant> call, Response<ResponseEventParticipant> response) {

                ResponseEventParticipant responseEventParticipant = response.body();
                isChecked = Boolean.TRUE;

                if (responseEventParticipant.eventParticipant == null){

                    //No existing event participant
                    isNew = Boolean.TRUE;
                    eventParticipant.setUserId(String.valueOf(preferenceConfig.readUserId()));
                    eventParticipant.setEventId(event.getId());
                    eventParticipant.setCreator("false");
                    eventParticipant.setAdmin("false");

                    //Change button text
                    attendingText.setText("Not Going");

                }
                else {
                    eventParticipant.setUserId(responseEventParticipant.eventParticipant.getUserId());
                    eventParticipant.setEventId(responseEventParticipant.eventParticipant.getEventId());
                    eventParticipant.setAttending(responseEventParticipant.eventParticipant.getAttending());
                    eventParticipant.setCreator(responseEventParticipant.eventParticipant.getCreator());
                    eventParticipant.setAdmin(responseEventParticipant.eventParticipant.getAdmin());
                    eventParticipant.setId(responseEventParticipant.eventParticipant.getId());

                    //Change button visibility
                    if ("True".equals(eventParticipant.getAttending())){

                        attendingText.setText("Attending");
                        attendingButton.setVisibility(View.GONE);
                        maybeButton.setVisibility(View.VISIBLE);
                        leaveButton.setVisibility(View.VISIBLE);
                    }
                    else{
                        attendingText.setText("Maybe Attending");
                        attendingButton.setVisibility(View.VISIBLE);
                        maybeButton.setVisibility(View.GONE);
                        leaveButton.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseEventParticipant> call, Throwable t) {
                //Testing
                Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    void updateStatus(final Boolean isNew){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        if (isNew){
            //Create
            Api api = retrofit.create(Api.class);
            HashMap<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/json");

            Call<JsonObject> call = api.postEventParticipant(headerMap,
                    eventParticipant );
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    JsonObject responseMessage = response.body();
                    Log.d(TAG, "edit onResponse: json: " + responseMessage.get("message").toString());

                    if (("Attending Event").equals(responseMessage.get("message").toString().replace("\"", ""))) {

                        //Update Event Attending
                        eventParticipant.setAttending("true");

                        //Update text and button visibility
                        attendingText.setText("Attending");
                        attendingButton.setVisibility(View.GONE);
                        maybeButton.setVisibility(View.VISIBLE);
                        leaveButton.setVisibility(View.VISIBLE);

                    }
                    else {
                        //Update Event Maybe
                        eventParticipant.setAttending("false");

                        //Update text and button visibility
                        attendingText.setText("Maybe Attending");
                        attendingButton.setVisibility(View.VISIBLE);
                        maybeButton.setVisibility(View.GONE);
                        leaveButton.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    //Testing
                    Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            //Edit
            Api api = retrofit.create(Api.class);
            HashMap<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/json");

            Call<JsonObject> call = api.editAttendance(eventParticipant.getId(),
                    eventParticipant.getAttending());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    JsonObject responseMessage = response.body();
                    Log.d(TAG, "edit onResponse: json: " + responseMessage.get("message").toString());

                    if (("Attending Event").equals(responseMessage.get("message").toString().replace("\"", ""))) {

                        //Update Event
                        eventParticipant.setAttending("true");

                        //Update text and button visibility
                        attendingText.setText("Attending");
                        attendingButton.setVisibility(View.GONE);
                        maybeButton.setVisibility(View.VISIBLE);
                        leaveButton.setVisibility(View.VISIBLE);

                    }
                    else {
                        //Update Event
                        eventParticipant.setAttending("false");

                        //Update text and button visibility
                        attendingText.setText("Maybe Attending");
                        attendingButton.setVisibility(View.VISIBLE);
                        maybeButton.setVisibility(View.GONE);
                        leaveButton.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    //Testing
                    Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
