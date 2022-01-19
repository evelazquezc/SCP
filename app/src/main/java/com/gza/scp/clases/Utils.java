package com.gza.scp.clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;

import com.google.gson.Gson;
import com.gza.scp.model.Dt_artAlerta;
import com.gza.scp.model.Dt_articulo;
import com.gza.scp.model.Dt_listaTickets;
import com.gza.scp.model.Dt_usuario;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static <T> T getObjectFromJson(String json,Class<T> clase)
    {
        try
        {
            Gson gson = new Gson();
            T objeto = gson.fromJson(json, clase);
            return objeto;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static void GuardarUsuario(Dt_usuario usuario, Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("nombre", usuario.getNombre());
        editor.putString("password", usuario.getPwd());
        editor.putString("puesto", usuario.getPuesto());
        editor.putString("suc", usuario.getCveSuc());
        editor.apply();
    }

    public static void SetHost(String host, Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("HostPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("host", host);
        editor.apply();
    }

    public static String GetHost(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("HostPreferences", Context.MODE_PRIVATE);

        return sharedPref.getString("host", "");
    }

    public static Dt_usuario ObtenerUsuario(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        Dt_usuario dt_usuario = new Dt_usuario();

        dt_usuario.setNombre(sharedPref.getString("nombre", ""));
        dt_usuario.setPwd(sharedPref.getString("password", ""));
        dt_usuario.setPuesto(sharedPref.getString("puesto", ""));
        dt_usuario.setCveSuc(sharedPref.getString("suc", ""));
        dt_usuario.setCiaVentas(sharedPref.getString("suc", ""));

        return dt_usuario;
    }

    public static boolean esAlerta(ArrayList<String> listaArticulos, String articulo)
    {
        try
        {
            boolean encontrado=false;
            for (String art: listaArticulos)
            {
                if(art.trim().equals( articulo.trim() ))
                {
                    encontrado=true;
                    break;
                }
            }
            return encontrado;
        }catch (Exception e)
        {
            return false;
        }


    }

    public static String addMinutes(Date date,int minutos)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutos);
        CharSequence s = DateFormat.format("HHmmss", calendar.getTime());
        return s.toString();
    }

    public static Date stringToDate(String fecha)
    {
        try
        {
            SimpleDateFormat formatter1=new SimpleDateFormat("HHmmss", Locale.getDefault());
            return formatter1.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String stringToTime(String hora)
    {
        try
        {
            SimpleDateFormat formatter1=new SimpleDateFormat("HHmmss", Locale.getDefault());
            Date date = formatter1.parse(hora);
            CharSequence s = DateFormat.format("HH:mm:ss", date);
            return s.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getHoraLocal()
    {
        Date date = new Date();
        CharSequence s = DateFormat.format("HHmmss", date.getTime());
        return s.toString();
    }

    public static String getFechaLocal()
    {
        Date date = new Date();
        CharSequence s = DateFormat.format("yyyyMMdd", date.getTime());
        String horaLocal = s.toString();
        return horaLocal;

    }

    public static String getPesos(String cantidadStr)
    {
        try {
            cantidadStr = cantidadStr.trim();
            double cantidad = Double.parseDouble(cantidadStr);
            Locale locale = new Locale("es", "MX");
            NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
            nf.setRoundingMode(RoundingMode.DOWN);
            return nf.format(cantidad);
        }catch (Exception e)
        {
            return "$0.00";
        }
    }

    public static String getNumberDecimal(String numero)
    {
        try
        {
            double number = Double.parseDouble(numero);
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(number);
        }catch (Exception e)
        {
            return "0.00";
        }
    }

    public static String getNumberEntero(String numero)
    {
        try
        {
            double number = Double.parseDouble(numero);
            int numberInt = (int) number;
            return String.valueOf(numberInt);
        }catch (Exception e)
        {
            return "0";
        }
    }

}
