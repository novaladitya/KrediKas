package com.praktikumobile.kredikas.db;

import android.provider.BaseColumns;

public class DatabaseContract {
    public static final class KasColumns implements BaseColumns {
        public static String TABLE_NAME = "hutang";
        public static String JENIS = "jenis";
        public static String JUMLAH = "jumlah";
        public static String CATATAN = "catatan";
        public static String TANGGAL = "tanggal";
    }

    public static final class HutangColumns implements BaseColumns {
        public static String TABLE_NAME = "kas";
        public static String JUMLAH = "jumlah";
        public static String CATATAN = "catatan";
        public static String BATAS_TANGGAL = "batasTanggal";
    }
}
