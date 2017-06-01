package com.foodxplorer.foodxplorer.helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.foodxplorer.foodxplorer.objetos.Usuario;

import java.io.InputStreamReader;

/**
 * Projecte FoodXplorer
 *
 * @Autors Abel Serrano, Sergi Gonazalez, Roger G.
 * Created by IES on 01/06/2017.
 */

public class TareaComprobarLogin extends AsyncTask<Object, Void, Boolean> {

   public AsyncResponse delegate = null;

    @Override
    protected Boolean doInBackground(Object... params) {
        boolean result = true;
        InputStreamReader osw;
        RestManager restManager;
        try {
            Usuario user = (Usuario) params[0];
            String aux = (Settings.DIRECCIO_SERVIDOR + Settings.PATH + "loguearUsuario");
            MD5 md5 = new MD5();
            System.out.println(aux);
            aux = aux + "/" + user.getUsername() + "/" + MD5.hash(user.getPassword()) + "/";
            System.out.println("HOLA!!!");
            Log.e(Settings.LOGTAG, "Aux es:" + aux);
            System.out.println("ADIOS!!!");
            restManager = new RestManager(aux);
            osw = restManager.getInputStream();
            int data = osw.read();
            String res = "";
            while (data != -1) {
                char current = (char) data;
                data = osw.read();
                res = res + current;
            }
            if (!res.equals("true")) {
                Log.e(Settings.LOGTAG, "Error de login para user: " + user.getUsername());
                result = false;
            }
            osw.close();
            restManager.disconnect();
        } catch (java.net.ProtocolException ex) {
            Log.e(Settings.LOGTAG, "Error de protocol: " + ex);
            result = false;
        } catch (java.io.FileNotFoundException | java.net.MalformedURLException ex) {
            Log.e(Settings.LOGTAG, "Error de ruta d'acces: " + ex);
            result = false;
        } catch (java.net.SocketTimeoutException ex) {
            Log.e(Settings.LOGTAG, "Temps d'espera esgotat al iniciar la conexio amb la BBDD extena: " + ex);
            result = false;
        } catch (java.io.IOException ex) {
            Log.e(Settings.LOGTAG, "Undefined error: " + ex);
            result = false;
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        delegate.processFinish(result);
    }
}
