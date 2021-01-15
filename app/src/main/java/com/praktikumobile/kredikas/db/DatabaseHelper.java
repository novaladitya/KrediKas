package com.praktikumobile.kredikas.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.praktikumobile.kredikas.db.DatabaseContract.KasColumns;
import com.praktikumobile.kredikas.db.DatabaseContract.HutangColumns;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "dbkredikas";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_KAS = String.format("CREATE TABLE %s" +
            " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
            " %s TEXT NOT NULL," +
            " %s INTEGER NOT NULL," +
            " %s TEXT NOT NULL," +
            " %s TEXT NOT NULL)",
            KasColumns.TABLE_NAME,
            KasColumns._ID,
            KasColumns.JENIS,
            KasColumns.JUMLAH,
            KasColumns.CATATAN,
            KasColumns.TANGGAL
    );
    private static final String SQL_CREATE_TABLE_HUTANG = String.format("CREATE TABLE %s" +
                    " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s INTEGER NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            HutangColumns.TABLE_NAME,
            HutangColumns._ID,
            HutangColumns.JUMLAH,
            HutangColumns.CATATAN,
            HutangColumns.BATAS_TANGGAL
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_KAS);
        db.execSQL(SQL_CREATE_TABLE_HUTANG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + KasColumns.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HutangColumns.TABLE_NAME);
        onCreate(db);
    }
}
