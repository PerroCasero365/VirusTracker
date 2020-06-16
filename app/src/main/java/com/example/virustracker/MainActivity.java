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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    boolean bluetooth = true;
    private String PREFS_KEY = "mispreferencias";
    private final int REQUEST_ACCESS_FINE = 0;

    Drawable azul;
    Drawable gris;
    BluetoothAdapter adaptador;
    int ACTIVAR_BLUETOOTH = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        redondear(getValuePreferenceColor(getApplicationContext()));

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_ACCESS_FINE);

        azul = getResources().getDrawable( R.drawable.rounded_imagebutton_azul);
        gris = getResources().getDrawable( R.drawable.rounded_imagebutton_gris);
        adaptador = BluetoothAdapter.getDefaultAdapter();

        if(!adaptador.isEnabled())
        {
            cambiaColorBluetooth(gris);
        } else
        {
            cambiaColorBluetooth(azul);
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
        //Drawable azul = getResources().getDrawable( R.drawable.rounded_imagebutton_azul);
        //Drawable gris = getResources().getDrawable( R.drawable.rounded_imagebutton_gris);

        /*if(bluetooth){
            Toast.makeText(getApplicationContext(), "Bluetooth desactivado", Toast.LENGTH_SHORT).show();
            bluetooth = false;
            cambiaColorBluetooth(gris);
        }
        else{
            Toast.makeText(getApplicationContext(), "Bluetooth activado", Toast.LENGTH_SHORT).show();
            bluetooth = true;
            cambiaColorBluetooth(azul);
        }*/
        if(!adaptador.isEnabled()){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, ACTIVAR_BLUETOOTH);
            cambiaColorBluetooth(azul);
            // bluetooth = false;
        }
        else{
            adaptador.disable();
            cambiaColorBluetooth(gris);
            // bluetooth = true;
        }
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



}
