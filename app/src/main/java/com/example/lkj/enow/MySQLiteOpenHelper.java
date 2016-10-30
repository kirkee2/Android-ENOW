package com.example.lkj.enow;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    // 안드로이드에서 SQLite 데이터 베이스를 쉽게 사용할 수 있도록 도와주는 클래스
    public MySQLiteOpenHelper(Context context, String name,
                              CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table mytable(ip text, port text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table mytable;"; // 테이블 드랍
        db.execSQL(sql);
        onCreate(db); // 다시 테이블 생성
    }
}