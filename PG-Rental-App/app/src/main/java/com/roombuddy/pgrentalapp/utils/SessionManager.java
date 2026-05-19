package com.roombuddy.pgrentalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "pg_session";

    private static final String KEY_LOGIN = "is_logged_in";
    private static final String KEY_ROLE = "user_role";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PHONE = "user_phone";

    private static final String KEY_ADDRESS = "user_address";
    private static final String KEY_CITY = "user_city";
    private static final String KEY_GENDER = "user_gender";

    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_OWNER = "OWNER";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }
    public void login(String role, String name, String email, String phone) {
        editor.putBoolean(KEY_LOGIN, true);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.apply();
    }

    public void createLoginSession(
            int userId,
            String name,
            String email,
            String phone,
            String address,
            String city,
            String gender,
            String role
    ) {
        editor.putBoolean(KEY_LOGIN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_ROLE, role);

        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_GENDER, gender);

        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGIN, false);
    }

    public String getUserRole() {
        return prefs.getString(KEY_ROLE, "");
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return prefs.getString(KEY_NAME, "User");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getUserPhone() {
        return prefs.getString(KEY_PHONE, "");
    }

    public String getUserAddress() {
        return prefs.getString(KEY_ADDRESS, "");
    }

    public String getUserCity() {
        return prefs.getString(KEY_CITY, "");
    }

    public String getUserGender() {
        return prefs.getString(KEY_GENDER, "");
    }

    public void updateProfile(
            String name,
            String email,
            String phone,
            String address,
            String city,
            String gender
    ) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_GENDER, gender);
        editor.apply();
    }

    public void setBookingStatus(int pgId, String status) {
        editor.putString("booking_pg_" + pgId, status);
        editor.apply();
    }

    public String getBookingStatus(int pgId) {
        return prefs.getString("booking_pg_" + pgId, "Pending");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
