package com.roombuddy.pgrentalapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.roombuddy.pgrentalapp.database.DatabaseHelper;
import com.roombuddy.pgrentalapp.model.PgModel;

import java.util.ArrayList;
import java.util.List;

public class PgRepository {

    public static void addPg(Context context, PgModel pg) {

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("pg_name", pg.getName());
        cv.put("location", pg.getLocation());
        cv.put("rent", pg.getRent());
        cv.put("type", pg.getType());
        cv.put("address", pg.getAddress());
        cv.put("description", pg.getDescription());
        cv.put("owner_name", pg.getOwnerName());
        cv.put("owner_phone", pg.getOwnerPhone());
        cv.put("owner_email", pg.getOwnerEmail());
        cv.put("image_uri", pg.getImageUri());
        cv.put("latitude", pg.getLatitude());
        cv.put("longitude", pg.getLongitude());

        db.insert("pg", null, cv);
        db.close();
    }

    public static void updatePg(Context context, PgModel pg) {

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("pg_name", pg.getName());
        cv.put("location", pg.getLocation());
        cv.put("rent", pg.getRent());
        cv.put("type", pg.getType());
        cv.put("address", pg.getAddress());
        cv.put("description", pg.getDescription());
        cv.put("owner_phone", pg.getOwnerPhone());
        cv.put("image_uri", pg.getImageUri());
        cv.put("latitude", pg.getLatitude());
        cv.put("longitude", pg.getLongitude());

        db.update("pg", cv, "pg_id=?", new String[]{String.valueOf(pg.getPgId())});
        db.close();
    }

    public static void deletePg(Context context, int pgId) {

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        db.delete("pg", "pg_id=?", new String[]{String.valueOf(pgId)});
        db.close();
    }

    public static List<PgModel> getPgList(Context context) {

        List<PgModel> list = new ArrayList<>();
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM pg", null);
        if (c.moveToFirst()) {
            do {
                list.add(buildPgFromCursor(c));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    public static List<PgModel> searchPgs(
            Context context,
            String pgNameKeyword,
            String location,
            String addressKeyword,
            Integer minRent,
            Integer maxRent
    ) {

        List<PgModel> list = new ArrayList<>();
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();

        StringBuilder query = new StringBuilder("SELECT * FROM pg WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (pgNameKeyword != null && !pgNameKeyword.trim().isEmpty()) {
            query.append(" AND pg_name LIKE ?");
            args.add("%" + pgNameKeyword + "%");
        }

        if (location != null && !location.trim().isEmpty()) {
            query.append(" AND location LIKE ?");
            args.add("%" + location + "%");
        }

        if (addressKeyword != null && !addressKeyword.trim().isEmpty()) {
            query.append(" AND address LIKE ?");
            args.add("%" + addressKeyword + "%");
        }

        if (minRent != null) {
            query.append(" AND CAST(REPLACE(REPLACE(rent,'₹',''),' ', '') AS INTEGER) >= ?");
            args.add(String.valueOf(minRent));
        }

        if (maxRent != null) {
            query.append(" AND CAST(REPLACE(REPLACE(rent,'₹',''),' ', '') AS INTEGER) <= ?");
            args.add(String.valueOf(maxRent));
        }

        Cursor c = db.rawQuery(query.toString(), args.toArray(new String[0]));

        if (c.moveToFirst()) {
            do {
                list.add(buildPgFromCursor(c));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    public static PgModel getPgById(Context context, int pgId) {

        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM pg WHERE pg_id=?",
                new String[]{String.valueOf(pgId)});

        PgModel pg = null;
        if (c.moveToFirst()) {
            pg = buildPgFromCursor(c);
        }

        c.close();
        db.close();
        return pg;
    }

    public static List<PgModel> getPgsByOwner(Context context, String ownerEmail) {

        List<PgModel> list = new ArrayList<>();
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM pg WHERE owner_email=?",
                new String[]{ownerEmail}
        );

        if (c.moveToFirst()) {
            do {
                list.add(buildPgFromCursor(c));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    private static PgModel buildPgFromCursor(Cursor c) {

        PgModel pg = new PgModel(
                c.getString(c.getColumnIndexOrThrow("pg_name")),
                c.getString(c.getColumnIndexOrThrow("location")),
                c.getString(c.getColumnIndexOrThrow("rent")),
                c.getString(c.getColumnIndexOrThrow("type")),
                c.getString(c.getColumnIndexOrThrow("address")),
                c.getString(c.getColumnIndexOrThrow("description")),
                c.getString(c.getColumnIndexOrThrow("owner_name")),
                c.getString(c.getColumnIndexOrThrow("owner_phone")),
                c.getString(c.getColumnIndexOrThrow("owner_email")),
                c.getString(c.getColumnIndexOrThrow("image_uri")),
                c.getDouble(c.getColumnIndexOrThrow("latitude")),
                c.getDouble(c.getColumnIndexOrThrow("longitude"))
        );

        pg.setPgId(c.getInt(c.getColumnIndexOrThrow("pg_id")));
        return pg;
    }
}
