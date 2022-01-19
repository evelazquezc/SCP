package com.gza.scp.clases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gza.scp.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class BaseLocal {

    public static String Select(String consulta, Context context)
    {
        String nombreBase = context.getString( R.string.baseLocal);
        String json=null;

        DatabaseHelper databaseHelper = new DatabaseHelper(context, nombreBase, null, 1);
        SQLiteDatabase bd = databaseHelper.getReadableDatabase();

        try {

            Cursor cursor = bd.rawQuery(consulta, null);

            if(cursor.getCount()>0)
                Log.d("salida","Encontro info en la Base local "+consulta);

            json = cur2Json(cursor);

            bd.close();

        }
        catch (Exception e)
        {
            Log.d("salida","Error baseLocal Select: "+e.toString());
            json=null;
        }
        finally {
            if(bd.isOpen())
                bd.close();
        }

        return json;
    }

    public static void Insert(String consulta,Context context)
    {
        try {
            String nombreBase = context.getString(R.string.baseLocal);

            DatabaseHelper databaseHelper = new DatabaseHelper(context, nombreBase, null, 1);
            SQLiteDatabase bd = databaseHelper.getWritableDatabase();
            bd.execSQL(consulta);
            bd.close();
        }
        catch (Exception e)
        {
            Log.d("salida","Error baseLocal Insert "+ e.getMessage());
        }

    }

    public static String SelectDato(String consulta, Context context)
    {
        String nombreBase = context.getString( R.string.baseLocal );
        String datoReturn=null;

        try {

            DatabaseHelper databaseHelper = new DatabaseHelper(context, nombreBase, null, 1);
            SQLiteDatabase bd = databaseHelper.getReadableDatabase();

            Cursor cursor = bd.rawQuery(consulta, null);

            if(cursor.moveToNext())
            {
                datoReturn= cursor.getString(0 );
            }

            bd.close();

        }
        catch (Exception e)
        {
            Log.d("salida","Error baseL: "+e.toString());
            datoReturn=null;
        }

        return datoReturn;
    }

    public static String SelectCB(String consulta, Context context)
    {
        String nombreBase = context.getString( R.string.baseCodigos);
        String datoReturn=null;

        try {

            DatabaseHelper databaseHelper = new DatabaseHelper(context, nombreBase, null, 1);
            SQLiteDatabase bd = databaseHelper.getReadableDatabase();

            Cursor cursor = bd.rawQuery(consulta, null);

            if(cursor.moveToNext())
            {
                datoReturn= cursor.getString(0 );
            }

            bd.close();

        }
        catch (Exception e)
        {
            Log.d("salida","Error baseL: "+e.toString());
            datoReturn=null;
        }

        return datoReturn;
    }

    private static String cur2Json(Cursor cursor)
    {
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {

                if (cursor.getColumnName(i) != null) {
                    try
                    {
                        String name = cursor.getColumnName(i);
                        if(name.contains("."))
                            name=name.substring(name.indexOf(".")+1);

                        if(cursor.getString(i)==null)
                            rowObject.put(name, "");
                        else
                            rowObject.put(name, cursor.getString(i));

                    } catch (Exception e) {
                        Log.d("salida","Error cursor2json: "+ e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet.toString();
    }

}
