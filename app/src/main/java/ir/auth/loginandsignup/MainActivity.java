package ir.auth.loginandsignup;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import ir.auth.loginandsignup.api.KeyAuth;
import ir.auth.loginandsignup.api.UserData;

public class MainActivity extends AppCompatActivity {

    EditText username;
    Button login;
    TextInputLayout txtInLayoutUsername;
    CheckBox rememberMe;

    private static final String URL = "https://keyauth.win/api/1.2/";
    private static final String OWNERID = "XQFamIACDQ"; // Set your owner ID
    private static final String APPNAME = "animetone"; // Set your app name
    private static final String VERSION = "1.0"; // Set your app version

    private KeyAuth keyAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keyAuth = new KeyAuth(APPNAME, OWNERID, VERSION, URL);

        username = findViewById(R.id.username);
        login = findViewById(R.id.login);
        txtInLayoutUsername = findViewById(R.id.txtInLayoutUsername);
        rememberMe = findViewById(R.id.rememberMe);

        // Retrieve and display saved key
        loadSavedKey();

        ClickLogin();
    }

    private void loadSavedKey() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedKey = sharedPreferences.getString("savedKey", "");
        if (!savedKey.isEmpty()) {
            username.setText(savedKey);
            rememberMe.setChecked(true); // Optionally check the 'Remember Me' checkbox if a key is found
        }
    }

    private void ClickLogin() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = username.getText().toString().trim();

                if (key.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Please fill out these fields", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();
                    txtInLayoutUsername.setError("Key should not be empty");
                    return; // Exit method if key is empty
                }

                keyAuth.init(new KeyAuth.InitCallback() {
                    @Override
                    public void onSuccess() {
                        keyAuth.license(key, new KeyAuth.LicenseCallback() {
                            @Override
                            public void onSuccess(UserData userData) {
                                runOnUiThread(() -> {
                                    Snackbar snackbar = Snackbar.make(view, "Logged in!", Snackbar.LENGTH_LONG);
                                    View snackbarView = snackbar.getView();
                                    snackbarView.setBackgroundColor(getResources().getColor(R.color.yellow));
                                    snackbar.show();

                                    // Save the key if 'Remember Me' is checked
                                    if (rememberMe.isChecked()) {
                                        saveKey(key);
                                    }

                                    Toast.makeText(MainActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();

                                    // Start the HomeActivity or another activity
                                  //  Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                  //  startActivity(intent);

                                    // Optionally finish the current activity
                                   // finish();
                                });
                            }

                            @Override
                            public void onFailure(String message) {
                                runOnUiThread(() -> Toast.makeText(MainActivity.this, "License Failed: " + message, Toast.LENGTH_SHORT).show());
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Initialization Failed: " + message, Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });
    }

    private void saveKey(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedKey", key);
        editor.apply();
    }
}

