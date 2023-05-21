package me.matthewgordon.medicare;

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

import me.matthewgordon.medicare.model.Health;
import me.matthewgordon.medicare.model.User;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MedicareHealthTest {
    private Context appContext;
    private User user = new User();

    @Before
    public void setup() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        user.setId(1);
        user.setEmail("matthew.s.gordon@btinternet.com");
        user.setPassword("P@ssw0rd!");
        user.setFirstName("Matthew");
        user.setLastName("Gordon");
        user.setAge(55);
        user.setHealthContactURI("content://com.android.contacts/contacts/lookup/1");
    }

    @Test
    public void test01_registerHealthNormal() {
        Health health = new Health();
        health.register(user,36,79,119,72);
        assert  health.getHealthRating().equals("Normal") == true;
    }

    @Test
    public void test02_registerHealthLowRisk1() {
        Health health = new Health();
        health.register(user,37,80,110,159);
        assert health.getHealthRating().equals("Low Risk") == true;
    }

    @Test
    public void test03_registerHealthHighRisk1() {
        Health health = new Health();
        health.register(user,38,80,110,159);
        assert health.getHealthRating().equals("High Risk") == true;
    }

    @Test
    public void test04_registerHealthLowRisk2() {
        Health health = new Health();
        health.register(user,37.99,110,180,159);
        assert health.getHealthRating().equals("Low Risk") == true;
    }

    @Test
    public void test05_registerHealthHighRisk3() {
        Health health = new Health();
        health.register(user,38,70,110,159);
        assert health.getHealthRating().equals("High Risk") == true;
    }

    @Test
    public void test06_registerHealthHighRisk4() {
        Health health = new Health();
        health.register(user,38,111,110,159);
        assert health.getHealthRating().equals("High Risk") == true;
    }

    @Test
    public void test07_registerHealthHighRisk5() {
        Health health = new Health();
        health.register(user,38,70,110,161);
        assert health.getHealthRating().equals("High Risk") == true;
    }
}