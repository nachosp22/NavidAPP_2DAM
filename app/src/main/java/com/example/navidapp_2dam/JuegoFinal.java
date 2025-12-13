package com.example.navidapp_2dam;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class JuegoFinal extends Fragment {

    // Solo declaramos lo que existe en el XML actual
    private TextView tvGrinch, tvSanta;
    private ImageView imgGrinch, imgSanta;
    private LinearLayout layoutInfo;
    private Button btnReiniciar;

    private String nombreJugador;
    private Handler handler = new Handler(Looper.getMainLooper());

    private String textoGrinch = "GRINCH: Grrr... ¡Me has ganado! Nunca puedo contigo... Pero supongo que al final los niños merecen recibir sus regalos.";
    private String textoSanta = "SANTA: ¡Ho Ho Ho! ¡Feliz Navidad muchacho! Has salvado la navidad. Ya te dejaré algo en tu casa como recompensa.";

    public JuegoFinal() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_juego_final, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Recuperar nombre
        nombreJugador = "Aventurero";
        if (getArguments() != null) {
            nombreJugador = getArguments().getString("nombreJugador", "Aventurero");
        }

        // 2. Vincular vistas (YA NO BUSCAMOS tvTiempoFinal)
        imgGrinch = view.findViewById(R.id.imgGrinchFinal);
        tvGrinch = view.findViewById(R.id.tvGrinchFinal);

        imgSanta = view.findViewById(R.id.imgSantaFinal);
        tvSanta = view.findViewById(R.id.tvSantaFinal);

        layoutInfo = view.findViewById(R.id.layoutInfoFinal);
        btnReiniciar = view.findViewById(R.id.btnVolverInicio);

        // 3. Conectar al servidor (Silencioso)
        finalizarEnServidor();

        // 4. Iniciar animación
        reproducirEscenaFinal();

        // 5. Botón reiniciar
        btnReiniciar.setOnClickListener(v -> {
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build();
            Navigation.findNavController(view).navigate(R.id.action_final_to_login, null, navOptions);
        });
    }

    private void reproducirEscenaFinal() {
        new Thread(() -> {
            try {
                // Pausa inicial
                Thread.sleep(500);

                // FASE 1: GRINCH
                handler.post(() -> {
                    imgGrinch.setVisibility(View.VISIBLE);
                    tvGrinch.setVisibility(View.VISIBLE);
                    tvGrinch.setText("");
                });
                escribirTexto(tvGrinch, textoGrinch);
                Thread.sleep(textoGrinch.length() * 50 + 1500);

                // FASE 2: SANTA
                handler.post(() -> {
                    imgSanta.setVisibility(View.VISIBLE);
                    tvSanta.setVisibility(View.VISIBLE);
                    tvSanta.setText("");
                });
                escribirTexto(tvSanta, textoSanta);
                Thread.sleep(textoSanta.length() * 50 + 1500);

                // FASE 3: INFO FINAL
                handler.post(() -> layoutInfo.setVisibility(View.VISIBLE));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void escribirTexto(TextView tv, String texto) {
        new Thread(() -> {
            for (int i = 0; i < texto.length(); i++) {
                int finalI = i;
                handler.post(() -> tv.append(String.valueOf(texto.charAt(finalI))));
                try { Thread.sleep(45); } catch (InterruptedException e) {}
            }
        }).start();
    }

    private void finalizarEnServidor() {
        // Si es offline, simplemente no hacemos nada (ya no hay texto que actualizar)
        if (nombreJugador.equals("Aventurero") || nombreJugador.contains("Cheater")) {
            return;
        }

        // Llamada a la API en segundo plano
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = Constantes.URL_SERVIDOR + "/finalizar/" + nombreJugador;

        // Enviamos la petición pero no necesitamos actualizar la UI con el resultado
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> { /* Éxito silencioso */ },
                error -> { /* Error silencioso */ });

        queue.add(stringRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}