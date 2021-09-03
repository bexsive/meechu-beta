package  com.inertiamobility.meechu;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.Patterns;
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
                // Validate and assign First Name
                if(firstName.getText().toString().equals("")){
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("First name cannot be left blank");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                } else if(firstName.getText().length() < 2){
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("First name has to be at lease two letters");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                } else {
                    newUser.setFirstName(firstName.getText().toString());
                }

                // Validate and assign Last Name
                if(lastName.getText().toString().equals("")){
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Last name cannot be left blank");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }  else if(lastName.getText().toString().length() < 2) {
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Last  name has to be at lease two letters");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                } else {
                    newUser.setLastName(lastName.getText().toString());
                }

                // Phone Number
                if(phoneNumber.getText().toString().equals("")){
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Phone Number cannot be left blank");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }
                // Phone number must be 10 digits
                else if(phoneNumber.getText().toString().replaceAll("\\D+", "").length() != 10){
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Phone number must be 10 digits");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                } else{
                    newUser.setPhoneNumber(phoneNumber.getText().toString());
                }

                // Email
                if(email.getText().toString().equals("")){
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Email cannot be left blank");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }
                // Validate email format
                else if(!isValidEmail(email.getText().toString())){
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Email format not valid");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }
                else{
                    newUser.setEmail(email.getText().toString());
                }

                // Password
                if(pass.getText().toString().equals("")){
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Password cannot be left blank");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                } else if(pass.getText().toString().length() < 6) {
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Password must be at least 6 characters long");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }

                if(!(pass.getText().toString().equals(conPass.getText().toString()))){
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
                    return;
                } else{
                    newUser.setPassword(pass.getText().toString());

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Api.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    Api api = retrofit.create(Api.class);
                    HashMap<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Content-Type", "application/json");
                    Call<User> call = api.registerUser(headerMap, newUser);

                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                            // TODO: Remove this after testing
                            Log.d(TAG, "onResponse: Server Response: " + response.toString());


                            if (response.message().equals("Unprocessable Entity")){
                                Toast.makeText(RegisterActivity.this, "Email not valid", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (newUser.getEmail().equals(response.body().getEmail()))
                                newUser.setId(response.body().getId());
                                Log.d(TAG, "onResponse: Server Response Message: " + response.message());
                                Log.d(TAG, "onResponse: Server Response Body: " + response.toString());
                                Log.d(TAG, "onResponse: Server Response ID: " + response.body().getId());
                                Log.d(TAG, "onResponse: Server Response email: " + response.body().getEmail());


                            // TODO: Remove this after testing
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

    public static boolean isValidEmail(CharSequence target) {
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
