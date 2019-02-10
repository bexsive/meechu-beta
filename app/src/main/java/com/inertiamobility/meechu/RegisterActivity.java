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

    EditText firstName, lastName, phoneNumber, email, pass, conPass;
    Button reg_button;
    User newUser;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        newUser = new User();
        firstName = findViewById(R.id.reg_first_name);
        lastName = findViewById(R.id.reg_last_name);
        phoneNumber = findViewById(R.id.reg_number);
        email = findViewById(R.id.reg_email);
        pass = findViewById(R.id.reg_password);
        conPass = findViewById(R.id.reg_con_password);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        reg_button = findViewById(R.id.reg_button);
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firstName.getText().toString().equals("") || lastName.getText().toString().equals("") || phoneNumber.getText().toString().equals("") || email.getText().toString().equals("") || pass.getText().toString().equals("")){
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
                else if (!(pass.getText().toString().equals(conPass.getText().toString()))){
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Your passwords are not matching");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            pass.setText("");
                            conPass.setText("");
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                else{

                        newUser.setFirstName(firstName.getText().toString());
                        newUser.setLastName(lastName.getText().toString());
                        newUser.setPhoneNumber(phoneNumber.getText().toString());
                        newUser.setEmail(email.getText().toString());
                        newUser.setPassword(pass.getText().toString());
                        newUser.setPasswordConfirmation(conPass.getText().toString());

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

                            if (newUser.getEmail().equals(response.body().getEmail()))
                                newUser.setId(response.body().getId());
                                Toast.makeText(RegisterActivity.this, "Response" + newUser.getId(), Toast.LENGTH_LONG).show();
                                preferenceConfig.writeLoginStatus(true);
                                preferenceConfig.writeUserId(Integer.parseInt(newUser.getId()));

                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
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
