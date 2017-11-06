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
        db.execSQL("CREATE TABLE USER (serverID INTEGER PRIMARY KEY, wearGlasses INTEGER, eyeSize REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(int serverID, int wearGlasses, float eyeSize) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO USER VALUES ( " + serverID + ", " + wearGlasses + ", " + eyeSize + ");");

        Log.d("DB Insert", "(serverID : " + serverID + ") (wearGlasses : " + wearGlasses + ") (eyeSize : " + eyeSize + ")");
        db.close();
    }

    public void delete() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM USER");
        db.close();
    }

    public void dropTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE USER");
        db.close();
    }
}
