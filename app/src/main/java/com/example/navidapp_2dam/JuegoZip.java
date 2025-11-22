package com.example.navidapp_2dam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation; // Importante para la navegación

public class JuegoZip extends Fragment {

    public JuegoZip() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos el diseño que contiene el tablero
        return inflater.inflate(R.layout.fragment_juego_zip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Recuperar nombre del jugador (viene del diálogo anterior)
        String nombre = "Aventurero";
        if (getArguments() != null) {
            nombre = getArguments().getString("nombreJugador", "Aventurero");
        }

        // 2. Vincular las vistas del XML
        TableroView tablero = view.findViewById(R.id.tableroZip);
        Button btnReiniciar = view.findViewById(R.id.btnReiniciarZip);

        // 3. Configurar el botón de reiniciar
        btnReiniciar.setOnClickListener(v -> tablero.reiniciar());

        // 4. LÓGICA DE VICTORIA
        // Aquí escuchamos a la "alarma" que pusimos dentro de TableroView
        String finalNombre = nombre;

        tablero.setOnJuegoGanadoListener(() -> {
            // A. Mensaje de felicitación
            Toast.makeText(getContext(), "¡MOTOR ARREGLADO! ⚡", Toast.LENGTH_LONG).show();

            // B. Preparar el paquete para el siguiente nivel
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", finalNombre);

            // C. Navegar al siguiente juego (Descifrar)
            // Asegúrate de que esta flecha existe en tu nav_graph.xml
            Navigation.findNavController(view).navigate(R.id.action_juegoZip_to_descifrar, bundle);
        });
    }
}