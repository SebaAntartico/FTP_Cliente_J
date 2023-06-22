package com.app.ftp_cliente_j;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOError;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;


public class MainActivity<val> extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.app.ftp_cliente_j.MESSAGE";
    public static final String EXTRA_IP = "com.app.ftp_cliente_j.IP";
    public static final String EXTRA_USER = "com.app.ftp_cliente_j.USER";
    public static final String EXTRA_PASS = "com.app.ftp_cliente_j.PASS";
    public static final String EXTRA_PASIVO = "com.app.ftp_cliente_j.PASIVO";

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Server = "serverKey";
    public static final String User = "userKey";
    public static final String Password = "passKey";

    SharedPreferences sharedpreferences;

    //Campos----------------------------------------------------

    EditText nombreArhivo;        // Almacena el id del componente donde está localizado el nombre del archivo a subir
    Button subir;                // Almacena el id del componente donde está localizado el botón para subir el archivo

    //Credenciales
    String ip;                    //Almacena la direción ip del servidor
    String usuario;                //Almacena el usuario
    String contrasena;            //Almacena la contraseña
    Boolean pasivo;

    Ftp ftp;                    //Instancia manejador ftp

    String Version = "FTP Idear - Versión ";
    String version = BuildConfig.VERSION_NAME;

    int request_code = 1;
    Button btnDatos;

    private MiTareaAsincrona tarea1;

    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 102;

    private static final int PERMISSION_REQUEST_CODE = 200;
    private View view;

    //String[] perms = { "android.permission.CAMERA"};

    //-----------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_subir);

        //Inicializa las credenciales
        //ip = "152.170.252.4";
        //ip = "idear.dyndns.org";
        //usuario = "sebastian";
        //contrasena ="1234";


        // Retrieving the value using its keys the file name
        // must be same in both saving and retrieving the data
        SharedPreferences sh = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // The value will be default as empty string because for
        // the very first time when the app is opened, there is nothing to show
        ip = sh.getString("serverKey", "");
        usuario = sh.getString("userKey", "");
        contrasena = sh.getString("passKey", "");
        pasivo = sh.getBoolean("pasivoKey",true);

        //Establece los ids de la vista
        nombreArhivo  = (EditText) findViewById(R.id.edtxtNombreArchivo);
        subir = (Button) findViewById(R.id.btnSubir);

        //Evento OnClick (btnSubir)
        subir.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                //PedirEstado();

                //int permsRequestCode = 200;
                //requestPermissions(perms, permsRequestCode);


                //if(checkPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE))
                if (checkPermission())
                {
                    PedirEstado();
                }
                else {
                    Toast.makeText(MainActivity.this, "Please request permission.", Toast.LENGTH_SHORT).show();
                }
            }
        });




        Button check_permission = (Button) findViewById(R.id.check_permission);
        Button request_permission = (Button) findViewById(R.id.request_permission);


        //check_permission.setOnClickListener(this);
        //request_permission.setOnClickListener(this);

        check_permission.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (checkPermission()) {

                    //Snackbar.make(view, "Permission already granted.", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, "Permission already granted.", Toast.LENGTH_SHORT).show();

                } else {

                    //Snackbar.make(view, "Please request permission.", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, "Please request permission.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        request_permission.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (!checkPermission()) {

                    requestPermission();

                } else {

                    //Snackbar.make(view, "Permission already granted.", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, "Permission already granted.", Toast.LENGTH_SHORT).show();

                }
            }
        });




        /*btnDatos = (Button)findViewById(R.id.btnDatos);
        btnDatos.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v)
                  {
                      //Creamos el Intent
                      //Intent intent = new Intent(MainActivity.this, IngresoUsuario.class);

                      //Iniciamos la nueva actividad
                      //startActivityForResult(intent,request_code);

                      sendMessage(v);

                  }
              }
        );*/


    }


    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        //Intent intent = new Intent(this, IngresoUsuario.class);
        //intent.putExtra(EXTRA_MESSAGE, "Idear");
        //startActivity(intent);
    }


    // Function to check and request permission.
    /*public boolean checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
            //return false;

            //Intent intent = new Intent();
            //intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            //Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            //intent.setData(uri);
            //startActivity(intent);
            return true;

        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
            return true;
        }
    }*/

    /*
// If you have access to the external storage, do whatever you need
if (Environment.isExternalStorageManager()){

// If you don't have access, launch a new activity to show the user the system's dialog
// to allow access to the external storage
}else{
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
    intent.setData(uri);
    startActivity(intent);
}


     */




    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }*/







    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result1 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE,CAMERA}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (readAccepted && cameraAccepted) {
                        //Snackbar.make(view, "Permission Granted.", Snackbar.LENGTH_LONG).show();
                        Toast.makeText(MainActivity.this, "Permission Granted.", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        //Snackbar.make(view, "Permission Denied.", Snackbar.LENGTH_LONG).show();
                        Toast.makeText(MainActivity.this, "Permission Denied.", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.configuracion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ingreso:

                Intent intent = new Intent(MainActivity.this, IngresoUsuario.class);

                intent.putExtra(EXTRA_IP, ip);
                intent.putExtra(EXTRA_USER, usuario);
                intent.putExtra(EXTRA_PASS, contrasena);
                intent.putExtra(EXTRA_PASIVO, pasivo);

                //Iniciamos la nueva actividad
                startActivityForResult(intent,request_code);

                return true;

            //case R.id.ms250:
            //    intervalo=250;
            //    return true;

            case R.id.info:
                Toast.makeText(this, Version + version, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private class MiTareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            //Establece un servidor
            ftp = new Ftp(ip, usuario, contrasena, pasivo, getApplicationContext());
            boolean resultado = false;



            //Realiza login en el servidor
            try {
                //final Toast toast = Toast.makeText(MainActivity.this, "Conectando . . .", Toast.LENGTH_SHORT);
                //toast.show();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Conectando . . .", Toast.LENGTH_SHORT).show();
                    }
                });


                resultado = ftp.Login(usuario, contrasena, pasivo);
            } catch (SocketException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "SocketException", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "IOException", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if(resultado) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Login correcto . . .", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Login incorrecto . . .", Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }

            resultado =false;
            //Sube el archivo al servidor
            try {
                resultado = ftp.SubirArchivo(nombreArhivo.getText().toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(resultado) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Archivo Subido . . .", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Archivo No Subido . . .", Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();

            //pbarProgreso.setProgress(progreso);
        }

        @Override
        protected void onPreExecute() {
            //pbarProgreso.setMax(100);
            //pbarProgreso.setProgress(0);
        }

        @Override
        protected void onPostExecute(Boolean result) {

            String Texto, Texto1, Presion_temp;
            int ChkSum, caracter;
            boolean sinrespuesta;
            sinrespuesta = false;


            if(result)
            {
                //yyy-mm-ddThh:mmZ:ss
                /*SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd'_'HH:mm:ss");

                Calendar calendar = Calendar.getInstance();
                Date now = calendar.getTime();
                String timestamp = simpleDateFormat.format(now);

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Finalizado " + timestamp, Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
            else
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }

        @Override
        protected void onCancelled() {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "Cancelado!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private void PedirEstado() {
            tarea1 = new MiTareaAsincrona();
            tarea1.execute();
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        //if ((requestCode == request_code) && (resultCode == RESULT_OK)){
        //    //tvTexto.setText(data.getDataString());
        //}
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // De lo contrario, recogemos el resultado de la segunda actividad.
            ip = data.getExtras().getString("SERVIDOR");
            usuario = data.getExtras().getString("USUARIO");
            contrasena = data.getExtras().getString("CLAVE");
            pasivo = data.getExtras().getBoolean("PASIVO");

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("serverKey", ip.toString());
            myEdit.putString("userKey", usuario);
            myEdit.putString("passKey", contrasena);
            myEdit.putBoolean("pasivoKey", pasivo);
            myEdit.commit();

        } else {
            // Si es así mostramos mensaje de cancelado por pantalla.
            Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show();
        }
    }



}