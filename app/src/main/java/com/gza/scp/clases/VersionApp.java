package com.gza.scp.clases;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.gza.scp.BuildConfig;
import com.gza.scp.LoginActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*
    AGREGAR LAS SIGUENTES LIBRERIAS EN GRADLE
        implementation 'com.google.code.ksoap2-android:ksoap2-android:3.6.2'
        implementation 'com.google.code.gson:gson:2.8.6'

    AGREGAR PERMISO EN EL MANIFEST
        <uses-permission
            android:name="android.permission.QUERY_ALL_PACKAGES"
          tools:ignore="QueryAllPackagesPermission" />

    AGREGAR LA LLAMADA A LA CLASE EN LA ACTIVITY PRINCIPAL
        VersionApp versionApp = new VersionApp();
        versionApp.valida(LoginActivity.this);
*/

public class VersionApp
{
    public void valida(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        String suc = sharedPref.getString("apksync_sucursal", "");
        if(!suc.equals(""))
        {
            Version version = new Version(context,suc);
            version.execute();
        }
        else
        {
            Sucursales sucursales = new Sucursales(context);
            sucursales.execute();
        }
    }

}

class Version extends AsyncTask<String,Integer,Boolean> {

    private Context context;
    private String respuesta;
    private ArrayList<PropertyInfo> propertyInfos = null;
    private ProgressDialog progreso;
    private final String KEY="zfY4drR7p51ZBCV,OaMLiN^aBPVkjoq^u@m7cbZQ2c0@jA9@ZPFWKm";
    private final String URL="http://192.168.1.88/ApkSync_WS.asmx";
    private final int TIMEOUT = 10000;
    private final String NAMESPACES = "http://web.asezor.com/";
    private final String METODO = "getAplicacionInfoHH";
    private final String SOAP_ACTION= NAMESPACES + METODO ;
    private String packgeName;

    public Version(Context context,String suc) {
        this.context = context;

        propertyInfos = new ArrayList<>();
        packgeName = BuildConfig.APPLICATION_ID;

        addProperty("key",KEY);
        addProperty("package",packgeName);
        addProperty("suc",suc);
    }

    @Override protected void onPreExecute() {
        progreso = new ProgressDialog(context);
        progreso.setMessage("Buscando actualizaciones");
        progreso.setCancelable(false);
        progreso.show();
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        boolean result;
        respuesta=null;

        try
        {
            //Metodo al que se accede
            SoapObject Solicitud = new SoapObject(NAMESPACES, METODO);

            //Agrega los parametros que recibe el metodo
            for (int i = 0; i < propertyInfos.size(); i++)
                Solicitud.addProperty(propertyInfos.get(i));

            SoapSerializationEnvelope Envoltorio = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            Envoltorio.dotNet = true;
            Envoltorio.setOutputSoapObject(Solicitud);
            HttpTransportSE TransporteHttp = new HttpTransportSE(URL,TIMEOUT);

            try
            {
                TransporteHttp.call(SOAP_ACTION, Envoltorio);
                SoapPrimitive response = (SoapPrimitive) Envoltorio.getResponse();

                if(response!=null&& !response.toString().equals("null"))
                {
                    respuesta = response.toString();
                }
                result = true;

            } catch (Exception e) {
                respuesta = "Error: "+ e.getMessage();
                result=false;
            }

        }catch (Exception e)
        {
            respuesta = "Error: "+ e.getMessage();
            result=false;
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progreso.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        progreso.dismiss();

        if(result)
        {
            try
            {
                if(!respuesta.contains("0:"))
                {
                    Gson gson = new Gson();
                    Type userListType = new TypeToken<ArrayList<Dt_aplicacion>>() {}.getType();
                    ArrayList<Dt_aplicacion> appDescargada = gson.fromJson(respuesta, userListType);

                    int versionCode = BuildConfig.VERSION_CODE;

                    if (versionCode < appDescargada.get(0).versionCode) {
                        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.gza.apksync");

                        if (launchIntent != null) {
                            Toast.makeText(context, "Hay una actualización disponible.", Toast.LENGTH_LONG).show();
                            context.startActivity(launchIntent);
                        } else {
                            Toast.makeText(context, "Existe una actualización pero el actualizador no se encuentra instalado.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

            }catch (Exception e)
            {
                Toast.makeText(context, "ApkSync: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(context, "ApkSync:"+ respuesta, Toast.LENGTH_LONG).show();
        }
    }

    public void addProperty(String nombre, String valor)
    {
        PropertyInfo pi = new PropertyInfo();
        pi.setName(nombre);
        pi.setValue(valor);
        propertyInfos.add(pi);
    }
}

class Sucursales extends AsyncTask<String,Integer,Boolean> {

    private Context context;
    private String respuesta;
    private ArrayList<PropertyInfo> propertyInfos = null;
    private ProgressDialog progreso;
    private final String KEY="zfY4drR7p51ZBCV,OaMLiN^aBPVkjoq^u@m7cbZQ2c0@jA9@ZPFWKm";
    private final String URL="http://192.168.1.88/ApkSync_WS.asmx";
    private final int TIMEOUT = 10000;
    private final String NAMESPACES = "http://web.asezor.com/";
    private final String METODO = "getSucursales";
    private final String SOAP_ACTION= NAMESPACES + METODO ;

    public Sucursales(Context context) {
        this.context = context;

        propertyInfos = new ArrayList<>();
        addProperty("key",KEY);
    }

    @Override protected void onPreExecute() {
        progreso = new ProgressDialog(context);
        progreso.setMessage("Cargando sucursales");
        progreso.setCancelable(false);
        progreso.show();
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        boolean result;
        respuesta=null;

        try
        {
            //Metodo al que se accede
            SoapObject Solicitud = new SoapObject(NAMESPACES, METODO);

            //Agrega los parametros que recibe el metodo
            for (int i = 0; i < propertyInfos.size(); i++)
                Solicitud.addProperty(propertyInfos.get(i));

            SoapSerializationEnvelope Envoltorio = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            Envoltorio.dotNet = true;
            Envoltorio.setOutputSoapObject(Solicitud);
            HttpTransportSE TransporteHttp = new HttpTransportSE(URL,TIMEOUT);

            try
            {
                TransporteHttp.call(SOAP_ACTION, Envoltorio);
                SoapPrimitive response = (SoapPrimitive) Envoltorio.getResponse();

                if(response!=null&& !response.toString().equals("null"))
                {
                    respuesta = response.toString();
                }
                result = true;

            } catch (Exception e) {
                respuesta = "Error: "+ e.getMessage();
                result=false;
            }

        }catch (Exception e)
        {
            respuesta = "Error: "+ e.getMessage();
            result=false;
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progreso.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        progreso.dismiss();

        if(result)
        {
            try
            {
                Gson gson = new Gson();
                Type userListType = new TypeToken<ArrayList<Dt_sucursal>>() {}.getType();
                ArrayList<Dt_sucursal> listSucursales = gson.fromJson(respuesta, userListType);

                if(listSucursales.size()>0)
                {
                    ingresarSucursal(listSucursales);
                }
            }catch (Exception e)
            {
                Toast.makeText(context, "ApkSync: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(context, "ApkSync:"+ respuesta, Toast.LENGTH_LONG).show();
        }
    }

    private void ingresarSucursal(ArrayList<Dt_sucursal> listSucursales)
    {
        List<String> list = new ArrayList<>();
        list.add("No aplica");
        for(Dt_sucursal s: listSucursales)
            list.add(s.valor);

        String[] arr = list.toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,arr );

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ApkSync");
        builder.setMessage("\nEscriba y/o seleccione una sucursal");
        LinearLayout parentLayout = new LinearLayout(context);
        AutoCompleteTextView editText = new AutoCompleteTextView(context);
        editText.setHint("Buscar");
        editText.setSingleLine(true);
        editText.setAdapter(adapter);
        editText.setThreshold(1);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        int margin = 50;
        layoutParams.setMargins(margin, margin, margin, margin);

        editText.setLayoutParams(layoutParams);
        parentLayout.addView(editText);
        builder.setView(parentLayout);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String text = editText.getText().toString();
                int id=-1;
                for(int i=0; i<list.size();i++)
                {
                    if(text.equals(list.get(i)))
                    {
                        id=i;
                        break;
                    }
                }

                if(id!=-1)
                {
                    String suc;

                    if(id==0)
                        suc="0";
                    else
                        suc= listSucursales.get(id-1).clave;

                    SharedPreferences sharedPref = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("apksync_sucursal", suc);
                    editor.apply();

                    Version version = new Version(context,suc);
                    version.execute();

                    dialog.dismiss();
                }
                else
                {
                    Toast.makeText(context, "Ingrese una sucursal valida", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void addProperty(String nombre, String valor)
    {
        PropertyInfo pi = new PropertyInfo();
        pi.setName(nombre);
        pi.setValue(valor);
        propertyInfos.add(pi);
    }

}

class Dt_aplicacion
{
    @SerializedName("package")
    String packageName;

    @SerializedName("nombre")
    String nombre;

    @SerializedName("ruta_app")
    String ruta_app;

    @SerializedName("icono")
    String icono;

    @SerializedName("versionCode")
    int versionCode;

    @SerializedName("versionName")
    String versionName;

    @SerializedName("usr_alta")
    String usr_alta;

    @SerializedName("f_alta")
    String f_alta;

    @SerializedName("h_alta")
    String h_alta;

    @SerializedName("usr_mod")
    String usr_mod;

    @SerializedName("f_mod")
    String f_mod;

    @SerializedName("h_mod")
    String h_mod;
}

class Dt_sucursal
{
    @SerializedName("clave")
    String clave;

    @SerializedName("valor")
    String valor;
}