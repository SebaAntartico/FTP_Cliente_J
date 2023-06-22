package com.app.ftp_cliente_j;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;



/**
 *
 * Subir archivos a un servidor FTP
 *
 */
public class Ftp {

    //Campos-----------------------------------------------------------

    private String ip;                //Almacena la dirección del servidor

    private String usuario;            //Almacena el nombre de usuario
    private String contrasena;        //Almacena la contraseña del usuario
    private Boolean pasivo;
    FTPClient ftpClient;            //Crea la conexión con el servidor
    BufferedInputStream buffer;        //Crea una buffer de lectura
    File rutaSd;                    //Almacena la ruta sd
    File rutaCompleta;                //Almacena la ruta completa del archivo

    Context context;                //Almacena el contexto de la aplicacion
    //util a la hora de mostrar mensajes


    //------------------------------------------------------------------
    /**
     * Crea una instancia de FTP sin credenciales
     */
    public Ftp(String ip, Context context) {

        //Inicialización de variables
        this.ip = ip;
        usuario = null;
        contrasena = null;
        pasivo=true;

        ftpClient = null;
        buffer = null;
        rutaSd = null;
        rutaCompleta = null;

        this.context = context;
    }

    /**
     * Crea una instancia de FTP con credenciales
     *
     * @param usuario    El nombre de usuario
     * @param contrasena La contraseña de usuario
     * @param pasivo modo de conexion
     */
    public Ftp(String ip, String usuario, String contrasena, Boolean pasivo, Context context) {

        //Inicialización de variables
        this.ip = ip;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.pasivo = pasivo;

        ftpClient = null;
        buffer = null;
        rutaSd = null;
        rutaCompleta = null;

        this.context = context;

    }


    //------------------------------------------------------------------
    //Propiedades-------------------------------------------------------

    /**
     * Obtiene el nombre de usuario
     *
     * @return El nombre de usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Establece el nombre de usuario
     *
     * @param usuario El nombre de usuario
     */

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene la contraseña de usuario
     *
     * @return La contraseña de usuario
     */

    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña de usuario
     *
     * @param contrasena La contraseña de usuario
     */

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Realiza el login en el servidor
     *
     * @param usuario    El nombre de usuario
     * @param contrasena La contraseña de usuario
     * @return true en caso de haber realizado login correctamente
     * @throws SocketException
     * @throws IOException
     */
    public boolean Login(String usuario, String contrasena, boolean pasivo) throws SocketException, IOException {

        //Almacena los valores en la clase
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.pasivo = pasivo;

        //Establece conexión con el servidor

        try {

            ftpClient = new FTPClient();
            ftpClient.connect(ip);

        } catch (Exception e) {

            //Informa al usuario
            //Toast.makeText(context, "Imposible conectar . . .", Toast.LENGTH_SHORT).show();
            return false;    //En caso de que no sea posible la conexion

        }


        //Hace login en el servidor
        if (ftpClient.login(usuario, contrasena)) {

            //Informa al usuario
            //Toast.makeText(context, "Login correcto . . .", Toast.LENGTH_SHORT).show();

            //return true;    //En caso de login correcto
        } else {

            //Informa al usuario
            //Toast.makeText(context, "Login incorrecto . . .", Toast.LENGTH_SHORT).show();
            return false;    //En caso de login incorrecto
        }

        try {

            if(pasivo)
            {
                ftpClient.enterLocalPassiveMode();
                ftpClient.setKeepAlive(true);
                ftpClient.setControlKeepAliveTimeout(30);
            }
            else
            {
                ftpClient.enterLocalActiveMode();
                ftpClient.setKeepAlive(true);
                ftpClient.setControlKeepAliveTimeout(30);
            }


            return true;
        } catch (Exception e) {
            return false;    //En caso de que no sea posible la conexion
        }


    }



    /**
     * Sube un archivo al servidor FTP si previamente se ha hecho login correctamente
     *
     * @param nombreArchivo Nombre del archivo que se quiere subir
     * @return Verdad en caso de que se haya subido con éxito
     * @throws IOException
     */
    public boolean SubirArchivo(String nombreArchivo) throws IOException {

        //ftpClient.enterLocalActiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);






        //Cambia la carpeta Ftp
        if (ftpClient.changeWorkingDirectory("ftp")) {

            //Informa al usuario
            //Toast.makeText(context, "Carpeta ftp cambiada . . .", Toast.LENGTH_SHORT).show();

            //Obtiene la dirección de la ruta sd
            //Toast.makeText(context, "Ruta SD obtenida . . .", Toast.LENGTH_SHORT).show();
            rutaSd = Environment.getExternalStorageDirectory();

            //Obtiene la ruta completa donde se encuentra el archivo
            //Toast.makeText(context, "Ruta completa archivo obtenida . . .", Toast.LENGTH_SHORT).show();
            rutaCompleta = new File(rutaSd.getAbsolutePath(), nombreArchivo);

            //Crea un buffer hacia el servidor de subida
            buffer = new BufferedInputStream(new FileInputStream(rutaCompleta));


            String NombreArchivo_FTP;

            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyyMMdd'_'HHmmss");

            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            String timestamp = simpleDateFormat.format(now);

            NombreArchivo_FTP=timestamp+"_"+nombreArchivo;

            try{
                if (ftpClient.storeFile(NombreArchivo_FTP, buffer)) {

                    //Informa al usuario
                    //Toast.makeText(context, "Archivo subido . . .", Toast.LENGTH_SHORT).show();
                    buffer.close();        //Cierra el bufer
                    return true;        //Se ha subido con éxito
                } else {

                    //Informa al usuario
                    //Toast.makeText(context, "Imposible subir archivo . . .", Toast.LENGTH_SHORT).show();
                    buffer.close();        //Cierra el bufer
                    return false;        //No se ha subido
                }
            } catch (Exception e) {
                return false;
            }

        } else {

            //Informa al usuario
            //Toast.makeText(context, "Carpeta ftp imposible cambiar . . .", Toast.LENGTH_SHORT).show();
            return false;        //Imposible cambiar de directo en servidor ftp
        }

    }



}