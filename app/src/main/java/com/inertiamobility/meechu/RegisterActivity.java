package  com.inertiamobility.meechu;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private SharedPreferenceConfig preferenceConfig;

    EditText Name, Email, Pass, ConPass;
    Button reg_button;
    User newUser = new User();

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Name = findViewById(R.id.reg_name);
        Email = findViewById(R.id.reg_email);
        Pass = findViewById(R.id.reg_password);
        ConPass = findViewById(R.id.reg_con_password);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        reg_button = findViewById(R.id.reg_button);
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newUser.setFirstName(Name.getText().toString());
                newUser.setEmail(Email.getText().toString());
                newUser.setPassword(Pass.getText().toString());
                newUser.setPasswordConfirmation(ConPass.getText().toString());

                if (Name.getText().toString().equals("") || Email.getText().toString().equals("") || Pass.getText().toString().equals("")){
                    builder = new AlertDialog.Builder(RegisterActivity.this);
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
                else if (!(Pass.getText().toString().equals(ConPass.getText().toString()))){
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Your passwords are not matching");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Pass.setText("");
                            ConPass.setText("");
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                else{

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Api.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    Api api = retrofit.create(Api.class);
                    HashMap<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Content-Type", "application/json");
                    Call<User> call = api.createUser(headerMap,
                            newUser );

                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                            Log.d(TAG, "onResponse: Server Response: " + response.toString());

                            newUser.setId(response.body().getId());
                            Toast.makeText(RegisterActivity.this, "Response" + newUser.getId(), Toast.LENGTH_LONG).show();
                            if (newUser.getEmail() == response.body().getEmail())

                                preferenceConfig.writeLoginStatus(true);
                                preferenceConfig.writeUserId(Integer.parseInt(newUser.getId()));

                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.e(TAG,"onFailure: Something went wrong:" + t.getMessage() );
                            Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });
    }
}
