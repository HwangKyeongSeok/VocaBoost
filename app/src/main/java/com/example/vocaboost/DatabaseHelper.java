package com.example.vocaboost;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "words.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_WORDS = "words";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_WORD = "word";
    private static final String COLUMN_MEANING = "meaning";
    private static final String COLUMN_DATE_ADDED = "date_added";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_WORDS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_WORD + " TEXT, "
                + COLUMN_MEANING + " TEXT, "
                + COLUMN_DATE_ADDED + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        onCreate(db);
    }

    // 데이터 삽입
    public void insertWord(String word, String meaning, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word);
        values.put(COLUMN_MEANING, meaning);
        values.put(COLUMN_DATE_ADDED, date);
        db.insert(TABLE_WORDS, null, values);
        db.close();
    }

    // 특정 날짜 데이터 조회
    public Cursor getWordsByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_WORDS, null, COLUMN_DATE_ADDED + "=?", new String[]{date}, null, null, null);
    }
}
