package com.gza.scp.clases;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gza.scp.R;
import com.gza.scp.interfaces.AsyncResponseJSON;

import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class ConexionWS_JSON extends AsyncTask<String,Integer,Boolean> {

    private Context context;
    private String respuesta;
    private AsyncResponseJSON delegate = null;
    private ArrayList<PropertyInfo> propertyInfos = null;
    public ParametrosWS parametrosWS;
    //private ProgressDialog progreso;

    public ConexionWS_JSON(Context context, AsyncResponseJSON delegate, String metodo) {
        this.context = context;
        this.delegate = delegate;
        parametrosWS = new ParametrosWS(metodo, context);
        propertyInfos = new ArrayList<>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("key");
        pi.setValue(parametrosWS.getKEY());
        propertyInfos.add(pi);

    }

    @Override protected void onPreExecute() {
        //progreso = new ProgressDialog(context);
        //progreso.setMessage(context.getResources().getString(R.string.msg_descargando));
        //progreso.setCancelable(false);
        //progreso.show();
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        boolean result;
        respuesta=null;

        try
        {
            //Metodo al que se accede
            SoapObject Solicitud = new SoapObject(parametrosWS.getNAMESPACES(), parametrosWS.getMETODO());

            //Agrega los parametros que recibe el metodo
            for (int i = 0; i < propertyInfos.size(); i++)
                Solicitud.addProperty(propertyInfos.get(i));

            SoapSerializationEnvelope Envoltorio = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            Envoltorio.dotNet = true;
            Envoltorio.setOutputSoapObject(Solicitud);
            HttpTransportSE TransporteHttp = new HttpTransportSE(parametrosWS.getURL(),parametrosWS.getTIMEOUT());

            try
            {
                TransporteHttp.call(parametrosWS.getSOAP_ACTION(), Envoltorio);
                SoapPrimitive response = (SoapPrimitive) Envoltorio.getResponse();

                if(response!=null&& !response.toString().equals("null"))
                {
                    respuesta = response.toString();
                }
                result = true;

            } catch (Exception e) {
                Log.d("salida","error: "+e.getMessage());
                respuesta = "Error: "+ e.getMessage();
                result=false;
            }

        }catch (Exception e)
        {
            Log.d("salida","error: "+e.getMessage());
            respuesta = "Error: "+ e.getMessage();
            result=false;
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //progreso.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        //progreso.dismiss();
        delegate.recibirPeticion(result, respuesta);
    }

    public void addProperty(String nombre, String valor)
    {
        PropertyInfo pi = new PropertyInfo();
        pi.setName(nombre);
        pi.setValue(valor);
        propertyInfos.add(pi);
    }

}