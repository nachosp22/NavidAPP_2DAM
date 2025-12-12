package com.example.navidapp_2dam;

import android.os.Bundle;
import android.text.Editable; // IMPORTANTE
import android.text.TextWatcher; // IMPORTANTE
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginFragment extends Fragment {

    EditText etNombre;
    Button btnIniciar;
    Button btnAdmin; // Referencia al nuevo botón

    public LoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNombre = view.findViewById(R.id.etNombre);
        btnIniciar = view.findViewById(R.id.btnIniciar);
        btnAdmin = view.findViewById(R.id.btnAdminBypass); // Vinculamos

        // --- 1. LÓGICA DE JUEGO NORMAL ---
        btnIniciar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            if (!nombre.isEmpty()) {
                iniciarPartida(nombre);
            } else {
                Toast.makeText(getContext(), "Escribe un nombre", Toast.LENGTH_SHORT).show();
            }
        });

        // --- 2. LÓGICA DE "TRUCO" (Easter Egg) ---
        etNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Si escribe la contraseña secreta, mostramos el botón
                if (s.toString().trim().equals("adminadmin")) {
                    btnAdmin.setVisibility(View.VISIBLE);
                } else {
                    btnAdmin.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // --- 3. ACCIÓN DEL BOTÓN ADMIN ---
        btnAdmin.setOnClickListener(v -> {
            // Saltamos directamente sin llamar al servidor (Modo Dios)
            Constantes.TIEMPO_INICIO = System.currentTimeMillis();

            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", "Admin (Cheater)"); // Nombre especial

            Toast.makeText(getContext(), "¡Modo Admin Activado!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.action_login_to_inicioHistoria, bundle);
        });

        btnAdmin.setOnClickListener(v -> {
            // ACTIVAMOS EL MODO DIOS
            Constantes.TIEMPO_INICIO = System.currentTimeMillis();
            Constantes.IS_ADMIN = true; // <--- ESTO ES LO NUEVO

            Bundle bundle = new Bundle();
            bundle.putString("nombreJugador", "Admin (Cheater)");

            Toast.makeText(getContext(), "⚡ MODO SUPER-ADMIN ACTIVADO ⚡", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.action_login_to_inicioHistoria, bundle);
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
                    if (getView() != null) {
                        Navigation.findNavController(getView())
                                .navigate(R.id.action_login_to_inicioHistoria, bundle);
                    }
                },
                error -> {
                    Constantes.TIEMPO_INICIO = System.currentTimeMillis();
                    Bundle bundle = new Bundle();
                    bundle.putString("nombreJugador", nombre);
                    Toast.makeText(getContext(), "Modo Offline activado", Toast.LENGTH_SHORT).show();
                    if (getView() != null) {
                        Navigation.findNavController(getView())
                                .navigate(R.id.action_login_to_inicioHistoria, bundle);
                    }
                });

        queue.add(stringRequest);
    }
}