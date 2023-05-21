package me.matthewgordon.medicare.model;

import android.util.Patterns;

/**
 * User class. Holds information about a user
 *
 * @author  Matthew Gordon
 * @version 0.1.0
 * @since   18/04/2023
 */
public class User {
    // Unique id of user
    private long id;
    // Email address
    private String email;
    // First name
    private String firstName;
    // Last name
    private String lastName;
    // Age
    private long age;
    // Password
    private String password;
    // Android URI for health contact
    private String healthContactURI;

    // Id getter / setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Email getter / setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    // Names getter / setter
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Age getter / setter
    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    // Password getter / setter
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Health contact URI getter / setter
    public void setHealthContactURI(String healthContactURI) {
        this.healthContactURI = healthContactURI;
    }

    public String getHealthContactURI() {
        return this.healthContactURI;
    }

    /**
     * Registers a new User and performs
     * validation checks
     *
     * @param email
     * @param password1
     * @param password2
     * @param firstName
     * @param lastName
     * @param age
     * @param healthContactURI
     */
    public void register(String email, String password1, String password2, String firstName, String lastName, long age, String healthContactURI) throws Exception {
        // Check email is valid
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw new Exception("Invalid Email");
        }
        // Check age is 18 or above
        if (age < 18) {
            throw new Exception("Must be over 18");
        }
        // Check both passwords match
        if (!password1.equals(password2)) {
            throw new Exception("Passwords must match");
        }
        // Check password is not empty
        if (password1.isEmpty()) {
            throw new Exception("Password cannot be blank");
        }
        // Check password is a minimum of eight characters
        if (password1.length() < 8) {
            throw new Exception("Password must be more than 8 characters");
        }
        // Check name is filled in
        if (firstName.isEmpty() || lastName.isEmpty()) {
            throw new Exception("Names cannot be blank");
        }
        // Check Android contacts URI is valid
        if (!healthContactURI.startsWith("content://com.android.contacts/contacts")) {
            throw new Exception("Invalid contact");
        }
        this.email = email;
        this.password = password1;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.healthContactURI = healthContactURI;
    }
}
