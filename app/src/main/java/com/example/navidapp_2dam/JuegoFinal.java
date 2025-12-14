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

    private TextView tvGrinch, tvSanta;
    private ImageView imgGrinch, imgSanta;
    private LinearLayout layoutInfo;
    private Button btnReiniciar;

    private Handler handler = new Handler(Looper.getMainLooper());

    private String textoGrinch = "GRINCH: Grrr... ¡Me has ganado! Nunca puedo contigo... Pero supongo que al final los niños merecen recibir sus regalos.";
    private String textoSanta = "SANTA: ¡Ho Ho Ho! ¡Feliz Navidad muchacho! Has salvado la noche. Ya te dejaré algo bueno en tu casa de recompensa.";

    public JuegoFinal() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_juego_final, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgGrinch = view.findViewById(R.id.imgGrinchFinal);
        tvGrinch = view.findViewById(R.id.tvGrinchFinal);
        imgSanta = view.findViewById(R.id.imgSantaFinal);
        tvSanta = view.findViewById(R.id.tvSantaFinal);
        layoutInfo = view.findViewById(R.id.layoutInfoFinal);
        btnReiniciar = view.findViewById(R.id.btnVolverInicio);

        // --- 1. LLAMADA A LA API ---
        // Usamos el nombre directamente de la constante global
        finalizarEnServidor();

        // --- 2. ANIMACIÓN ---
        reproducirEscenaFinal();

        // --- 3. REINICIAR ---
        btnReiniciar.setOnClickListener(v -> {
            Constantes.IS_ADMIN = false;
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build();
            Navigation.findNavController(view).navigate(R.id.action_final_to_login, null, navOptions);
        });
    }

    private void finalizarEnServidor() {
        // Leemos el nombre de la constante
        String nombre = Constantes.NOMBRE_JUGADOR;

        // Si es Admin o no hay nombre, no hacemos petición
        if (nombre == null || nombre.equals("Admin") || nombre.equals("Aventurero")) return;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = Constantes.URL_SERVIDOR + "/finalizar/" + nombre;

        // Enviamos petición silenciosa (sin actualizar UI)
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> { /* Éxito: Tiempo guardado en servidor */ },
                error -> { /* Error de red: No pasa nada */ });

        queue.add(stringRequest);
    }

    private void reproducirEscenaFinal() {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                handler.post(() -> { imgGrinch.setVisibility(View.VISIBLE); tvGrinch.setVisibility(View.VISIBLE); tvGrinch.setText(""); });
                escribirTexto(tvGrinch, textoGrinch);
                Thread.sleep(textoGrinch.length() * 50 + 1500);

                handler.post(() -> { imgSanta.setVisibility(View.VISIBLE); tvSanta.setVisibility(View.VISIBLE); tvSanta.setText(""); });
                escribirTexto(tvSanta, textoSanta);
                Thread.sleep(textoSanta.length() * 50 + 1500);

                handler.post(() -> layoutInfo.setVisibility(View.VISIBLE));
            } catch (InterruptedException e) {}
        }).start();
    }

    private void escribirTexto(TextView tv, String texto) {
        new Thread(() -> {
            for (int i = 0; i < texto.length(); i++) {
                int finalI = i;
                handler.post(() -> tv.append(String.valueOf(texto.charAt(finalI))));
                try { Thread.sleep(45); } catch (Exception e) {}
            }
        }).start();
    }
}