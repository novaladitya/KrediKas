package com.praktikumobile.kredikas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.praktikumobile.kredikas.adapter.KasAdapter;
import com.praktikumobile.kredikas.db.KasHelper;
import com.praktikumobile.kredikas.entity.Kas;
import com.praktikumobile.kredikas.helper.MappingHelper;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ArusKasFragment extends Fragment implements LoadKasCallback {

    public ArusKasFragment() {
        // Required empty public constructor
    }

    private ProgressBar progressBar;
    private RecyclerView rvKas;
    private KasAdapter adapter;
    private KasHelper kasHelper;
    private LinearLayout llKas;
    private TextView tvPemasukan, tvPengeluaran, tvTotalKas;
    private Cursor getPemasukan, getPengeluaran;
    private long jumlahPemasukan, jumlahPengeluaran, totalKas;

    private static final String EXTRA_STATE = "EXTRA_STATE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        View mView = inflater.inflate(R.layout.fragment_arus_kas, container, false);

        progressBar = (ProgressBar) mView.findViewById(R.id.progressbar);
        rvKas = (RecyclerView) mView.findViewById(R.id.rv_kas);
        llKas = (LinearLayout) mView.findViewById(R.id.ll_kas);
        tvPemasukan = (TextView) mView.findViewById(R.id.tv_pemasukan);
        tvPengeluaran = (TextView) mView.findViewById(R.id.tv_pengeluaran);
        tvTotalKas = (TextView) mView.findViewById(R.id.tv_totalKas);

        rvKas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvKas.setHasFixedSize(true);
        adapter = new KasAdapter(this);
        rvKas.setAdapter(adapter);

        FloatingActionButton fabAdd = mView.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), KasAddUpdateActivity.class);
            startActivityForResult(intent, KasAddUpdateActivity.REQUEST_ADD);
        });

        kasHelper = KasHelper.getInstance(getActivity().getApplicationContext());
        kasHelper.open();

        // proses ambil data
        getPemasukan();
        getPengeluaran();
        getTotalKas();
        new LoadKasAsync(kasHelper, this).execute();

        if (savedInstanceState == null) {
            // proses ambil data
            getPemasukan();
            getPengeluaran();
            getTotalKas();
            new LoadKasAsync(kasHelper, this).execute();
        } else {
            ArrayList<Kas> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                adapter.setListKas(list);
            }
        }

        return mView;
    }

    @SuppressLint("SetTextI18n")
    private void getPemasukan() {
        getPemasukan = kasHelper.getPemasukan();
        getPemasukan.moveToFirst();

        if (getPemasukan.getCount() > 0) {
            getPemasukan.moveToPosition(0);
            if (getPemasukan.isNull(0)) {
                tvPemasukan.setText("Rp. 0");
                jumlahPemasukan = 0;
            } else {
                jumlahPemasukan = Long.parseLong(getPemasukan.getString(0));
                DecimalFormat df = new DecimalFormat("#,###");
                tvPemasukan.setText("Rp. " + df.format(jumlahPemasukan));
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private void getPengeluaran() {
        getPengeluaran = kasHelper.getPengeluaran();
        getPengeluaran.moveToFirst();

        if (getPengeluaran.getCount() > 0) {
            getPengeluaran.moveToPosition(0);
            if (getPengeluaran.isNull(0)) {
                tvPengeluaran.setText("Rp. 0");
                jumlahPengeluaran = 0;
            } else {
                jumlahPengeluaran = Long.parseLong(getPengeluaran.getString(0));
                DecimalFormat df = new DecimalFormat("#,###");
                tvPengeluaran.setText("Rp. " + df.format(jumlahPengeluaran));
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private void getTotalKas() {
        totalKas = jumlahPemasukan - jumlahPengeluaran;
        DecimalFormat df = new DecimalFormat("#,###");
        tvTotalKas.setText("Rp. " + df.format(totalKas));
        if (totalKas < 0) {
            tvTotalKas.setTextColor(Color.parseColor("#FF0000"));
        } else if (totalKas > 0) {
            tvTotalKas.setTextColor(Color.parseColor("#01E701"));
        } else {
            tvTotalKas.setTextColor(Color.parseColor("#000000"));
        }
    }

    private static class LoadKasAsync extends AsyncTask<Void, Void, ArrayList<Kas>> {
        private final WeakReference<KasHelper> weakKasHelper;
        private final WeakReference<LoadKasCallback> weakCallback;
        private LoadKasAsync(KasHelper kasHelper, LoadKasCallback callback) {
            weakKasHelper = new WeakReference<>(kasHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }
        @Override
        protected ArrayList<Kas> doInBackground(Void... voids) {
            Cursor dataCursor = weakKasHelper.get().queryAll();
            return MappingHelper.mapCursorToArrayListKas(dataCursor);
        }
        @Override
        protected void onPostExecute(ArrayList<Kas> kas) {
            super.onPostExecute(kas);
            weakCallback.get().postExecute(kas);
        }
    }

    @Override
    public void preExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(ArrayList<Kas> kas) {
        progressBar.setVisibility(View.INVISIBLE);
        if (kas.size() > 0) {
            adapter.setListKas(kas);
        } else {
            adapter.setListKas(new ArrayList<Kas>());
            showSnackbarMessage("Tidak ada data saat ini");
        }
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(llKas, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            // Akan dipanggil jika request codenya ADD
            if (requestCode == KasAddUpdateActivity.REQUEST_ADD) {
                if (resultCode == KasAddUpdateActivity.RESULT_ADD) {
                    Kas kas = data.getParcelableExtra(KasAddUpdateActivity.EXTRA_KAS);
                    adapter.addItem(kas);
                    rvKas.smoothScrollToPosition(adapter.getItemCount() - 1);
                    getPemasukan();
                    getPengeluaran();
                    getTotalKas();
                    showSnackbarMessage("Catatan berhasil ditambahkan");
                }
            }
            // Update dan Delete memiliki request code sama akan tetapi result codenya berbeda
            else if (requestCode == KasAddUpdateActivity.REQUEST_UPDATE) {
                if (resultCode == KasAddUpdateActivity.RESULT_UPDATE) {
                    Kas kas = data.getParcelableExtra(KasAddUpdateActivity.EXTRA_KAS);
                    int position = data.getIntExtra(KasAddUpdateActivity.EXTRA_POSITION, 0);
                    adapter.updateItem(position, kas);
                    rvKas.smoothScrollToPosition(position);
                    getPemasukan();
                    getPengeluaran();
                    getTotalKas();
                    showSnackbarMessage("Catatan berhasil diubah");
                }
                else if (resultCode == KasAddUpdateActivity.RESULT_DELETE) {
                    int position = data.getIntExtra(KasAddUpdateActivity.EXTRA_POSITION, 0);
                    adapter.removeItem(position);
                    getPemasukan();
                    getPengeluaran();
                    getTotalKas();
                    showSnackbarMessage("Catatan berhasil dihapus");
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListKas());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        kasHelper.close();
    }
}

interface LoadKasCallback {
    void preExecute();
    void postExecute(ArrayList<Kas> kas);
}