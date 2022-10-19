package com.ndp.flazzbca.menu.RecyclerVIew;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ndp.flazzbca.R;
import com.ndp.flazzbca.menu.PrintTransaksi;

import java.sql.Array;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList id, referensi, tgl, amount, pan, data, batch;
    TextView Title, close, ref, Tgl, ttl, Hour, Status, btch;
    Button next;

    public RecyclerViewAdapter(Context context, ArrayList id, ArrayList referensi, ArrayList tgl, ArrayList amount, ArrayList pan, ArrayList data, ArrayList batch){
        this.context = context;
        this.id = id;
        this.referensi = referensi;
        this.pan = pan;
        this.tgl = tgl;
        this.amount = amount;
        this.data = data;
        this.batch = batch;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_transaksi, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.referensi_txt.setText("Trace: "+referensi.get(i));
        holder.tgl_txt.setText(String.valueOf(tgl.get(i)));
        holder.amount_txt.setText(String.valueOf(amount.get(i)));
        int x = i;

        holder.itemTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(String.valueOf(pan.get(x)), String.valueOf(tgl.get(x)), String.valueOf(amount.get(x)), String.valueOf(data.get(x)), String.valueOf(batch.get(x)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView referensi_txt, tgl_txt, amount_txt, jam_txt;
        LinearLayout itemTrx;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            referensi_txt   = itemView.findViewById(R.id.txtRef);
            tgl_txt         = itemView.findViewById(R.id.txtTgl);
            amount_txt      = itemView.findViewById(R.id.txt_amount);
            Status          = itemView.findViewById(R.id.status);
            itemTrx         = itemView.findViewById(R.id.item_Trx);
        }
    }

    private void showDialog(String refr, String date, String total, String data, String batch) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.detail_transaksi);

        ref     = dialog.findViewById(R.id.txtRef);
        Tgl     = dialog.findViewById(R.id.txtTgl);
        ttl     = dialog.findViewById(R.id.txtTtl);
        btch    = dialog.findViewById(R.id.txtBatch);

        ref.setText(refr);
        Tgl.setText(date);
        ttl.setText(total);
        btch.setText(batch);

        close = dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        next = dialog.findViewById(R.id.btnConfirm);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PrintTransaksi.class);
                intent.putExtra("data", data);
                intent.putExtra("detail", "yes");
                context.startActivity(intent);
            }
        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }

}
