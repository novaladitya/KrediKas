package com.praktikumobile.kredikas.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.praktikumobile.kredikas.CustomOnItemClickListener;
import com.praktikumobile.kredikas.KasAddUpdateActivity;
import com.praktikumobile.kredikas.R;
import com.praktikumobile.kredikas.entity.Kas;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class KasAdapter extends RecyclerView.Adapter<KasAdapter.KasViewHolder>{
    private final ArrayList<Kas> listKas = new ArrayList<>();
    private final Fragment fragment;

    public KasAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public ArrayList<Kas> getListKas() { return listKas; }

    public void setListKas(ArrayList<Kas> listKas) {
        if (listKas.size() > 0) {
            this.listKas.clear();
        }
        this.listKas.addAll(listKas);

        notifyDataSetChanged();
    }

    public void addItem(Kas kas) {
        this.listKas.add(kas);
        notifyItemInserted(listKas.size() - 1);
    }

    public void updateItem(int position, Kas kas) {
        this.listKas.set(position, kas);
        notifyItemChanged(position, kas);
    }

    public void removeItem(int position) {
        this.listKas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listKas.size());
    }

    static class KasViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTanggal, tvJenis, tvJumlah, tvCatatan, tvBulan, tvTahun;
        final CardView cvKas;

        KasViewHolder(View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvBulan = itemView.findViewById(R.id.tv_bulan);
            tvTahun = itemView.findViewById(R.id.tv_tahun);
            tvJenis = itemView.findViewById(R.id.tv_jenis);
            tvJumlah = itemView.findViewById(R.id.tv_jumlah);
            tvCatatan = itemView.findViewById(R.id.tv_catatan);
            cvKas = itemView.findViewById(R.id.cv_kas);
        }
    }

    @NonNull
    @Override
    public KasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_kas, parent, false);
        return new KasViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull KasViewHolder holder, int position) {
        String[] splitTanggal = listKas.get(position).getTanggal().split("/");
        int noBulan = Integer.parseInt(splitTanggal[1]) - 1;
        String[] namaBulan = new String[]{"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        holder.tvTanggal.setText(splitTanggal[0]);
        holder.tvBulan.setText(namaBulan[noBulan]);
        holder.tvTahun.setText(splitTanggal[2]);

        if (listKas.get(position).getJenis().equals("pengeluaran")) {
            holder.tvJenis.setText("Pengeluaran");
            holder.tvJenis.setTextColor(Color.parseColor("#FF0000"));
        } else {
            holder.tvJenis.setText("Pemasukan");
            holder.tvJenis.setTextColor(Color.parseColor("#01E701"));
        }
        //holder.tvJenis.setText(listKas.get(position).getJenis());

        DecimalFormat df = new DecimalFormat("#,###");
        holder.tvJumlah.setText("Rp. " + df.format(listKas.get(position).getJumlah()));

        holder.tvCatatan.setText(listKas.get(position).getCatatan());

        holder.cvKas.setOnClickListener(new CustomOnItemClickListener(position, (view, position1) -> {
            Intent intent = new Intent(fragment.getActivity(), KasAddUpdateActivity.class);
            intent.putExtra(KasAddUpdateActivity.EXTRA_POSITION, position1);
            intent.putExtra(KasAddUpdateActivity.EXTRA_KAS, listKas.get(position1));
            fragment.startActivityForResult(intent, KasAddUpdateActivity.REQUEST_UPDATE);
        }));
    }

    @Override
    public int getItemCount() {
        return listKas.size();
    }
}
