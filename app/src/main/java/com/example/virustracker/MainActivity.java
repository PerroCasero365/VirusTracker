package com.example.virustracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        switch(item.getItemId())
        {
            case R.id.menu_sintomas:
                Toast.makeText(this, "Menu síntomas", Toast.LENGTH_LONG).show();
                break;

            case R.id.menu_informacion:
                Toast.makeText(this, "Menu Información", Toast.LENGTH_LONG).show();
                break;

            case R.id.menu_consejos_higiene:
                Toast.makeText(this, "Menu Consejos de higiene", Toast.LENGTH_LONG).show();
                break;

            case R.id.menu_ayuda:
                Toast.makeText(this, "Menu ayuda", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //evento vacío para que no vuelva a la pantalla de carga
    public void onBackPressed(){ }

    public void cambia0rEstado(View v)
    {
        redondear(R.drawable.amarillo);
    }




}
