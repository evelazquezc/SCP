package com.gza.scp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.gza.scp.adapter.ArticulosMainRecyclerAdapter;
import com.gza.scp.clases.BaseLocal;
import com.gza.scp.clases.ConexionWS_JSON;
import com.gza.scp.clases.ParametrosWS;
import com.gza.scp.clases.Utils;
import com.gza.scp.interfaces.AsyncResponseJSON;
import com.gza.scp.model.Dt_articulo;
import com.gza.scp.model.Dt_ticket;
import com.gza.scp.model.Dt_usuario;
import com.gza.scp.model.RevisionArticulo;
import com.gza.scp.model.SectionProducto;
import com.gza.scp.model.Ticket;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RevisarActivity extends AppCompatActivity implements AsyncResponseJSON {

    private ProgressBar progressBarCircle;
    private RecyclerView mainRecyclerView;
    private View contextView;
    private Ticket ticket;
    private ArticulosMainRecyclerAdapter mainRecyclerAdapter;
    private List<SectionProducto> sectionList;
    private Dt_usuario user;
    private String peticion;
    private ArrayList<String> artAlertList;

    private final String section1 = "Artículos en alerta";
    private final String section2 = "Artículos generales";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revisar);

        showToolbar(getResources().getString(R.string.title_revisar), true);

        Intent intent = getIntent();
        ticket = (Ticket) intent.getSerializableExtra("ticket");
        artAlertList = intent.getStringArrayListExtra("alertList");

        user = Utils.ObtenerUsuario(getApplicationContext());

        TextView tv_caja,tv_codigo,tv_cantidad,tv_hora;

        tv_caja = findViewById(R.id.tv_caja);
        tv_codigo = findViewById(R.id.tv_codigo);
        tv_hora = findViewById(R.id.tv_hora);
        tv_cantidad = findViewById(R.id.tv_cantidad);
        progressBarCircle = findViewById(R.id.progressBarCircle);
        contextView = findViewById(android.R.id.content);

        tv_caja.setText( String.valueOf( Integer.parseInt( ticket.getTicket().getCaja().trim() ) )  );
        tv_codigo.setText(ticket.getTicket().getTransaccion().trim());
        tv_hora.setText( Utils.stringToTime( ticket.getTicket().getHra_cob() ) );
        tv_cantidad.setText( Utils.getPesos( ticket.getTicket().getImporte() ) );

        sectionList = new ArrayList<>();

        if(ticket.getArtAlert().size()>0)
            sectionList.add(new SectionProducto(section1,ticket.getArtAlert()));
        if(ticket.getArtGral().size()>0)
            sectionList.add(new SectionProducto(section2,ticket.getArtGral()));

        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        mainRecyclerAdapter = new ArticulosMainRecyclerAdapter(sectionList);
        mainRecyclerView.setAdapter(mainRecyclerAdapter);

        //PRUEBAS
        /*
        String[] pruebas = {"7701405","9801624","7501039127285","9801625"};
        final int[] numero = {0};
        tv_cantidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarCodigo(pruebas[numero[0]]);
                numero[0]++;
            }
        });
        */

    }

    public void showToolbar(String titulo, boolean upButton)
    {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(titulo);
            getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

            toolbar.setNavigationOnClickListener(View ->{
               clickGuardar();
            });

        }
        catch (Exception e)
        {
            Snackbar.make(contextView, getString(R.string.err_toolbar), Snackbar.LENGTH_LONG).show();
        }
    }

    public void clickGuardar()
    {
        try {
            int canTot = ticket.getArtAlert().size()+ticket.getArtGral().size();
            int canScan = 0;

            for (int i = 0; i < ticket.getArtAlert().size(); i++) {
                if (ticket.getArtAlert().get(i).isProductoEscaneado())
                    canScan++;
            }

            for (int i = 0; i < ticket.getArtGral().size(); i++) {
                if (ticket.getArtGral().get(i).isProductoEscaneado())
                    canScan++;
            }

            boolean guardar = false;
            if (canTot >= 3)
            {
                if (canScan >= 3)
                    guardar = true;
            }
            else
            {
                if (canTot == canScan)
                    guardar = true;
            }

            if (guardar) {
                guardarTicketLocal();
                guardarTicketWS();
            } else
                Snackbar.make(contextView, getString(R.string.msg_escanearMas), Snackbar.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Snackbar.make(contextView, getString(R.string.msg_errGuardar), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(getResources().getString(R.string.msg_importante));
        dialogo1.setMessage( getResources().getString(R.string.msg_cancelarLectura) );
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton(getResources().getString(R.string.msg_si), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                setResult(0);
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

    String barcode="";
    @Override
    public boolean dispatchKeyEvent(KeyEvent e)
    {
        if(e.getAction()==KeyEvent.ACTION_DOWN){
            char pressedKey = (char) e.getUnicodeChar();
            barcode += pressedKey;
        }
        if (e.getAction()==KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            playTone(this,ToneGenerator.TONE_CDMA_LOW_S_X4);

            verificarCodigo(barcode.trim());
            barcode="";
        }

        return super.dispatchKeyEvent(e);
    }

    private static ToneGenerator toneGenerator;
    private static void playTone(Context context, int mediaFileRawId)
    {
        //Log.d(TAG, "playTone");
        try
        {
            if (toneGenerator == null) {
                toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            }
            toneGenerator.startTone(mediaFileRawId, 150);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (toneGenerator != null) {
                        //Log.d(TAG, "ToneGenerator released");
                        toneGenerator.release();
                        toneGenerator = null;
                    }
                }

            }, 150);
        } catch (Exception e) {
            //Log.d(TAG, "Exception while playing sound:" + e);
        }
    }

    private void verificarCodigo(String codigo)
    {
        try
        {
            boolean encontrado = false;
            codigo = compararCodigoBarras(codigo);

            if (codigo != null)
            {
                if (ticket.getArtAlert().size() > 0) {
                    for (int i = 0; i < ticket.getArtAlert().size(); i++) {
                        if (ticket.getArtAlert().get(i).getCve_art().trim().equals(codigo.trim())) {
                            verificarConcidencia(i, true);
                            encontrado = true;
                            break;
                        }
                    }
                }

                if (ticket.getArtGral().size() > 0 && !encontrado) {
                    for (int i = 0; i < ticket.getArtGral().size(); i++) {
                        if (ticket.getArtGral().get(i).getCve_art().trim().equals(codigo.trim())) {
                            verificarConcidencia(i, false);
                            encontrado = true;
                            break;
                        }
                    }
                }

                if (!encontrado)
                {
                    agregarNoEncontrado(codigo);
                }
            } else
                Snackbar.make(contextView, getString(R.string.err_noEncontrado), Snackbar.LENGTH_LONG).show();

        }catch (Exception e)
        {
            Snackbar.make(contextView, getString(R.string.err_verificarCod), Snackbar.LENGTH_LONG).show();
        }
    }

    private void agregarNoEncontrado(String codigo)
    {
        try
        {
            String con, res;
            con = "select DES1 FROM ARTICULOS where TRIM(ART)='%s'";
            res = BaseLocal.SelectCB(String.format(con, codigo), getApplicationContext());

            if (res != null)
            {
                Dt_articulo artNuevo = new Dt_articulo();
                artNuevo.setCve_art(codigo);
                artNuevo.setDes_art(res);
                artNuevo.setCaja( ticket.getTicket().getCaja() );
                artNuevo.setNumTra( ticket.getTicket().getTransaccion() );
                artNuevo.setNoEncontrado(true);
                artNuevo.setProductoEscaneado(true);

                boolean esAlerta = Utils.esAlerta(artAlertList,codigo);

                artNuevo.setProductoAlerta(esAlerta);

                if (esAlerta)
                {
                    if (ticket.getArtAlert().size() == 0)
                        sectionList.add(0, new SectionProducto(section1, ticket.getArtAlert()));
                    ticket.getArtAlert().add(0,artNuevo);
                    mainRecyclerAdapter.notifyItemChanged(0);
                }
                else
                {
                    if(ticket.getArtGral().size()==0)
                        sectionList.add(new SectionProducto(section2,ticket.getArtGral()));

                    ticket.getArtGral().add(0,artNuevo);

                    if(ticket.getArtAlert().size()>0)
                        mainRecyclerAdapter.notifyItemChanged(1);
                    else
                        mainRecyclerAdapter.notifyItemChanged(0);
                }

                mainRecyclerView.scrollToPosition(0);
            }

            Snackbar.make(contextView, getString(R.string.err_noEncontradoEnTicket), Snackbar.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            Log.d("salida",e.getMessage());
            Snackbar.make(contextView, getString(R.string.err_addItem), Snackbar.LENGTH_LONG).show();
        }
    }

    private void verificarConcidencia(int i, boolean esAlerta)
    {
        try
        {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            TextView tv = new TextView(this);
            tv.setPadding(30, 20, 30, 10);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(18);
            String titulo,cantidad;

            if (esAlerta)
            {
                if(ticket.getArtAlert().get(i).isPesable())
                    cantidad= Utils.getNumberDecimal(ticket.getArtAlert().get(i).getCantidad());
                else
                    cantidad= Utils.getNumberEntero(ticket.getArtAlert().get(i).getCantidad());

                titulo = ticket.getArtAlert().get(i).getDes_art().trim() + "\n\nCantidad: " + cantidad+" "+ticket.getArtAlert().get(i).getEMP();
            }

            else
            {
                if(ticket.getArtGral().get(i).isPesable())
                    cantidad= Utils.getNumberDecimal(ticket.getArtGral().get(i).getCantidad());
                else
                    cantidad= Utils.getNumberEntero(ticket.getArtGral().get(i).getCantidad());

                titulo = ticket.getArtGral().get(i).getDes_art().trim() + "\n\nCantidad: " + cantidad+" "+ticket.getArtGral().get(i).getEMP();
            }

            tv.setText(titulo);
            dialogo1.setCustomTitle(tv);
            dialogo1.setMessage(getResources().getString(R.string.msg_coincide));
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton(getResources().getString(R.string.msg_si), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    actualizarItem(i, esAlerta, true);
                }
            });
            dialogo1.setNegativeButton(getResources().getString(R.string.msg_noC), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    actualizarItem(i, esAlerta, false);
                }
            });
            dialogo1.show();
        }catch (Exception e)
        {
            Snackbar.make(contextView, getString(R.string.err_verificanCoin), Snackbar.LENGTH_LONG).show();
            Log.d("salida","error: "+e.getMessage());
        }
    }

    private void actualizarItem(int i, boolean esAlerta,boolean coincide)
    {
        try {
            if (esAlerta) {
                ticket.getArtAlert().get(i).setProductoEscaneado(true);
                ticket.getArtAlert().get(i).setCoincideCan(coincide);

                Dt_articulo copia = ticket.getArtAlert().get(i);
                ticket.getArtAlert().remove(i);
                ticket.getArtAlert().add(0, copia);

                mainRecyclerAdapter.notifyItemChanged(0);
                mainRecyclerView.scrollToPosition(0);
            } else {
                ticket.getArtGral().get(i).setProductoEscaneado(true);
                ticket.getArtGral().get(i).setCoincideCan(coincide);

                Dt_articulo copia = ticket.getArtGral().get(i);
                ticket.getArtGral().remove(i);
                ticket.getArtGral().add(0, copia);

                if (ticket.getArtAlert().size() > 0)
                    mainRecyclerAdapter.notifyItemChanged(1);
                else
                    mainRecyclerAdapter.notifyItemChanged(0);

                mainRecyclerView.scrollToPosition(0);
            }
        }catch (Exception e)
        {
            Snackbar.make(contextView, getString(R.string.err_actItem), Snackbar.LENGTH_LONG).show();
        }
    }

    private String compararCodigoBarras(String barcode)
    {
        try {
            barcode = barcode.trim();
            String sql, cveArt = null;
            Log.d("mensaje",barcode);

            sql = "SELECT trim(ART) cve_art from ARTICULOS where trim(CVE_LAR) = '%s'";
            cveArt = BaseLocal.SelectCB(String.format(sql, barcode), getApplicationContext());

            Log.d("mensaje",sql);

            if (cveArt != null)
                return cveArt;
            else {
                sql = "SELECT trim(ART) cve_art from CODBAR where trim(COD_BAR) = '%s'";
                cveArt = BaseLocal.SelectCB(String.format(sql, barcode), getApplicationContext());

                if (cveArt != null)
                    return cveArt;
                else {
                    sql = "SELECT trim(ART) cve_art from ARTICULOS where trim(ART) = '%s'";
                    cveArt = BaseLocal.SelectCB(String.format(sql, barcode), getApplicationContext());

                    if (cveArt != null)
                        return cveArt;
                }
            }
            return cveArt;
        }catch (Exception e)
        {
            Snackbar.make(contextView, getString(R.string.err_compararCod), Snackbar.LENGTH_LONG).show();
            return null;
        }
    }

    private void guardarTicketLocal()
    {
        try
        {
            Dt_ticket t = ticket.getTicket();

            String con = "SELECT count(*) FROM CTRPER_th_TicketAuditados WHERE Suc='%s' AND Caja='%s' AND Ticket='%s' and FecCierreTicket='%s'";
            con = String.format(con,t.getSuc(),t.getCaja(),t.getTransaccion(),t.getFec_cob());

            int cant = Integer.parseInt( BaseLocal.SelectDato(con,getApplicationContext()) );

            if(cant==0)
            {
                String sql = "INSERT INTO CTRPER_th_TicketAuditados VALUES ('%s','%s','%s','%s','%s','%s',%s,'%s','%s','%s')";

                String f = Utils.getFechaLocal();
                String h = Utils.getHoraLocal();

                sql = String.format(sql, t.getSuc(), t.getCaja(), t.getTransaccion(), user.getNombre(), f, h, t.getImporte(), t.getHra_cob(), t.getFec_cob(), "1");
                BaseLocal.Insert(sql, getApplicationContext());

                //ESTATUS ARTICULO: 1- Coincide cantidad escaneados
                                 // 2- No coincide cantidad escaneados
                                 // 3- No se encontraba en el ticket


                String estado;
                if (ticket.getArtAlert().size() > 0) {

                    for (Dt_articulo a : ticket.getArtAlert())
                    {
                        if(a.isProductoEscaneado())
                        {
                            if(a.isNoEncontrado())
                                estado="3";
                            else
                                estado = a.isCoincideCan() ? "1" : "2";

                            sql = "INSERT INTO CTRPER_td_TicketAuditados VALUES ('%s','%s','%s','%s','%s','%s','%s','%s')";
                            sql = String.format(sql, t.getSuc(), a.getCaja(), a.getNumTra(), a.getCve_art(), estado, f, h, user.getNombre());
                            BaseLocal.Insert(sql, getApplicationContext());
                        }
                    }
                }

                if (ticket.getArtGral().size() > 0) {
                    for (Dt_articulo a : ticket.getArtGral())
                    {
                        if(a.isProductoEscaneado())
                        {
                            if(a.isNoEncontrado())
                                estado="3";
                            else
                                estado = a.isCoincideCan() ? "1" : "2";

                            sql = "INSERT INTO CTRPER_td_TicketAuditados VALUES ('%s','%s','%s','%s','%s','%s','%s','%s')";
                            sql = String.format(sql, t.getSuc(), a.getCaja(), a.getNumTra(), a.getCve_art(), estado, f, h, user.getNombre());
                            BaseLocal.Insert(sql,getApplicationContext());
                        }

                    }
                }
            }

        }catch (Exception e)
        {
            Snackbar.make(contextView, getString(R.string.err_guardarLoc), Snackbar.LENGTH_LONG).show();
        }
    }

    private void guardarTicketWS()
    {
        try
        {
            Gson gson = new Gson();
            String jsonTicket = gson.toJson(ticket);

            progressBarCircle.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            peticion = "guardar";
            //conexion con el ws
            ConexionWS_JSON conexionWS = new ConexionWS_JSON(RevisarActivity.this, RevisarActivity.this, "setTickets");

            //parametros del metodo
            conexionWS.addProperty("json", jsonTicket);
            conexionWS.addProperty("usr", user.getNombre());

            conexionWS.execute();

        }catch (Exception e)
        {
            Snackbar.make(contextView, getString(R.string.err_guardarWs), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void recibirPeticion(boolean estado, String respuesta)
    {
        try
        {
            progressBarCircle.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if (estado)
            {
                if(peticion.equals("guardar"))
                {
                    if(respuesta!=null)
                    {
                        if(respuesta.equals("ok"))
                        {
                            Intent intent = new Intent();
                            intent.putExtra("ticket", ticket);
                            setResult(1, intent);
                            finish();
                        }
                        else
                            Snackbar.make(contextView, respuesta, Snackbar.LENGTH_LONG).show();
                    }
                    else
                        Snackbar.make(contextView, getString(R.string.msg_errorGuardar)+" Respuesta nula", Snackbar.LENGTH_LONG).show();
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

}