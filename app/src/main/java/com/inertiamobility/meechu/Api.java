package  com.inertiamobility.meechu;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "https://meechu.me/api/v1/";
    // String BASE_URL = "http://34.70.78.151/api/v1/";

    // User Registration
    @POST("users/register_new_user")
    Call<User> registerUser(
            @HeaderMap Map<String, String> headers,
            @Body User user);

    // Make an event
    @POST("events")
    Call<Event> postEvent(
            @HeaderMap Map<String, String> headers,
            @Body Event event);

    // Timeline
    @GET("feed")
    Call<EventList> getFeed(@Query("user_id") String params);

    // Check Participant status
    @GET("check_attendance")
    Call<EventParticipant> check_attendance(@Query("user_id") Integer params, @Query(value = "event_id") Integer params2 );

    // Create Participant
    @POST("ep")
    Call<EventParticipant> createEventParticipant(
            @HeaderMap Map<String, String> headers,
            @Body EventParticipant eventParticipant);

    // Update Participant
    @PUT("ep/{id}")
    Call<EventParticipant> updateAttendance(
            @Path("id") int id,
            @HeaderMap Map<String, String> headers,
            @Body EventParticipant eventParticipant);

    // destroyParticipant
    // @DELETE

    @POST("api/v1/sessions")
    Call<User> login(
            @HeaderMap Map<String, String> headers,
            @Body User user);

    @GET("api/v1/users")
    Call<User> findUsersFromPhoneNumber(@Query("phone_number") String params );

    //Follow status
    @GET("api/v1/followStatus")
    Call<JsonObject> followStatus(@Query("user_id") String params, @Query(value = "user_id_passive") String params2 );

    //addContacts (From phone contacts list)
    @GET("api/v1/addContacts")
    Call<UserList> addContacts(@Query("user_id") String params, @Query(value = "numbers[]") List<String> params2);

    //@PUT("api/v1/users")
    //Call<User> updateUser(
    //        @HeaderMap Map<String, String> headers,
    //        @Body User user);
}
