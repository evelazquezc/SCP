package com.gza.scp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gza.scp.R;
import com.gza.scp.model.Dt_articulo;
import com.gza.scp.model.SectionProducto;

import java.util.ArrayList;
import java.util.List;

public class ArticulosMainRecyclerAdapter extends RecyclerView.Adapter<ArticulosMainRecyclerAdapter.ViewHolder> {

    List<SectionProducto> sectionList = new ArrayList<>();

    public ArticulosMainRecyclerAdapter(List<SectionProducto> sectionList) {
        this.sectionList = sectionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cardview_titulo_producto, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticulosMainRecyclerAdapter.ViewHolder holder, int position) {
        SectionProducto section = sectionList.get(position);

        String sectionName = section.getTitulo();

        List<Dt_articulo> sectionItems = section.getArticulos();

        holder.sectionName.setText(sectionName);

        ArticulosChildRecyclerAdapter articulosChildRecyclerAdapter = new ArticulosChildRecyclerAdapter(sectionItems);
        holder.childRecyclerView.setAdapter(articulosChildRecyclerAdapter);

    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView sectionName;
        RecyclerView childRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sectionName = itemView.findViewById(R.id.cv_titulo);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);

        }
    }

}
