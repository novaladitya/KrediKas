package com.praktikumobile.kredikas.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Hutang implements Parcelable {
    private int id;
    private int jumlah;
    private String catatan;
    private String batasTanggal;

    //constructor
    public Hutang(){
    }

    public Hutang(int id, int jumlah, String catatan, String batasTanggal){
        this.id = id;
        this.jumlah = jumlah;
        this.catatan = catatan;
        this.batasTanggal = batasTanggal;
    }

    protected Hutang(Parcel in) {
        id = in.readInt();
        jumlah = in.readInt();
        catatan = in.readString();
        batasTanggal = in.readString();
    }

    public static final Creator<Hutang> CREATOR = new Creator<Hutang>() {
        @Override
        public Hutang createFromParcel(Parcel in) {
            return new Hutang(in);
        }

        @Override
        public Hutang[] newArray(int size) {
            return new Hutang[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getBatasTanggal() {
        return batasTanggal;
    }

    public void setBatasTanggal(String batasTanggal) {
        this.batasTanggal = batasTanggal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(jumlah);
        parcel.writeString(catatan);
        parcel.writeString(batasTanggal);
    }
}
