package me.matthewgordon.medicare.infrastructure;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import me.matthewgordon.medicare.model.*;

/**
 * MedicareRepository provides database persistence
 * and access to inbuilt SQLite. Is an extension of
 * the inbuilt SQLiteOpenHelper as per example at
 * https://tinyurl.com/bddunaxn
 *
 * @author  Matthew Gordon
 * @version 0.1.0
 * @since   18/04/2023
 */
public class MedicareRepository extends SQLiteOpenHelper {

    // Database version;
    private static final int DATABASE_VERSION = 6;

    // Database name;
    private static String DATABASE_NAME = "Medicare.db";

    // Database tables
    //
    // user table stores information about the user
    private static final String TABLE_USER = "user";
    // health_activity table stores information about
    // individual health entries    //
    private static final String TABLE_HEALTH_ACTIVITY = "health_activity";

    // User table columns
    //
    // Unique user id
    private static final String COLUMN_USER_ID = "id";
    // Email address
    private static final String COLUMN_USER_EMAIL = "email";
    // First name
    private static final String COLUMN_USER_FIRST_NAME = "first_name";
    // Last name
    private static final String COLUMN_USER_LAST_NAME = "last_name";
    // Age
    private static final String COLUMN_USER_AGE = "age";
    // Password
    private static final String COLUMN_USER_PASSWORD = "password";
    // Android URI to health contact
    private static final String COLUMN_USER_HEALTH_CONTACT_URI = "health_contact_uri";

    // Create User table SQL
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_EMAIL + " TEXT,"
            + COLUMN_USER_FIRST_NAME + " TEXT," + COLUMN_USER_LAST_NAME + " TEXT,"
            + COLUMN_USER_AGE + " NUMBER,"
            + COLUMN_USER_PASSWORD + " TEXT," + COLUMN_USER_HEALTH_CONTACT_URI + " TEXT" +  ")";

    // Drop User table SQL
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    // Health Activity columns
    //
    // Unique health activity id
    private static final String COLUMN_HEALTH_ACTIVITY_ID = "id";
    // Date & time of health activity entry
    private static final String COLUMN_HEALTH_ACTIVITY_DATETIME = "date_time";
    // Id of the user
    private static final String COLUMN_HEALTH_ACTIVITY_USER_ID = "user_id";
    // Temperature
    private static final String COLUMN_HEALTH_ACTIVITY_TEMPERATURE = "temperature";
    // Lower blood pressure
    private static final String COLUMN_HEALTH_ACTIVITY_LOWER_BLOOD_PRESSURE = "lower_blood_pressure";
    // Higher blood pressure
    private static final String COLUMN_HEALTH_ACTIVITY_HIGHER_BLOOD_PRESSURE = "higher_blood_pressure";
    // Heart beats per min
    private static final String COLUMN_HEALTH_ACTIVITY_HEART_BEATS_PER_MIN = "heart_beats_per_min";
    // Health rating
    private static final String COLUMN_HEALTH_ACTIVITY_HEALTH_RATING = "health_rating";

    // Create Health Activity table SQL
    private String CREATE_HEALTH_ACTIVITY_TABLE = "CREATE TABLE " + TABLE_HEALTH_ACTIVITY + "("
            + COLUMN_HEALTH_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_HEALTH_ACTIVITY_DATETIME + " INTEGER,"
            + COLUMN_HEALTH_ACTIVITY_USER_ID + " INTEGER," + COLUMN_HEALTH_ACTIVITY_TEMPERATURE + " REAL,"
            + COLUMN_HEALTH_ACTIVITY_LOWER_BLOOD_PRESSURE + " REAL," + COLUMN_HEALTH_ACTIVITY_HIGHER_BLOOD_PRESSURE + " REAL,"
            + COLUMN_HEALTH_ACTIVITY_HEART_BEATS_PER_MIN + " REAL,"
            + COLUMN_HEALTH_ACTIVITY_HEALTH_RATING + " TEXT" + ")";

    // Drop Health Activity table SQL
    private String DROP_HEALTH_ACTIVITY_TABLE = "DROP TABLE IF EXISTS " + TABLE_HEALTH_ACTIVITY;

    /**
     * Constructor. Initiates the database
     *
     * @param context
     */
    public MedicareRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    /**
     * onCreate triggers when database created
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_HEALTH_ACTIVITY_TABLE);
    }
    /**
     * onCreate triggers when database created
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop User Table if exist
        db.execSQL(DROP_HEALTH_ACTIVITY_TABLE);
        db.execSQL(DROP_USER_TABLE);
        // Create tables again
        onCreate(db);
    }

    /**
     * Add a user
     *
     * @param user
     */
    public void addUser(User user) {
        // Get access to the database
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a set of content values
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_USER_LAST_NAME, user.getLastName());
        values.put(COLUMN_USER_AGE, user.getAge());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_HEALTH_CONTACT_URI, user.getHealthContactURI());

        // Insert the user data in the user table
        db.insert(TABLE_USER, null, values);
        // Close the database
        db.close();
    }

    /**
     * Returns a User from a database cursor
     *
     * @param cursor
     * @return User
     */
    private User getUserFromCursor(Cursor cursor) {
        // Create a new User
        User user = new User();
        // Set the user parameters from the cursor
        user.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
        user.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRST_NAME)));
        user.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LAST_NAME)));
        user.setAge(cursor.getLong(cursor.getColumnIndex(COLUMN_USER_AGE)));
        user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
        user.setHealthContactURI(cursor.getString(cursor.getColumnIndex(COLUMN_USER_HEALTH_CONTACT_URI)));
        return user;
    }

    /**
     * Get all users
     *
     * @return List
     */
    public List<User> getAllUser() {
        // Array of columns to fetch from the user database table
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_EMAIL,
                COLUMN_USER_FIRST_NAME,
                COLUMN_USER_LAST_NAME,
                COLUMN_USER_AGE,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_HEALTH_CONTACT_URI
        };
        // Sort order
        String sortOrder = COLUMN_USER_LAST_NAME + " ASC";
        List<User> userList = new ArrayList<User>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Query the user database table
        Cursor cursor = db.query(TABLE_USER,
                columns,
                null,
                null,
                null,
                null,
                sortOrder
                );
        // Add all the users to the list
        if (cursor.moveToFirst()) {
            do {
                    userList.add(getUserFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        // Close the cursor and database
        cursor.close();
        db.close();
        return userList;
    }

    /**
     * Get user from email address
     *
     * @param email
     * @return User
     */
    public User getUserByEmail(String email) throws Exception {
        // Array of columns to fetch from the user table
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_EMAIL,
                COLUMN_USER_FIRST_NAME,
                COLUMN_USER_LAST_NAME,
                COLUMN_USER_AGE,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_HEALTH_CONTACT_URI
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";
        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        Cursor cursor = db.query(TABLE_USER,//Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order
        // If the user exists
        if (cursor.getCount() == 1) {
            // Get the user and return
            cursor.moveToFirst();
            User user = getUserFromCursor(cursor);
            cursor.close();
            return user;
        } else {
            // Throw an error as the user does not exist
            throw new Exception("User id does not exist");
        }
    }

    /**
     * Update User record
     *
     * @param user
     */
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Set the user parameters from the cursor
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_USER_LAST_NAME, user.getLastName());
        values.put(COLUMN_USER_AGE, user.getAge());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_HEALTH_CONTACT_URI, user.getHealthContactURI());

        // updating row
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    /**
     * This method to check user exist or not
     * by email address.
     *
     * @param email
     * @return true/false
     */
    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        Cursor cursor = db.query(TABLE_USER,//Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        // If there's more than one match
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    /**
     * This method to check user exist or not
     * for password verification.
     *
     * @param email
     * @param password
     * @return true/false
     */
    public boolean checkUser(String email, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query user table with conditions
        Cursor cursor = db.query(TABLE_USER,//Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    /**
     * Delete all user by email
     *
     */
    public void deleteUsers(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER,"email=?",new String[]{email});
        db.close();
    }

    /**
     * Delete all users
     */
    public void deleteAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM " + TABLE_USER;
        db.execSQL(sql);
        db.close();
    }


    /**
     * Add a health activity
     *
     * @param health
     */
    public void addHealthActivity(Health health) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_HEALTH_ACTIVITY_USER_ID, health.getUserId());
        values.put(COLUMN_HEALTH_ACTIVITY_DATETIME, System.currentTimeMillis());
        values.put(COLUMN_HEALTH_ACTIVITY_TEMPERATURE, health.getTemperature());
        values.put(COLUMN_HEALTH_ACTIVITY_LOWER_BLOOD_PRESSURE, health.getLowerBloodPressure());
        values.put(COLUMN_HEALTH_ACTIVITY_HIGHER_BLOOD_PRESSURE, health.getHigherBloodPressure());
        values.put(COLUMN_HEALTH_ACTIVITY_HEART_BEATS_PER_MIN, health.getHeartBeatsPerMin());
        values.put(COLUMN_HEALTH_ACTIVITY_HEALTH_RATING, health.getHealthRating());

        db.insert(TABLE_HEALTH_ACTIVITY, null, values);
        db.close();
    }

    /**
     * Get all health activity by user
     *
     * @param user
     * @return List
     */
    public List<Health> getHealthActivityByUser(User user) {
        // Array of columns to fetch
        String[] columns = {
                COLUMN_HEALTH_ACTIVITY_ID,
                COLUMN_HEALTH_ACTIVITY_USER_ID,
                COLUMN_HEALTH_ACTIVITY_DATETIME,
                COLUMN_HEALTH_ACTIVITY_TEMPERATURE,
                COLUMN_HEALTH_ACTIVITY_LOWER_BLOOD_PRESSURE,
                COLUMN_HEALTH_ACTIVITY_HIGHER_BLOOD_PRESSURE,
                COLUMN_HEALTH_ACTIVITY_HEART_BEATS_PER_MIN,
                COLUMN_HEALTH_ACTIVITY_HEALTH_RATING
        };
        String selection = COLUMN_HEALTH_ACTIVITY_USER_ID + " = ?";
        String[] selectionArgs = {Long.toString(user.getId())};
        // Sort order
        String sortOrder = COLUMN_HEALTH_ACTIVITY_DATETIME + " DESC";
        List<Health> healthList = new ArrayList<Health>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Query the user database table
        Cursor cursor = db.query(TABLE_HEALTH_ACTIVITY,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        // Format the date
        DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");
        // If there are results
        if (cursor.moveToFirst()) {
            // Loop while results
            do {
                Health health = new Health();
                health.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_HEALTH_ACTIVITY_ID)));
                health.setUserId(cursor.getLong(cursor.getColumnIndex(COLUMN_HEALTH_ACTIVITY_USER_ID)));
                Date d = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_HEALTH_ACTIVITY_DATETIME)));
                health.setDateTime(df.format(d));
                health.setTemperature(cursor.getDouble(cursor.getColumnIndex(COLUMN_HEALTH_ACTIVITY_TEMPERATURE)));
                health.setLowerBloodPressure(cursor.getDouble(cursor.getColumnIndex(COLUMN_HEALTH_ACTIVITY_LOWER_BLOOD_PRESSURE)));
                health.setHigherBloodPressure(cursor.getDouble(cursor.getColumnIndex(COLUMN_HEALTH_ACTIVITY_HIGHER_BLOOD_PRESSURE)));
                health.setHeartBeatsPerMin(cursor.getDouble(cursor.getColumnIndex(COLUMN_HEALTH_ACTIVITY_HEART_BEATS_PER_MIN)));
                health.setHealthRating(cursor.getString(cursor.getColumnIndex(COLUMN_HEALTH_ACTIVITY_HEALTH_RATING)));
                // Adding user record to list
                healthList.add(health);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return healthList;
    }

    /**
     * Get health summary of user. Used for
     * display
     *
     * @param user
     * @return Array
     */
    public String[] getHealthActivitySummaryByUser(User user) {
        List<Health> healthList = getHealthActivityByUser(user);
        String[] healthSummaryList = new String[healthList.size()];
        int i = 0;
        for (Health health: healthList) {
            String healthSummary = health.getDateTime() +
                    "\nTemperature: " + health.getTemperature() +
                    "\nLow Blood: "+ health.getLowerBloodPressure() +
                    "\nHigh Blood: " + health.getHigherBloodPressure() +
                    "\nHeart Beats: " + health.getHeartBeatsPerMin() +
                    "\nRating: " + health.getHealthRating();
            healthSummaryList[i] = healthSummary;
            i++;
        }
        return healthSummaryList;
    }

    /**
     * Delete all health activity by user
     *
     */
    public void deleteHealthActivityByUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HEALTH_ACTIVITY,"user_id=?",new String[]{Long.toString(user.getId())});
        db.close();
    }

    /**
     * Delete all health activity
     *
     */
    public void deleteAllHealthActivity() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_HEALTH_ACTIVITY);
        db.close();
    }
}

