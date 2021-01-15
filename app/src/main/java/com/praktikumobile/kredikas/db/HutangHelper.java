package com.praktikumobile.kredikas.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.praktikumobile.kredikas.db.DatabaseContract.HutangColumns.JUMLAH;
import static com.praktikumobile.kredikas.db.DatabaseContract.HutangColumns.TABLE_NAME;

public class HutangHelper {
    private static final String DATABASE_TABLE = TABLE_NAME;
    private static DatabaseHelper dataBaseHelper;
    private static HutangHelper INSTANCE;
    private static SQLiteDatabase database;

    private HutangHelper(Context context){
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static HutangHelper getInstance(Context context){
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HutangHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
    }

    public void close() throws SQLException {
        database.close();
        if (database.isOpen()) database.close();
    }

    public Cursor queryAll() {
        return database.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                _ID + " ASC");
    }

    public Cursor queryById(String id) {
        return database.query(
                DATABASE_TABLE,
                null,
                _ID + " = ?",
                new String[]{id},
                null,
                null,
                null,
                null);
    }

    public Cursor getTotal(){
        return database.rawQuery("SELECT SUM(" + JUMLAH + ") FROM " + DATABASE_TABLE + "", null);
    }

    public long insert(ContentValues values) {
        return database.insert(DATABASE_TABLE, null, values);
    }

    public int update(String id, ContentValues values) {
        return database.update(DATABASE_TABLE, values, _ID + " = ?", new String[]{id});
    }

    public int deleteById(String id) {
        return database.delete(DATABASE_TABLE, _ID + " = ?", new String[]{id});
    }
}
