package me.matthewgordon.medicare.model;

/**
 * Health class. Holds information about a health entry
 *
 * @author  Matthew Gordon
 * @version 0.1.0
 * @since   18/04/2023
 */
public class Health {
    // Unique id of health entry
    private long id;
    // User id this health entry is for
    private long userId;
    // Date adn time entry occurred
    private String dateTime;
    // Temperature
    private double temperature;
    // Lower blood pressure
    private double lowerBloodPressure;
    // Higher blood pressure
    private double higherBloodPressure;
    // Heart beats per minute
    private double heartBeatsPerMin;
    // Health rating
    private String healthRating;

    // Normal temperature
    private final double NORMAL_TEMPERATURE = 37.0F;
    // Normal lower blood pressure
    private final double NORMAL_LOWER_BLOOD_PRESSURE = 80.0F;
    // Normal higher blood pressure
    private final double NORMAL_HIGHER_BLOOD_PRESSURE = 120.0F;
    // normal heart rate
    private final double NORMAL_HEART_BEATS_PER_MIN = 72.0F;
    // Maximum low risk temperature
    private final double LOW_RISK_TEMPERATURE_MAX = 38.0F;
    // Maximum low risk lower blood pressure
    private final double LOW_RISK_LOWER_BLOOD_PRESSURE_MAX = 110.0F;
    // Maximum low risk higher blood pressure
    private final double LOW_RISK_HIGHER_BLOOD_PRESSURE_MAX = 180.0F;
    // Maximum low risk heart beat
    private final double LOW_RISK_HEART_BEATS_PER_MIN = 160.0F;

    // Health id getter / setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // User id getter / setter
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    // Date time getter / setter
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    // Temperature getter / setter
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    // Lower blood pressure getter / setter
    public double getLowerBloodPressure() {
        return lowerBloodPressure;
    }

    public void setLowerBloodPressure(double lowerBloodPressure) {
        this.lowerBloodPressure = lowerBloodPressure;
    }

    // Higher blood pressure getter / setter
    public double getHigherBloodPressure() {
        return higherBloodPressure;
    }

    public void setHigherBloodPressure(double higherBloodPressure) {
        this.higherBloodPressure = higherBloodPressure;
    }

    // Heart rate getter / setter
    public double getHeartBeatsPerMin() {
        return heartBeatsPerMin;
    }

    public void setHeartBeatsPerMin(double heartBeatsPerMin) {
        this.heartBeatsPerMin = heartBeatsPerMin;
    }

    // Health rating getter / setter
    public String getHealthRating() {
        return healthRating;
    }

    public void setHealthRating(String healthRating) {
        this.healthRating = healthRating;
    }

    /**
     * Registers a new health entry and
     * determines health rating.
     *
     * @param User
     * @param temperature
     * @param lowerBloodPressure
     * @param higherBloodPressure
     * @param heartBeatsPerMin
     */
    public void register(User user, double temperature, double lowerBloodPressure, double higherBloodPressure, double heartBeatsPerMin) {
        this.userId = user.getId();
        this.temperature = temperature;
        this.lowerBloodPressure = lowerBloodPressure;
        this.higherBloodPressure = higherBloodPressure;
        this.heartBeatsPerMin = heartBeatsPerMin;
        // Normal rating
        // If temperature is less than or equal to normal temperature and lower
        // and all other parameters are less then normal and heart rate is normal
        if (
                (temperature <= NORMAL_TEMPERATURE) &&
                        (lowerBloodPressure < NORMAL_LOWER_BLOOD_PRESSURE &&
                                higherBloodPressure < NORMAL_HIGHER_BLOOD_PRESSURE) &&
                        (heartBeatsPerMin == NORMAL_HEART_BEATS_PER_MIN)
        ) {
            this.healthRating = "Normal";
            return;
        }
        // High risk
        // If temperature is greater than or equal to the maximum low risk temperature
        // or lower/higher blood pressure and heart rate or greater than the maximum values.
        if (
                (temperature >= LOW_RISK_TEMPERATURE_MAX) ||
                        (lowerBloodPressure > LOW_RISK_LOWER_BLOOD_PRESSURE_MAX) ||
                        (higherBloodPressure > LOW_RISK_HIGHER_BLOOD_PRESSURE_MAX) ||
                        (heartBeatsPerMin > LOW_RISK_HEART_BEATS_PER_MIN)
        ) {
            this.healthRating = "High Risk";
            return;
        }
        // Low risk
        // If temperature is between normal and maximum low risk or lower/higher
        // blood pressures are between normal and maximum low risk or heart rate
        // is less than maximum low risk
        if (
                (temperature > NORMAL_TEMPERATURE && temperature <= LOW_RISK_TEMPERATURE_MAX) ||
                        (
                                (lowerBloodPressure >= NORMAL_LOWER_BLOOD_PRESSURE && lowerBloodPressure <= LOW_RISK_LOWER_BLOOD_PRESSURE_MAX) ||
                                        (higherBloodPressure >= NORMAL_HIGHER_BLOOD_PRESSURE && higherBloodPressure <= LOW_RISK_HIGHER_BLOOD_PRESSURE_MAX)
                        ) ||
                        (heartBeatsPerMin < LOW_RISK_HEART_BEATS_PER_MIN)
        ) {
            this.healthRating = "Low Risk";
            return;
        }
    }
}
