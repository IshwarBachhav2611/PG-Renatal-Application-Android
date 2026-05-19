package com.roombuddy.pgrentalapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pg_rental.db";
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE student (" +
                        "student_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "email TEXT UNIQUE," +
                        "password TEXT," +
                        "phone TEXT," +
                        "address TEXT," +
                        "city TEXT," +
                        "gender TEXT)"
        );


        db.execSQL(
                "CREATE TABLE owner (" +
                        "owner_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "pg_name TEXT," +
                        "email TEXT UNIQUE," +
                        "password TEXT," +
                        "phone TEXT," +
                        "address TEXT," +
                        "city TEXT" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE pg (" +
                        "pg_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "pg_name TEXT," +
                        "location TEXT," +
                        "rent TEXT," +
                        "type TEXT," +
                        "address TEXT," +
                        "description TEXT," +
                        "owner_name TEXT," +
                        "owner_phone TEXT," +
                        "owner_email TEXT," +
                        "image_uri TEXT," +
                        "latitude REAL," +
                        "longitude REAL" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE booking (" +
                        "booking_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "pg_name TEXT," +
                        "rent TEXT," +
                        "pg_address TEXT," +
                        "pg_city TEXT," +
                        "student_name TEXT," +
                        "student_email TEXT," +
                        "student_phone TEXT," +
                        "owner_email TEXT," +
                        "status TEXT" +
                        ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS student");
        db.execSQL("DROP TABLE IF EXISTS owner");
        db.execSQL("DROP TABLE IF EXISTS pg");
        db.execSQL("DROP TABLE IF EXISTS booking");
        onCreate(db);
    }

}
