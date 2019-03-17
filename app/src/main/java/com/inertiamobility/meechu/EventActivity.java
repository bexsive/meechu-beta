package com.inertiamobility.meechu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventActivity extends AppCompatActivity {

    public static String TAG = "EventActivity";
    TextView eventNameText, venueNameText, startTimeText, distanceAwayText;
    Button attendingButton, maybeButton;

    Event event;
    EventParticipant eventParticipant;

    SharedPreferenceConfig preferenceConfig;
    Boolean isChecked, isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        eventNameText = findViewById(R.id.eventNameText);
        venueNameText = findViewById(R.id.venueNameText);
        startTimeText = findViewById(R.id.startTimeText);
        distanceAwayText = findViewById(R.id.distanceAwayText);

        attendingButton = findViewById(R.id.attendingButton);
        maybeButton = findViewById(R.id.maybeButton);

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
        startTimeText.setText(event.getStartTime());

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        distanceAwayText.setText(String.format("%.0f", haversine_mi(
                preferenceConfig.readUserLat(),
                preferenceConfig.readUserLng(), Double.valueOf(event.getLat()), Double.valueOf(event.getLng()))) + " Miles Away");

        // TODO: Check the attending status of this user to that event.
        //On result, change the button titles and going/not going/maybe going status
        // Then, build relevant eventParticipant object to reflect the needs based on the button selection
        isChecked = Boolean.FALSE;
        //Set Default
        isNew = Boolean.FALSE;
        eventParticipant = new EventParticipant();

        checkAttendance();

        maybeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventParticipant.setAttending("false");
                updateStatus(isNew);

            }
        });
        attendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eventParticipant.setAttending("true");
                updateStatus(isNew);

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

                }
                else {
                    eventParticipant.setUserId(responseEventParticipant.eventParticipant.getUserId());
                    eventParticipant.setEventId(responseEventParticipant.eventParticipant.getEventId());
                    eventParticipant.setAttending(responseEventParticipant.eventParticipant.getAttending());
                    eventParticipant.setCreator(responseEventParticipant.eventParticipant.getCreator());
                    eventParticipant.setAdmin(responseEventParticipant.eventParticipant.getAdmin());
                    eventParticipant.setId(responseEventParticipant.eventParticipant.getId());
                }
            }

            @Override
            public void onFailure(Call<ResponseEventParticipant> call, Throwable t) {
                //Testing
                Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });

    }

    void updateStatus(Boolean isNew){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        if (isNew){
            //Create
            Api api = retrofit.create(Api.class);
            HashMap<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", "application/json");

            Call<ResponseBody> call = api.postEventParticipant(headerMap,
                    eventParticipant );
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        Log.d(TAG, "isNew post response:");
                        String json = response.body().toString();
                        Log.d(TAG, "onResponse: json: " + json);
                        JSONObject data = new JSONObject(json);
                    }catch (JSONException e){
                        Log.e(TAG, "onResponse: JSONException: " + e.getMessage() );
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //Testing
                    Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            });

        }
        else{
            //Edit
            Api api = retrofit.create(Api.class);
            HashMap<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", "application/json");

            Call<ResponseBody> call = api.changeAttendance(eventParticipant.getId(),
                    eventParticipant.getAttending());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        Log.d(TAG, "isNew edit response:");
                        String json = response.body().toString();
                        Log.d(TAG, "onResponse: json: " + json);
                        JSONObject data = new JSONObject(json);
                    }catch (JSONException e){
                        Log.e(TAG, "onResponse: JSONException: " + e.getMessage() );
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //Testing
                    Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}

//TODO:
//On loading, check the event for users that you are following, and total attendees to event.
// Users that you are following, list them below the event somehow(User id{as a reference}, name, profile picture)
//If you are attending, build an EventParticipant object from the call, and populate buttons
//If you aren't attending

//DONE
//Also, check your status on the event (If event_participant exists, and if so the data)

