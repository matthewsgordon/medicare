package me.matthewgordon.medicare.view;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import me.matthewgordon.medicare.infrastructure.MedicareRepository;
import me.matthewgordon.medicare.R;

/**
 * LoginActivity Login Screen
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // Reference this this activity
    private final AppCompatActivity activity = LoginActivity.this;
    // Shared preferences used to hold the session details of
    // the logged in user
    private SharedPreferences sharedPreferences;
    // Used to hold a reference to the application database
    private MedicareRepository medicareRepository;

    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get the session parameter from Shared Preferences
        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear the value as we are at the Login Screen
        editor.clear();
        editor.commit();

        // Handle to the Login Button
        Button loginButton = (Button)findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        // Handle to the Register Button
        Button registerButton = (Button)findViewById(R.id.login_register);
        registerButton.setOnClickListener(this);

        // Create a instance of the database repository
        medicareRepository = new MedicareRepository(activity);
    }

    /**
     * Checks login and navigates to the main application
     * screen if login valid
     */
    private void checkLogin() {
        // Access the login details from the form
        EditText email = (EditText) findViewById(R.id.email_login);
        EditText password = (EditText)findViewById(R.id.password_login);
        // Are user credentials valid?
        boolean isValid = medicareRepository.checkUser(email.getText().toString(),password.getText().toString());
        // If not than inform user
        if (!isValid) {
            Toast.makeText(getApplicationContext(), "Login Incorrect", Toast.LENGTH_LONG).show();
        } else {
            // If credentials okay then set the email to the Shared Preferences
            // session variable
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email.getText().toString());
            editor.commit();
            // Display a Toast message of login success
            Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_LONG).show();
            // Navigate to the HealthActivity screen
            Intent intentHealth = new Intent(getApplicationContext(), HealthActivity.class);
            startActivity(intentHealth);
        }
    }

    /**
     * Navigates to the Register screen
     */
    private void register() {
        // Navigate to the Register screen
        Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intentRegister);
    }

    /**
     * onClick handles display click events
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        // Given the view id
        switch (view.getId()) {
            case R.id.login:
                // If login button pressed
                checkLogin();
                break;
            case R.id.login_register:
                // If register button pressed
                register();
                break;
        }
    }
}