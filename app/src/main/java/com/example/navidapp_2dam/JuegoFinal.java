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
        if (getArguments() != null) {
            nombreJugador = getArguments().getString("nombreJugador", "Aventurero");
        }

        tvTiempo = view.findViewById(R.id.tvTiempoFinal);
        Button btnReiniciar = view.findViewById(R.id.btnVolverInicio);

        tvTiempo.setText("Conectando con Santa...");

        // 2. LLAMAR AL SERVIDOR PARA FINALIZAR Y GUARDAR
        finalizarEnServidor();

        // 3. Botón Volver
        btnReiniciar.setOnClickListener(v -> {
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build();
            Navigation.findNavController(view).navigate(R.id.action_final_to_login, null, navOptions);
        });
    }

    private void finalizarEnServidor() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = Constantes.URL_SERVIDOR + "/finalizar/" + nombreJugador;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // El servidor nos devuelve el jugador actualizado con el tiempo calculado
                        JSONObject jsonResponse = new JSONObject(response);

                        // OJO: Tu servidor calcula en MINUTOS.
                        // Si jugaste menos de 60 segundos, saldrá 0.
                        long tiempo = jsonResponse.optLong("totalTiempo", 0);

                        tvTiempo.setText("¡Tiempo Total: " + tiempo + " min!");
                        Toast.makeText(getContext(), "¡Récord Guardado!", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        tvTiempo.setText("Error al leer respuesta");
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                    tvTiempo.setText("Tiempo no registrado");
                });

        queue.add(stringRequest);
    }
}