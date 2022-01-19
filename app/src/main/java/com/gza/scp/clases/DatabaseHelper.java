package com.gza.scp.clases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.gza.scp.R;

public class DatabaseHelper extends SQLiteOpenHelper {

    private String name;
    private Context context;

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.name=name;
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if(name.equals(context.getString(R.string.baseLocal)))
        {

            String CreaTickets = "CREATE TABLE Tickets(" +
                    "Caja nvarchar(3)," +
                    "Transaccion nvarchar(5)," +
                    "Importe money," +
                    "Fec_cob nvarchar(8)," +
                    "Hra_cob nvarchar(6))";

            String CreaTh_tickets = "Create table CTRPER_th_TicketAuditados(" +
                    "Suc char(3) not null," +
                    "Caja char(3) not null," +
                    "Ticket char(5) not null," +
                    "UsrChecador char(10) not null," +
                    "FecChecador char(8) not null," +
                    "HoraChecador char(6) not null," +
                    "MontoTicket numeric not null," +
                    "HoraCierreTicket char(6) not null," +
                    "FecCierreTicket char(8) not null," +
                    "Estatus char(1) not null" +
                    ")";

            String CreaTd_tickets = "Create table CTRPER_td_TicketAuditados(" +
                    "Suc char(3) not null," +
                    "Caja char(3) not null," +
                    "Ticket char(5) not null," +
                    "cve_art char(15) not null," +
                    "Estatus char(1) not null," +
                    "Fecha char(8) not null," +
                    "Hora char(6) not null," +
                    "UsrChecador char(10) not null" +
                    ")";

            db.execSQL(CreaTickets);
            db.execSQL(CreaTh_tickets);
            db.execSQL(CreaTd_tickets);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


    }
}
