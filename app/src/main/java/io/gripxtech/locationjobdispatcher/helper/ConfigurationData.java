package io.gripxtech.locationjobdispatcher.helper;

public class ConfigurationData {


    public static final String TABLE_NAME = "tblConfiguration";

    public static final String CONFIGURATION_ID = "configurationId";
    public static final String USERID = "userId";
    public static final String PASSWORD = "password";
    public static final String TIMEINTERVAL = "timeInterval";
    // public static final String TIME = "time";
    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + CONFIGURATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + USERID + " INTEGER,"
                    + PASSWORD + " TEXT,"
                    + TIMEINTERVAL + " REAL,"

                    + ")";
    int configurationId;
    int userId;
    String password;
    String timeInterval;


    public ConfigurationData(int configurationId, int userId, String password, String timeInterval) {
        this.configurationId = configurationId;
        this.userId = userId;
        this.password = password;
        this.timeInterval = timeInterval;
    }

    public ConfigurationData() {
    }

    public int getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(int configurationId) {
        this.configurationId = configurationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }
}
