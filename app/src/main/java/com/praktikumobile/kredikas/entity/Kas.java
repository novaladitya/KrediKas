package com.praktikumobile.kredikas.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Kas implements Parcelable {
    private int id;
    private String jenis;
    private int jumlah;
    private String catatan;
    private String tanggal;

    //constructor
    public Kas(){
    }

    public Kas(int id, String jenis, int jumlah, String catatan, String tanggal){
        this.id = id;
        this.jenis = jenis;
        this.jumlah = jumlah;
        this.catatan = catatan;
        this.tanggal = tanggal;
    }

    protected Kas(Parcel in) {
        id = in.readInt();
        jenis = in.readString();
        jumlah = in.readInt();
        catatan = in.readString();
        tanggal = in.readString();
    }

    public static final Creator<Kas> CREATOR = new Creator<Kas>() {
        @Override
        public Kas createFromParcel(Parcel in) {
            return new Kas(in);
        }

        @Override
        public Kas[] newArray(int size) {
            return new Kas[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(jenis);
        parcel.writeInt(jumlah);
        parcel.writeString(catatan);
        parcel.writeString(tanggal);
    }
}
