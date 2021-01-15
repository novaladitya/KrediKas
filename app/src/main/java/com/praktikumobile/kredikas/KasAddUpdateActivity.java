package com.praktikumobile.kredikas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.praktikumobile.kredikas.db.KasHelper;
import com.praktikumobile.kredikas.entity.Kas;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.praktikumobile.kredikas.db.DatabaseContract.KasColumns.CATATAN;
import static com.praktikumobile.kredikas.db.DatabaseContract.KasColumns.JENIS;
import static com.praktikumobile.kredikas.db.DatabaseContract.KasColumns.JUMLAH;
import static com.praktikumobile.kredikas.db.DatabaseContract.KasColumns.TANGGAL;

public class KasAddUpdateActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edtJumlah, edtCatatan;
    private Button btnPemasukan, btnPengeluaran, btnTanggal, btnSubmit;
    private TextView tvTanggal;
    private Calendar calTanggal;

    private boolean isEdit = false;
    private Kas kas;
    private int position;
    private KasHelper kasHelper;

    String jenis = "pemasukan";
    String tanggal = getCurrentDate();

    public static final String EXTRA_KAS = "extra_kas";
    public static final String EXTRA_POSITION = "extra_position";
    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;
    private final int ALERT_DIALOG_CLOSE = 10;
    private final int ALERT_DIALOG_DELETE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kas_add_update);

        edtJumlah = findViewById(R.id.edt_addup_jumlah);
        edtCatatan = findViewById(R.id.edt_addup_catatan);
        btnPemasukan = findViewById(R.id.btn_pemasukan);
        btnPengeluaran = findViewById(R.id.btn_pengeluaran);
        btnTanggal = findViewById(R.id.btn_addup_tanggal);
        tvTanggal = findViewById(R.id.tv_addup_tanggal);
        btnSubmit = findViewById(R.id.btn_addup_submit);

        kasHelper = KasHelper.getInstance(getApplicationContext());
        kasHelper.open();

        kas = getIntent().getParcelableExtra(EXTRA_KAS);
        if (kas != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        } else {
            kas = new Kas();
        }

        String actionBarTitle;
        String btnTitle;

        if (isEdit) {
            actionBarTitle = "Ubah";
            btnTitle = "Ubah";

            if (kas != null) {
                tvTanggal.setText(kas.getTanggal());
                edtJumlah.setText(String.valueOf(kas.getJumlah()));
                edtCatatan.setText(kas.getCatatan());
            }
        } else {
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSubmit.setText(btnTitle);
        tvTanggal.setText(tanggal);

        btnSubmit.setOnClickListener(this);
        btnPemasukan.setOnClickListener(this);
        btnPengeluaran.setOnClickListener(this);
        btnTanggal.setOnClickListener(this);

        calTanggal = Calendar.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_pemasukan) {
            jenis = "pemasukan";
            btnPemasukan.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorPrimaryDark));
            btnPengeluaran.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorPrimary));
        } else if (view.getId() == R.id.btn_pengeluaran) {
            jenis = "pengeluaran";
            btnPemasukan.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorPrimary));
            btnPengeluaran.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorPrimaryDark));
        } else if (view.getId() == R.id.btn_addup_tanggal) {
            final Calendar currentDate = Calendar.getInstance();
            new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    calTanggal.set(year, monthOfYear, dayOfMonth);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    tvTanggal.setText(dateFormat.format(calTanggal.getTime()));
                    tanggal = dateFormat.format(calTanggal.getTime());
                }
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
        } else if (view.getId() == R.id.btn_addup_submit) {
            String jumlah = edtJumlah.getText().toString().trim();
            String catatan = edtCatatan.getText().toString().trim();
            if (TextUtils.isEmpty(jumlah)) {
                edtJumlah.setError("Masih kosong");
                return;
            }
            if (TextUtils.isEmpty(catatan)) {
                edtCatatan.setError("Masih kosong");
                return;
            }

            kas.setJenis(jenis);
            kas.setJumlah(Integer.parseInt(jumlah));
            kas.setCatatan(catatan);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_KAS, kas);
            intent.putExtra(EXTRA_POSITION, position);

            ContentValues values = new ContentValues();
            values.put(JENIS, jenis);
            values.put(JUMLAH, jumlah);
            values.put(CATATAN, catatan);

            if (isEdit) {
                long result = kasHelper.update(String.valueOf(kas.getId()), values);
                if (result > 0) {
                    setResult(RESULT_UPDATE, intent);
                    finish();
                } else {
                    Toast.makeText(KasAddUpdateActivity.this, "Gagal mengubah data", Toast.LENGTH_SHORT).show();
                }
            } else {
                kas.setTanggal(tanggal);
                values.put(TANGGAL, tanggal);
                long result = kasHelper.insert(values);
                if (result > 0) {
                    kas.setId((int) result);
                    setResult(RESULT_ADD, intent);
                    finish();
                } else {
                    Toast.makeText(KasAddUpdateActivity.this, "Gagal menambah data", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            showAlertDialog(ALERT_DIALOG_DELETE);
        } else if (id == android.R.id.home) {
            showAlertDialog(ALERT_DIALOG_CLOSE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;
        if (isDialogClose) {
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada catatan?";
        } else {
            dialogTitle = "Hapus Catatan";
            dialogMessage = "Apakah anda yakin ingin menghapus catatan ini?";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, id) -> {
                    if (isDialogClose) {
                        finish();
                    } else {
                        long result = kasHelper.deleteById(String.valueOf(kas.getId()));
                        if (result > 0) {
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_POSITION, position);
                            setResult(RESULT_DELETE, intent);
                            finish();
                        } else {
                            Toast.makeText(KasAddUpdateActivity.this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Tidak", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}