package com.gza.scp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.gza.scp.clases.ConexionWS_JSON;
import com.gza.scp.clases.Utils;
import com.gza.scp.interfaces.AsyncResponseJSON;
import com.gza.scp.model.UsuarioLogin;

public class ConfiguracionActivity  extends AppCompatActivity implements AsyncResponseJSON {

    private TextInputEditText ti_host;
    private ProgressBar progressBar;
    private View contextView;
    private boolean prueba=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        Button bt_guardar= findViewById(R.id.bt_guardar);
        Button bt_prueba = findViewById(R.id.bt_prueba);

        progressBar = findViewById(R.id.progressBar);
        ti_host = findViewById(R.id.ti_host);
        contextView = findViewById(android.R.id.content);

        TextView tv_version = findViewById(R.id.tv_version);
        String versionName = getString(R.string.tv_version)+" "+ BuildConfig.VERSION_NAME;
        tv_version.setText( versionName );

        String host = Utils.GetHost(getApplicationContext());

        if(!host.isEmpty())
        {
            Intent intent = new Intent(ConfiguracionActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        bt_prueba.setOnClickListener(view ->{

            if(validar())
            {
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                //conexion con el ws
                ConexionWS_JSON conexionWS = new ConexionWS_JSON(ConfiguracionActivity.this,ConfiguracionActivity.this, "prueba");
                String url= String.format(getApplication().getResources().getString(R.string.ws_url), ti_host.getText().toString() ) ;
                conexionWS.parametrosWS.setURL( url );
                conexionWS.execute();
            }
        });

        bt_guardar.setOnClickListener(view ->{
            if(validar())
            {
                if(prueba)
                    guardar();
                else
                    Snackbar.make(contextView, R.string.msg_realizaPrueba, Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private boolean validar()
    {
        if (ti_host.getText().toString().isEmpty()){
            Snackbar.make(contextView, R.string.msg_rellenaCampos, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            if (Patterns.IP_ADDRESS.matcher(ti_host.getText().toString()).matches())
            {
                return true;
            }
            else
            {
                Snackbar.make(contextView, R.string.msg_hostValido, Snackbar.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    private void guardar()
    {
        Utils.SetHost(ti_host.getText().toString(),getApplicationContext());
        Toast.makeText(this, getResources().getString(R.string.msg_correcto), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ConfiguracionActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();

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
                    if(respuesta.equals("ok"))
                    {
                        prueba=true;
                        Snackbar.make(contextView, getString(R.string.msg_pruebaExitosa), Snackbar.LENGTH_LONG).show();
                    }

                }
            }
            else
            {
                Snackbar.make(contextView, getString(R.string.msg_errorWs)+" - "+respuesta, Snackbar.LENGTH_LONG).show();
            }

        }catch (Exception e)
        {
            Snackbar.make(contextView, e.toString(), Snackbar.LENGTH_LONG).show();
        }

    }
}