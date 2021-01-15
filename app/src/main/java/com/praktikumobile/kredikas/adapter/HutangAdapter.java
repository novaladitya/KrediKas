package com.praktikumobile.kredikas.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.praktikumobile.kredikas.CustomOnItemClickListener;
import com.praktikumobile.kredikas.HutangAddUpdateActivity;
import com.praktikumobile.kredikas.R;
import com.praktikumobile.kredikas.entity.Hutang;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HutangAdapter extends RecyclerView.Adapter<HutangAdapter.HutangViewHolder>{
    private final ArrayList<Hutang> listHutang = new ArrayList<>();
    private final Fragment fragment;

    public HutangAdapter(Fragment fragment) { this.fragment = fragment; }

    public ArrayList<Hutang> getListHutang() { return listHutang; }

    public void setListHutang(ArrayList<Hutang> listKas) {
        if (listKas.size() > 0) {
            this.listHutang.clear();
        }
        this.listHutang.addAll(listKas);

        notifyDataSetChanged();
    }

    public void addItem(Hutang hutang) {
        this.listHutang.add(hutang);
        notifyItemInserted(listHutang.size() - 1);
    }

    public void updateItem(int position, Hutang hutang) {
        this.listHutang.set(position, hutang);
        notifyItemChanged(position, hutang);
    }

    public void removeItem(int position) {
        this.listHutang.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listHutang.size());
    }

    static class HutangViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTanggalHutang, tvBulanHutang, tvTahunHutang, tvJumlahHutang, tvCatatanHutang;
        final CardView cvHutang;

        HutangViewHolder(View itemView) {
            super(itemView);
            tvTanggalHutang = itemView.findViewById(R.id.tv_tanggal_htg);
            tvBulanHutang = itemView.findViewById(R.id.tv_bulan_htg);
            tvTahunHutang = itemView.findViewById(R.id.tv_tahun_htg);
            tvJumlahHutang = itemView.findViewById(R.id.tv_jumlah_htg);
            tvCatatanHutang = itemView.findViewById(R.id.tv_catatan_htg);
            cvHutang = itemView.findViewById(R.id.cv_hutang);
        }
    }

    @NonNull
    @Override
    public HutangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_hutang, parent, false);
        return new HutangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HutangViewHolder holder, int position) {
        String[] splitTanggal = listHutang.get(position).getBatasTanggal().split("/");
        int noBulan = Integer.parseInt(splitTanggal[1]) - 1;
        String[] namaBulan = new String[]{"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        holder.tvTanggalHutang.setText(splitTanggal[0]);
        holder.tvBulanHutang.setText(namaBulan[noBulan]);
        holder.tvTahunHutang.setText(splitTanggal[2]);

        DecimalFormat df = new DecimalFormat("#,###");
        holder.tvJumlahHutang.setText("Rp. " + df.format(listHutang.get(position).getJumlah()));

        holder.tvCatatanHutang.setText(listHutang.get(position).getCatatan());

        holder.cvHutang.setOnClickListener(new CustomOnItemClickListener(position, (view, position1) -> {
            Intent intent = new Intent(fragment.getActivity(), HutangAddUpdateActivity.class);
            intent.putExtra(HutangAddUpdateActivity.EXTRA_POSITION, position1);
            intent.putExtra(HutangAddUpdateActivity.EXTRA_HUTANG, listHutang.get(position1));
            fragment.startActivityForResult(intent, HutangAddUpdateActivity.REQUEST_UPDATE);
        }));
    }

    @Override
    public int getItemCount() {
        return listHutang.size();
    }
}
