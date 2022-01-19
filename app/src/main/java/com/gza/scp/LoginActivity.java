package com.gza.scp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.gza.scp.clases.BaseLocal;
import com.gza.scp.clases.ConexionWS_JSON;
import com.gza.scp.clases.DatabaseHelper;
import com.gza.scp.clases.Utils;
import com.gza.scp.clases.VersionApp;
import com.gza.scp.interfaces.AsyncResponseJSON;
import com.gza.scp.model.Dt_usuario;
import com.gza.scp.model.UsuarioLogin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LoginActivity extends AppCompatActivity implements AsyncResponseJSON {

    private String usuario, password;
    private ProgressBar progressBar;
    private View contextView;
    private String peticion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);

        TextInputEditText et_usuario,et_password;
        Button bt_iniciar = findViewById(R.id.bt_iniciar);
        et_usuario = findViewById(R.id.ti_usuario);
        et_password = findViewById(R.id.ti_password);

        contextView = findViewById(android.R.id.content);

        TextView tv_version = findViewById(R.id.tv_version);
        String versionName = getString(R.string.tv_version)+" "+ BuildConfig.VERSION_NAME;
        tv_version.setText( versionName );

        Dt_usuario user = Utils.ObtenerUsuario(getApplicationContext());

        if(!user.getNombre().isEmpty())
            et_usuario.setText(user.getNombre().trim());

        bt_iniciar.setOnClickListener(view -> {

            usuario = et_usuario.getText().toString();
            password = et_password.getText().toString();

            if(!usuario.isEmpty() && !password.isEmpty())
            {
                peticionBase();
            }
            else
                Snackbar.make(contextView, R.string.msg_rellenaCampos, Snackbar.LENGTH_SHORT).show();

        });

        crearBaseLocal();

        //VersionApp versionApp = new VersionApp();
        //versionApp.valida(LoginActivity.this);

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(getResources().getString(R.string.msg_importante));
        dialogo1.setMessage( getResources().getString(R.string.msg_cerrarApp) );
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton(getResources().getString(R.string.msg_si), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
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

    public void peticionBase()
    {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        peticion="base";
        ConexionWS_JSON conexionWS = new ConexionWS_JSON(LoginActivity.this,LoginActivity.this,"getBaseCodigos");
        conexionWS.execute();
    }

    public void peticionInicio()
    {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        peticion="login";
        //conexion con el ws
        ConexionWS_JSON conexionWS = new ConexionWS_JSON(LoginActivity.this,LoginActivity.this, "InicioSesionDS");

        //parametros del metodo
        conexionWS.addProperty("usuario",usuario);
        conexionWS.addProperty("password",password);

        conexionWS.execute();
    }

    @Override
    public void recibirPeticion(boolean estado, String respuesta) {
        try
        {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if (estado)
            {
                if(peticion.equals("base"))
                {
                    almacenarBaseCodigos(respuesta);
                }
                else
                if(peticion.equals("login"))
                {
                    if (respuesta != null)
                    {
                        Gson gson = new Gson();
                        UsuarioLogin user = gson.fromJson(respuesta, UsuarioLogin.class);
                        String mensaje = user.getDtmensaje().get(0).getMensaje();

                        if (mensaje.equals("Correcto"))
                        {
                            Utils.GuardarUsuario(user.getDtusuario().get(0), getApplicationContext());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else
                            Snackbar.make(contextView, user.getDtmensaje().get(0).getMensaje(), Snackbar.LENGTH_LONG).show();
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

    private void crearBaseLocal()
    {
        try
        {
            DatabaseHelper databaseHelper = new DatabaseHelper(getApplication(), getResources().getString(R.string.baseLocal), null, 1);
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.close();

            BaseLocal.Insert("delete from Tickets",getApplicationContext());

        }catch (Exception e)
        {
            Snackbar.make(contextView, R.string.err_base, Snackbar.LENGTH_SHORT).show();
        }

    }

    private void almacenarBaseCodigos(String respuesta)
    {
        byte[] bloc = Base64.decode(respuesta,Base64.DEFAULT);
        OutputStream out = null;
        try {
            try
            {
                File nuevaCarpeta = new File(getApplicationContext().getApplicationInfo().dataDir, "databases");
                nuevaCarpeta.mkdirs();
                String databaseDir = getApplicationContext().getApplicationInfo().dataDir + "/databases/";

                File file = new File(databaseDir, getString(R.string.baseCodigos) );
                file.createNewFile();
                out = new FileOutputStream(databaseDir +getString(R.string.baseCodigos) );
                out.write(bloc);
                out.close();

                if(file.exists())
                    peticionInicio();
                else
                    Snackbar.make(contextView, getResources().getString(R.string.err_guardarBase), Snackbar.LENGTH_LONG).show();

            }catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }catch (IOException e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e) {
            Snackbar.make(contextView, getResources().getString(R.string.err_guardarBase), Snackbar.LENGTH_LONG).show();
        }
    }

}
