package me.matthewgordon.medicare;

import static org.junit.Assert.assertEquals;
import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;
import java.util.List;

import me.matthewgordon.medicare.infrastructure.MedicareRepository;
import me.matthewgordon.medicare.model.Health;
import me.matthewgordon.medicare.model.User;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MedicareRepositoryTest {
    private MedicareRepository medicareRepository;
    private Context appContext;

    @Before
    public void setup() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        medicareRepository = new MedicareRepository(appContext);
    }

    @After
    public void tearDown() throws Exception {
        medicareRepository.close();
    }

    @Test
    public void test01_databaseName() {
        assert medicareRepository.getDatabaseName() == "Medicare.db";
    }

    @Test
    public void test02_addUser() {
        User user = new User();
        user.setEmail("matthew.s.gordon@btinternet.com");
        user.setFirstName("Matthew");
        user.setLastName("Gordon");
        user.setAge(20);
        user.setPassword("P@ssw0rd!");
        user.setHealthContactURI("content://contacts/1");
        medicareRepository.addUser(user);
        try {
            user = medicareRepository.getUserByEmail("matthew.s.gordon@btinternet.com");
        } catch (Exception e) {
            // Do nothing
        }
        user.setHealthContactURI("content://contacts/2");
        user.setAge(19);
        medicareRepository.updateUser(user);
    }

    @Test
    public void test03_getUserByEmail() {
        try {
            User user = medicareRepository.getUserByEmail("matthew.s.gordon@btinternet.com");
            assert user.getFirstName().equals("Matthew") == true;
            assert user.getAge() == 19;
            assert user.getHealthContactURI().equals("content://contacts/2");
        } catch (Exception e) {
            // do nothing
        }
    }

    @Test
    public void test04_checkUser() {
        assert medicareRepository.checkUser("matthew.s.gordon@btinternet.com") == true;
    }

    @Test
    public void test05_checkUserPassword() {
        assert medicareRepository.checkUser("matthew.s.gordon@btinternet.com","P@ssw0rd!") == true;
    }

    @Test
    public void test06_getAllUsers() {
        List<User> userList = medicareRepository.getAllUser();
        assert userList.size() == 1;
        User user = userList.get(0);
        assert user.getEmail().equals("matthew.s.gordon@btinternet.com") == true;
    }

    @Test
    public void test07_addHealthActivity() {
        List<User> userList = medicareRepository.getAllUser();
        User user = userList.get(0);
        Health healthActivity = new Health();
        healthActivity.setUserId(user.getId());
        healthActivity.setTemperature(37.0F);
        healthActivity.setLowerBloodPressure(80.0F);
        healthActivity.setHigherBloodPressure(120.0F);
        healthActivity.setHeartBeatsPerMin(72.0F);
        healthActivity.setHealthRating("Low Risk");
        medicareRepository.addHealthActivity(healthActivity);
        List<Health> healthActivityList = medicareRepository.getHealthActivityByUser(user);
        assert healthActivityList.size() == 1;
        healthActivity = healthActivityList.get(0);
        assert healthActivity.getUserId() == user.getId();
        assert healthActivity.getTemperature() == 37.0F;
        assert healthActivity.getLowerBloodPressure() == 80.0F;
        assert healthActivity.getHigherBloodPressure() == 120.0F;
        assert healthActivity.getHeartBeatsPerMin() == 72.0F;
        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        Date d = new Date(System.currentTimeMillis());
        assert healthActivity.getDateTime().contains(df.format(d)) == true;
        String[] healthActivitySummaryList = medicareRepository.getHealthActivitySummaryByUser(user);
        assert healthActivitySummaryList.length == 1;
        assert healthActivitySummaryList[0].contains(df.format(d)) == true;
        assert healthActivitySummaryList[0].contains("Low Risk") == true;
    }

    @Test
    public void test08_deleteHealthActivityByUser() {
        List<User> userList = medicareRepository.getAllUser();
        User user = userList.get(0);
        medicareRepository.deleteHealthActivityByUser(user);
    }

    @Test
    public void test09_deleteAllUsers() {
        medicareRepository.deleteAllHealthActivity();
        medicareRepository.deleteAllUsers();
    }
}