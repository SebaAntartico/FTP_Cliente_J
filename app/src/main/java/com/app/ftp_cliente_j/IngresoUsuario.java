package com.app.ftp_cliente_j;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import static com.app.ftp_cliente_j.R.id.checkBox;
import static com.app.ftp_cliente_j.R.id.editTextClave;
import static com.app.ftp_cliente_j.R.id.editTextServidor;
import static com.app.ftp_cliente_j.R.id.editTextUsuario;


public class IngresoUsuario extends AppCompatActivity{

    private EditText txtservidor;
    private EditText txtusuario;
    private EditText txtclave;
    private CheckBox valorpasivo;
    private Button btnAceptar;
    private Button btnCancelar;
    String str=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_datos_ftp);



        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        String ip_main = intent.getStringExtra(MainActivity.EXTRA_IP);
        String usuario_main = intent.getStringExtra(MainActivity.EXTRA_USER);
        String contrasena_main = intent.getStringExtra(MainActivity.EXTRA_PASS);
        Boolean pasivo_main = intent.getBooleanExtra(MainActivity.EXTRA_PASIVO,true);

        // Capture the layout's TextView and set the string as its text
        //TextView textView = findViewById(R.id.textView3);
        //textView.setText(message);


        //Localizar los controles
        txtservidor = (EditText)findViewById(editTextServidor);
        txtusuario = (EditText)findViewById(editTextUsuario);
        txtclave = (EditText)findViewById(editTextClave);
        valorpasivo=(CheckBox) findViewById(checkBox);

        //Recuperamos la información pasada en el intent
        //Bundle bundle = this.getIntent().getExtras();

        //Construimos el mensaje a mostrar
        //txtusuario.setText("Hola " + bundle.getString("USUARIO"));

        txtservidor.setText(ip_main);
        txtusuario.setText(usuario_main);
        txtclave.setText(contrasena_main);
        if(pasivo_main)
        {
            valorpasivo.setChecked(true);
        }else
        {
            valorpasivo.setChecked(false);
        }

        btnCancelar = (Button) findViewById(R.id.buttonCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnAceptar = (Button) findViewById(R.id.buttonAceptar);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el EditText no está vacío recogemos el resultado.
                if(txtservidor.getText().length()!=0  && txtusuario.getText().length()!=0  && txtclave.getText().length()!=0)
                {
                    // Guardamos el texto del EditText en un String.
                    String servidor = txtservidor.getText().toString();
                    String usuario = txtusuario.getText().toString();
                    String clave = txtclave.getText().toString();
                    Boolean pasivo = true;

                    if(valorpasivo.isChecked()) {
                        pasivo = true;
                    }else
                    {
                        pasivo=false;
                    }
                    // Recogemos el intent que ha llamado a esta actividad.
                    Intent i = getIntent();
                    // Le metemos el resultado que queremos mandar a la
                    // actividad principal.
                    i.putExtra("SERVIDOR", servidor);
                    i.putExtra("USUARIO", usuario);
                    i.putExtra("CLAVE", clave);
                    i.putExtra("PASIVO", pasivo);
                    // Establecemos el resultado, y volvemos a la actividad
                    // principal. La variable que introducimos en primer lugar
                    // "RESULT_OK" es de la propia actividad, no tenemos que
                    // declararla nosotros.
                    setResult(RESULT_OK, i);

                    // Finalizamos la Activity para volver a la anterior
                    finish();

                }
                else
                {
                    // Si no tenía nada escrito el EditText lo avisamos.
                    Toast.makeText(IngresoUsuario.this,"No se han introducido todos los datos",Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                }

            }
        });

    }





}
