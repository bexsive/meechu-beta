package  com.inertiamobility.meechu;


import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface Api {

    String BASE_URL = "http://54.67.75.160/";

    @POST("api/v1/events")
    Call<ResponseBody> postEvent(
            @HeaderMap Map<String, String> headers,
            @Body Event event);

    @GET("api/v1/events")
    Call<EventList> getEvents(@Query("user_id") String params);

    //@GET("api/v1/events/?user_id={params}")
    //Call<EventList> getEvents(@Path("params") String params);

    @POST("api/v1/sessions")
    Call<User> login(
            @HeaderMap Map<String, String> headers,
            @Body User user);

    @POST("api/v1/users")
    Call<User> createUser(
            @HeaderMap Map<String, String> headers,
            @Body User user);

    @PUT("api/v1/users")
    Call<User> updateUser(
            @HeaderMap Map<String, String> headers,
            @Body User user);
}
