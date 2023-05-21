package me.matthewgordon.medicare.view;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.List;
import me.matthewgordon.medicare.R;
import me.matthewgordon.medicare.infrastructure.MedicareRepository;
import me.matthewgordon.medicare.model.Health;
import me.matthewgordon.medicare.model.User;

public class HealthActivity extends AppCompatActivity implements View.OnClickListener {

    // Handle to this HealthActivity
    private final AppCompatActivity activity = HealthActivity.this;
    // Application database
    private MedicareRepository medicareRepository;
    // Shared preferences holding session details
    private SharedPreferences sharedPreferences;
    // Current user logged ib
    private User user;
    // Email address of current logged in user
    private String loggedInEmail;

    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        // Create an instance of the database repository
        medicareRepository = new MedicareRepository(activity);

        // Get the Shared Preferences and session details
        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        // Get the user email
        loggedInEmail = sharedPreferences.getString("email", "none");
        // Get the user from the email
        try {
            user = medicareRepository.getUserByEmail(loggedInEmail);
        } catch (Exception e) {
            // Error, should never reach here as a logged in user
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
        }
        // Update the summary display
        updateUserSummaryDisplay();

        // Handle to the Health Entry button
        Button enterHealthButton = (Button)findViewById(R.id.enter_health);
        enterHealthButton.setOnClickListener(this);
        // Handle to to Profile / Register button
        Button profileButton = (Button)findViewById(R.id.profile);
        profileButton.setOnClickListener(this);
        // Handle to the Logout button
        Button logoutButton = (Button)findViewById(R.id.logout);
        logoutButton.setOnClickListener(this);

        // Update the health list display
        updateHealthList();
    }

    /**
     * Updates the health activity list
     */
    private void updateHealthList() {
        // Handle to the health list
        ListView healthListView = (ListView)findViewById(R.id.health_list);
        // Get a string array of health summary to display
        String healthSummaryList[] = medicareRepository.getHealthActivitySummaryByUser(user);
        // Add string array to array adapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_health_listview, healthSummaryList);
        // Update the list display
        healthListView.setAdapter(arrayAdapter);
    }

    /**
     * Updates the summary display
     */
    private void updateUserSummaryDisplay() {
        // Handle to the display
        EditText userSummaryDisplay = (EditText)findViewById(R.id.user_name);
        // Concat a string of First Name, Last Name and Age
        String userSummary = user.getFirstName() + " " + user.getLastName() + " " + user.getAge();
        // Update the display
        userSummaryDisplay.setText(userSummary);
    }

    /**
     * Logout
     */
    private void logout() {
        // Edit the Shared Preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear them as we're logging out
        editor.clear();
        editor.commit();
        // Navigate to the Login screen
        Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intentLogin);
    }

    /**
     * Profile
     */
    private void profile() {
        // Navigate to the Profile / Register screen
        Intent intentProfile = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intentProfile);
    }

    /**
     * Add Health Entry
     */
    private void enterHealth() {
        // Navigate to the Health Entry screen
        Intent intentHealthEntry = new Intent(getApplicationContext(), HealthEntryActivity.class);
        startActivity(intentHealthEntry);
    }

    /**
     * onClick handles display click events
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                // Logout
                logout();
                break;
            case R.id.enter_health:
                // Add a health entry
                enterHealth();
                break;
            case R.id.profile:
                // Profile
                profile();
                break;
        }
    }
}
