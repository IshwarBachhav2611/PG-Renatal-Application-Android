package com.roombuddy.pgrentalapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.roombuddy.pgrentalapp.database.DatabaseHelper;

public class StudentDao {

    private final DatabaseHelper dbHelper;

    public StudentDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT student_id FROM student WHERE email = ?",
                new String[]{email}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
    public boolean registerStudent(
            String name,
            String email,
            String password,
            String phone,
            String address,
            String city,
            String gender
    ) {

        if (isEmailExists(email)) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("phone", phone);
        values.put("address", address);
        values.put("city", city);
        values.put("gender", gender);

        long result = db.insert("student", null, values);
        return result != -1;
    }

    public Cursor loginStudent(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM student WHERE email = ? AND password = ?",
                new String[]{email, password}
        );
    }
    public boolean updateStudentProfile(
            int studentId,
            String name,
            String email,
            String phone,
            String address,
            String city,
            String gender
    ) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        values.put("address", address);
        values.put("city", city);
        values.put("gender", gender);

        int rows = db.update(
                "student",
                values,
                "student_id = ?",
                new String[]{String.valueOf(studentId)}
        );

        return rows > 0;
    }

    public boolean checkOldPassword(int studentId, String oldPassword) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT student_id FROM student WHERE student_id = ? AND password = ?",
                new String[]{String.valueOf(studentId), oldPassword}
        );

        boolean match = cursor.moveToFirst();
        cursor.close();
        return match;
    }

    public boolean updatePassword(int studentId, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int rows = db.update(
                "student",
                values,
                "student_id = ?",
                new String[]{String.valueOf(studentId)}
        );

        return rows > 0;
    }

}
