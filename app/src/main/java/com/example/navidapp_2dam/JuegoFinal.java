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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class JuegoFinal extends Fragment {

    private TextView tvGrinch, tvSanta, tvTiempo;
    private ImageView imgGrinch, imgSanta;
    private LinearLayout layoutInfo;
    private Button btnReiniciar;

    private String nombreJugador;
    private Handler handler = new Handler(Looper.getMainLooper());

    // GUIONES
    private String textoGrinch = "GRINCH: Grrr... ¡Me has ganado! Nunca puedo contigo... Pero supongo que al final los niños merecen recibir sus regalos.";
    private String textoSanta = "SANTA: ¡Jo jo jo! ¡Feliz Navidad muchacho! Has salvado la noche. Ya te dejaré algo bueno en tu casa de recompensa.";

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

        // 2. Vincular vistas
        imgGrinch = view.findViewById(R.id.imgGrinchFinal);
        tvGrinch = view.findViewById(R.id.tvGrinchFinal);

        imgSanta = view.findViewById(R.id.imgSantaFinal);
        tvSanta = view.findViewById(R.id.tvSantaFinal);

        layoutInfo = view.findViewById(R.id.layoutInfoFinal);
        tvTiempo = view.findViewById(R.id.tvTiempoFinal);
        btnReiniciar = view.findViewById(R.id.btnVolverInicio);

        // 3. Conectar al servidor
        finalizarEnServidor();

        // 4. Iniciar la secuencia de cine
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

                // --- FASE 1: EL GRINCH ---
                handler.post(() -> {
                    imgGrinch.setVisibility(View.VISIBLE);
                    tvGrinch.setVisibility(View.VISIBLE);
                    tvGrinch.setText(""); // Limpieza de seguridad
                });
                escribirTexto(tvGrinch, textoGrinch);
                Thread.sleep(textoGrinch.length() * 50 + 1500);

                // --- FASE 2: SANTA CLAUS ---
                handler.post(() -> {
                    imgSanta.setVisibility(View.VISIBLE);
                    tvSanta.setVisibility(View.VISIBLE);
                    tvSanta.setText(""); // Limpieza de seguridad
                });
                escribirTexto(tvSanta, textoSanta);
                Thread.sleep(textoSanta.length() * 50 + 1500);

                // --- FASE 3: INFO FINAL ---
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
        // Evitar conexión si es modo offline/bypass
        if (nombreJugador.equals("Aventurero") || nombreJugador.contains("Cheater")) {
            tvTiempo.setText("Tiempo: Modo Offline");
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = Constantes.URL_SERVIDOR + "/finalizar/" + nombreJugador;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        long tiempo = jsonResponse.optLong("totalTiempo", 0);
                        tvTiempo.setText("⏱️ TIEMPO TOTAL: " + tiempo + " MIN");
                    } catch (Exception e) {
                        tvTiempo.setText("Tiempo guardado (Error formato)");
                    }
                },
                error -> tvTiempo.setText("Tiempo no registrado (Error Red)"));

        queue.add(stringRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}