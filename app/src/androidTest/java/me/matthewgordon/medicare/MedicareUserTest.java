package me.matthewgordon.medicare;

import static org.junit.Assert.assertThrows;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import me.matthewgordon.medicare.model.User;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MedicareUserTest {
    private Context appContext;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }
    @Test
    public void test01_registerUserInvalidEmail() throws Exception {
        User user = new User();
        thrown.expect(Exception.class);
        thrown.expectMessage("Invalid Email");
        user.register("Matthew.gordon68","Password1234","Password1234","Matthew","Gordon",55,"content://com.android.contacts/contacts");

    }
    @Test
    public void test02_registerUserEmptyEmail() throws Exception {
        User user = new User();
        thrown.expect(Exception.class);
        thrown.expectMessage("Invalid Email");
        user.register("","Password1234","Password1234","Matthew","Gordon",55,"content://com.android.contacts/contacts");

    }
    @Test
    public void test03_registerUserPasswordNoMatch() throws Exception {
        User user = new User();
        thrown.expect(Exception.class);
        thrown.expectMessage("Passwords must match");
        user.register("matthew.s.gordon@btinternet.com","Password1234","Password1235","Matthew","Gordon",55,"content://com.android.contacts/contacts");

    }
    @Test
    public void test04_registerUserPasswordLength() throws Exception {
        User user = new User();
        thrown.expect(Exception.class);
        thrown.expectMessage("Password must be more than 8 characters");
        user.register("matthew.s.gordon@btinternet.com","P","P","Matthew","Gordon",55,"content://com.android.contacts/contacts");

    }
    @Test
    public void test05_registerUserOver18() throws Exception {
        User user = new User();
        thrown.expect(Exception.class);
        thrown.expectMessage("Must be over 18");
        user.register("matthew.s.gordon@btinternet.com","Password1234","Password1234","Matthew","Gordon",17,"content://com.android.contacts/contacts");
    }

    @Test
    public void test06_invalidContactUri() throws Exception {
        User user = new User();
        thrown.expect(Exception.class);
        thrown.expectMessage("Invalid contact");
        user.register("matthew.s.gordon@btinternet.com","Password1234","Password1234","Matthew","Gordon",55,"content://com.android.xxx/xxx");
    }
}