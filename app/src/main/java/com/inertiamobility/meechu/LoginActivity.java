package  com.inertiamobility.meechu;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    TextView signup_text;
    Button login_button;
    EditText Email, Pass;
    AlertDialog.Builder builder;
    User user;

    private SharedPreferenceConfig preferenceConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = new User();
        Email = findViewById(R.id.email);
        Pass = findViewById(R.id.password);
        signup_text = findViewById(R.id.sign_up);
        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        //TODO: Re-Do this with new shared Preferences
        if (preferenceConfig.readLoginStatus()){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Email.getText().toString().equals("") || Pass.getText().toString().equals("")){
                    builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Please fill all fields");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {

                    user.setEmail(Email.getText().toString());
                    user.setPassword(Pass.getText().toString());
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Api.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    Api api = retrofit.create(Api.class);
                    HashMap<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Content-Type", "application/json");
                    Call<User> call = api.login(headerMap,
                            user );

                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            // Tracking
                            Log.d(TAG, "onResponse: Server Response: " + response.toString());

                            user.setId(response.body().getId());
                            Toast.makeText(LoginActivity.this, "Response" + user.getId(), Toast.LENGTH_LONG).show();

                            //Shared Preferences Login
                            //TODO: Re-Do this with new shared Preferences
                            preferenceConfig.writeLoginStatus(true);
                            preferenceConfig.writeUserId(Integer.parseInt(user.getId()));

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.e(TAG,"onFailure: Something went wrong:" + t.getMessage() );
                            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        signup_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }
}
