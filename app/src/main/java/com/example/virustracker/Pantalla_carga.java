package com.example.virustracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class Pantalla_carga extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);


        ImageView virus = findViewById(R.id.imagenvirus);
        rotarImagen(virus);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Intent intent;
                boolean muestra = getValuePreference(getApplicationContext());
                if (!muestra){
                    intent = new Intent(Pantalla_carga.this, MainActivity.class);
                }else{
                    intent = new Intent(Pantalla_carga.this, Activity_encuesta_positivo.class);
                    saveValuePreference(getApplicationContext(), false);
                }
                //Intent intent = new Intent(Pantalla_carga.this, MainActivity.class);
                //Intent intent = new Intent(Pantalla_carga.this, Activity_encuesta_positivo.class);

                startActivity(intent);
            }
        },4000);
    }

    protected void rotarImagen(ImageView virus)
    {
        RotateAnimation animation = new RotateAnimation(0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animation.setDuration(2000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        virus.startAnimation(animation);
    }

    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("license", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("license", true);
    }
}
