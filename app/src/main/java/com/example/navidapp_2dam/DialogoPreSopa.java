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

public class DialogoPreSopa extends Fragment {

    private TextView tvSanta;
    private Button btnCocina;
    private String nombreJugador;

    // EL GUIÓN
    private String textoSanta = "SANTA: ¡Jojo! Gracias por aclararme lo del turrón, la verdad es que me encanta.\n\n" +
            "Pero hablar de comida me ha abierto el apetito...\n\n" +
            "Tengo un hambre voraz. ¿Podrías ir a la cocina y conseguirme un buen menú navideño?";

    private Handler handler = new Handler(Looper.getMainLooper());

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
        btnCocina = view.findViewById(R.id.btnIrCocina);

        tvSanta.setText("");

        // Animación de texto
        new Thread(() -> {
            for (int i = 0; i < textoSanta.length(); i++) {
                int finalI = i;
                handler.post(() -> tvSanta.append(String.valueOf(textoSanta.charAt(finalI))));
                try { Thread.sleep(40); } catch (InterruptedException e) {}
            }
            handler.post(() -> btnCocina.setVisibility(View.VISIBLE));
        }).start();

        // Navegación a la Sopa
        btnCocina.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", nombreJugador);
            // Vamos a la sopa
            Navigation.findNavController(view).navigate(R.id.action_dialogoHambre_to_sopa, bundle);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}