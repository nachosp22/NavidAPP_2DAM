package com.example.navidapp_2dam;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Importante
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation; // Importante para navegar

public class InicioHistoria extends Fragment {

    // EL TEXTO DE TU HISTORIA
    private String historia = "¡OH NO, DIOS MIO!\n SANTA CLAUS ACABA DE TENER UN ACCIDENTE, NECESITA AYUDA PARA PODER REPARTIR TODOS LOS REGALOS ESTE PROXIMO DIA 25\n ¿ESTAS DISPUESTO A AYUDARLE?";

    private TextView tvHistoria;
    private Button btnAyudar; // Referencia al botón
    private int indice = 0;
    private long velocidad = 50; // Velocidad de escritura
    private Handler handler = new Handler(Looper.getMainLooper());

    public InicioHistoria() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Carga tu XML
        return inflater.inflate(R.layout.inicio_historia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ByPass
        Button btnSaltar = view.findViewById(R.id.btnSaltarQR);

        btnSaltar.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", "Aventurero (Cheater)"); // O la variable 'nombre'
            Navigation.findNavController(view).navigate(R.id.action_historia_to_dialogo_directo, bundle);
        });

        // 1. Vincular vistas
        tvHistoria = view.findViewById(R.id.tvHistoria);
        // Asegúrate de que en tu XML el botón se llame "btnAyudarSanta" o cambia esto
        btnAyudar = view.findViewById(R.id.btnAyudarSanta);

        // 2. Estado inicial
        tvHistoria.setText(""); // Texto vacío
        btnAyudar.setVisibility(View.INVISIBLE); // BOTÓN OCULTO AL PRINCIPIO

        // 3. Empezar animación
        empezarAnimacion();

        // 4. Lógica del botón para ir al siguiente juego
        btnAyudar.setOnClickListener(v -> {
            // Navegamos a la pantalla del JuegoQR (la que tiene el fondo vacío)
            Navigation.findNavController(view).navigate(R.id.action_historia_to_juegoQR);
        });
    }

    private void empezarAnimacion() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (indice < historia.length()) {
                    // Escribiendo letra a letra...
                    tvHistoria.append(String.valueOf(historia.charAt(indice)));
                    indice++;
                    handler.postDelayed(this, velocidad);
                } else {
                    // YA ACABÓ EL TEXTO: MOSTRAR EL BOTÓN
                    btnAyudar.setVisibility(View.VISIBLE);
                }
            }
        }, 500); // Espera medio segundo antes de empezar
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null); // Limpieza para evitar errores
    }
}