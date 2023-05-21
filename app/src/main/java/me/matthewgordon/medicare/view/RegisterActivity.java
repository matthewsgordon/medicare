package me.matthewgordon.medicare.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import me.matthewgordon.medicare.infrastructure.MedicareRepository;
import me.matthewgordon.medicare.R;
import me.matthewgordon.medicare.model.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity = RegisterActivity.this;
    private MedicareRepository medicareRepository;
    private SharedPreferences sharedPreferences;
    private User user;
    private String healthContactUri = "";
    private String loggedInEmail;
    private static final int REQUEST_READ_CONTACTS_PERMISSION = 0;
    private static final int REQUEST_CONTACT = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        medicareRepository = new MedicareRepository(activity);

        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        loggedInEmail = sharedPreferences.getString("email", "none");
        if (!loggedInEmail.equals("none")) {
            try {
                user = medicareRepository.getUserByEmail(loggedInEmail);
                healthContactUri = user.getHealthContactURI();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
            }
            updateUserProfile();
        } else {
            user = new User();
        }

        Button registerButton = (Button)findViewById(R.id.register);
        registerButton.setOnClickListener(this);
        EditText selectHealthContactText = (EditText)(findViewById(R.id.health_contact_register));
        selectHealthContactText.setFocusable(false);
        selectHealthContactText.setClickable(true);
        selectHealthContactText.setOnClickListener(this);

        requestContactsPermission();
    }

    private boolean hasContactsPermission()
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestContactsPermission()
    {
        if (!hasContactsPermission())
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS_PERMISSION);
        }
    }

    private void updateUserProfile() {
        EditText email = (EditText) findViewById(R.id.email_register);
        email.setText(user.getEmail());
        email.setEnabled(false);
        EditText password = (EditText)findViewById(R.id.password_register);
        password.setText(user.getPassword());
        EditText passwordAgain = (EditText)findViewById(R.id.password_again_register);
        passwordAgain.setText(user.getPassword());
        EditText firstName = (EditText)findViewById(R.id.firstname_register);
        firstName.setText(user.getFirstName());
        EditText lastName = (EditText)findViewById(R.id.lastname_register);
        lastName.setText(user.getLastName());
        EditText age = (EditText)findViewById(R.id.age_register);
        age.setText(Long.toString(user.getAge()));
        updateHealthContactName();
    }

    private void updateHealthContactName() {
        if (!healthContactUri.isEmpty()) {
            EditText healthContact = (EditText)findViewById(R.id.health_contact_register);
            healthContact.setText(getContactNameFromUri(healthContactUri));
        }
    }

    private void registerOrUpdateUser() {
        EditText email = (EditText) findViewById(R.id.email_register);
        EditText password = (EditText)findViewById(R.id.password_register);
        EditText passwordAgain = (EditText)findViewById(R.id.password_again_register);
        EditText firstName = (EditText)findViewById(R.id.firstname_register);
        EditText lastName = (EditText)findViewById(R.id.lastname_register);
        EditText age = (EditText)findViewById(R.id.age_register);

        try {
            user.register(
                    email.getText().toString(),
                    password.getText().toString(),
                    passwordAgain.getText().toString(),
                    firstName.getText().toString(),
                    lastName.getText().toString(),
                    Integer.parseInt(age.getText().toString()),
                    healthContactUri
            );
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        if (loggedInEmail.equals("none")) {
            if (medicareRepository.checkUser(email.getText().toString())) {
                Toast.makeText(getApplicationContext(),"User already registered", Toast.LENGTH_LONG).show();
                return;
            }
            medicareRepository.addUser(user);
            Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentLogin);
        } else {
            medicareRepository.updateUser(user);
            Toast.makeText(getApplicationContext(),"User profile updated", Toast.LENGTH_LONG).show();
            Intent intentHealth = new Intent(getApplicationContext(), HealthActivity.class);
            startActivity(intentHealth);
        }
    }

    String getContactNameFromUri(String uri) {
        String contactName;
        Uri contactUri = Uri.parse(uri);
        String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
        Cursor cursor = this.getContentResolver().query(contactUri, queryFields, null, null, null);
        try
        {
           if (cursor.getCount() == 0) return "";
           cursor.moveToFirst();
           contactName = cursor.getString(0);
        }
        finally
        {
            cursor.close();
        }
        return contactName;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_CONTACT && data != null)
        {
            healthContactUri = data.getData().toString();
            updateHealthContactName();
        }
    }

    private void selectHealthContact() {
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(pickContact, REQUEST_CONTACT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                registerOrUpdateUser();
                break;
            case R.id.health_contact_register:
                selectHealthContact();
                break;
        }
    }
}
