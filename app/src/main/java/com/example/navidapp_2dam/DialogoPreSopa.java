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

public class DialogoPreSopa extends Fragment {

    private TextView tvSanta;
    private ImageView imgSanta;
    private Button btnCocina;
    private String nombreJugador;
    private Handler handler = new Handler(Looper.getMainLooper());

    private String textoSanta = "SANTA: ¡Ho Ho Ho! Gracias por aclararme lo del turrón.\n\nPero hablar de comida me ha abierto el apetito...\n¿Podrías conseguirme un buen menú navideño?";

    public DialogoPreSopa() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialogo_pre_sopa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) nombreJugador = getArguments().getString("nombreJugador", "Aventurero");

        tvSanta = view.findViewById(R.id.tvSantaHambre);
        imgSanta = view.findViewById(R.id.imgSantaSopa);
        btnCocina = view.findViewById(R.id.btnIrCocina);

        new Thread(() -> {
            try {
                handler.post(() -> { imgSanta.setVisibility(View.VISIBLE); tvSanta.setVisibility(View.VISIBLE); });
                for (int i = 0; i < textoSanta.length(); i++) {
                    int finalI = i;
                    handler.post(() -> tvSanta.append(String.valueOf(textoSanta.charAt(finalI))));
                    try { Thread.sleep(40); } catch (Exception e) {}
                }
                handler.post(() -> btnCocina.setVisibility(View.VISIBLE));
            } catch(Exception e){}
        }).start();

        btnCocina.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", nombreJugador);
            Navigation.findNavController(view).navigate(R.id.action_dialogoHambre_to_sopa, bundle);
        });
    }
}