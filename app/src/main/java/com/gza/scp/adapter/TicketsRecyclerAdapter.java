package com.gza.scp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gza.scp.MainActivity;
import com.gza.scp.R;
import com.gza.scp.RevisarActivity;
import com.gza.scp.clases.Utils;
import com.gza.scp.model.Ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketsRecyclerAdapter extends RecyclerView.Adapter<TicketsRecyclerAdapter.ViewHolder> {

    List<Ticket> tickets;
    MainActivity activity;

    public TicketsRecyclerAdapter(List<Ticket> tickets,MainActivity activity) {
        this.tickets = tickets;
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cardview_ticket, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  TicketsRecyclerAdapter.ViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        Resources resources = holder.itemView.getContext().getResources();

        holder.tv_caja.setText( String.valueOf( Integer.parseInt( ticket.getTicket().getCaja().trim() ) )  );
        holder.tv_codigo.setText(ticket.getTicket().getTransaccion().trim());
        holder.tv_hora.setText( Utils.stringToTime( ticket.getTicket().getHra_cob() )   );
        holder.tv_cantidad.setText(Utils.getPesos( ticket.getTicket().getImporte() )  );


        if(ticket.getTicket().isProductosAlerta())
            holder.cl_identificador.setBackgroundColor(resources.getColor(R.color.red_200));
        else
            holder.cl_identificador.setBackgroundColor(resources.getColor(R.color.green));

        holder.cl_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    Intent intent = new Intent(activity, RevisarActivity.class);
                    intent.putExtra("ticket", ticket);
                    intent.putStringArrayListExtra("alertList",activity.getCveAlertList());
                    (activity).startActivityForResult(intent,0);

                }catch (Exception e)
                {
                    Log.d("salida",e.getMessage());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_caja;
        TextView tv_codigo;
        TextView tv_cantidad;
        TextView tv_hora;
        ConstraintLayout cl_ticket;
        ConstraintLayout cl_identificador;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_caja = itemView.findViewById(R.id.cv_caja);
            tv_codigo = itemView.findViewById(R.id.cv_codigo);
            tv_cantidad = itemView.findViewById(R.id.cv_cantidad);
            cl_ticket = itemView.findViewById(R.id.cl_ticket);
            tv_hora = itemView.findViewById(R.id.cv_hora);
            cl_identificador = itemView.findViewById(R.id.cl_identificador);
        }
    }
}
