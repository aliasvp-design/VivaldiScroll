package com.example.vivaldiscroll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 60, 40, 40);
        TextView info = new TextView(this);
        info.setText("Vivaldi Scroll 0.1\n\nActiva el servicio en Accesibilidad. La flecha aparece solo en Vivaldi.\n\nToque: subir · Pulsación larga: bajar\n\nSin Internet, capturas ni lectura de contenido.\n\nPrototipo experimental: los gestos no garantizan llegar al extremo de todas las páginas.");
        info.setTextSize(18);
        layout.addView(info);
        Button button = new Button(this);
        button.setText("Abrir ajustes de accesibilidad");
        button.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        layout.addView(button);
        setContentView(layout);
    }
}
