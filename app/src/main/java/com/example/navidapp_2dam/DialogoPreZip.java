package com.example.navidapp_2dam;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class DialogoPreZip extends Fragment {

    private TextView tvSanta, tvElfos;
    private Button btnCaja;

    // TUS TEXTOS
    private String textoSanta = "SANTA: Hola jove ¿Como jdjsfj3j4iojio435?";
    private String textoElfos = "ELFOS: ¡Cielos! Santa se ha golpeado la cabeza.\n\nNecesitamos la 'Tabla de Equivalencias' que está en su caja de herramientas.\n\n¡Rápido, intenta abrir el candado!";

    private Handler handler = new Handler(Looper.getMainLooper());

    public DialogoPreZip() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialogo_pre_zip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recuperar nombre
        String nombre = "Aventurero";
        if (getArguments() != null) nombre = getArguments().getString("nombreJugador", "Aventurero");

        tvSanta = view.findViewById(R.id.tvSantaHabla);
        tvElfos = view.findViewById(R.id.tvElfos);
        btnCaja = view.findViewById(R.id.btnAbrirCaja);

        // Limpiamos los textos antes de empezar
        tvSanta.setText("");
        tvElfos.setText("");
        tvElfos.setVisibility(View.INVISIBLE);
        btnCaja.setVisibility(View.INVISIBLE);

        // 1. INICIAR LA ANIMACIÓN COMPLETA
        iniciarDialogoSecuencial();

        // 2. BOTÓN PARA IR AL ZIP
        String finalNombre = nombre;
        btnCaja.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", finalNombre);
            // Navegamos al juego del ZIP
            Navigation.findNavController(view).navigate(R.id.action_historia_to_juegoZip_directo, bundle);
        });
    }

    private void iniciarDialogoSecuencial() {
        new Thread(() -> {
            // --- FASE 1: SANTA HABLA ---
            for (int i = 0; i < textoSanta.length(); i++) {
                int finalI = i;
                handler.post(() -> tvSanta.append(String.valueOf(textoSanta.charAt(finalI))));
                try { Thread.sleep(60); } catch (InterruptedException e) {}
            }

            // PAUSA DRAMÁTICA DE 1 SEGUNDO
            try { Thread.sleep(1000); } catch (InterruptedException e) {}

            // --- FASE 2: PREPARAR ELFOS ---
            handler.post(() -> {
                tvElfos.setVisibility(View.VISIBLE); // Aparece el cuadro
                tvElfos.setText(""); // Aseguramos que esté vacío
            });

            // --- FASE 3: ELFOS HABLAN (LETRA A LETRA) ---
            for (int i = 0; i < textoElfos.length(); i++) {
                int finalI = i;
                handler.post(() -> tvElfos.append(String.valueOf(textoElfos.charAt(finalI))));
                // Un pelín más rápido que Santa (40ms)
                try { Thread.sleep(40); } catch (InterruptedException e) {}
            }

            // --- FASE 4: APARECE EL BOTÓN ---
            handler.post(() -> btnCaja.setVisibility(View.VISIBLE));

        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null); // Limpieza
    }
}