package com.praktikumobile.kredikas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.praktikumobile.kredikas.db.HutangHelper;
import com.praktikumobile.kredikas.entity.Hutang;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.praktikumobile.kredikas.db.DatabaseContract.HutangColumns.CATATAN;
import static com.praktikumobile.kredikas.db.DatabaseContract.HutangColumns.JUMLAH;
import static com.praktikumobile.kredikas.db.DatabaseContract.HutangColumns.BATAS_TANGGAL;

public class HutangAddUpdateActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edtJumlah, edtCatatan;
    private Button btnTanggal, btnSubmit;
    private TextView tvTanggal;
    private Calendar calTanggal;

    private boolean isEdit = false;
    private Hutang hutang;
    private int position;
    private HutangHelper hutangHelper;

    String tanggal = getCurrentDate();

    public static final String EXTRA_HUTANG = "extra_hutang";
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
        setContentView(R.layout.activity_hutang_add_update);

        edtJumlah = findViewById(R.id.edt_addup_jumlah_htg);
        edtCatatan = findViewById(R.id.edt_addup_catatan_htg);
        btnTanggal = findViewById(R.id.btn_addup_tanggal_htg);
        tvTanggal = findViewById(R.id.tv_addup_tanggal_htg);
        btnSubmit = findViewById(R.id.btn_addup_submit_htg);

        hutangHelper = HutangHelper.getInstance(getApplicationContext());
        hutangHelper.open();

        hutang = getIntent().getParcelableExtra(EXTRA_HUTANG);
        if (hutang != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        } else {
            hutang = new Hutang();
        }

        String actionBarTitle;
        String btnTitle;

        if (isEdit) {
            actionBarTitle = "Ubah";
            btnTitle = "Ubah";

            if (hutang != null) {
                tvTanggal.setText(hutang.getBatasTanggal());
                edtJumlah.setText(String.valueOf(hutang.getJumlah()));
                edtCatatan.setText(hutang.getCatatan());
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

        btnSubmit.setOnClickListener(this);
        btnTanggal.setOnClickListener(this);

        calTanggal = Calendar.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_addup_tanggal_htg) {
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
        } else if (view.getId() == R.id.btn_addup_submit_htg) {
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

            hutang.setJumlah(Integer.parseInt(jumlah));
            hutang.setCatatan(catatan);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_HUTANG, hutang);
            intent.putExtra(EXTRA_POSITION, position);

            ContentValues values = new ContentValues();
            values.put(JUMLAH, jumlah);
            values.put(CATATAN, catatan);

            if (isEdit) {
                long result = hutangHelper.update(String.valueOf(hutang.getId()), values);
                if (result > 0) {
                    setResult(RESULT_UPDATE, intent);
                    finish();
                } else {
                    Toast.makeText(HutangAddUpdateActivity.this, "Gagal mengubah data", Toast.LENGTH_SHORT).show();
                }
            } else {
                hutang.setBatasTanggal(tanggal);
                values.put(BATAS_TANGGAL, tanggal);
                long result = hutangHelper.insert(values);
                if (result > 0) {
                    hutang.setId((int) result);
                    setResult(RESULT_ADD, intent);
                    finish();
                } else {
                    Toast.makeText(HutangAddUpdateActivity.this, "Gagal menambah data", Toast.LENGTH_SHORT).show();
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
                        long result = hutangHelper.deleteById(String.valueOf(hutang.getId()));
                        if (result > 0) {
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_POSITION, position);
                            setResult(RESULT_DELETE, intent);
                            finish();
                        } else {
                            Toast.makeText(HutangAddUpdateActivity.this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Tidak", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}