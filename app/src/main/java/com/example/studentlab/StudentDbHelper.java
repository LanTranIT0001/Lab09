package com.example.studentlab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudentDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "student.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_STUDENT = "student";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_TEL = "telephone";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_STUDENT + " (" +
                    COL_ID + " INTEGER PRIMARY KEY, " +
                    COL_NAME + " TEXT, " +
                    COL_EMAIL + " TEXT, " +
                    COL_TEL + " TEXT)";

    public StudentDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        onCreate(db);
    }

    /** Lưu Student duy nhất: xóa dữ liệu cũ rồi chèn bản ghi mới (trong 1 transaction). */
    public boolean saveStudent(Student s) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_STUDENT, null, null);

            ContentValues values = new ContentValues();
            values.put(COL_ID, s.getId());
            values.put(COL_NAME, s.getName());
            values.put(COL_EMAIL, s.getEmail());
            values.put(COL_TEL, s.getTelephone());

            long row = db.insert(TABLE_STUDENT, null, values);
            if (row == -1) return false;

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    /** Đọc Student (bản ghi đầu tiên). Trả về null nếu bảng rỗng. */
    public Student getStudent() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_STUDENT, null, null, null, null, null, null, "1");
        try {
            if (c.moveToFirst()) {
                return new Student(
                        c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                        c.getString(c.getColumnIndexOrThrow(COL_EMAIL)),
                        c.getString(c.getColumnIndexOrThrow(COL_TEL)));
            }
            return null;
        } finally {
            c.close();
        }
    }
}