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
        db.execSQL("CREATE TABLE USER (serverID INTEGER PRIMARY KEY, eyeSize REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(int serverID, float eyeSize) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO USER VALUES ( " + serverID + ", " + eyeSize + ");");

        Log.d("DB Insert", "<serverID : " + serverID + "> <eyeSize : " + eyeSize + ">");
        db.close();
    }

    public void delete() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM USER");
        db.close();
    }
}
