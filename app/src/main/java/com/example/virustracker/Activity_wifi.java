package com.example.virustracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Activity_wifi extends AppCompatActivity
{
    private WifiManager manejadorWifi;
    private ListView listViewRedes;
    private ListView listViewRedesGuardadas;
    private Button botonEscaneo;
    private Button botonGuardar;
    private TextView redHogar;
    private List<ScanResult> redes;
    private ArrayList<String> arrayListRedes = new ArrayList<>();
    private ArrayList<String> arrayListRedesGuardadas = new ArrayList<>();
    private ArrayAdapter adaptador;
    private ArrayAdapter adaptadorGuardados;

    private String nuevaRed = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        botonEscaneo = findViewById(R.id.botonEscanear);
        botonGuardar = findViewById(R.id.botonGuardar);
        redHogar = findViewById(R.id.redHogar);
        listViewRedes = findViewById(R.id.RedesDisponiblesLV);
        listViewRedesGuardadas = findViewById(R.id.RedesGuardadasLV);

        arrayListRedesGuardadas = (ArrayList<String>) this.getIntent().getSerializableExtra("misRedes");

        // Con esta condición se puede comprobar si el array tiene valores o si está vacío (es la primera vez, o no se han seleccionado redes)
        /*if (arrayListRedesGuardadas == null)
        {
            redHogar.setText(Integer.toString(arrayListRedesGuardadas.size()));
        } else
        {
            redHogar.setText("0");
        }*/

        // Creamos un nuevo ArrayAdapter y comprobamos si está vacío o no, si no lo está (contiene redes) las mostramos en el ListView específico para las redes.
        adaptadorGuardados = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1 , arrayListRedesGuardadas);
        if (!arrayListRedesGuardadas.isEmpty())
        {
            listViewRedesGuardadas.setAdapter(adaptadorGuardados);
        }
        else
        {

        }

        // Gestor de eventos del botón de Escanear Redes, el cuál busca las redes cercanas al dispositivo y las muestra en pantalla.
        botonEscaneo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                escanearRedes();
            }
        });

        // Gestor de clic del botón de Guardar configuración, el cuál manda (devuelve) el ArrayList con las redes almacenadas, establece el resultado del Intent a OK y finaliza este activity.
        botonGuardar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent();
                i.putExtra("nuevasRedes", arrayListRedesGuardadas);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        // Gestor de clic en el item del ListView que indica que red se ha seleccionado como red de hogar.
        listViewRedes.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                redHogar.setText((String) adaptador.getItem(position));
                nuevaRed = (String) adaptador.getItem(position);

                if (arrayListRedesGuardadas.contains(nuevaRed))
                {
                    Toast.makeText(Activity_wifi.this, "Esta red ya ha sido guardada.", Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(Activity_wifi.this, adaptador.getItem(position) + " seleccionada como Red del Hogar.", Toast.LENGTH_SHORT).show();
                    arrayListRedesGuardadas.add((String) adaptador.getItem(position));
                }

                adaptadorGuardados = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1 , arrayListRedesGuardadas);
                listViewRedesGuardadas.setAdapter(adaptadorGuardados);
            }
        });

        manejadorWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Comprobamos si el WiFi del dispositivo está apagado, de ser así mostramos un mensaje por pantalla y lo encendemos.
        if (!manejadorWifi.isWifiEnabled())
        {
            Toast.makeText(this, "Encendiendo WiFi...", Toast.LENGTH_LONG).show();
            manejadorWifi.setWifiEnabled(true);
        }
    }

    // Método que realiza el escaneo de redes WiFi cercanas.
    private void escanearRedes()
    {
        arrayListRedes.clear();
        //listViewRedes.setAdapter(null);
        registerReceiver(receptorWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        manejadorWifi.startScan();
        Toast.makeText(this, "Buscando Redes Wifi...", Toast.LENGTH_SHORT).show();

        adaptador = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1 , arrayListRedes);
        listViewRedes.setAdapter(adaptador);
    }

    // Creamos un nuevo BroadcastReceiver
    BroadcastReceiver receptorWifi = new BroadcastReceiver()
    {
        /* El método onReceive del BroadcastReceiver se ejecutará cuando termine el escaneo de redes, por eso mostramos un mensaje por pantalla diciendo que el escaneo ha finalizado
        y mostramos todos los nombres (SSID) de cada red en el ListView. */
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Toast.makeText(getApplicationContext(), "Escaneo completado", Toast.LENGTH_SHORT).show();

            redes = manejadorWifi.getScanResults();
            unregisterReceiver(this);

            for (ScanResult red : redes)
            {
                arrayListRedes.add(red.SSID);
                //arrayListRedes.add(red.BSSID);
                adaptador.notifyDataSetChanged();
            }
        }
    };
}