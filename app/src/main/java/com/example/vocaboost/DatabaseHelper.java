package com.example.vocaboost;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "words.db";
    private static final int DATABASE_VERSION = 3; // 버전 업데이트

    private static final String TABLE_WORDS = "words";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_WORD = "word";
    private static final String COLUMN_MEANING = "meaning";
    private static final String COLUMN_EXAMPLE = "example"; // 예문 추가
    private static final String COLUMN_TRANSLATION = "translation"; // 예문 해석 추가
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
                + COLUMN_EXAMPLE + " TEXT, " // 예문 컬럼 추가
                + COLUMN_TRANSLATION + " TEXT, " // 예문 해석 컬럼 추가
                + COLUMN_DATE_ADDED + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_WORDS + " ADD COLUMN " + COLUMN_EXAMPLE + " TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_WORDS + " ADD COLUMN " + COLUMN_TRANSLATION + " TEXT");
        }
    }

    // 데이터 삽입
    public void insertWord(String word, String meaning, String example, String translation, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word);
        values.put(COLUMN_MEANING, meaning);
        values.put(COLUMN_EXAMPLE, example); // 예문 추가
        values.put(COLUMN_TRANSLATION, translation); // 예문 해석 추가
        values.put(COLUMN_DATE_ADDED, date);
        db.insert(TABLE_WORDS, null, values);
        db.close();
    }

    // 특정 단어 데이터 조회
    public Cursor getWordByWord(String word) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_WORDS, null, COLUMN_WORD + "=?", new String[]{word}, null, null, null);
    }

    // 특정 날짜 데이터 조회
    public Cursor getWordsByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_WORDS, // 테이블 이름
                null, // 모든 컬럼 선택
                COLUMN_DATE_ADDED + "=?", // 조건
                new String[]{date}, // 조건 값
                null, // 그룹화
                null, // 필터
                null  // 정렬
        );
    }
}