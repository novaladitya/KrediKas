package com.praktikumobile.kredikas.helper;

import android.database.Cursor;

import com.praktikumobile.kredikas.db.DatabaseContract;
import com.praktikumobile.kredikas.entity.Kas;
import com.praktikumobile.kredikas.entity.Hutang;

import java.util.ArrayList;

public class MappingHelper {
    public static ArrayList<Kas> mapCursorToArrayListKas(Cursor kasCursor) {
        ArrayList<Kas> kasList = new ArrayList<>();
        while (kasCursor.moveToNext()) {
            int id = kasCursor.getInt(kasCursor.getColumnIndexOrThrow(DatabaseContract.KasColumns._ID));
            String jenis = kasCursor.getString(kasCursor.getColumnIndexOrThrow(DatabaseContract.KasColumns.JENIS));
            int jumlah = kasCursor.getInt(kasCursor.getColumnIndexOrThrow(DatabaseContract.KasColumns.JUMLAH));
            String catatan = kasCursor.getString(kasCursor.getColumnIndexOrThrow(DatabaseContract.KasColumns.CATATAN));
            String tanggal = kasCursor.getString(kasCursor.getColumnIndexOrThrow(DatabaseContract.KasColumns.TANGGAL));
            kasList.add(new Kas(id, jenis, jumlah, catatan, tanggal));
        }
        return kasList;
    }

    public static ArrayList<Hutang> mapCursorToArrayListHutang(Cursor hutangCursor) {
        ArrayList<Hutang> hutangList = new ArrayList<>();
        while (hutangCursor.moveToNext()) {
            int id = hutangCursor.getInt(hutangCursor.getColumnIndexOrThrow(DatabaseContract.HutangColumns._ID));
            int jumlah = hutangCursor.getInt(hutangCursor.getColumnIndexOrThrow(DatabaseContract.HutangColumns.JUMLAH));
            String catatan = hutangCursor.getString(hutangCursor.getColumnIndexOrThrow(DatabaseContract.HutangColumns.CATATAN));
            String batasTanggal = hutangCursor.getString(hutangCursor.getColumnIndexOrThrow(DatabaseContract.HutangColumns.BATAS_TANGGAL));
            hutangList.add(new Hutang(id, jumlah, catatan, batasTanggal));
        }
        return hutangList;
    }
}
