package com.example.navidapp_2dam; // Tu paquete

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation; // IMPORTANTE

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginFragment extends Fragment {

    EditText etNombre;
    Button btnIniciar;
    Button btnSaltar;

    public LoginFragment() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNombre = view.findViewById(R.id.etNombre);
        btnIniciar = view.findViewById(R.id.btnIniciar);
        btnSaltar = view.findViewById(R.id.botonsaltarlogin);

        // --- BOTÓN 1: INICIAR (CONECTA A LA API) ---
        btnIniciar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            if (!nombre.isEmpty()) {
                iniciarPartida(nombre);
            } else {
                Toast.makeText(getContext(), "Escribe un nombre", Toast.LENGTH_SHORT).show();
            }
        });

        // --- BOTÓN 2: SALTAR (MODO OFFLINE) ---
        btnSaltar.setOnClickListener(v -> {
            // CORRECCIÓN AQUÍ: Usamos el ID exacto de tu nav_graph
            try {
                Navigation.findNavController(view).navigate(R.id.action_login_to_inicioHistoria);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error al navegar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void iniciarPartida(String nombre) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        String url = Constantes.URL_SERVIDOR + "/crear/" + nombre;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(getContext(), "¡Conectado!", Toast.LENGTH_SHORT).show();

                    Constantes.TIEMPO_INICIO = System.currentTimeMillis();

                    Bundle bundle = new Bundle();
                    bundle.putString("nombreJugador", nombre);

                    // CORRECCIÓN AQUÍ TAMBIÉN:
                    // Usamos R.id.action_login_to_inicioHistoria
                    if (getView() != null) {
                        Navigation.findNavController(getView())
                                .navigate(R.id.action_login_to_inicioHistoria, bundle);
                    }
                },
                error -> {
                    String msg = "Error de conexión";
                    if (error.networkResponse != null) msg = "Error: " + error.networkResponse.statusCode;
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                });

        queue.add(stringRequest);
    }
}