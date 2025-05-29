package com.example.firelogin.ui.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.firelogin.R;
import com.example.firelogin.ui.groups.HolderMensaje;
import com.google.firebase.Timestamp;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterMensajes extends RecyclerView.Adapter<HolderMensaje> {

    private List<MensajeRecibir> listMensaje = new ArrayList<>();
    private Context c;

    public AdapterMensajes(Context c) {
        this.c = c;
    }

    public void addMensaje(MensajeRecibir m){
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size() - 1);
    }


    @Override
    public HolderMensaje onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.groups,parent,false);
        return new HolderMensaje(v);
    }

    @Override
    public void onBindViewHolder(HolderMensaje holder, int position) {
        String tipo = listMensaje.get(position).getType_mensaje();
        Mensaje mensajeActual = listMensaje.get(position);

        holder.getNombre().setText(listMensaje.get(position).getNombre());
        holder.getMensaje().setText(listMensaje.get(position).getMensaje());

        if (tipo != null && (tipo.equals("1") || tipo.equals("2"))) {
            holder.getMensaje().setVisibility(View.VISIBLE);
        } else {
            holder.getMensaje().setVisibility(View.GONE);
        }

        Timestamp timestamp = listMensaje.get(position).getHora();
        if (timestamp != null) {
            Date d = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
            holder.getHora().setText(sdf.format(d));
        }else {
            holder.getHora().setText("");
        }
    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }

}
