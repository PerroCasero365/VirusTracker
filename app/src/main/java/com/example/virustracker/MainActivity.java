package com.example.virustracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.virustracker.UtilidadesBD.ConexionSQLiteHelper;
import com.example.virustracker.UtilidadesBD.Utilidades;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class MainActivity extends AppCompatActivity
{
    boolean bluetooth = true;
    private String PREFS_KEY = "mispreferencias";
    private final int REQUEST_ACCESS_FINE = 0;

    Drawable azul;
    Drawable gris;
    BluetoothAdapter adaptador;
    int ACTIVAR_BLUETOOTH = 2;

    // Base de Datos
    ConexionSQLiteHelper baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        redondear(getValuePreferenceColor(getApplicationContext()));
        baseDatos = new ConexionSQLiteHelper(this, "virusTrackerBBDD", null, 2);
        /*
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BLUETOOTH_ADMIN)) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, ACTIVAR_BLUETOOTH);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                        REQUEST_ACCESS_FINE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_ACCESS_FINE);
        */

        azul = getResources().getDrawable( R.drawable.rounded_imagebutton_azul);
        gris = getResources().getDrawable( R.drawable.rounded_imagebutton_gris);
        adaptador = BluetoothAdapter.getDefaultAdapter();

        if(adaptador == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_LONG).show();
            cambiaColorBluetooth(gris);
        }
        else{
            if (!adaptador.isEnabled()) {
                cambiaColorBluetooth(azul);
            } else {
                cambiaColorBluetooth(gris);
            }
        }

        new Conexion().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_FINE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    //redondea el imageview y se le pasa el R.drawable del color
    public void redondear(int id)
    {
        //obtenemos el drawable original
        Drawable originalDrawable = getResources().getDrawable(id);
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);
        float num = 2000;
        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(num);
        ImageView estado = findViewById(R.id.estado_usuario);
        estado.setImageDrawable(roundedDrawable);
    }

    //Muestra el menú inflando el layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Infla el menú,añade items a la barra de acción si ésta se presiona
        getMenuInflater().inflate(R.menu.menu_pantalla_inicio, menu);
        return true;
    }

    //Evento que controla qué opción del menú se ha pulsado para abrir su Activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        switch(item.getItemId())
        {
            case R.id.menu_sintomas:
                intent = new Intent(this, Activity_sintomas.class);
                startActivity(intent);
                break;

            case R.id.menu_informacion:
                intent = new Intent(this, Activity_informacion.class);
                startActivity(intent);
                break;

            case R.id.menu_consejos_higiene:
                intent = new Intent(this, Activity_consejos_higiene.class);
                startActivity(intent);
                break;

            case R.id.menu_ayuda:
                intent = new Intent(this, Activity_ayuda_usuario.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //evento vacío para que no vuelva a la pantalla de carga
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Salir");
        builder.setMessage("¿Quieres cerrar la app?");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void cambiaEstado(View v)
    {
        Intent intent = new Intent(this, Activity_cambia_estado.class);
        startActivityForResult(intent, 1001);
    }

    public void GestionBluetooth(View v)
    {
        if(adaptador == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_LONG).show();
        }
        else{
            if (!adaptador.isEnabled()) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, ACTIVAR_BLUETOOTH);
                cambiaColorBluetooth(azul);
                // bluetooth = false;
            } else {
                adaptador.disable();
                cambiaColorBluetooth(gris);
                // bluetooth = true;
            }
        }
    }
    public void cambiaColorBluetooth(Drawable color){
        ImageButton imageButtonBluetooth = (ImageButton) findViewById(R.id.imageButton_Bluetooth);
        imageButtonBluetooth.setBackground(color);
    }

    public int getValuePreferenceColor(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getInt("color", R.drawable.verde);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == 2001 && requestCode == 1001)
        {
            if (data.hasExtra("nuevoEstado"))
            {
                redondear(getValuePreferenceColor(getApplicationContext()));
            }
        }
    }

    class Conexion extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... strings)
        {
            String result = "";

            /* Dejo comentadas estas líneas ya que son las IP's de nuestros servidores, en lugar de tener que ir escribiéndola
            o haciendo el ipconfig en el pc las descomentamos por si alguno de nosotros quiere hacer pruebas y que cada uno funcione
             con su propia IP */
            /* Host máquina Angel */
            String host = "http://192.168.0.13/pruebaServer/consulta.php";
            /* Host Máquina Alan
            String host = "http://192.168.100.114/pruebaServer/consulta.php"; */

            try
            {
                HttpClient cliente = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = cliente.execute(request);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer stringBuffer = new StringBuffer("");
                String linea = "";

                while ((linea = reader.readLine()) != null)
                {
                    stringBuffer.append(linea);
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (Exception e)
            {
                return new String("Ha ocurrido una una excepción: " + e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // Parseamos la información del JSON aquí
            try
            {
                JSONObject resultadoJson = new JSONObject(result);
                int exito = resultadoJson.getInt("exito");
                SQLiteDatabase db = baseDatos.getWritableDatabase();

                // en el php del server hay un array 'respuesta' donde el índice éxito tiene un valor de 1,
                // ese valor se pone a 1 cuándo el JSON ya posee valores y a 0 si está vacío.
                if (exito == 1)
                {
                    JSONArray dispositivos = resultadoJson.getJSONArray("dispositivos");

                    //si el json ya tiene valores eliminamos la tabla sqlite y luego se va creando de nuevo
                    db.execSQL(Utilidades.ELIMINAR_TABLA_USUARIO);
                    db.execSQL(Utilidades.CREAR_TABLA_USUARIO);

                    for (int i = 0; i < dispositivos.length(); i++)
                    {
                        JSONObject dispositivo = dispositivos.getJSONObject(i);
                        int id = dispositivo.getInt("DEVICE_ID");
                        String fecha = dispositivo.getString("DATE_STATUS");

                        db.execSQL("INSERT INTO dispositivosInfectados (DEVICE_ID, DATE_STATUS)" +
                                "VALUES ('" + id + "', '" + fecha + "')");
                    }
                } else
                {
                    Toast.makeText(getApplicationContext(), "No hay dispositivos registrados", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException jsone)
            {
                Toast.makeText(getApplicationContext(), "Error en el JSON: " + jsone.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(getApplicationContext(), "Base de Datos actualizada.", Toast.LENGTH_SHORT).show();
        }

        /*protected String onPostExecute(Void result)
        {
            return null;
        }*/
    }
}
