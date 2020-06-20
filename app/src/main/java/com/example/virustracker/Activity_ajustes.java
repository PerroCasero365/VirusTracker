package com.example.virustracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class Activity_ajustes extends AppCompatActivity {
    Intent intent;
    // Wifi
    WifiManager wifi;
    String ssid;
    // Array de redes guardas por el usuario.
    private ArrayList<String> redes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        //Detectamos si hay alguna red wifi conectada y recogemos su ssid
        wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //Pone que el wifi conectado es desconocido y no devuelve su ssid
        ssid = wifi.getConnectionInfo().getBSSID();
    }

    public void onClickAjustes(View v){
        switch(v.getId()){
            case R.id.button_reiniciar:
                    //Sharedpreferences a verde
                break;
            case R.id.button_wifi:
                intent = new Intent(this, Activity_wifi.class);
                intent.putExtra("misRedes", redes);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                // Obtengo las redes almacenadas en el activity Activity_ajustes y lo almaceno en el array de redes.
                redes = (ArrayList<String>) data.getExtras().getSerializable("nuevasRedes");
            }
        }
    }

    // BroadcastReceiver para ver si alguna de las redes wifi guardadas está conectada, y detener el escaneo, pero no funciona.
    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();

            if(ni != null)
            {
                Toast.makeText(context, "El wifi está conectado", Toast.LENGTH_LONG).show();

                // No entra al contains
                if(redes.contains(ssid))
                {
                    Toast.makeText(context,"Esta red está guardada.", Toast.LENGTH_LONG).show();
                    // Aquí se debe parar el escaneo de dispositivos.
                    // adaptador.disable();
                }
            }
            else
            {
                Toast.makeText(context, "El Wifi está desconectado", Toast.LENGTH_LONG).show();
                // adaptador.enable();
            }
        }
    };
}
