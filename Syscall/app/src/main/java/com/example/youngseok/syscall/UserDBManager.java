package com.example.youngseok.syscall;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDBManager extends SQLiteOpenHelper {

    public UserDBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user " +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "brand TEXT, " +
                    "classification TEXT, " +
                    "color TEXT, " +
                    "startDriver INTEGER, " +
                    "ordinaryEyeSize REAL, " +
                    "sleepyEyeSize REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String brand, String classification, String color, int startDriver, float ordinaryEyeSize, float sleepyEyeSize) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO user " +
                    "VALUES (NULL, '" +
                    brand + "', '" +
                    classification + "', '" +
                    color + "', " +
                    startDriver + ", " +
                    ordinaryEyeSize + ", " +
                    sleepyEyeSize + ");");

        Log.d("SQL", "<Brand : " + brand + "> <Classification : " + classification + "> <Color : " + color + "> <Start Driver : " + startDriver + "> <Ordinary Eye Size : " + ordinaryEyeSize + "> <Sleepy Eye Size : " + sleepyEyeSize + ">");
        db.close();
    }

    public void delete() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM user");
        db.close();
    }
}
