package com.example.navidapp_2dam;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class DialogoPreZip extends Fragment {

    private TextView tvSanta, tvElfos;
    private ImageView imgSanta, imgElfos;
    private Button btnCaja;
    private String nombreJugador;
    private Handler handler = new Handler(Looper.getMainLooper());

    private String textoSanta = "SANTA: ¡Buenos días! En España ese XYVVSR que coméis... ¿De qué está hecho? jojo... brrzzzt...";
    private String textoElfos = "ELFOS: ¡Cielos! A Santa se le ha roto el traductor universal.\nBusca el DECODIFICADOR en la caja fuerte (Juego Zip).";

    public DialogoPreZip() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialogo_pre_zip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) nombreJugador = getArguments().getString("nombreJugador", "Aventurero");

        tvSanta = view.findViewById(R.id.tvSantaHabla);
        imgSanta = view.findViewById(R.id.imgSantaZip);

        tvElfos = view.findViewById(R.id.tvElfos);
        imgElfos = view.findViewById(R.id.imgElfosZip);

        btnCaja = view.findViewById(R.id.btnAbrirCaja);

        iniciarDialogo();

        btnCaja.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", nombreJugador);
            Navigation.findNavController(view).navigate(R.id.action_dialogo_to_juegoZip, bundle);
        });
    }

    private void iniciarDialogo() {
        new Thread(() -> {
            try {
                // FASE 1: SANTA
                handler.post(() -> { imgSanta.setVisibility(View.VISIBLE); tvSanta.setVisibility(View.VISIBLE); });
                escribirTexto(tvSanta, textoSanta);
                Thread.sleep(textoSanta.length() * 40 + 1000);

                // FASE 2: ELFOS
                handler.post(() -> { imgElfos.setVisibility(View.VISIBLE); tvElfos.setVisibility(View.VISIBLE); });
                escribirTexto(tvElfos, textoElfos);
                Thread.sleep(textoElfos.length() * 40 + 500);

                // FASE 3: BOTÓN
                handler.post(() -> btnCaja.setVisibility(View.VISIBLE));
            } catch (Exception e) {}
        }).start();
    }

    private void escribirTexto(TextView tv, String texto) {
        new Thread(() -> {
            for (int i = 0; i < texto.length(); i++) {
                int finalI = i;
                handler.post(() -> tv.append(String.valueOf(texto.charAt(finalI))));
                try { Thread.sleep(40); } catch (Exception e) {}
            }
        }).start();
    }
}