package com.gza.scp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.gza.scp.adapter.TicketsRecyclerAdapter;
import com.gza.scp.clases.BaseLocal;
import com.gza.scp.clases.ConexionWS_JSON;
import com.gza.scp.clases.ParametrosWS;
import com.gza.scp.clases.Utils;
import com.gza.scp.interfaces.AsyncResponseJSON;
import com.gza.scp.model.Dt_artAlerta;
import com.gza.scp.model.Dt_articulo;
import com.gza.scp.model.Dt_listaTickets;
import com.gza.scp.model.Dt_ticket;
import com.gza.scp.model.Dt_usuario;
import com.gza.scp.model.Ticket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AsyncResponseJSON {

    private TicketsRecyclerAdapter ticketsRecyclerAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private View contextView;
    private List<Ticket> ticketList;
    private ArrayList<String> cveAlertList;
    private String peticion;
    private Dt_usuario user;
    private boolean pausarTiempo=false;

    static final int TIEMPO_TICKETS=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = Utils.ObtenerUsuario(getApplicationContext());

        showToolbar(user.getCiaVentas().trim()+" - " +user.getNombre().trim(),false);

        Button bt_actualizar = findViewById(R.id.bt_actualizar);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        contextView = findViewById(android.R.id.content);

        ticketList = new ArrayList<>();

        ticketsRecyclerAdapter = new TicketsRecyclerAdapter(ticketList,this);
        recyclerView.setAdapter(ticketsRecyclerAdapter);

        bt_actualizar.setOnClickListener(view -> {
            peticionTickets();
        });

        peticionArtAlerta();
        validarTiempo();
    }

    public void showToolbar(String titulo, boolean upButton)
    {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(titulo);
            getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        }catch (Exception e)
        {
            Snackbar.make(contextView, getString(R.string.err_toolbar), Snackbar.LENGTH_LONG).show();
        }
    }

    private void peticionArtAlerta()
    {
        //OBTENER ARTICULOS ALERTA DEL WS
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        peticion="artAlerta";
        ConexionWS_JSON conexionWS = new ConexionWS_JSON(MainActivity.this,MainActivity.this, "getArtAlerta");
        conexionWS.addProperty("sucursal",user.getCiaVentas());
        conexionWS.execute();
    }

    private void peticionTickets()
    {
        //OBTENER TICKETS DEL WS
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        peticion="tickets";
        ConexionWS_JSON conexionWS = new ConexionWS_JSON(MainActivity.this,MainActivity.this, "getTickets");
        conexionWS.addProperty("sucursal",user.getCiaVentas());
        conexionWS.execute();
    }

    private void validarTiempo()
    {
        try
        {
            Runnable myRunnable;
            Handler handler = new Handler();

            Handler finalHandler = handler;
            myRunnable = new Runnable() {
                @Override
                public void run() {

                    if(!pausarTiempo)
                    {
                        String horaLocal = Utils.getHoraLocal();
                        Date dateTicket;
                        String horaTicket;

                        for (int i = 0; i < ticketList.size(); i++)
                        {
                            dateTicket = Utils.stringToDate(ticketList.get(i).getTicket().getHra_cob());
                            horaTicket = Utils.addMinutes(dateTicket, TIEMPO_TICKETS);

                            if (Integer.parseInt(horaTicket) < Integer.parseInt(horaLocal))
                            {
                                ticketList.remove(i);
                                ticketsRecyclerAdapter.notifyItemRemoved(i);
                                ticketsRecyclerAdapter.notifyItemRangeChanged(i, ticketList.size());
                            }
                        }
                        finalHandler.postDelayed(this, 1000);
                    }
                }
            };

            handler.post(myRunnable);
        }
        catch (Exception e)
        {
            Snackbar.make(contextView, getResources().getString(R.string.err_validarTiem), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void recibirPeticion(boolean estado, String respuesta)
    {
        try
        {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if (estado)
            {
                if (respuesta != null)
                {
                    if(peticion.equals("tickets"))
                        initTickets(respuesta);
                    else
                    if(peticion.equals("artAlerta"))
                    {
                        initArticulos(respuesta);
                        peticionTickets();
                    }
                }
            }
            else
            {
                Snackbar.make(contextView, getString(R.string.msg_errorWs), Snackbar.LENGTH_LONG).show();
            }
        }catch (Exception e)
        {
            Snackbar.make(contextView, e.toString(), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode==0)
        {
            if(resultCode==1)
            {
                Ticket ticketRec = (Ticket) intent.getSerializableExtra("ticket");
                int indiceEliminar=-1;
                for(int i=0;i<ticketList.size();i++)
                {
                    if( ticketList.get(i).getTicket().getCaja().trim().equals( ticketRec.getTicket().getCaja().trim() ) &&
                        ticketList.get(i).getTicket().getTransaccion().trim().equals( ticketRec.getTicket().getTransaccion().trim() )&&
                        ticketList.get(i).getTicket().getFec_cob().trim().equals( ticketRec.getTicket().getFec_cob().trim() ))
                    {
                        indiceEliminar=i;
                        break;
                    }
                }
                if(indiceEliminar!=-1)
                {
                    ticketList.remove(indiceEliminar);
                    ticketsRecyclerAdapter.notifyItemRemoved(indiceEliminar);
                    ticketsRecyclerAdapter.notifyItemRangeChanged(indiceEliminar, ticketList.size());
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(getResources().getString(R.string.msg_importante));
        dialogo1.setMessage( getResources().getString(R.string.msg_salir) );
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton(getResources().getString(R.string.msg_si), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialogo1.setNegativeButton(getResources().getString(R.string.msg_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                //cancelar();
            }
        });
        dialogo1.show();
    }

    private void initTickets(String jsonTickets)
    {
        try
        {
            pausarTiempo=true;

            Dt_listaTickets listaTickets = Utils.getObjectFromJson( jsonTickets, Dt_listaTickets.class );
            listaTickets = filtrarTickets(listaTickets);

            if(listaTickets!=null && listaTickets.getDtTickets()!=null)
            {
                for (Dt_ticket ticket : listaTickets.getDtTickets())
                {
                    List<Dt_articulo> artAlert = new ArrayList<>();
                    List<Dt_articulo> artGral = new ArrayList<>();

                    for (Dt_articulo articulo : listaTickets.getDtArticulos())
                    {
                        boolean contieneAlertas = false;

                        if (ticket.getTransaccion().trim().equals(articulo.getNumTra().trim()) &&
                            ticket.getCaja().trim().equals( articulo.getCaja().trim() )  &&
                            ticket.getFec_cob().trim().equals( articulo.getFecOpe().trim() ))
                        {
                            if(articulo.getTipo()!=null&&articulo.getTipo().equals("P"))
                            {
                                articulo.setEMP("KG");
                                articulo.setPesable(true);
                            }

                            if(articulo.getExcepcion().equals("1"))
                                articulo.setExcepcion(true);

                            if(articulo.getTipo()!=null&&articulo.getTipo().equals("2"))
                                articulo.setExcepcion(true);

                            if (Utils.esAlerta(cveAlertList, articulo.getCve_art()))
                            {
                                articulo.setProductoAlerta(true);
                                contieneAlertas = true;
                                artAlert.add(articulo);
                            } else
                                artGral.add(articulo);

                        }
                        if (contieneAlertas)
                            ticket.setProductosAlerta(true);
                    }

                    artAlert = eliminarDuplicados(artAlert);
                    artGral = eliminarDuplicados(artGral);

                    ticketList.add(0,new Ticket(ticket, artAlert, artGral));
                    ticketsRecyclerAdapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0);
                }
            }

            pausarTiempo=false;

        }catch (Exception e)
        {
            Log.d("salida","Error tickets: "+e.getMessage());
            Snackbar.make(contextView, getString(R.string.err_ticket), Snackbar.LENGTH_LONG).show();
        }
    }

    private List<Dt_articulo> eliminarDuplicados(List<Dt_articulo> lista)
    {
        List<Dt_articulo> filtrados = new ArrayList<>();

        for(Dt_articulo art : lista)
        {
            int pos=-1;
            int i=0;
            for(Dt_articulo f : filtrados)
            {
                if(f.getCve_art().equals(art.getCve_art()))
                {
                    pos=i;
                    break;
                }
                i++;
            }

            if(pos==-1||art.isPesable())
            {
                filtrados.add(art);
            }
            else
            {
                float can = Float.parseFloat(filtrados.get(pos).getCantidad()) + Float.parseFloat(art.getCantidad());
                filtrados.get(pos).setCantidad( String.valueOf(can) );
            }

        }
        return filtrados;
    }

    private Dt_listaTickets filtrarTickets(Dt_listaTickets listaFiltrar)
    {
        try
        {
            List<Dt_ticket> ticketsFiltrados = new ArrayList<>(listaFiltrar.getDtTickets());

            for (Dt_ticket ticket : listaFiltrar.getDtTickets())
            {
                String sql = "SELECT COUNT(*) from Tickets where Caja='" + ticket.getCaja() + "' and Transaccion='" + ticket.getTransaccion() + "' and Fec_cob='" + ticket.getFec_cob() + "' ";
                int res = Integer.parseInt(BaseLocal.SelectDato(sql, getApplicationContext()));
                if (res == 0)
                {
                    String insert = "INSERT INTO Tickets VALUES ('" + ticket.getCaja() + "','" + ticket.getTransaccion() + "'," + ticket.getImporte() + ",'" + ticket.getFec_cob() + "','" + ticket.getHra_cob() + "') ";
                    BaseLocal.Insert(insert, getApplicationContext());
                } else
                {
                    ticketsFiltrados.remove(ticket);
                }
            }

            listaFiltrar.setDtTickets(ticketsFiltrados);

            return listaFiltrar;
        }
        catch (Exception e)
        {
            Snackbar.make(contextView, getString(R.string.err_ticketFil), Snackbar.LENGTH_LONG).show();
            return null;
        }
    }

    private void initArticulos(String jsonArt)
    {
        try
        {
            Dt_artAlerta[] artAlertas = Utils.getObjectFromJson(jsonArt,Dt_artAlerta[].class);
            List<Dt_artAlerta> artAlertaList = Arrays.asList(artAlertas);

            cveAlertList= new ArrayList<>();
            for(Dt_artAlerta a : artAlertaList)
                cveAlertList.add(a.getCve_art().trim());

        }catch (Exception e)
        {
            Snackbar.make(contextView, getString(R.string.err_artAlerta), Snackbar.LENGTH_LONG).show();
        }
    }

    public ArrayList<String> getCveAlertList()
    {
        return cveAlertList;
    }

}