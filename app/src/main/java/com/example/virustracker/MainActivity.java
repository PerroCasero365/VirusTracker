package com.example.virustracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private String PREFS_KEY = "mispreferencias";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        redondear(getValuePreferenceColor(getApplicationContext()));

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
    public void onBackPressed(){ }

    public void cambiaEstado(View v)
    {
        Intent intent = new Intent(this, Activity_cambia_estado.class);
        startActivityForResult(intent, 1001);
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
