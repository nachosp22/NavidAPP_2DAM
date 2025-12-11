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

    // --- TEXTOS ACTUALIZADOS ---
    // Santa pregunta por el "XYVVSR" (TURRON cifrado +4)
    private String textoSanta = "SANTA: ¡Buenos días joven! En España ese XYVVSR que coméis de postre... ¿De qué está hecho? jojo... brrzzzt...";

    private String textoElfos = "ELFOS: ¿¿ XYVVSR ?? ¿Que narices dira este? Se le ha ido la pinza, seguro que se ha pasado con la nieve.\n\n" +
            "Necesitamos el DECODIFICADOR que está guardado en su caja de herramientas.\n\n" +
            "¡Resuelve el puzzle del candado ZIP para abrir la caja!";

    private Handler handler = new Handler(Looper.getMainLooper());
    private String nombreJugador;

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
        nombreJugador = "Aventurero";
        if (getArguments() != null) nombreJugador = getArguments().getString("nombreJugador", "Aventurero");

        tvSanta = view.findViewById(R.id.tvSantaHabla);
        tvElfos = view.findViewById(R.id.tvElfos);
        btnCaja = view.findViewById(R.id.btnAbrirCaja);

        // Limpieza inicial
        tvSanta.setText("");
        tvElfos.setText("");
        tvElfos.setVisibility(View.INVISIBLE);
        btnCaja.setVisibility(View.INVISIBLE);

        // INICIAR LA ANIMACIÓN
        iniciarDialogoSecuencial();

        // BOTÓN PARA IR AL ZIP
        btnCaja.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", nombreJugador);
            // Navegamos al juego del ZIP para abrir la caja
            Navigation.findNavController(view).navigate(R.id.action_dialogo_to_juegoZip, bundle);
        });
    }

    private void iniciarDialogoSecuencial() {
        new Thread(() -> {
            // --- FASE 1: SANTA HABLA ---
            for (int i = 0; i < textoSanta.length(); i++) {
                int finalI = i;
                handler.post(() -> tvSanta.append(String.valueOf(textoSanta.charAt(finalI))));
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }

            // PAUSA
            try { Thread.sleep(800); } catch (InterruptedException e) {}

            // --- FASE 2: PREPARAR ELFOS ---
            handler.post(() -> {
                tvElfos.setVisibility(View.VISIBLE);
                tvElfos.setText("");
            });

            // --- FASE 3: ELFOS HABLAN ---
            for (int i = 0; i < textoElfos.length(); i++) {
                int finalI = i;
                handler.post(() -> tvElfos.append(String.valueOf(textoElfos.charAt(finalI))));
                try { Thread.sleep(30); } catch (InterruptedException e) {}
            }

            // --- FASE 4: APARECE EL BOTÓN ---
            handler.post(() -> btnCaja.setVisibility(View.VISIBLE));

        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}