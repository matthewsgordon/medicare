package me.matthewgordon.medicare.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import me.matthewgordon.medicare.R;
import me.matthewgordon.medicare.infrastructure.MedicareRepository;
import me.matthewgordon.medicare.model.*;

public class HealthEntryActivity extends AppCompatActivity implements View.OnClickListener  {

    private final AppCompatActivity activity = HealthEntryActivity.this;
    // Application database repository
    private MedicareRepository medicareRepository;
    // Shared Preferences to hold session state
    private SharedPreferences sharedPreferences;
    // User logged in
    private User user;
    // Email of logged in user
    private String loggedInEmail;

    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_entry);

        // Request permission to send SMS
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);

        /// Create a instance of the database repository
        medicareRepository = new MedicareRepository(activity);

        // Get Shared Preferences to get the logged in user
        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        loggedInEmail = sharedPreferences.getString("email", "none");
        // From email get the user
        if (!loggedInEmail.equals("none")) {
            try {
                user = medicareRepository.getUserByEmail(loggedInEmail);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Error, Not logged in", Toast.LENGTH_LONG).show();
        }

        // Handle to Save button
        Button saveHealthButton = (Button)findViewById(R.id.save_health);
        saveHealthButton.setOnClickListener(this);
    }

    /**
     * Gets a phone number from Contacts via a name
     * Adapted from https://tinyurl.com/4v84cnb2
     *
     * @param personName
     * @return String
     */
    String getPhoneNoFromName(String personName) {
        String phoneNumber = null;
        Cursor cursor = null;
        try {
            // Get all contacts data
            cursor = this.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);
            // Data exists
            if (cursor != null) {
                // Get contact id
                int contactIdIdx = cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                // Get the display name id
                int nameIdx = cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                // Get the phone number id
                int phoneNumberIdx = cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                // Goto the start of the cursor
                cursor.moveToFirst();
                // While there's data
                do {
                    // If the name equals get the phone number
                    String name = cursor.getString(nameIdx);
                    if (name.equalsIgnoreCase(personName)) {
                        phoneNumber = cursor.getString(phoneNumberIdx);
                        break;
                    }
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(getApplicationContext(),"No Phone Number", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return phoneNumber;
    }

    /**
     * Gets a contact name from a URI
     *
     * @param uri
     * @return String
     */
    String getContactNameFromUri(String uri) {
        String contactName;
        Uri contactUri = Uri.parse(uri);
        // Build the query
        String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
        // Query the contacts
        Cursor cursor = this.getContentResolver().query(contactUri, queryFields, null, null, null);
        try
        {
            if (cursor.getCount() == 0) return "";
            cursor.moveToFirst();
            // Contact name should be first parameter of cursor
            contactName = cursor.getString(0);
        }
        finally
        {
            cursor.close();
        }
        return contactName;
    }
    /**
     * Sends a SMS message to the users
     * health contact
     *
     * @param health
     */
    private void sendSMSToHealthContact(Health health) {
        // Get the contact name from the URI
        String contactName = getContactNameFromUri(user.getHealthContactURI());
        // Get the phone number from the contact name
        String phoneNo = getPhoneNoFromName(contactName);
        // Construct the SMS message
        String smsMessage = "Patient " + user.getFirstName() + " " + user.getLastName() + " identified as " +
                health.getHealthRating() + " with temperature of " + health.getTemperature() + ", lower blood pressure of " +
                health.getLowerBloodPressure() + ", higher blood pressure of " + health.getHigherBloodPressure() +
                " and heartbeats per min of " + health.getHeartBeatsPerMin();
        // Handle to the SMS Manager
        SmsManager smsManager = SmsManager.getDefault();
        // More than 160 characters requires SMS message to be split in parts
        ArrayList<String> smsMessageParts = smsManager.divideMessage(smsMessage);
        // Attempt to send the SMS message
        try {
            smsManager.sendMultipartTextMessage(phoneNo,null,smsMessageParts,null,null);
            Toast.makeText(getApplicationContext(),"Informing " + contactName + " on " + phoneNo, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"SMS error", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Add health details
     *
     */
    private void enterHealthDetails() {
        // Handle to the ui display elements
        EditText temperatureText = (EditText)findViewById(R.id.health_entry_temperature);
        EditText lowerBloodText = (EditText)findViewById(R.id.health_entry_lower_blood);
        EditText higherBloodText = (EditText)findViewById(R.id.health_entry_higher_blood);
        EditText beatsPerMinText = (EditText)findViewById(R.id.health_entry_beats);
        // Create a new Health object
        Health health = new Health();
        // Convert the ui elements to correct data types
        Double temperature = Double.parseDouble(temperatureText.getText().toString());
        Double lowerBlood = Double.parseDouble(lowerBloodText.getText().toString());
        Double higherBlood = Double.parseDouble(higherBloodText.getText().toString());
        Double beatsPerMin = Double.parseDouble(beatsPerMinText.getText().toString());

        // Attempt to register a health entry
        try {
            health.register(
                    user,
                    temperature,
                    lowerBlood,
                    higherBlood,
                    beatsPerMin
            );
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        // Add the health entry to the database
        medicareRepository.addHealthActivity(health);
        // If health rating is High Risk then send SMS message
        if (health.getHealthRating().equals("High Risk")) {
            sendSMSToHealthContact(health);
        }
        // Navigate to the Health Activity screen
        Intent intentHealth = new Intent(getApplicationContext(), HealthActivity.class);
        startActivity(intentHealth);
    }

    /**
     * OnClick handle display
     *
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_health) {
            enterHealthDetails();
        }
    }
}
