package com.praktikumobile.kredikas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
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
import com.praktikumobile.kredikas.adapter.HutangAdapter;
import com.praktikumobile.kredikas.db.HutangHelper;
import com.praktikumobile.kredikas.entity.Hutang;
import com.praktikumobile.kredikas.helper.MappingHelper;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class HutangFragment extends Fragment implements LoadHutangCallback {

    public HutangFragment() {
        // Required empty public constructor
    }

    private ProgressBar progressBar;
    private RecyclerView rvHutang;
    private HutangAdapter adapter;
    private HutangHelper hutangHelper;
    private LinearLayout llHutang;
    private TextView tvTotalHutang;
    private Cursor getTotalHutang;

    private static final String EXTRA_STATE = "EXTRA_STATE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_hutang, container, false);

        progressBar = (ProgressBar) mView.findViewById(R.id.progressbar_htg);
        rvHutang = (RecyclerView) mView.findViewById(R.id.rv_hutang);
        llHutang = (LinearLayout) mView.findViewById(R.id.ll_hutang);
        tvTotalHutang = (TextView) mView.findViewById(R.id.tv_totalHutang);

        rvHutang.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHutang.setHasFixedSize(true);
        adapter = new HutangAdapter(this);
        rvHutang.setAdapter(adapter);

        FloatingActionButton fabAddHutang = mView.findViewById(R.id.fab_add_htg);
        fabAddHutang.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), HutangAddUpdateActivity.class);
            startActivityForResult(intent, HutangAddUpdateActivity.REQUEST_ADD);
        });

        hutangHelper = HutangHelper.getInstance(getActivity().getApplicationContext());
        hutangHelper.open();

        // proses ambil data
        getTotalHutang();
        new LoadHutangAsync(hutangHelper, this).execute();

        if (savedInstanceState == null) {
            // proses ambil data
            getTotalHutang();
            new LoadHutangAsync(hutangHelper, this).execute();
        } else {
            ArrayList<Hutang> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                adapter.setListHutang(list);
            }
        }

        return mView;
    }

    @SuppressLint("SetTextI18n")
    private void getTotalHutang() {
        getTotalHutang = hutangHelper.getTotal();
        getTotalHutang.moveToFirst();

        if (getTotalHutang.getCount() > 0) {
            getTotalHutang.moveToPosition(0);
            if (getTotalHutang.isNull(0)) {
                tvTotalHutang.setText("Rp. 0");
            } else {
                long totalHutang = Long.parseLong(getTotalHutang.getString(0));
                DecimalFormat df = new DecimalFormat("#,###");
                tvTotalHutang.setText("Rp. " + df.format(totalHutang));
            }
        }
    }

    private static class LoadHutangAsync extends AsyncTask<Void, Void, ArrayList<Hutang>> {
        private final WeakReference<HutangHelper> weakHutangHelper;
        private final WeakReference<LoadHutangCallback> weakCallback;
        private LoadHutangAsync(HutangHelper hutangHelper, LoadHutangCallback callback) {
            weakHutangHelper = new WeakReference<>(hutangHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }
        @Override
        protected ArrayList<Hutang> doInBackground(Void... voids) {
            Cursor dataCursor = weakHutangHelper.get().queryAll();
            return MappingHelper.mapCursorToArrayListHutang(dataCursor);
        }
        @Override
        protected void onPostExecute(ArrayList<Hutang> hutang) {
            super.onPostExecute(hutang);
            weakCallback.get().postExecute(hutang);
        }
    }

    @Override
    public void preExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(ArrayList<Hutang> hutang) {
        progressBar.setVisibility(View.INVISIBLE);
        if (hutang.size() > 0) {
            adapter.setListHutang(hutang);
        } else {
            adapter.setListHutang(new ArrayList<Hutang>());
            showSnackbarMessage("Tidak ada data saat ini");
        }
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(llHutang, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            // Akan dipanggil jika request codenya ADD
            if (requestCode == HutangAddUpdateActivity.REQUEST_ADD) {
                if (resultCode == HutangAddUpdateActivity.RESULT_ADD) {
                    Hutang hutang = data.getParcelableExtra(HutangAddUpdateActivity.EXTRA_HUTANG);
                    adapter.addItem(hutang);
                    rvHutang.smoothScrollToPosition(adapter.getItemCount() - 1);
                    getTotalHutang();
                    showSnackbarMessage("Catatan berhasil ditambahkan");
                }
            }
            // Update dan Delete memiliki request code sama akan tetapi result codenya berbeda
            else if (requestCode == HutangAddUpdateActivity.REQUEST_UPDATE) {
                if (resultCode == HutangAddUpdateActivity.RESULT_UPDATE) {
                    Hutang hutang = data.getParcelableExtra(HutangAddUpdateActivity.EXTRA_HUTANG);
                    int position = data.getIntExtra(HutangAddUpdateActivity.EXTRA_POSITION, 0);
                    adapter.updateItem(position, hutang);
                    rvHutang.smoothScrollToPosition(position);
                    getTotalHutang();
                    showSnackbarMessage("Catatan berhasil diubah");
                }
                else if (resultCode == HutangAddUpdateActivity.RESULT_DELETE) {
                    int position = data.getIntExtra(HutangAddUpdateActivity.EXTRA_POSITION, 0);
                    adapter.removeItem(position);
                    getTotalHutang();
                    showSnackbarMessage("Catatan berhasil dihapus");
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListHutang());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hutangHelper.close();
    }
}

interface LoadHutangCallback {
    void preExecute();
    void postExecute(ArrayList<Hutang> hutang);
}