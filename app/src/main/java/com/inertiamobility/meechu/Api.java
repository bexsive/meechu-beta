package  com.inertiamobility.meechu;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "https://34.70.78.151/";

    @POST("api/v1/events")
    Call<EventResponse> postEvent(
            @HeaderMap Map<String, String> headers,
            @Body Event event);

    @GET("api/v1/events")
    Call<EventList> getEvents(@Query("user_id") String params);


    @POST("api/v1/sessions")
    Call<User> login(
            @HeaderMap Map<String, String> headers,
            @Body User user);

    @POST("api/v1/users")
    Call<User> createUser(
            @HeaderMap Map<String, String> headers,
            @Body User user);

    //@PUT("api/v1/users")
    //Call<User> updateUser(
    //        @HeaderMap Map<String, String> headers,
    //        @Body User user);

    @GET("api/v1/users")
    Call<ResponseUser> findUsers(@Query("phone_number") String params );

    //Follow status
    @GET("api/v1/followStatus")
    Call<JsonObject> followStatus(@Query("user_id") String params, @Query(value = "user_id_passive") String params2 );

    //addContacts (From phone contacts list)
    @GET("api/v1/addContacts")
    Call<UserList> addContacts(@Query("user_id") String params, @Query(value = "numbers[]") List<String> params2);

    //Check Participant status
    @GET("api/v1/findEventParticipant")
    Call<ResponseEventParticipant> findEventParticipant(@Query("user_id") String params, @Query(value = "event_id") String params2 );

    //createParticipant
    @POST("api/v1/event_participants")
    Call<JsonObject> postEventParticipant(
            @HeaderMap Map<String, String> headers,
            @Body EventParticipant eventParticipant);

    //editParticipant
    @GET("api/v1/editAttendance")
    Call<JsonObject> editAttendance(@Query("id") String params, @Query(value = "attending") String params2);

    //destroyParticipant
   // @DELETE

}
