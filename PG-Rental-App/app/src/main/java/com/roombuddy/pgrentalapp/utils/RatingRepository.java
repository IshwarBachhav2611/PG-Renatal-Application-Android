package com.roombuddy.pgrentalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class RatingRepository {

    private static final String PREF_NAME = "PG_RATINGS";
    private static final String KEY_TOTAL = "_total";
    private static final String KEY_COUNT = "_count";

    // 🔹 Add new rating
    public static void addRating(Context context, String pgName, float rating) {

        if (context == null || pgName == null || pgName.trim().isEmpty()) {
            return;
        }

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String totalKey = pgName + KEY_TOTAL;
        String countKey = pgName + KEY_COUNT;

        float total = prefs.getFloat(totalKey, 0f);
        int count = prefs.getInt(countKey, 0);

        total += rating;
        count++;

        prefs.edit()
                .putFloat(totalKey, total)
                .putInt(countKey, count)
                .apply();
    }

    public static float getAverageRating(Context context, String pgName) {

        if (context == null || pgName == null || pgName.trim().isEmpty()) {
            return 0f;
        }

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        float total = prefs.getFloat(pgName + KEY_TOTAL, 0f);
        int count = prefs.getInt(pgName + KEY_COUNT, 0);

        if (count == 0) return 0f;

        return total / count;
    }

    public static int getRatingCount(Context context, String pgName) {

        if (context == null || pgName == null || pgName.trim().isEmpty()) {
            return 0;
        }

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return prefs.getInt(pgName + KEY_COUNT, 0);
    }
}
