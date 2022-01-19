package com.gza.scp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.gza.scp.LoginActivity;
import com.gza.scp.R;
import com.gza.scp.clases.Utils;
import com.gza.scp.model.Dt_articulo;

import java.util.List;

public class ArticulosChildRecyclerAdapter extends RecyclerView.Adapter<ArticulosChildRecyclerAdapter.ViewHolder> {

    List<Dt_articulo> items;

    public ArticulosChildRecyclerAdapter(List<Dt_articulo> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        Context context;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cardview_producto, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ArticulosChildRecyclerAdapter.ViewHolder holder, int position) {
        Dt_articulo articulo = items.get(position);
        Resources resources = holder.itemView.getContext().getResources();

        holder.tv_descripcion.setText(articulo.getDes_art());

        String can;

        if(articulo.isNoEncontrado())
            can="NA";
        else
        {
            if(articulo.isPesable())
                can=Utils.getNumberDecimal( articulo.getCantidad() )+" "+articulo.getEMP().trim();
            else
                can = Utils.getNumberEntero( articulo.getCantidad() ) +" "+ articulo.getEMP().trim();
        }

        holder.tv_cantidad.setText(can );

        if(articulo.isNoEncontrado())
        {
            holder.iv_estado.setImageDrawable( resources.getDrawable(R.drawable.ic_check_danger) );
        }
        else
        {
            if(!articulo.isProductoEscaneado())
                holder.iv_estado.setImageDrawable( resources.getDrawable(R.drawable.ic_pending) );
            else
            {
                if(articulo.isCoincideCan())
                    holder.iv_estado.setImageDrawable( resources.getDrawable(R.drawable.ic_check_ok) );
                else
                    holder.iv_estado.setImageDrawable( resources.getDrawable(R.drawable.ic_check_warning) );
            }
        }

        holder.iv_estado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if(articulo.isPesable() || articulo.isExcepcion())
                    {
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(v.getContext());
                        TextView tv = new TextView(v.getContext());
                        tv.setPadding(30, 20, 30, 10);
                        tv.setTypeface(null, Typeface.BOLD);
                        tv.setTextColor(Color.BLACK);
                        tv.setTextSize(18);

                        String cantidad;
                        if(articulo.isPesable())
                            cantidad= Utils.getNumberDecimal(articulo.getCantidad());
                        else
                            cantidad= Utils.getNumberEntero(articulo.getCantidad());

                        String titulo = articulo.getDes_art().trim() + "\n\nCantidad: " + cantidad +" "+articulo.getEMP(); ;
                        tv.setText(titulo);

                        dialogo1.setCustomTitle(tv);
                        dialogo1.setMessage(resources.getString(R.string.msg_coincide));
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton(resources.getString(R.string.msg_si), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                holder.iv_estado.setImageDrawable(resources.getDrawable(R.drawable.ic_check_ok));
                                items.get(position).setProductoEscaneado(true);
                                items.get(position).setCoincideCan(true);
                            }
                        });
                        dialogo1.setNegativeButton(resources.getString(R.string.msg_noC), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                holder.iv_estado.setImageDrawable(resources.getDrawable(R.drawable.ic_check_warning));
                                items.get(position).setProductoEscaneado(true);
                                items.get(position).setCoincideCan(false);
                            }
                        });
                        dialogo1.show();
                    }
                    else
                        Toast.makeText(v.getContext(), resources.getString(R.string.msg_escanear), Toast.LENGTH_SHORT).show();

                }catch (Exception e)
                {
                    Toast.makeText(v.getContext(), resources.getString(R.string.err_verificanCoin), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_descripcion;
        TextView tv_cantidad;
        ImageView iv_estado;
        ConstraintLayout cl_producto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_descripcion = itemView.findViewById(R.id.tv_descripcion);
            tv_cantidad = itemView.findViewById(R.id.tv_cantidad);
            iv_estado = itemView.findViewById(R.id.iv_estado);
            cl_producto = itemView.findViewById(R.id.cl_producto);

        }
    }
}
