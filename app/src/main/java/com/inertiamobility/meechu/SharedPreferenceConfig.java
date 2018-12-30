package  com.inertiamobility.meechu;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceConfig {

    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceConfig(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

    }

    public void writeLoginStatus(boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.login_status_preference), status);
        editor.commit();
    }

    public boolean readLoginStatus(){
        boolean status = sharedPreferences.getBoolean(context.getResources().getString(R.string.login_status_preference), false);
        return status;
    }

    public void writeUserId(Integer id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getResources().getString(R.string.login_user_id), id);
        editor.commit();
    }

    public int readUserId(){
        int id = sharedPreferences.getInt(context.getResources().getString(R.string.login_user_id), 1);
        return id;
    }

    public void writeUserLat(Float lat){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(String.valueOf(context.getResources().getString(R.string.login_user_lat)), lat);
        editor.commit();
    }

    public float readUserLat(){
        float lat = sharedPreferences.getFloat(context.getResources().getString(R.string.login_user_lat), 1);
        return lat;
    }

    public void writeUserLng(float lng){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(String.valueOf(context.getResources().getString(R.string.login_user_lng)), lng);
        editor.commit();
    }

    public float readUserLng(){
        float lng = sharedPreferences.getFloat(context.getResources().getString(R.string.login_user_lng), 1);
        return lng;
    }
}
