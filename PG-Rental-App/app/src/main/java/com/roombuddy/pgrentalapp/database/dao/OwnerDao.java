package com.roombuddy.pgrentalapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.roombuddy.pgrentalapp.database.DatabaseHelper;

public class OwnerDao {

    private DatabaseHelper dbHelper;

    public OwnerDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean isEmailExists(String email) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT owner_id FROM owner WHERE email = ?",
                new String[]{email}
        );

        boolean exists = cursor != null && cursor.moveToFirst();

        if (cursor != null) cursor.close();

        return exists;
    }

    public boolean registerOwner(
            String name,
            String pgName,
            String email,
            String password,
            String phone,
            String address,
            String city
    ) {

        if (isEmailExists(email)) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("pg_name", pgName);
        values.put("email", email);
        values.put("password", password);
        values.put("phone", phone);
        values.put("address", address);
        values.put("city", city);

        long result = db.insert("owner", null, values);

        return result != -1;
    }

    public Cursor loginOwner(String email, String password) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM owner WHERE email = ? AND password = ?",
                new String[]{email, password}
        );
    }
    public int getOwnerIdByEmail(String email) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT owner_id FROM owner WHERE email = ?",
                new String[]{email}
        );

        int ownerId = -1;

        if (cursor != null && cursor.moveToFirst()) {
            ownerId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("owner_id")
            );
        }

        if (cursor != null) cursor.close();

        return ownerId;
    }
    public String getOwnerPhoneByEmail(String email) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String phone = null;

        Cursor cursor = db.rawQuery(
                "SELECT phone FROM owner WHERE email = ?",
                new String[]{email}
        );

        if (cursor != null && cursor.moveToFirst()) {
            phone = cursor.getString(
                    cursor.getColumnIndexOrThrow("phone")
            );
        }

        if (cursor != null) cursor.close();

        return phone;
    }
}
