package com.example.navidapp_2dam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.NavOptions;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class JuegoFinal extends Fragment {

    private TextView tvTiempo;
    private String nombreJugador;

    public JuegoFinal() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_juego_final, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Recuperar nombre
        nombreJugador = "Aventurero";
        if (getArguments() != null) nombreJugador = getArguments().getString("nombreJugador", "Aventurero");

        tvTiempo = view.findViewById(R.id.tvTiempoFinal);
        Button btnReiniciar = view.findViewById(R.id.btnVolverInicio);

        // 2. Llamar a la API para cerrar tiempo
        obtenerTiempoFinal();

        // 3. Botón Reiniciar (Vuelve al Login)
        btnReiniciar.setOnClickListener(v -> {
            // Esto limpia la pila para que no puedas volver atrás con el botón del móvil
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build();

            Navigation.findNavController(view).navigate(R.id.action_final_to_login, null, navOptions);
        });
    }

    private void obtenerTiempoFinal() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Usamos tu constante o la URL directa
        String url = Constantes.URL_SERVIDOR + "/finalizar/" + nombreJugador;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // PROCESAR EL JSON COMO EN TU MAINACTIVITY2
                        JSONObject jsonResponse = new JSONObject(response);
                        long tiempo = jsonResponse.optLong("totalTiempo", 0); //

                        tvTiempo.setText("Tiempo Total: " + tiempo + " segs");

                    } catch (Exception e) {
                        e.printStackTrace();
                        tvTiempo.setText("Tiempo: Error al leer");
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error al conectar con servidor", Toast.LENGTH_SHORT).show();
                    tvTiempo.setText("Tiempo: Desconocido");
                });

        queue.add(stringRequest);
    }
}